package com.modutaxi.api.domain.member.repository;

import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.member.entity.Role;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByIdAndStatusTrue(@Param("memberId") Long memberId);

    Optional<Member> findBySnsIdAndStatusTrue(String snsId);

    Boolean existsBySnsId(String snsId);

    @Query("SELECT m " +
        "FROM Member m " +
        "WHERE " +
        "(m.id = :userId AND m.email IS NOT NULL) " +
        "OR " +
        "(m.id = :userId AND m.role <> :role) " +
        "OR " +
        "(m.email = :email)")
    Optional<Member> findCertificatedMember(@Param("userId") Long userId,
        @Param("email") String email, @Param("role") Role role);

    @Query("SELECT m " +
        "FROM Member m " +
        "WHERE m.id IN :memberIdList " +
        "AND m.status = true")
    List<Member> findByIdIsInMemberIdListAndStatusTrue(List<Long> memberIdList);

    Boolean existsByNickname(String nickname);
}
