package com.modutaxi.api.domain.room.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.modutaxi.api.common.converter.NaverMapConverter;
import com.modutaxi.api.common.converter.RoomTagBitMaskConverter;
import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.RoomErrorCode;
import com.modutaxi.api.common.exception.errorcode.TaxiInfoErrorCode;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.room.dto.RoomInternalDto.InternalUpdateRoomDto;
import com.modutaxi.api.domain.room.dto.RoomRequestDto.UpdateRoomRequest;
import com.modutaxi.api.domain.room.dto.RoomResponseDto.RoomDetailResponse;
import com.modutaxi.api.domain.room.entity.Room;
import com.modutaxi.api.domain.room.repository.RoomRepository;
import com.modutaxi.api.domain.spot.repository.SpotRepository;
import com.modutaxi.api.domain.taxiinfo.entity.TaxiInfo;
import com.modutaxi.api.domain.taxiinfo.repository.TaxiInfoMongoRepository;
import com.modutaxi.api.domain.taxiinfo.service.GetTaxiInfoService;
import com.mongodb.client.model.geojson.LineString;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UpdateRoomService {

    private final RoomRepository roomRepository;
    private final TaxiInfoMongoRepository taxiInfoMongoRepository;
    private final SpotRepository spotRepository;

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
        // TODO: 2024/04/05 JPA와 달리 모든 과정이 끝난 후 db에 반영되는 이슈가 있기에 임시 변수 추가
        LineString path = taxiInfo.getPath();
        if (shouldUpdateTaxiInfo(updateRoomRequest)) {
            path = updateTaxiInfo(roomId, newRoomData);
        }

        room.update(newRoomData);

        return RoomDetailResponse.toDto(room, path);
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

        oldRoomData.setRoomTagBitMask(
            RoomTagBitMaskConverter.convertRoomTagListToBitMask(
                updateRoomRequest.getRoomTagBitMask()) != 0
                ? RoomTagBitMaskConverter.convertRoomTagListToBitMask(
                updateRoomRequest.getRoomTagBitMask())
                : oldRoomData.getRoomTagBitMask()
        );

        oldRoomData.setDepartureTime(
            updateRoomRequest.getDepartureTime() != null ? updateRoomRequest.getDepartureTime()
                : oldRoomData.getDepartureTime());

        oldRoomData.setWishHeadcount(
            updateRoomRequest.getWishHeadcount() != 0 ? updateRoomRequest.getWishHeadcount()
                : oldRoomData.getWishHeadcount());

        oldRoomData.setSpot(
            updateRoomRequest.getSpotId() != null ? spotRepository.findById(
                updateRoomRequest.getSpotId()).orElseThrow() : oldRoomData.getSpot());

        oldRoomData.setDeparturePoint(
            updateRoomRequest.getDeparturePoint() != null ? updateRoomRequest.getDeparturePoint()
                : oldRoomData.getDeparturePoint());

        return oldRoomData;
    }

    @Transactional
    public LineString updateTaxiInfo(Long roomId, InternalUpdateRoomDto internalUpdateRoomDto) {
        String startCoordinate =
            NaverMapConverter.coordinateToString(internalUpdateRoomDto.getDeparturePoint().getX(),
                internalUpdateRoomDto.getDeparturePoint().getY());

        String goalCoordinate =
            NaverMapConverter.coordinateToString(
                internalUpdateRoomDto.getSpot().getSpotPoint().getX(),
                internalUpdateRoomDto.getSpot().getSpotPoint().getY());

        JsonNode jsonNode =
            getTaxiInfoService.getDrivingInfo(startCoordinate, goalCoordinate);

        LineString path = NaverMapConverter.jsonNodeToLineString(jsonNode.get("path"));

        internalUpdateRoomDto.setExpectedCharge(jsonNode.get("taxiFare").asInt());
        internalUpdateRoomDto.setDuration(jsonNode.get("duration").asLong());

        taxiInfoMongoRepository.save(TaxiInfo.toEntity(roomId, path));
        return path;
    }

    public boolean shouldUpdateTaxiInfo(UpdateRoomRequest updateRoomRequest) {
        return updateRoomRequest.getSpotId() != null
                || updateRoomRequest.getDeparturePoint() != null;
    }

    void checkManager(Long managerId, Long memberId) {
        if (!managerId.equals(memberId)) {
            throw new BaseException(RoomErrorCode.NOT_ROOM_MANAGER);
        }
    }
}
