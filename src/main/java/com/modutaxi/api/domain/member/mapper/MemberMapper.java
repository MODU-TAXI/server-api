package com.modutaxi.api.domain.member.mapper;

import com.modutaxi.api.domain.member.dto.MemberResponseDto.MemberInfoResponse;
import com.modutaxi.api.domain.member.dto.MemberResponseDto.MemberProfileResponse;
import com.modutaxi.api.domain.member.entity.Gender;
import com.modutaxi.api.domain.member.entity.Member;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {

    public static Member toEntity(String snsId, String name, Gender gender, String phoneNumber) {
        return Member.builder()
            .snsId(snsId)
            .name(name)
            .gender(gender)
            .phoneNumber(phoneNumber)
            .build();
    }

    public static MemberInfoResponse toDto(Member member) {
        return MemberInfoResponse.builder()
            .id(member.getId())
            .name(member.getName())
            .nickname(member.getNickname())
            .gender(member.getGender())
            .phoneNumber(member.getPhoneNumber())
            .email(member.getEmail())
            .imageUrl(member.getImageUrl())
            .build();
    }

    public static MemberProfileResponse toDto2(Member member) {
        return MemberProfileResponse.builder()
            .id(member.getId())
            .nickname(member.getNickname())
            .isCertified(member.isCertified())
            .imageUrl(member.getImageUrl())
            .build();
    }

}
