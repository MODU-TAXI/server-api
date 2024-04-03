package com.modutaxi.api.domain.room.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.modutaxi.api.common.converter.Converter;
import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.RoomErrorCode;
import com.modutaxi.api.common.exception.errorcode.TaxiInfoErrorCode;
import com.modutaxi.api.domain.destination.repository.DestinationRepository;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.room.dto.RoomInternalDto.InternalUpdateRoomDto;
import com.modutaxi.api.domain.room.dto.RoomRequestDto.UpdateRoomRequest;
import com.modutaxi.api.domain.room.dto.RoomResponseDto.RoomDetailResponse;
import com.modutaxi.api.domain.room.entity.Room;
import com.modutaxi.api.domain.room.repository.RoomRepository;
import com.modutaxi.api.domain.taxiinfo.entity.Point;
import com.modutaxi.api.domain.taxiinfo.entity.TaxiInfo;
import com.modutaxi.api.domain.taxiinfo.repository.TaxiInfoMongoRepository;
import com.modutaxi.api.domain.taxiinfo.service.GetTaxiInfoService;
import jakarta.transaction.Transactional;
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

        InternalUpdateRoomDto oldRoomData = InternalUpdateRoomDto.toDto(room);

        InternalUpdateRoomDto newRoomData = validateAndReturnNewDto(oldRoomData, updateRoomRequest);

        //TaxiInfo db 업데이트
        if (shouldUpdateTaxiInfo(updateRoomRequest)) {
            updateTaxiInfo(roomId, newRoomData);
        }

        room.update(newRoomData);

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

    public InternalUpdateRoomDto validateAndReturnNewDto(InternalUpdateRoomDto oldRoomData,
        UpdateRoomRequest updateRoomRequest) {

        if (updateRoomRequest.getDescription() != null) {
            oldRoomData.setDescription(updateRoomRequest.getDescription());
        }
        if (updateRoomRequest.getRoomTagBitMask() != 0) {
            oldRoomData.setRoomTagBitMask(updateRoomRequest.getRoomTagBitMask());
        }
        if (updateRoomRequest.getDepartTime() != null) {
            oldRoomData.setDepartTime(updateRoomRequest.getDepartTime());
        }
        if (updateRoomRequest.getWishHeadcount() != 0) {
            oldRoomData.setWishHeadcount(updateRoomRequest.getWishHeadcount());
        }
        // TODO: 2024/04/03 망고스틴의 거점에러 추가 시 삽입
        if (updateRoomRequest.getDestinationId() != null) {
            oldRoomData.setDestination(
                destinationRepository.findById(updateRoomRequest.getDestinationId())
                    .orElseThrow());
        }
        if (updateRoomRequest.getStartLongitude() != 0) {
            oldRoomData.setStartLongitude(updateRoomRequest.getStartLongitude());
        }
        if (updateRoomRequest.getStartLatitude() != 0) {
            oldRoomData.setStartLatitude(updateRoomRequest.getStartLatitude());
        }

        return oldRoomData;
    }

    @Transactional
    public void updateTaxiInfo(Long roomId, InternalUpdateRoomDto internalUpdateRoomDto) {
        String startCoordinate =
            Converter.coordinateToString(internalUpdateRoomDto.getStartLongitude(),
                internalUpdateRoomDto.getStartLatitude());

        String goalCoordinate =
            Converter.coordinateToString(internalUpdateRoomDto.getDestination().getLongitude(),
                internalUpdateRoomDto.getDestination().getLatitude());

        JsonNode jsonNode =
            getTaxiInfoService.getDrivingInfo(startCoordinate, goalCoordinate);

        List<Point> path = Converter.jsonNodeToPointList(jsonNode.get("path"));

        internalUpdateRoomDto.setExpectedCharge(jsonNode.get("taxiFare").asInt());
        internalUpdateRoomDto.setDuration(jsonNode.get("duration").asLong());

        taxiInfoMongoRepository.save(TaxiInfo.toEntity(roomId, path));
    }

    public boolean shouldUpdateTaxiInfo(UpdateRoomRequest updateRoomRequest) {
        return updateRoomRequest.getDestinationId() != null
            || updateRoomRequest.getStartLongitude() != 0
            || updateRoomRequest.getStartLatitude() != 0;
    }

    void checkManager(Long managerId, Long memberId) {
        if (!managerId.equals(memberId)) {
            throw new BaseException(RoomErrorCode.NOT_ROOM_MANAGER);
        }
    }
}
