package com.modutaxi.api.domain.member.entity;

import com.modutaxi.api.common.entity.BaseTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 2, max = 8)
    private String name;
    @NotNull
    private String snsId;
    @NotNull
    private Gender gender;
    @NotNull
    private String phoneNumber;

    @Email
    private String email;

    @NotNull
    @Builder.Default
    private double score = 0.0; // 평점

    @NotNull
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.ROLE_VISITOR;

    @NotNull
    @Builder.Default
    private int reportCount = 0;
    @NotNull
    @Builder.Default
    private boolean status = true;

    public void certificateEmail(String email) {
        this.role = Role.ROLE_MEMBER;
        this.email = email;
    }
}
