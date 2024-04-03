package com.modutaxi.api.domain.room.entity;

import com.modutaxi.api.common.entity.BaseTime;
import com.modutaxi.api.domain.destination.entity.Destination;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.room.dto.RoomInternalDto.InternalUpdateRoomDto;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
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
    private long duration = 3600000;

    @NotNull
    @Builder.Default
    private String roomName = "기본 방제";

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
    private LocalDateTime departTime = LocalDateTime.now();

    @NotNull
    @Builder.Default
    private int wishHeadcount = 3;


    public void update(InternalUpdateRoomDto updateRoomDto) {
        this.destination = updateRoomDto.getDestination();
        this.description = updateRoomDto.getDescription();
        this.roomTagBitMask = updateRoomDto.getRoomTagBitMask();
        this.startLongitude = updateRoomDto.getStartLongitude();
        this.startLatitude = updateRoomDto.getStartLatitude();
        this.departTime = updateRoomDto.getDepartTime();
        this.wishHeadcount = updateRoomDto.getWishHeadcount();
        this.expectedCharge = updateRoomDto.getExpectedCharge();
        this.duration = updateRoomDto.getDuration();
    }
}
