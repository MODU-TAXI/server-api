package com.modutaxi.api.domain.room.repository;

import com.modutaxi.api.domain.room.dao.RoomMysqlResponse.SearchListResponse;
import com.modutaxi.api.domain.room.dao.RoomMysqlResponse.SearchMapResponse;
import com.modutaxi.api.domain.room.entity.QRoom;
import com.modutaxi.api.domain.room.entity.RoomSortType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.modutaxi.api.domain.room.entity.RoomStatus.PROCEEDING;

@Repository
@RequiredArgsConstructor
public class RoomRepositoryDSLImpl implements RoomRepositoryDSL {
    private final JPAQueryFactory queryFactory;
    private final QRoom room = QRoom.room;

    @Override
    public Slice<SearchListResponse> findNearRoomsList(Long spotId, Integer tagBitMask, Boolean isImminent, Point point, Long radius, LocalDateTime timeAfter, LocalDateTime timeBefore, Pageable pageable, RoomSortType sortType) {
        return listToSlice(
            queryFactory
                .select(createSearchExpression(SearchListResponse.class))
                .from(room)
                .where(createSearchRoomPredicate(spotId, tagBitMask, isImminent, point, radius, timeAfter, timeBefore))
                .orderBy(createListOrderSpecifiers(point, sortType))
                .limit(pageable.getPageSize() + 1)
                .offset(pageable.getOffset())
                .fetch()
            , pageable);
    }

    @Override
    public List<SearchMapResponse> findNearRoomsMap(Long spotId, Integer tagBitMask, Boolean isImminent, Point point, Long radius, LocalDateTime timeAfter, LocalDateTime timeBefore) {
        return queryFactory
            .select(createSearchExpression(SearchMapResponse.class))
            .from(room)
            .where(createSearchRoomPredicate(spotId, tagBitMask, isImminent, point, radius, timeAfter, timeBefore))
            .fetch();
    }

    private <T> Expression<T> createSearchExpression(Class<? extends T> type) {
        if (type == SearchListResponse.class) {
            return Projections.fields(
                type
                , room.id.as("id")
                , room.spot.id.as("spotId")
                , room.departureTime.as("departureTime")
                , room.spot.spotPoint.as("spotPoint")
                , room.spot.name.as("spotName")
                , room.roomTagBitMask.as("roomTagBitMask")
                , room.departurePoint.as("departurePoint")
                , room.departureName.as("departureName")
                , room.currentHeadcount.as("currentHeadcount")
                , room.wishHeadcount.as("wishHeadcount")
                , room.durationMinutes.as("durationMinutes")
                , room.expectedCharge.as("expectedCharge")
            );
        }
        if (type == SearchMapResponse.class) {
            return Projections.fields(
                type
                , room.id.as("id")
                , room.departurePoint.as("departurePoint")
                , room.spot.name.as("spotName")
            );
        }
        return null;
    }

    private BooleanBuilder createSearchRoomPredicate(Long spotId, Integer tagBitMask, Boolean isImminent, Point point, Long radius, LocalDateTime timeAfter, LocalDateTime timeBefore) {
        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(room.roomStatus.eq(PROCEEDING))
            .and(Expressions.numberTemplate(Long.class, "ST_DISTANCE_SPHERE({0}, {1})", room.departurePoint, point).loe(radius))
            .and(Expressions.numberTemplate(Integer.class, "BIT_AND({0}, {1})", room.roomTagBitMask, tagBitMask).eq(tagBitMask))
        ;
        if (isImminent) {
            predicate.and(room.departureTime.between(timeAfter, timeBefore));
        }
        if (spotId != null) {
            predicate.and(room.spot.id.eq(spotId));
        }
        return predicate;
    }

    private OrderSpecifier[] createListOrderSpecifiers(Point point, RoomSortType sortType) {
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();
        switch (sortType) {
            case NEW:
                orderSpecifiers.add(Expressions.numberTemplate(Long.class, "TIMESTAMPDIFF(SECOND, {0}, {1})", LocalDateTime.now(), room.createdAt).abs().asc());
                orderSpecifiers.add(Expressions.numberTemplate(Float.class, "ST_DISTANCE_SPHERE({0}, {1})", room.departurePoint, point).asc());
                break;
            case DISTANCE:
                orderSpecifiers.add(Expressions.numberTemplate(Float.class, "ST_DISTANCE_SPHERE({0}, {1})", room.departurePoint, point).asc());
                orderSpecifiers.add(room.departureTime.asc());
                break;
            case ENDTIME:
                orderSpecifiers.add(room.departureTime.asc());
                orderSpecifiers.add(Expressions.numberTemplate(Float.class, "ST_DISTANCE_SPHERE({0}, {1})", room.departurePoint, point).asc());
                break;
        }
        return orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]);
    }

    private <T> Slice<T> listToSlice(List<T> content, Pageable pageable) {
        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            hasNext = true;
            content.remove(content.size() - 1);
        }
        return new SliceImpl<>(content, pageable, hasNext);
    }
}
