package com.modutaxi.api.domain.room.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.modutaxi.api.common.converter.Converter;
import com.modutaxi.api.domain.destination.entity.Destination;
import com.modutaxi.api.domain.destination.repository.DestinationRepository;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.room.dto.RoomRequestDto.CreateRoomRequest;
import com.modutaxi.api.domain.room.dto.RoomResponseDto.RoomDetailResponse;
import com.modutaxi.api.domain.room.entity.Room;
import com.modutaxi.api.domain.room.mapper.RoomMapper;
import com.modutaxi.api.domain.room.repository.RoomRepository;
import com.modutaxi.api.domain.taxiinfo.entity.Point;
import com.modutaxi.api.domain.taxiinfo.service.GetTaxiInfoService;
import com.modutaxi.api.domain.taxiinfo.service.RegisterTaxiInfoService;
import jakarta.transaction.Transactional;
import java.util.List;
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
    public RoomDetailResponse createRoom(Member member, CreateRoomRequest roomRequest) {

        // TODO: 2024/04/03 망고스틴님 거점 조회에러 만들면 넣던가 하겠습니다!
        //거점 찾기
        Destination destination = destinationRepository.findById(roomRequest.getDestinationId())
            .orElseThrow();

        //시작 지점, 목표 지점 설정
        String startCoordinate =
            Converter.coordinateToString(roomRequest.getStartLongitude(),
                roomRequest.getStartLatitude());
        String goalCoordinate =
            Converter.coordinateToString(destination.getLongitude(), destination.getLatitude());

        //택시 정보 조회
        JsonNode taxiInfo = getTaxiInfoService.getDrivingInfo(startCoordinate, goalCoordinate);

        int expectedCharge = taxiInfo.get("taxiFare").asInt();

        long duration = taxiInfo.get("duration").asLong();

        Room room = RoomMapper.toEntity(member, roomRequest.getRoomName(), destination,
            expectedCharge, duration,
            roomRequest.getDescription(), roomRequest.getRoomTagBitMask(),
            roomRequest.getStartLongitude(), roomRequest.getStartLatitude(),
            roomRequest.getDepartTime()
        );

        List<Point> path = Converter.jsonNodeToPointList(taxiInfo.get("path"));

        roomRepository.save(room);
        registerTaxiInfoService.savePath(room.getId(), path);
        return RoomDetailResponse.toDto(room, path);
    }
}