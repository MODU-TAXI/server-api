package com.modutaxi.api.domain.member.service;

import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetMemberService {
    private final MemberRepository memberRepository;

    public List<Member> getMemberList(List<Long> memberIdList) {
        return memberRepository.findByIdIsInMemberIdListAndStatusTrue(memberIdList);
    }
}
