package com.modutaxi.api.domain.room.service;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.RoomErrorCode;
import com.modutaxi.api.common.exception.errorcode.TaxiInfoErrorCode;
import com.modutaxi.api.domain.room.dto.RoomResponseDto.RoomDetailResponse;
import com.modutaxi.api.domain.room.entity.Room;
import com.modutaxi.api.domain.room.repository.RoomRepository;
import com.modutaxi.api.domain.taxiinfo.repository.TaxiInfoMongoRepository;
import com.mongodb.client.model.geojson.LineString;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetRoomService {

    private final RoomRepository roomRepository;
    private final TaxiInfoMongoRepository taxiInfoMongoRepository;

    public RoomDetailResponse getRoomDetail(Long roomId) {
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new BaseException(RoomErrorCode.EMPTY_ROOM));

        LineString path = taxiInfoMongoRepository.findById(roomId)
            .orElseThrow(() -> new BaseException(
                TaxiInfoErrorCode.EMPTY_TAXI_INFO)).getPath();

        return RoomDetailResponse.toDto(room, path);
    }
}
