package com.modutaxi.api.domain.member.service;

import static com.modutaxi.api.common.constants.ServerConstants.BASIC_PROFILE_IMAGE_URL;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.MemberErrorCode;
import com.modutaxi.api.domain.member.dto.MemberResponseDto.MemberProfileResponse;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.member.mapper.MemberMapper;
import com.modutaxi.api.domain.member.repository.MemberRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetMemberService {
    private final MemberRepository memberRepository;

    public List<Member> getMemberList(List<Long> memberIdList) {
        return memberRepository.findByIdIsInMemberIdListAndStatusTrue(memberIdList);
    }

    public MemberProfileResponse getMemberProfile(Long id) {
        Member member = memberRepository.findByIdAndStatusTrue(id).orElse(null);
        if (member == null) {
            return new MemberProfileResponse(0L, "(알수없음)", 0, false, BASIC_PROFILE_IMAGE_URL);
        } else {
            return MemberMapper.toDto2(member);
        }
    }

    public Member getMemberByAppleSnsId(String snsId) {
        return memberRepository.findByAppleSnsIdAndStatusTrue(snsId)
            .orElseThrow(() -> new BaseException(MemberErrorCode.EMPTY_MEMBER));
    }

    public Integer countTodaySignups() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        return memberRepository.countByCreatedAtBetween(startOfDay, endOfDay);
    }

}
