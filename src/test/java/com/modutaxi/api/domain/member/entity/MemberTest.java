package com.modutaxi.api.domain.member.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemberTest {

    @Test
    @DisplayName("빌더로 Member 엔티티가 만들어지는지 테스트")
    void builder() {
        // given
        Member member = Member.builder()
                .snsId(123456L)
                .name("지수")
                .gender(Gender.FEMALE)
                .build();

        // when, then
        assertEquals(member.getSnsId(), 123456L);
        assertEquals(member.getName(), "지수");
        assertEquals(member.getGender(), Gender.FEMALE);
    }

    @Test
    @DisplayName("refreshToken이 바뀌는지 테스트")
    void changeRefreshToken() {
        // given
        Member member = Member.builder()
                .snsId(123456L)
                .name("지수")
                .gender(Gender.FEMALE)
                .build();

        // when
        member.changeRefreshToken("new token");

        // then
        assertEquals(member.getRefreshToken(), "new token");
    }
}