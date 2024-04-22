package com.modutaxi.api.domain.room.service;

import com.modutaxi.api.common.converter.RoomTagBitMaskConverter;
import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.RoomErrorCode;
import com.modutaxi.api.common.exception.errorcode.TaxiInfoErrorCode;
import com.modutaxi.api.common.pagination.PageResponseDto;
import com.modutaxi.api.domain.room.dao.RoomMysqlResponse.SearchWithRadiusResponseInterface;
import com.modutaxi.api.domain.room.dto.RoomResponseDto.RoomDetailResponse;
import com.modutaxi.api.domain.room.dto.RoomResponseDto.RoomSimpleResponse;
import com.modutaxi.api.domain.room.dto.RoomResponseDto.SearchWithRadiusResponse;
import com.modutaxi.api.domain.room.dto.RoomResponseDto.SearchWithRadiusResponses;
import com.modutaxi.api.domain.room.entity.Room;

import com.modutaxi.api.domain.room.mapper.RoomResponseMapper;
import com.modutaxi.api.domain.room.entity.RoomTagBitMask;
import com.modutaxi.api.domain.room.repository.RoomRepository;
import com.modutaxi.api.domain.spot.service.GetSpotService;
import com.modutaxi.api.domain.taxiinfo.repository.TaxiInfoMongoRepository;
import com.mongodb.client.model.geojson.LineString;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GetRoomService {
    @Value("${env.imminent-time.front}")
    private Long imminentTimeFront;
    @Value("${env.imminent-time.back}")
    private Long imminentTimeBack;

    private final RoomRepository roomRepository;
    private final TaxiInfoMongoRepository taxiInfoMongoRepository;
    private final GetSpotService getSpotService;

    public RoomDetailResponse getRoomDetail(Long roomId) {
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new BaseException(RoomErrorCode.EMPTY_ROOM));

        LineString path = taxiInfoMongoRepository.findById(roomId)
            .orElseThrow(() -> new BaseException(
                TaxiInfoErrorCode.EMPTY_TAXI_INFO)).getPath();

        return RoomDetailResponse.toDto(room, path);
    }

    public PageResponseDto<List<RoomSimpleResponse>> getRoomSimpleList(int page, int size, Long spotId, List<RoomTagBitMask> tags, Boolean isImminent) {
        Pageable pageable = PageRequest.of(page, size);
        Slice<Room> roomSlice = getRoomSlice(pageable, spotId, checkTags(tags), isImminent);
        List<RoomSimpleResponse> roomSimpleResponseList = roomSlice.stream()
            .map(RoomSimpleResponse::toDto)
            .collect(Collectors.toList());

        return new PageResponseDto<>(pageable.getPageNumber(), roomSlice.hasNext(),
            roomSimpleResponseList);
    }

    public SearchWithRadiusResponses getRadiusRooms(Point searchPoint, Long radius) {
        List<SearchWithRadiusResponseInterface> rooms = roomRepository.findNearRoomsInRadius(searchPoint, radius);
        List<SearchWithRadiusResponse> roomList = rooms.stream().map(RoomResponseMapper::toSearchWithRadiusResponse).toList();
        return new SearchWithRadiusResponses(roomList);
    }
  
    private Integer checkTags(List<RoomTagBitMask> tags) {
        Integer tagBitMask = RoomTagBitMaskConverter.convertRoomTagListToBitMask(tags);
        if ((RoomTagBitMask.ONLY_WOMAN.getValue() + RoomTagBitMask.ONLY_MAN.getValue() & tagBitMask) == RoomTagBitMask.ONLY_WOMAN.getValue() + RoomTagBitMask.ONLY_MAN.getValue())
            throw new BaseException(RoomErrorCode.BOTH_GENDER);
        return tagBitMask;
    }

    private Slice<Room> getRoomSlice(Pageable pageable, Long spotId, Integer tagBitMask, Boolean isImminent) {
        if (spotId != null)
            getSpotService.getSpot(spotId);
        LocalDateTime timeNow = LocalDateTime.now();
        return roomRepository.findAllWhereTagBitMaskOrderByCreatedAtDesc(spotId, tagBitMask, isImminent, timeNow.minusMinutes(imminentTimeFront), timeNow.plusMinutes(imminentTimeBack), pageable);
    }
}
