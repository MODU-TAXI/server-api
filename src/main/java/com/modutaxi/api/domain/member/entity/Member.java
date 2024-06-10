package com.modutaxi.api.domain.member.entity;

import static com.modutaxi.api.common.constants.ServerConstants.BASIC_PROFILE_IMAGE_URL;

import com.modutaxi.api.common.entity.BaseTime;
import com.modutaxi.api.domain.account.entity.Account;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    private String nickname;

    private String imageUrl;

    //TODO: 도착 확정 API에서 매칭 횟수와 노쇼 횟수 카운팅
    @NotNull
    @Builder.Default
    private int matchingCount = 0;  // 매칭 횟수
    @NotNull
    @Builder.Default
    private int noShowCount = 0;    // 노쇼 횟수

    @NotNull
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.ROLE_VISITOR;

    @OneToMany(mappedBy = "member", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<Account> accounts;

    @NotNull
    @Builder.Default
    private int reportCount = 0;
    @NotNull
    @Builder.Default
    private boolean blocked = false;
    @NotNull
    @Builder.Default
    private boolean status = true;

    public void certificateEmail(String email) {
        this.role = Role.ROLE_MEMBER;
        this.email = email;
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void addAccount(Account account) {
        this.accounts.add(account);
    }

    public void updateProfile(String name, Gender gender, String phoneNumber, String imageUrl) {
        this.name = name;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return Objects.requireNonNullElse(this.imageUrl, BASIC_PROFILE_IMAGE_URL);
    }

    public boolean isCertified() {
        return this.role == Role.ROLE_MEMBER;
    }

    public boolean existsImageUrl() {
        return this.imageUrl != null;
    }

    public void plusOneReportCount() {
        this.reportCount++;
    }

    public void setBlockedTrue() {
        this.blocked = true;
    }

}
