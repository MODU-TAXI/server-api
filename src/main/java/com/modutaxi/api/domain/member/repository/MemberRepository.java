package com.modutaxi.api.domain.member.repository;

import com.modutaxi.api.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByIdAndStatusTrue(@Param("memberId") Long memberId);
    Optional<Member> findBySnsId(String snsId);
    Boolean existsBySnsId(String snsId);
}
