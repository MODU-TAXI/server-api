package com.modutaxi.api.domain.spot.entity;

import com.modutaxi.api.common.entity.BaseTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
public class Spot extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Builder.Default
    private String name = "인하대학교";

    @NotNull
    @Builder.Default
    private String address = "인천광역시 미추홀구 인하로 100";

    @NotNull
    private Point spotPoint;

    public static Spot toEntity(String name, String address, Point spotPoint) {
        return Spot.builder()
                .name(name)
                .address(address)
                .spotPoint(spotPoint)
                .build();
    }

    public void updateSpotInfo(String name, String address, Point point) {
        this.name = name == null ? this.name : name;
        this.address = address == null ? this.address : address;
        if (point != null) {
            System.out.println(point.getX());
            System.out.println(point.getY());
            double newX = point.getX() == 0 ? this.spotPoint.getX() : point.getX();
            double newY = point.getY() == 0 ? this.spotPoint.getY() : point.getY();
            GeometryFactory geometryFactory = new GeometryFactory();
            Coordinate coordinate = new Coordinate(newX, newY);
            this.spotPoint = geometryFactory.createPoint(coordinate);
        }
    }
}