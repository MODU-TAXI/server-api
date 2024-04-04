package com.modutaxi.api.domain.room.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.modutaxi.api.common.converter.NaverMapConverter;
import com.modutaxi.api.common.converter.RoomTagBitMaskConverter;
import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.ErrorCode;
import com.modutaxi.api.common.exception.errorcode.RoomErrorCode;
import com.modutaxi.api.domain.destination.entity.Destination;
import com.modutaxi.api.domain.destination.repository.DestinationRepository;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.room.dto.RoomRequestDto.CreateRoomRequest;
import com.modutaxi.api.domain.room.dto.RoomResponseDto.RoomDetailResponse;
import com.modutaxi.api.domain.room.entity.Room;
import com.modutaxi.api.domain.room.mapper.RoomMapper;
import com.modutaxi.api.domain.room.repository.RoomRepository;
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
    private final DestinationRepository destinationRepository;

    private final GetTaxiInfoService getTaxiInfoService;
    private final RegisterTaxiInfoService registerTaxiInfoService;

    @Transactional
    public RoomDetailResponse createRoom(Member member, CreateRoomRequest createRoomRequest) {
        if (roomRepository.existsRoomByRoomManagerId(member.getId())) {
            throw new BaseException(RoomErrorCode.ALREADY_MEMBER_IS_MANAGER);
        }

        // TODO: 2024/04/03 망고스틴님 거점 조회에러 만들면 넣던가 하겠습니다!
        //거점 찾기
        Destination destination = destinationRepository.findById(
                createRoomRequest.getDestinationId())
            .orElseThrow();

        //시작 지점, 목표 지점 설정
        String startCoordinate =
            NaverMapConverter.coordinateToString(createRoomRequest.getPoint().getX(),
                createRoomRequest.getPoint().getY());
        String goalCoordinate =
            NaverMapConverter.coordinateToString(destination.getLongitude(),
                destination.getLatitude());

        //택시 정보 조회
        JsonNode taxiInfo = getTaxiInfoService.getDrivingInfo(startCoordinate, goalCoordinate);

        int expectedCharge = taxiInfo.get("taxiFare").asInt();

        long duration = taxiInfo.get("duration").asLong();

        Room room = RoomMapper.toEntity(member, createRoomRequest.getRoomName(), destination,
            expectedCharge, duration,
            createRoomRequest.getDescription(),
            RoomTagBitMaskConverter.convertRoomTagListToBitMask(
                createRoomRequest.getRoomTagBitMask()),
            createRoomRequest.getPoint(), createRoomRequest.getDepartTime()
        );

        LineString path = NaverMapConverter.jsonNodeToLineString(taxiInfo.get("path"));

        roomRepository.save(room);
        registerTaxiInfoService.savePath(room.getId(), path);
        return RoomDetailResponse.toDto(room, path);
    }
}