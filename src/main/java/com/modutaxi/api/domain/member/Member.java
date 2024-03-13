package com.modutaxi.api.domain.member;

import com.modutaxi.api.common.entity.BaseTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
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
    @Email
    private String email;
    @NotNull
    @ColumnDefault("")
    private String refreshToken;
    @NotNull
    @ColumnDefault("0")
    private double score; // 평점

    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;

    @NotNull
    @ColumnDefault("true")
    private boolean status;

    public Member(String name, String email) {
        this.name = name;
        this.email = email;
    }

}
