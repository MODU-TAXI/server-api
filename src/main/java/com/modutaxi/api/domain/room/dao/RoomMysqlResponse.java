package com.modutaxi.api.domain.room.dao;

import org.locationtech.jts.geom.Point;

public class RoomMysqlResponse {
    public interface SearchWithRadiusResponseInterface {
        Long getId();

        Point getDeparturePoint();

        String getSpotName();
    }
}
