package com.modutaxi.api.domain.room.entity;

import com.modutaxi.api.common.entity.BaseTime;
import com.modutaxi.api.domain.spot.entity.Spot;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.room.dto.RoomInternalDto.InternalUpdateRoomDto;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
public class Room extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "spot_id")
    private Spot spot;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member roomManager;

    @NotNull
    @Builder.Default
    private int expectedCharge = 100000;

    @NotNull
    @Builder.Default
    private long durationMinutes = 3600000;

    @NotNull
    @Builder.Default
    private RoomStatus roomStatus = RoomStatus.PROCEEDING;

    @Nullable
    private int roomTagBitMask;

    @NotNull
    @Builder.Default
    private Point departurePoint = null;

    @NotNull
    private String departureName;

    @NotNull
    @Builder.Default
    private LocalDateTime departureTime = LocalDateTime.now();

    @NotNull
    @Builder.Default
    private int currentHeadcount = 1;

    @NotNull
    @Builder.Default
    private int wishHeadcount = 3;


    public void update(InternalUpdateRoomDto updateRoomDto) {
        this.spot = updateRoomDto.getSpot();
        this.roomTagBitMask = updateRoomDto.getRoomTagBitMask();
        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate coordinate
                = new Coordinate(updateRoomDto.getDepartureLongitude(), updateRoomDto.getDepartureLatitude());
        this.departurePoint = geometryFactory.createPoint(coordinate);
        this.departureName = updateRoomDto.getDepartureName();
        this.departureTime = updateRoomDto.getDepartureTime();
        this.wishHeadcount = updateRoomDto.getWishHeadcount();
        this.expectedCharge = updateRoomDto.getExpectedCharge();
        this.durationMinutes = updateRoomDto.getDurationMinutes();
    }

    public void plusCurrentHeadCount() {
        this.currentHeadcount++;
    }

    public void minusCurrentHeadCount() {
        this.currentHeadcount++;
    }
}
