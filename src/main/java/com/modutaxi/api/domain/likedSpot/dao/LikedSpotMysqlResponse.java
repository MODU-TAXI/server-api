package com.modutaxi.api.domain.likedSpot.dao;

import org.locationtech.jts.geom.Point;

public class LikedSpotMysqlResponse {
    public interface LikedSpotResponseInterface {
        Long getSpotId();

        String getSpotName();

        Point getSpotpoint();
    }
}
