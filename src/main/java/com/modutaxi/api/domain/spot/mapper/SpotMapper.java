package com.modutaxi.api.domain.spot.mapper;

import com.modutaxi.api.domain.spot.entity.Spot;
import org.locationtech.jts.geom.Point;

public class SpotMapper {
    public static Spot toEntity(String name, String address, Point spotPoint) {
        return Spot.builder()
                .address(address)
                .name(name)
                .spotPoint(spotPoint)
                .build();
    }
}
