package com.modutaxi.api.domain.history.repository;

import com.modutaxi.api.domain.history.entity.History;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.room.entity.Room;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.Tuple;
import java.lang.reflect.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import org.springframework.security.core.parameters.P;

public interface HistoryRepository extends JpaRepository<History, Long> {
    @Query("SELECT h FROM History h WHERE h.member.id = :memberId " +
        "AND YEAR(h.room.departureTime) = :year AND MONTH(h.room.departureTime) = :month")
    List<History> findByMemberIdAndDepartureDate(@Param("memberId") Long memberId, @Param("year") int year, @Param("month") int month);

    @Query("SELECT SUM(h.totalCharge) as accumulateTotalCharge, SUM(h.portionCharge) as accumulatePortionCharge " +
        "FROM History h WHERE h.member.id = :memberId " +
        "AND YEAR(h.room.departureTime) = :year AND MONTH(h.room.departureTime) = :month")
    Tuple findTotalChargeAndPortionChargeByMemberIdAndDepartureDate(@Param("memberId") Long memberId, @Param("year") int year, @Param("month") int month);

    List<History> findAllByMemberOrderByRoomDepartureTimeDesc(Member member);

    void deleteAllByRoom(Room room);

    @Query("SELECT MIN(r.departureTime) as startDate, max (r.departureTime) as endDate " +
        "FROM History h JOIN Room r on h.room.id = r.id "
        +"WHERE h.member.id = :memberId ")
    Tuple findStartDateAndEndDateByMemberId(Long memberId);
}