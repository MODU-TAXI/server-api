package com.modutaxi.api.domain.room.service;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.RoomErrorCode;
import com.modutaxi.api.common.exception.errorcode.TaxiInfoErrorCode;
import com.modutaxi.api.common.pagination.PageResponseDto;
import com.modutaxi.api.domain.room.dto.RoomResponseDto.RoomDetailResponse;
import com.modutaxi.api.domain.room.dto.RoomResponseDto.RoomSimpleResponse;
import com.modutaxi.api.domain.room.entity.Room;
import com.modutaxi.api.domain.room.repository.RoomRepository;
import com.modutaxi.api.domain.taxiinfo.repository.TaxiInfoMongoRepository;
import com.mongodb.client.model.geojson.LineString;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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

    public PageResponseDto<List<RoomSimpleResponse>> getRoomSimpleList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Slice<Room> roomSlice = roomRepository.findAllByOrderByCreatedAtDesc(pageable);

        List<RoomSimpleResponse> roomSimpleResponseList = roomSlice.stream()
            .map(RoomSimpleResponse::toDto)
            .collect(Collectors.toList());

        return new PageResponseDto<>(pageable.getPageNumber(), roomSlice.hasNext(),
            roomSimpleResponseList);
    }
}
