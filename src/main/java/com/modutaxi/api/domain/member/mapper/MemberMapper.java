package com.modutaxi.api.domain.member.mapper;

import com.modutaxi.api.domain.member.entity.Member;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {

    public Member ToEntity(Long snsId, String name) {
        return Member.builder()
                .snsId(snsId)
                .name(name)
                .build();
    }

}
