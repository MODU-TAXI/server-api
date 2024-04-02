package com.modutaxi.api.domain.room.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.modutaxi.api.domain.destination.entity.Destination;
import com.modutaxi.api.domain.destination.repository.DestinationRepository;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.room.dto.RoomRequestDto.CreateRoomRequest;
import com.modutaxi.api.domain.room.dto.RoomResponseDto.RoomDetailResponse;
import com.modutaxi.api.domain.room.entity.Room;
import com.modutaxi.api.domain.room.mapper.RoomMapper;
import com.modutaxi.api.domain.room.repository.RoomRepository;
import com.modutaxi.api.domain.taxiinfo.entity.Point;
import com.modutaxi.api.domain.taxiinfo.repository.TaxiInfoRepository;
import com.modutaxi.api.domain.taxiinfo.service.GetTaxiInfoService;
import com.modutaxi.api.domain.taxiinfo.service.RegisterTaxiInfoService;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RegisterRoomService {

    private final RoomRepository roomRepository;
    private final DestinationRepository destinationRepository;

    private final GetTaxiInfoService taxiInfoService;
    private final RegisterTaxiInfoService registerTaxiInfoService;

    @Transactional
    public RoomDetailResponse createRoom(Member member, CreateRoomRequest roomRequest) {

        //거점 찾기
        Destination destination = destinationRepository.findById(roomRequest.getDestinationId())
            .orElseThrow();

        //시작 좌표 설정
        String startCoordinate =
            toCoordinate(roomRequest.getStartLongitude(), roomRequest.getStartLatitude());
        String goalCoordinate =
            toCoordinate(destination.getLongitude(), destination.getLatitude());

        //택시 정보 조회
        JsonNode taxiInfo = taxiInfoService.getDrivingInfo(startCoordinate, goalCoordinate);

        int expectedCharge = taxiInfo.get("taxiFare").asInt();

        long duration = taxiInfo.get("duration").asLong();


        Room room = RoomMapper.toEntity(member, roomRequest.getRoomName(), destination,
            expectedCharge, duration,
            roomRequest.getDescription(), roomRequest.getRoomTagBitMask(),
            roomRequest.getStartLongitude(), roomRequest.getStartLatitude(),
            roomRequest.getDepartTime()
        );

        List<Point> path = jsonNodeToPointList(taxiInfo.get("path"));

        roomRepository.save(room);
        registerTaxiInfoService.savePath(room.getId(), path);
        return RoomDetailResponse.toDto(room, path);
    }

    public String toCoordinate(float longitude, float latitude) {
        return String.format("%.6f, %.6f", longitude, latitude);
    }

    private static List<Point> jsonNodeToPointList(JsonNode pathNode){
        List<Point> path = new ArrayList<>();
        for (JsonNode pointNode : pathNode) {
            float latitude = (float) pointNode.get(1).asDouble();
            float longitude = (float) pointNode.get(0).asDouble();
            Point point = new Point(latitude, longitude);
            path.add(point);
        }
        return path;
    }
}