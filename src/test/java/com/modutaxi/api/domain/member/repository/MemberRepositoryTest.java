package com.modutaxi.api.domain.member.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.modutaxi.api.domain.member.entity.Gender;
import com.modutaxi.api.domain.member.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    Member member1, member2;

    @BeforeEach
    void setUp() {
        member1 = Member.builder()
            .snsId("123456")
            .name("지수")
            .gender(Gender.FEMALE)
            .build();
        member2 = Member.builder()
            .snsId("123457")
            .name("이름")
            .status(false)
            .gender(Gender.FEMALE)
            .build();
    }

    @Test
    @DisplayName("Member Id로 객체를 잘 반환하는지 테스트")
    void findByIdAndStatusTrue() {
        // given
        memberRepository.save(member1);
        memberRepository.save(member2);

        // when
        Member result1 = memberRepository.findByIdAndStatusTrue(member1.getId()).orElse(null);
        Member result2 = memberRepository.findByIdAndStatusTrue(member2.getId()).orElse(null);
        Member result3 = memberRepository.findByIdAndStatusTrue(17L).orElse(null);

        // then
        assertThat(result1.getId()).isEqualTo(member1.getId());
        assertThat(result2).isNull();
        assertThat(result3).isNull();
    }

    @Test
    @DisplayName("SnsId로 객체를 잘 반환하는지 테스트")
    void findBySnsId() {
        // given
        memberRepository.save(member1);

        // when
        Member result1 = memberRepository.findBySnsIdAndStatusTrue("123456").orElse(null);
        Member result2 = memberRepository.findBySnsIdAndStatusTrue("456789").orElse(null);

        // then
        assertThat(result1.getSnsId()).isEqualTo("123456");
        assertThatThrownBy(() ->
            result2.getSnsId()).isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("SnsId로 DB에 존재하는지 조회 테스트")
    void existsBySnsId() {
        // given
        memberRepository.save(member1);

        // when
        Boolean result1 = memberRepository.existsBySnsId("123456");
        Boolean result2 = memberRepository.existsBySnsId("111111");

        // then
        assertThat(result1).isTrue();
        assertThat(result2).isFalse();
    }

}