package com.modutaxi.api.domain.spot.dao;

import org.locationtech.jts.geom.Point;

public class SpotMysqlResponse {
    public interface SpotWithDistanceResponseInterface {
        Long getId();

        String getName();

        String getAddress();

        Point getSpotpoint();

        Double getDistance();

        Boolean getLiked();
    }

    public interface SearchWithRadiusResponseInterface {
        Long getId();

        Point getSpotPoint();
    }
}
