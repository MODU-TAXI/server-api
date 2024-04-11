package com.modutaxi.api.domain.spot.entity;

import com.modutaxi.api.common.entity.BaseTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.*;
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
}
