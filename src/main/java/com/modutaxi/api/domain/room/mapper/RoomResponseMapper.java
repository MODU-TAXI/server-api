package com.modutaxi.api.domain.room.mapper;

import com.modutaxi.api.domain.room.dao.RoomMysqlResponse;
import com.modutaxi.api.domain.room.dto.RoomResponseDto.SearchWithRadiusResponse;
import org.springframework.data.geo.Point;

public class RoomResponseMapper {
    public static SearchWithRadiusResponse toSearchWithRadiusResponse(RoomMysqlResponse.SearchWithRadiusResponseInterface room) {
        return new SearchWithRadiusResponse(room.getId(), new Point(room.getDeparturePoint().getX(), room.getDeparturePoint().getY()), room.getSpotName());
    }
}
