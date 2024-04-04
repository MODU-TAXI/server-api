package com.modutaxi.api.domain.member.mapper;

import com.modutaxi.api.domain.member.entity.Gender;
import com.modutaxi.api.domain.member.entity.Member;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {

    public Member toEntity(String snsId, String name, Gender gender, String phoneNumber) {
        return Member.builder()
                .snsId(snsId)
                .name(name)
                .gender(gender)
                .phoneNumber(phoneNumber)
                .build();
    }

}
