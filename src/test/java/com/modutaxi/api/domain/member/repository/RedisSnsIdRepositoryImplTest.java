package com.modutaxi.api.domain.member.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import static org.assertj.core.api.Assertions.*;

@DisplayName("RedisSnsIdRepositoryImplTest 테스트")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RedisSnsIdRepositoryImplTest {

    @Autowired
    RedisSnsIdRepositoryImpl redisSnsIdRepository;

    @Test
    @DisplayName("key-value 저장 시 정상적으로 조회되는지 테스트")
    void save_and_findById() {
        // given
        String snsId = "123456";

        // when
        String key = redisSnsIdRepository.save(snsId);
        String result = redisSnsIdRepository.findById(key);

        // then
        assertThat(result).isEqualTo(snsId);
    }

}