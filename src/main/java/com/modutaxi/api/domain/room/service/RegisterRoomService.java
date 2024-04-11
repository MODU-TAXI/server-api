package com.modutaxi.api.domain.room.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.modutaxi.api.common.converter.NaverMapConverter;
import com.modutaxi.api.common.converter.RoomTagBitMaskConverter;
import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.RoomErrorCode;
import com.modutaxi.api.common.exception.errorcode.SpotError;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.room.dto.RoomRequestDto.CreateRoomRequest;
import com.modutaxi.api.domain.room.dto.RoomResponseDto.RoomDetailResponse;
import com.modutaxi.api.domain.room.entity.Room;
import com.modutaxi.api.domain.room.mapper.RoomMapper;
import com.modutaxi.api.domain.room.repository.RoomRepository;
import com.modutaxi.api.domain.spot.entity.Spot;
import com.modutaxi.api.domain.spot.repository.SpotRepository;
import com.modutaxi.api.domain.taxiinfo.service.GetTaxiInfoService;
import com.modutaxi.api.domain.taxiinfo.service.RegisterTaxiInfoService;
import com.mongodb.client.model.geojson.LineString;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RegisterRoomService {

    private final RoomRepository roomRepository;
    private final SpotRepository spotRepository;

    private final GetTaxiInfoService getTaxiInfoService;
    private final RegisterTaxiInfoService registerTaxiInfoService;

    @Transactional
    public RoomDetailResponse createRoom(Member member, CreateRoomRequest createRoomRequest) {
        if (roomRepository.existsRoomByRoomManagerId(member.getId())) {
            throw new BaseException(RoomErrorCode.ALREADY_MEMBER_IS_MANAGER);
        }

        //거점 찾기
        Spot spot = spotRepository.findById(createRoomRequest.getSpotId())
            .orElseThrow(() -> new BaseException(SpotError.SPOT_ID_NOT_FOUND));

        //시작 지점, 목표 지점 설정
        String startCoordinate =
            NaverMapConverter.coordinateToString(createRoomRequest.getDeparturePoint().getX(),
                createRoomRequest.getDeparturePoint().getY());
        String goalCoordinate =
            NaverMapConverter.coordinateToString(spot.getSpotPoint().getX(),
                spot.getSpotPoint().getY());

        //택시 정보 조회
        JsonNode taxiInfo = getTaxiInfoService.getDrivingInfo(startCoordinate, goalCoordinate);

        int expectedCharge = taxiInfo.get("taxiFare").asInt();

        long duration = taxiInfo.get("duration").asLong();

        Room room = RoomMapper.toEntity(member, spot,
            expectedCharge, duration,
            RoomTagBitMaskConverter.convertRoomTagListToBitMask(
                createRoomRequest.getRoomTagBitMask()),
            createRoomRequest.getDeparturePoint(), createRoomRequest.getDepartureTime()
        );

        LineString path = NaverMapConverter.jsonNodeToLineString(taxiInfo.get("path"));

        roomRepository.save(room);
        registerTaxiInfoService.savePath(room.getId(), path);
        return RoomDetailResponse.toDto(room, path);
    }
}