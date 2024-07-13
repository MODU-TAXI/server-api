package com.modutaxi.api.domain.room.dao;

import lombok.Getter;
import lombok.ToString;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;

public class RoomMysqlResponse {
    @Getter
    @ToString
    public static class SearchIntegrationResponse {
        Long id;
        Long spotId;
        LocalDateTime departureTime;
        Point spotPoint;
        String spotName;
        int roomTagBitMask;
        Point departurePoint;
        String departureName;
        int currentHeadcount;
        int wishHeadcount;
        long durationMinutes;
        int expectedCharge;
    }

    @Getter
    @ToString
    public static class SearchMapResponse {
        Long id;
        Point departurePoint;
        String spotName;
    }

    @Getter
    @ToString
    public static class SearchListResponse {
        Long id;
        Long spotId;
        LocalDateTime departureTime;
        String spotName;
        int roomTagBitMask;
        String departureName;
        int currentHeadcount;
        int wishHeadcount;
        long durationMinutes;
        int expectedCharge;
    }
}
