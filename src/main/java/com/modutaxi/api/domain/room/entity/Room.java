package com.modutaxi.api.domain.room.entity;

import com.modutaxi.api.common.entity.BaseTime;
import com.modutaxi.api.domain.destination.entity.Destination;
import com.modutaxi.api.domain.member.entity.Member;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
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
public class Room extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "destination_id")
    private Destination destination;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member roomManager;

    @NotNull
    @Builder.Default
    private int expectedCharge = 100000;

    @NotNull
    @Builder.Default
    private String name = "훈";

    @Nullable
    @Column(length = 200)
    private String description;

    @NotNull
    @Builder.Default
    private RoomStatus roomStatus = RoomStatus.PROCEEDING;

    @Nullable
    private int roomTagBitMask;

    @NotNull
    @Builder.Default
    private float startLongitude = 0.0F;

    @NotNull
    @Builder.Default
    private float startLatitude = 0.0F;

    @NotNull
    @Builder.Default
    private LocalTime departTime = LocalTime.of(0,0);

    @NotNull
    @Builder.Default
    private int wishHeadcount = 4;

    @NotNull
    @Builder.Default
    private int reportCount = 0;

    public static Room toEntity(
        Member member, Destination destination, int expectedCharge,
        String name, String description, RoomStatus roomStatus, int roomTagBitMask,
        float startLongitude, float startLatitude, LocalTime departTime
    ){
        return Room.builder()
            .destination(destination)
            .roomManager(member)
            .expectedCharge(expectedCharge)
            .name(name)
            .description(description)
            .roomStatus(roomStatus)
            .roomTagBitMask(roomTagBitMask)
            .startLatitude(startLatitude)
            .startLongitude(startLongitude)
            .departTime(departTime)
            .build();
    }

}
