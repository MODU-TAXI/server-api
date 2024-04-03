package com.modutaxi.api.domain.room.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.modutaxi.api.common.converter.Converter;
import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.RoomErrorCode;
import com.modutaxi.api.common.exception.errorcode.TaxiInfoErrorCode;
import com.modutaxi.api.domain.destination.entity.Destination;
import com.modutaxi.api.domain.destination.repository.DestinationRepository;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.room.dto.RoomRequestDto.UpdateRoomRequest;
import com.modutaxi.api.domain.room.dto.RoomResponseDto.RoomDetailResponse;
import com.modutaxi.api.domain.room.entity.Room;
import com.modutaxi.api.domain.room.repository.RoomRepository;
import com.modutaxi.api.domain.taxiinfo.entity.Point;
import com.modutaxi.api.domain.taxiinfo.entity.TaxiInfo;
import com.modutaxi.api.domain.taxiinfo.repository.TaxiInfoMongoRepository;
import com.modutaxi.api.domain.taxiinfo.service.GetTaxiInfoService;
import jakarta.transaction.Transactional;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UpdateRoomService {

    private final RoomRepository roomRepository;
    private final TaxiInfoMongoRepository taxiInfoMongoRepository;
    private final DestinationRepository destinationRepository;

    private final GetTaxiInfoService getTaxiInfoService;

    @Transactional
    public RoomDetailResponse updateRoom(Member member, Long roomId,
        UpdateRoomRequest updateRoomRequest) {

        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new BaseException(RoomErrorCode.EMPTY_ROOM));

        TaxiInfo taxiInfo = taxiInfoMongoRepository.findById(roomId)
            .orElseThrow(() -> new BaseException(TaxiInfoErrorCode.EMPTY_TAXI_INFO));

        checkManager(room.getRoomManager().getId(), member.getId());

        Destination destination = room.getDestination();
        String description = room.getDescription();
        int roomTagBitMask = room.getRoomTagBitMask();
        float startLongitude = room.getStartLongitude();
        float startLatitude = room.getStartLatitude();
        LocalTime departTime = room.getDepartTime();
        int wishHeadcount = room.getWishHeadcount();
        int expectedCharge = room.getExpectedCharge();
        long duration = room.getDuration();

        // 단순 수정
        if (updateRoomRequest.getDescription() != null) {
            description = updateRoomRequest.getDescription();
        }
        if (updateRoomRequest.getRoomTagBitMask() != 0) {
            roomTagBitMask = updateRoomRequest.getRoomTagBitMask();
        }
        if (updateRoomRequest.getDepartTime() != null) {
            departTime = room.getDepartTime();
        }
        if (updateRoomRequest.getWishHeadcount() != 0) {
            wishHeadcount = updateRoomRequest.getWishHeadcount();
        }

        // TODO: 2024/04/03 망고스틴의 거점에러 추가 시 삽입
        //경로를 바꿔줘야 하는 경우
        if (updateRoomRequest.getDestinationId() != null) {
            destination = destinationRepository.findById(updateRoomRequest.getDestinationId())
                .orElseThrow();
        }

        if (updateRoomRequest.getStartLongitude() != 0) {
            startLongitude = updateRoomRequest.getStartLongitude();
        }
        if (updateRoomRequest.getStartLatitude() != 0) {
            startLatitude = updateRoomRequest.getStartLatitude();
        }

        //db 업데이트
        if (updateRoomRequest.getDestinationId() != null
            || updateRoomRequest.getStartLongitude() != 0
            || updateRoomRequest.getStartLatitude() != 0) {
            String startCoordinate =
                Converter.coordinateToString(startLongitude, startLatitude);
            String goalCoordinate =
                Converter.coordinateToString(destination.getLongitude(), destination.getLatitude());
            JsonNode jsonNode =
                getTaxiInfoService.getDrivingInfo(startCoordinate, goalCoordinate);

            List<Point> path = Converter.jsonNodeToPointList(jsonNode.get("path"));

            expectedCharge = jsonNode.get("taxiFare").asInt();
            duration = jsonNode.get("duration").asLong();

            taxiInfoMongoRepository.save(TaxiInfo.toEntity(roomId, path));
        }

        room.update(destination, description, roomTagBitMask, startLongitude, startLatitude,
            departTime, wishHeadcount, expectedCharge, duration);

        return RoomDetailResponse.toDto(room, taxiInfo.getPath());
    }

    @Transactional
    public void deleteRoom(Member member, Long roomId) {
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new BaseException(RoomErrorCode.EMPTY_ROOM));
        TaxiInfo taxiInfo = taxiInfoMongoRepository.findById(roomId)
            .orElseThrow(() -> new BaseException(TaxiInfoErrorCode.EMPTY_TAXI_INFO));

        checkManager(room.getRoomManager().getId(), member.getId());

        roomRepository.delete(room);
        taxiInfoMongoRepository.delete(taxiInfo);
    }

    void checkManager(Long managerId, Long memberId) {
        if (!managerId.equals(memberId)) {
            throw new BaseException(RoomErrorCode.NOT_ROOM_MANAGER);
        }
    }
}
