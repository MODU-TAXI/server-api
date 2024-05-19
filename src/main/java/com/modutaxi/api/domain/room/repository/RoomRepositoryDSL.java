package com.modutaxi.api.domain.room.repository;

import com.modutaxi.api.domain.room.dao.RoomMysqlResponse.SearchListResponse;
import com.modutaxi.api.domain.room.dao.RoomMysqlResponse.SearchMapResponse;
import com.modutaxi.api.domain.room.entity.RoomSortType;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.List;

public interface RoomRepositoryDSL {
    Slice<SearchListResponse> findNearRoomsList(Long spotId, Integer tagBitMask, Boolean isImminent, Point point, Long radius, LocalDateTime timeAfter, LocalDateTime timeBefore, Pageable pageable, RoomSortType sortType);

    List<SearchMapResponse> findNearRoomsMap(Long spotId, Integer tagBitMask, Boolean isImminent, Point point, Long radius, LocalDateTime timeAfter, LocalDateTime timeBefore);
}
