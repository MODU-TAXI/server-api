package com.modutaxi.api.domain.room.service;

import static com.modutaxi.api.common.converter.RoomTagBitMaskConverter.convertRoomTagListToBitMask;
import static org.joda.time.DateTimeConstants.MILLIS_PER_MINUTE;

import com.fasterxml.jackson.databind.JsonNode;
import com.modutaxi.api.common.converter.NaverMapConverter;
import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.MemberErrorCode;
import com.modutaxi.api.common.exception.errorcode.RoomErrorCode;
import com.modutaxi.api.common.exception.errorcode.SpotError;
import com.modutaxi.api.common.fcm.FcmService;
import com.modutaxi.api.domain.chat.ChatRoomMappingInfo;
import com.modutaxi.api.domain.chat.repository.RedisChatRoomRepositoryImpl;
import com.modutaxi.api.domain.chatmessage.dto.ChatMessageRequestDto;
import com.modutaxi.api.domain.chatmessage.entity.MessageType;
import com.modutaxi.api.domain.scheduledmessage.service.ScheduledMessageService;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.room.dto.RoomRequestDto.CreateRoomRequest;
import com.modutaxi.api.domain.room.dto.RoomResponseDto.RoomDetailResponse;
import com.modutaxi.api.domain.room.entity.Room;
import com.modutaxi.api.domain.room.entity.RoomTagBitMask;
import com.modutaxi.api.domain.room.mapper.RoomMapper;
import com.modutaxi.api.domain.room.repository.RoomRepository;
import com.modutaxi.api.domain.spot.entity.Spot;
import com.modutaxi.api.domain.spot.repository.SpotRepository;
import com.modutaxi.api.domain.taxiinfo.service.GetTaxiInfoService;
import com.modutaxi.api.domain.taxiinfo.service.RegisterTaxiInfoService;
import com.mongodb.client.model.geojson.LineString;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RegisterRoomService {

    private static final float MIN_LATITUDE = 33;
    private static final float MAX_LATITUDE = 40;
    private static final float MIN_LONGITUDE = 124;
    private static final float MAX_LONGITUDE = 132;
    private final RoomRepository roomRepository;
    private final SpotRepository spotRepository;
    private final RedisChatRoomRepositoryImpl redisChatRoomRepositoryImpl;
    private final GetTaxiInfoService getTaxiInfoService;
    private final RegisterTaxiInfoService registerTaxiInfoService;
    private final ScheduledMessageService scheduledMessageService;
    private final FcmService fcmService;

    @Transactional
    public RoomDetailResponse createRoom(Member member, CreateRoomRequest createRoomRequest) {
        if (member.isBlocked()) {
            throw new BaseException(MemberErrorCode.BLOCKED_MEMBER);
        }
        createRoomRequestValidator(member, createRoomRequest);

        //거점 찾기
        Spot spot = spotRepository.findById(createRoomRequest.getSpotId())
            .orElseThrow(() -> new BaseException(SpotError.SPOT_ID_NOT_FOUND));

        //시작 지점, 목표 지점 설정
        String startCoordinate =
            NaverMapConverter.coordinateToString(createRoomRequest.getDepartureLongitude(),
                createRoomRequest.getDepartureLatitude());

        String goalCoordinate =
            NaverMapConverter.coordinateToString(spot.getSpotPoint().getX(),
                spot.getSpotPoint().getY());

        //택시 정보 조회
        JsonNode taxiInfo = getTaxiInfoService.getDrivingInfo(startCoordinate, goalCoordinate);

        int expectedCharge = taxiInfo.get("taxiFare").asInt();

        long durationMinutes = taxiInfo.get("duration").asLong() / MILLIS_PER_MINUTE;

        Room room = RoomMapper.toEntity(
            member,
            spot,
            expectedCharge,
            durationMinutes,
            convertRoomTagListToBitMask(createRoomRequest.getRoomTagBitMask()),
            createRoomRequest.getDepartureLongitude(),
            createRoomRequest.getDepartureLatitude(),
            createRoomRequest.getDepartureTime(),
            createRoomRequest.getDepartureName(),
            createRoomRequest.getWishHeadcount()
        );

        LineString path = NaverMapConverter.jsonNodeToLineString(taxiInfo.get("path"));

        roomRepository.save(room);

        //fcm구독
        fcmService.subscribe(member.getId(), room.getId());

        redisChatRoomRepositoryImpl.addRoomInMemberList(room.getId().toString(),
            member.getId().toString());
        registerTaxiInfoService.savePath(room.getId(), path);

        //매핑 정보 저장
        ChatRoomMappingInfo chatRoomMappingInfo = new ChatRoomMappingInfo(room.getId().toString(), member.getNickname());
        redisChatRoomRepositoryImpl.setUserEnterInfo(member.getId().toString(), chatRoomMappingInfo);

        scheduledMessageService.addTask(room.getId(), room.getDepartureTime());

        return RoomMapper.toDto(room, member, path, true, false);
    }//순서 -> FCM 구독 -> 매핑 정보 저장 -> 메시지 전송

    private void createRoomRequestValidator(Member member, CreateRoomRequest createRoomRequest) {
        if (roomRepository.existsRoomByRoomManagerId(member.getId())) {
            throw new BaseException(RoomErrorCode.ALREADY_MEMBER_IS_MANAGER);
        }

        if (convertRoomTagListToBitMask(createRoomRequest.getRoomTagBitMask())
            == RoomTagBitMask.ONLY_WOMAN.getValue() + RoomTagBitMask.ONLY_MAN.getValue()) {
            throw new BaseException(RoomErrorCode.BOTH_GENDER);
        }

        if (createRoomRequest.getDepartureTime().isBefore(LocalDateTime.now())) {
            throw new BaseException(RoomErrorCode.DEPARTURE_BEFORE_CURRENT);
        }

        Float longitude = createRoomRequest.getDepartureLongitude();
        Float latitude = createRoomRequest.getDepartureLatitude();
        if (latitude < MIN_LATITUDE || latitude > MAX_LATITUDE
            || longitude < MIN_LONGITUDE || longitude > MAX_LONGITUDE) {
            throw new BaseException(RoomErrorCode.DEPARTURE_EXCEED_RANGE);
        }
    }
}