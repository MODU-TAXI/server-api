package com.modutaxi.api.domain.destination.entity;

import com.modutaxi.api.common.entity.BaseTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
public class Destination extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Builder.Default
    private String name = "청와대";

    @NotNull
    @Builder.Default
    private String address = "청와대로";

    @NotNull
    @Builder.Default
    private float longitude = 0.0F;

    @NotNull
    @Builder.Default
    private float latitude = 0.0F;

    public static Destination toEntity(String name, String address, float longitude, float latitude){
        return Destination.builder()
            .name(name)
            .address(address)
            .longitude(longitude)
            .latitude(latitude)
            .build();
    }
}
