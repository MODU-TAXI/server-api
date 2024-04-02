package com.modutaxi.api.domain.room.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.RoomErrorCode;
import com.modutaxi.api.domain.room.dto.RoomResponseDto.RoomDetailResponse;
import com.modutaxi.api.domain.room.entity.Room;
import com.modutaxi.api.domain.room.repository.RoomRepository;
import com.modutaxi.api.domain.taxiinfo.entity.Point;
import com.modutaxi.api.domain.taxiinfo.repository.TaxiInfoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetRoomService {

    private final RoomRepository roomRepository;
    private final TaxiInfoRepository taxiInfoRepository;

    public RoomDetailResponse getRoomDetail(Long roomId) {
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new BaseException(RoomErrorCode.EMPTY_ROOM));

        List<Point> path = taxiInfoRepository.findById(roomId).orElseThrow().getPath();

        return RoomDetailResponse.toDto(room, path);
    }
}
