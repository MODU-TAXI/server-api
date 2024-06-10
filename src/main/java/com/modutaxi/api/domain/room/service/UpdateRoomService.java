package com.modutaxi.api.domain.room.service;


import static org.joda.time.DateTimeConstants.MILLIS_PER_MINUTE;

import com.fasterxml.jackson.databind.JsonNode;
import com.modutaxi.api.common.converter.NaverMapConverter;
import com.modutaxi.api.common.converter.RoomTagBitMaskConverter;
import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.MemberErrorCode;
import com.modutaxi.api.common.exception.errorcode.ParticipateErrorCode;
import com.modutaxi.api.common.exception.errorcode.RoomErrorCode;
import com.modutaxi.api.common.exception.errorcode.SpotError;
import com.modutaxi.api.common.exception.errorcode.TaxiInfoErrorCode;
import com.modutaxi.api.common.fcm.FcmService;
import com.modutaxi.api.domain.chat.repository.RedisChatRoomRepositoryImpl;
import com.modutaxi.api.domain.chat.service.ChatService;
import com.modutaxi.api.domain.chatmessage.dto.ChatMessageRequestDto;
import com.modutaxi.api.domain.chatmessage.entity.MessageType;
import com.modutaxi.api.domain.chatmessage.service.ChatMessageService;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.member.repository.MemberRepository;
import com.modutaxi.api.domain.room.dto.RoomInternalDto.InternalUpdateRoomDto;
import com.modutaxi.api.domain.room.dto.RoomRequestDto.CreateRoomRequest;
import com.modutaxi.api.domain.room.dto.RoomRequestDto.NonParticipant;
import com.modutaxi.api.domain.room.dto.RoomRequestDto.UpdateRoomRequest;
import com.modutaxi.api.domain.room.dto.RoomRequestDto.UpdateRoomStatusRequest;
import com.modutaxi.api.domain.room.dto.RoomResponseDto.DeleteRoomResponse;
import com.modutaxi.api.domain.room.dto.RoomResponseDto.RoomDetailResponse;
import com.modutaxi.api.domain.room.dto.RoomResponseDto.UpdateRoomResponse;
import com.modutaxi.api.domain.room.entity.Room;
import com.modutaxi.api.domain.room.entity.RoomStatus;
import com.modutaxi.api.domain.room.entity.RoomTagBitMask;
import com.modutaxi.api.domain.room.mapper.RoomMapper;
import com.modutaxi.api.domain.room.repository.RoomRepository;
import com.modutaxi.api.domain.roomwaiting.mapper.RoomWaitingMapper.MemberRoomInResponseList;
import com.modutaxi.api.domain.roomwaiting.service.RoomWaitingService;
import com.modutaxi.api.domain.spot.repository.SpotRepository;
import com.modutaxi.api.domain.taxiinfo.entity.TaxiInfo;
import com.modutaxi.api.domain.taxiinfo.repository.TaxiInfoMongoRepository;
import com.modutaxi.api.domain.taxiinfo.service.GetTaxiInfoService;
import com.mongodb.client.model.geojson.LineString;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
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
    private final RoomWaitingService roomWaitingService;
    private final RedisChatRoomRepositoryImpl redisChatRoomRepositoryImpl;
    private final ChatMessageService chatMessageService;
    private final FcmService fcmService;
    private final MemberRepository memberRepository;
    private final ChatService chatService;

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
                = roomWaitingService.getParticipateInRoom(member, deleteRoomId);

        //메세지 삭제
        chatMessageService.deleteChatMessage(roomId);
        //방 삭제
        roomRepository.delete(room);

        // 참가자들에게 방 삭제 알림 및 FCM 구독 해지
        memberRoomInResponseList.getInList().forEach(item -> {
            try {
                fcmService.sendDeleteRoom(member.getId(), deleteRoomId);
                fcmService.unsubscribe(item.getMemberId(), deleteRoomId);
            } catch (IllegalArgumentException e) {
                log.error("memberId: {}에 대해 roomId: {}에서 FCM 알림 전송 또는 구독 해지 실패. 오류: {}", item.getMemberId(), deleteRoomId, e.getMessage());
            }
        });

        //참가자들의 매핑된 방 정보 삭제
        memberRoomInResponseList.getInList().forEach(item -> {
                try {
                    log.info("{}번 유저 삭제하겠습니다.", item.getMemberId());
                    redisChatRoomRepositoryImpl.removeUserByMemberIdEnterInfo(
                            item.getMemberId().toString());
                } catch (Exception e) {
                    log.error("memberId: {}에 대해 삭제 실패하셨습니다. 오류: {}",item.getMemberId(), e.getMessage());
                }
            }
        );

        //경로 정보 삭제
        taxiInfoMongoRepository.deleteById(taxiInfo.getId());
        log.info("taxiInfo정보가 삭제되었습니다.");
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
    public UpdateRoomResponse finishMatching(Member manager, Long roomId, UpdateRoomStatusRequest updateRoomStatusRequest) {

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new BaseException(RoomErrorCode.EMPTY_ROOM));

        checkManager(room.getRoomManager().getId(), manager.getId());

        if(!room.getRoomStatus().equals(RoomStatus.PROCEEDING)) {
            throw new BaseException(RoomErrorCode.ALREADY_MATCHING_COMPLETE);
        }
        //룸 상태 변경
        room.roomStatusUpdate();

        //request 조회
        List<NonParticipant> memberList = updateRoomStatusRequest.getNonParticipantList();

        //member 정당성 확인
        List<Member> nonParticipantList = memberList.stream()
                .map(nonParticipant -> {
                    Member member = memberRepository.findByIdAndStatusTrue(nonParticipant.getMemberId())
                            .orElseThrow(() -> new BaseException(MemberErrorCode.EMPTY_MEMBER));

                    boolean isInRoom = redisChatRoomRepositoryImpl.findMemberInRoomInList
                            (roomId.toString(), nonParticipant.toString());

                    if(!isInRoom) {
                        throw new BaseException(ParticipateErrorCode.USER_NOT_IN_ROOM);
                    }

                    return member;
                }).toList();

        // TODO: 5/27/24 실제로 참여하지 않은 애들 처리
        // ex) redisChatRoomRepositoryImpl.removeFromRoomInList(roomId.toString(), nonParticipant.getUserId().toString());
        // ex) 새로운 저장공간에 실제 참여한 리스트 저장 -> 이 방향이 요구사항에 적합할듯

        ChatMessageRequestDto chatMessageRequestDto =
                new ChatMessageRequestDto(roomId, MessageType.CHAT_BOT, "목적지에 도착했다면 정산하기를 눌러주세요."
                        ,"모두의 택시 봇",room.getRoomManager().getId().toString(),LocalDateTime.now());

        chatService.sendChatMessage(chatMessageRequestDto);

        return new UpdateRoomResponse(true);
    }

    @Transactional
    public UpdateRoomResponse finishMatchingTest(Member manager, Long roomId) {
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new BaseException(RoomErrorCode.EMPTY_ROOM));

        checkManager(room.getRoomManager().getId(), manager.getId());

        if (!room.getRoomStatus().equals(RoomStatus.PROCEEDING)) {
            throw new BaseException(RoomErrorCode.ALREADY_MATCHING_COMPLETE);
        }
        //룸 상태 변경
        room.roomStatusUpdate();

        ChatMessageRequestDto chatMessageRequestDto =
            new ChatMessageRequestDto(roomId, MessageType.CHAT_BOT, "목적지에 도착했다면 정산하기를 눌러주세요."
                , "모두의 택시 봇", room.getRoomManager().getId().toString(), LocalDateTime.now());

        chatService.sendChatMessage(chatMessageRequestDto);

        return new UpdateRoomResponse(true);
    }
}