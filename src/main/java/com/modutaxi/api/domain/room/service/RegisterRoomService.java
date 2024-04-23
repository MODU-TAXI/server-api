package com.modutaxi.api.domain.room.service;

import static com.modutaxi.api.common.converter.RoomTagBitMaskConverter.convertRoomTagListToBitMask;

import com.fasterxml.jackson.databind.JsonNode;
import com.modutaxi.api.common.converter.NaverMapConverter;
import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.RoomErrorCode;
import com.modutaxi.api.common.exception.errorcode.SpotError;
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
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RegisterRoomService {

    private final RoomRepository roomRepository;
    private final SpotRepository spotRepository;

    private final GetTaxiInfoService getTaxiInfoService;
    private final RegisterTaxiInfoService registerTaxiInfoService;

    private static final float MIN_LATITUDE = 33;
    private static final float MAX_LATITUDE = 40;
    private static final float MIN_LONGITUDE = 124;
    private static final float MAX_LONGITUDE = 132;

    @Transactional
    public RoomDetailResponse createRoom(Member member, CreateRoomRequest createRoomRequest) {

        createRoomRequestValidator(member, createRoomRequest);

        //거점 찾기
        Spot spot = spotRepository.findById(createRoomRequest.getSpotId())
            .orElseThrow(() -> new BaseException(SpotError.SPOT_ID_NOT_FOUND));

        //시작 지점, 목표 지점 설정
        String startCoordinate =
            NaverMapConverter.coordinateToString(createRoomRequest.getLongitude(),
                createRoomRequest.getLatitude());

        String goalCoordinate =
            NaverMapConverter.coordinateToString(spot.getSpotPoint().getX(),
                spot.getSpotPoint().getY());

        //택시 정보 조회
        JsonNode taxiInfo = getTaxiInfoService.getDrivingInfo(startCoordinate, goalCoordinate);

        int expectedCharge = taxiInfo.get("taxiFare").asInt();

        long duration = taxiInfo.get("duration").asLong();

        GeometryFactory geometryFactory = new GeometryFactory();

        Coordinate coordinate
            = new Coordinate(createRoomRequest.getLongitude(), createRoomRequest.getLatitude());

        Point point = geometryFactory.createPoint(coordinate);

        Room room = RoomMapper.toEntity(member, spot,
            expectedCharge, duration,
            convertRoomTagListToBitMask(
                createRoomRequest.getRoomTagBitMask()),
            point, createRoomRequest.getDepartureTime()
        );

        LineString path = NaverMapConverter.jsonNodeToLineString(taxiInfo.get("path"));

        roomRepository.save(room);
        registerTaxiInfoService.savePath(room.getId(), path);
        return RoomDetailResponse.toDto(room, path);
    }

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

        Float longitude = createRoomRequest.getLongitude();
        Float latitude = createRoomRequest.getLatitude();
        if (latitude < MIN_LATITUDE || latitude > MAX_LATITUDE
            || longitude < MIN_LONGITUDE || longitude > MAX_LONGITUDE) {
            throw new BaseException(RoomErrorCode.DEPARTURE_EXCEED_RANGE);
        }
    }
}