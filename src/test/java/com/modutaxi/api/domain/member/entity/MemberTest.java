package com.modutaxi.api.domain.member.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.modutaxi.api.domain.member.mapper.MemberMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MemberTest {

    @Test
    @DisplayName("빌더로 Member 엔티티가 만들어지는지 테스트")
    void builder() {
        // given
        Member member = MemberMapper.toEntity("123456", "지수", Gender.FEMALE, "010-7713-1554");
        String ProfileImgUrl = "https://modutaxi-production-bucket.s3.ap-northeast-2.amazonaws.com/fe87a02f-169c-48cb-859b-3ff04166757e_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202024-05-28%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%202.45.12.png";
        // when

        // then
        assertEquals(member.getImageUrl(), ProfileImgUrl);
    }

}