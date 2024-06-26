package com.modutaxi.api.domain.room.service;


import static org.joda.time.DateTimeConstants.MILLIS_PER_MINUTE;

import com.fasterxml.jackson.databind.JsonNode;
import com.modutaxi.api.common.converter.NaverMapConverter;
import com.modutaxi.api.common.converter.RoomTagBitMaskConverter;
import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.RoomErrorCode;
import com.modutaxi.api.common.exception.errorcode.SpotError;
import com.modutaxi.api.common.exception.errorcode.TaxiInfoErrorCode;
import com.modutaxi.api.common.fcm.FcmService;
import com.modutaxi.api.common.util.time.TimeFormatConverter;
import com.modutaxi.api.domain.alarm.entity.AlarmType;
import com.modutaxi.api.domain.alarm.service.RegisterAlarmService;
import com.modutaxi.api.domain.chat.repository.RedisChatRoomRepositoryImpl;
import com.modutaxi.api.domain.chat.service.ChatService;
import com.modutaxi.api.domain.chatmessage.dto.ChatMessageRequestDto;
import com.modutaxi.api.domain.chatmessage.entity.MessageType;
import com.modutaxi.api.domain.chatmessage.service.ChatMessageService;
import com.modutaxi.api.domain.history.repository.HistoryRepository;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.participant.dto.ParticipantResponseDto.MemberRoomInResponseList;
import com.modutaxi.api.domain.participant.repository.ParticipantRepository;
import com.modutaxi.api.domain.participant.service.GetParticipantService;
import com.modutaxi.api.domain.room.dto.RoomInternalDto.InternalUpdateRoomDto;
import com.modutaxi.api.domain.room.dto.RoomRequestDto.CreateRoomRequest;
import com.modutaxi.api.domain.room.dto.RoomRequestDto.UpdateRoomRequest;
import com.modutaxi.api.domain.room.dto.RoomResponseDto.DeleteRoomResponse;
import com.modutaxi.api.domain.room.dto.RoomResponseDto.RoomDetailResponse;
import com.modutaxi.api.domain.room.dto.RoomResponseDto.UpdateRoomResponse;
import com.modutaxi.api.domain.room.entity.Room;
import com.modutaxi.api.domain.room.entity.RoomStatus;
import com.modutaxi.api.domain.room.entity.RoomTagBitMask;
import com.modutaxi.api.domain.room.mapper.RoomMapper;
import com.modutaxi.api.domain.room.repository.RoomRepository;
import com.modutaxi.api.domain.spot.repository.SpotRepository;
import com.modutaxi.api.domain.taxiinfo.entity.TaxiInfo;
import com.modutaxi.api.domain.taxiinfo.repository.TaxiInfoMongoRepository;
import com.modutaxi.api.domain.taxiinfo.service.GetTaxiInfoService;
import com.mongodb.client.model.geojson.LineString;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class UpdateRoomService {

    private static final float MIN_LATITUDE = 33;
    private static final float MAX_LATITUDE = 40;
    private static final float MIN_LONGITUDE = 124;
    private static final float MAX_LONGITUDE = 132;
    private final RoomRepository roomRepository;
    private final TaxiInfoMongoRepository taxiInfoMongoRepository;
    private final SpotRepository spotRepository;
    private final GetTaxiInfoService getTaxiInfoService;
    private final RedisChatRoomRepositoryImpl redisChatRoomRepositoryImpl;
    private final ChatMessageService chatMessageService;
    private final FcmService fcmService;
    private final HistoryRepository historyRepository;
    private final ChatService chatService;
    private final GetParticipantService getParticipantService;
    private final ParticipantRepository participantRepository;
    private final RegisterAlarmService registerAlarmService;

    @Transactional
    public RoomDetailResponse updateRoom(Member member, Long roomId,
        UpdateRoomRequest updateRoomRequest) {

        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new BaseException(RoomErrorCode.EMPTY_ROOM));

        TaxiInfo taxiInfo = taxiInfoMongoRepository.findById(roomId)
            .orElseThrow(() -> new BaseException(TaxiInfoErrorCode.EMPTY_TAXI_INFO));

        checkManager(room.getRoomManager().getId(), member.getId());

        InternalUpdateRoomDto oldRoomData = InternalUpdateRoomDto.toDto(room);

        InternalUpdateRoomDto newRoomData = validateAndReturnNewDto(oldRoomData, updateRoomRequest);

        //TaxiInfo db 업데이트
        LineString path = taxiInfo.getPath();
        if (shouldUpdateTaxiInfo(updateRoomRequest)) {
            path = updateTaxiInfo(roomId, newRoomData);
        }

        room.update(newRoomData);
        fcmService.sendUpdateRoomInfo(member.getId(), roomId);
        return RoomMapper.toDto(room, member, path, true, false);
    }

    @Transactional
    public DeleteRoomResponse deleteRoom(Member member, Long roomId) {

        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new BaseException(RoomErrorCode.EMPTY_ROOM));

        TaxiInfo taxiInfo = taxiInfoMongoRepository.findById(roomId)
            .orElseThrow(() -> new BaseException(TaxiInfoErrorCode.EMPTY_TAXI_INFO));

        checkManager(room.getRoomManager().getId(), member.getId());

        Long deleteRoomId = room.getId();

        MemberRoomInResponseList memberRoomInResponseList
            = getParticipantService.getParticipateInRoom(member, deleteRoomId);

        //방 Soft Delete
        room.roomStatusUpdateDelete();

        // 참가자들에게 방 삭제 알림 및 FCM 구독 해지
        memberRoomInResponseList.getInList().forEach(item -> {
            try {
                fcmService.sendDeleteRoom(member.getId(), deleteRoomId);
                fcmService.unsubscribe(item.getMemberId(), deleteRoomId);
            } catch (IllegalArgumentException e) {
                log.error("memberId: {}에 대해 roomId: {}에서 FCM 알림 전송 또는 구독 해지 실패. 오류: {}",
                    item.getMemberId(), deleteRoomId, e.getMessage());
            }
        });

        //참가자들의 매핑된 방 정보 삭제
        participantRepository.deleteAllByRoom(room);

        memberRoomInResponseList.getInList().forEach(item -> {
                try {
                    log.info("{}번 유저 삭제하겠습니다.", item.getMemberId());
                    redisChatRoomRepositoryImpl.removeUserByMemberIdEnterInfo(
                        item.getMemberId().toString());
                } catch (Exception e) {
                    log.error("memberId: {}에 대해 삭제 실패하셨습니다. 오류: {}", item.getMemberId(),
                        e.getMessage());
                }
            }
        );

        return new DeleteRoomResponse(true);
    }

    private void createRoomRequestValidator(Member member, CreateRoomRequest createRoomRequest) {

        Float longitude = createRoomRequest.getDepartureLongitude();
        Float latitude = createRoomRequest.getDepartureLatitude();
        if (latitude < MIN_LATITUDE || latitude > MAX_LATITUDE
            || longitude < MIN_LONGITUDE || longitude > MAX_LONGITUDE) {
            throw new BaseException(RoomErrorCode.DEPARTURE_EXCEED_RANGE);
        }
    }

    public InternalUpdateRoomDto validateAndReturnNewDto(InternalUpdateRoomDto oldRoomData,
        UpdateRoomRequest updateRoomRequest) {

        // 태그 업데이트 및 예외처리
        oldRoomData.setRoomTagBitMask(
            RoomTagBitMaskConverter.convertRoomTagListToBitMask(
                updateRoomRequest.getRoomTagBitMask()) != 0
                ? RoomTagBitMaskConverter.convertRoomTagListToBitMask(
                updateRoomRequest.getRoomTagBitMask())
                : oldRoomData.getRoomTagBitMask()
        );

        if (oldRoomData.getRoomTagBitMask()
            == RoomTagBitMask.ONLY_WOMAN.getValue() + RoomTagBitMask.ONLY_MAN.getValue()) {
            throw new BaseException(RoomErrorCode.BOTH_GENDER);
        }

        // 출발시간 업데이트 및 예외처리
        oldRoomData.setDepartureTime(
            updateRoomRequest.getDepartureTime() != null ? updateRoomRequest.getDepartureTime()
                : oldRoomData.getDepartureTime());

        if (oldRoomData.getDepartureTime().isBefore(LocalDateTime.now())) {
            throw new BaseException(RoomErrorCode.DEPARTURE_BEFORE_CURRENT);
        }

        // 인원수 업데이트
        oldRoomData.setWishHeadcount(
            updateRoomRequest.getWishHeadcount() != 0 ? updateRoomRequest.getWishHeadcount()
                : oldRoomData.getWishHeadcount());

        // 목적지 업데이트
        oldRoomData.setSpot(
            updateRoomRequest.getSpotId() != null ? spotRepository.findById(
                    updateRoomRequest.getSpotId())
                .orElseThrow(() -> new BaseException(SpotError.SPOT_ID_NOT_FOUND))
                : oldRoomData.getSpot());

        // 출발지 업데이트 및 예외처리
        // 위도, 경도 중에서 값이 하나만 들어왔을 때 예외처리
        if ((updateRoomRequest.getDepartureLatitude() != null
            && updateRoomRequest.getDepartureLongitude() == null)
            || (updateRoomRequest.getDepartureLatitude() == null
            && updateRoomRequest.getDepartureLongitude() != null)) {
            throw new BaseException(RoomErrorCode.POINT_IS_NOT_INDEPENDENT);
        }
        oldRoomData.setDepartureLongitude(
            updateRoomRequest.getDepartureLongitude() != null
                ? updateRoomRequest.getDepartureLongitude()
                : oldRoomData.getDepartureLongitude());

        oldRoomData.setDepartureLatitude(
            updateRoomRequest.getDepartureLatitude() != null
                ? updateRoomRequest.getDepartureLatitude()
                : oldRoomData.getDepartureLatitude());

        Float longitude = oldRoomData.getDepartureLongitude();
        Float latitude = oldRoomData.getDepartureLatitude();
        if (latitude < MIN_LATITUDE || latitude > MAX_LATITUDE
            || longitude < MIN_LONGITUDE || longitude > MAX_LONGITUDE) {
            throw new BaseException(RoomErrorCode.DEPARTURE_EXCEED_RANGE);
        }

        // 출발지 이름 업데이트
        oldRoomData.setDepartureName(
            updateRoomRequest.getDepartureName() != null
                ? updateRoomRequest.getDepartureName()
                : oldRoomData.getDepartureName()
        );

        return oldRoomData;
    }

    @Transactional
    public LineString updateTaxiInfo(Long roomId, InternalUpdateRoomDto internalUpdateRoomDto) {
        String startCoordinate =
            NaverMapConverter.coordinateToString(internalUpdateRoomDto.getDepartureLongitude(),
                internalUpdateRoomDto.getDepartureLatitude());

        String goalCoordinate =
            NaverMapConverter.coordinateToString(
                internalUpdateRoomDto.getSpot().getSpotPoint().getX(),
                internalUpdateRoomDto.getSpot().getSpotPoint().getY());

        JsonNode jsonNode =
            getTaxiInfoService.getDrivingInfo(startCoordinate, goalCoordinate);

        LineString path = NaverMapConverter.jsonNodeToLineString(jsonNode.get("path"));

        internalUpdateRoomDto.setExpectedCharge(jsonNode.get("taxiFare").asInt());
        internalUpdateRoomDto.setDurationMinutes(
            jsonNode.get("duration").asLong() / MILLIS_PER_MINUTE);

        taxiInfoMongoRepository.save(TaxiInfo.toEntity(roomId, path));
        return path;
    }

    private boolean shouldUpdateTaxiInfo(UpdateRoomRequest updateRoomRequest) {
        return updateRoomRequest.getSpotId() != null
            || updateRoomRequest.getDepartureLongitude() != null
            || updateRoomRequest.getDepartureLatitude() != null;
    }

    private void checkManager(Long managerId, Long memberId) {
        if (!managerId.equals(memberId)) {
            throw new BaseException(RoomErrorCode.NOT_ROOM_MANAGER);
        }
    }

    @Transactional
    public UpdateRoomResponse finishMatching(Member manager, Long roomId) {
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new BaseException(RoomErrorCode.EMPTY_ROOM));

        checkManager(room.getRoomManager().getId(), manager.getId());

        if (!room.getRoomStatus().equals(RoomStatus.PROCEEDING)) {
            throw new BaseException(RoomErrorCode.ALREADY_MATCHING_COMPLETE);
        }

        // 룸 상태 변경
        room.roomStatusUpdate();
        // 정산 요청 메시지 전송
        ChatMessageRequestDto matchingCompleteMessageRequestDto =
            new ChatMessageRequestDto(
                roomId, MessageType.PAYMENT_REQUEST,
                "목적지에 도착했다면,\n정산하기를 눌러주세요.",
                MessageType.PAYMENT_REQUEST.getSenderName(),
                room.getRoomManager().getId().toString(),
                LocalDateTime.now(), "");

        // 택시 정보 메시지 전송
        String taxiInfoMessageContent =
            TimeFormatConverter.covertTimeToShortClockTime(
                room.getDepartureTime().plusMinutes(room.getDurationMinutes())) +
                "에 도착예정이에요,\n" + "예상 금액은 " + room.getExpectedCharge() + "원 이에요!";
        ChatMessageRequestDto taxiInfoMessageRequestDto =
            new ChatMessageRequestDto(
                roomId, MessageType.CHAT_BOT,
                taxiInfoMessageContent,
                MessageType.CHAT_BOT.getSenderName(),
                room.getRoomManager().getId().toString(),
                LocalDateTime.now(), "");

        chatService.sendChatMessage(matchingCompleteMessageRequestDto);
        registerAlarmService.registerAlarm(AlarmType.PAYMENT_REQUEST, roomId,
            room.getRoomManager().getId());
        chatService.sendChatMessage(taxiInfoMessageRequestDto);

        return new UpdateRoomResponse(true);
    }
}