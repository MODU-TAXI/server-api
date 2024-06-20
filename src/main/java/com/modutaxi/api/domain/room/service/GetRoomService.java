package com.modutaxi.api.domain.room.service;

import com.modutaxi.api.common.converter.RoomTagBitMaskConverter;
import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.RoomErrorCode;
import com.modutaxi.api.common.exception.errorcode.TaxiInfoErrorCode;
import com.modutaxi.api.common.pagination.PageResponseDto;
import com.modutaxi.api.domain.chat.ChatRoomMappingInfo;
import com.modutaxi.api.domain.chat.repository.RedisChatRoomRepositoryImpl;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.room.dao.RoomMysqlResponse.SearchIntegrationResponse;
import com.modutaxi.api.domain.room.dao.RoomMysqlResponse.SearchListResponse;
import com.modutaxi.api.domain.room.dao.RoomMysqlResponse.SearchMapResponse;
import com.modutaxi.api.domain.room.dto.RoomResponseDto.*;
import com.modutaxi.api.domain.room.entity.Room;
import com.modutaxi.api.domain.room.entity.RoomSortType;
import com.modutaxi.api.domain.room.entity.RoomTagBitMask;
import com.modutaxi.api.domain.room.mapper.RoomMapper;
import com.modutaxi.api.domain.room.repository.RoomRepository;
import com.modutaxi.api.domain.room.repository.RoomRepositoryDSL;
import com.modutaxi.api.domain.roomwaiting.repository.RoomWaitingRepository;
import com.modutaxi.api.domain.spot.service.GetSpotService;
import com.modutaxi.api.domain.taxiinfo.repository.TaxiInfoMongoRepository;
import com.mongodb.client.model.geojson.LineString;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GetRoomService {
    @Value("${env.imminent-time.front}")
    private Long imminentTimeFront;
    @Value("${env.imminent-time.back}")
    private Long imminentTimeBack;

    private final RoomRepository roomRepository;
    private final TaxiInfoMongoRepository taxiInfoMongoRepository;
    private final GetSpotService getSpotService;
    private final RoomRepositoryDSL roomRepositoryDSL;
    private final RedisChatRoomRepositoryImpl redisChatRoomRepository;
    private final RoomWaitingRepository roomWaitingRepository;

    public RoomDetailResponse getRoomDetail(Member member, Long roomId) {
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new BaseException(RoomErrorCode.EMPTY_ROOM));

        LineString path = taxiInfoMongoRepository.findById(roomId)
            .orElseThrow(() -> new BaseException(
                TaxiInfoErrorCode.EMPTY_TAXI_INFO)).getPath();

        boolean isParticipate = false;
        boolean isWaiting = roomWaitingRepository.existsByMemberAndRoom(member, room);

        ChatRoomMappingInfo chatRoomMappingInfo = redisChatRoomRepository.findChatInfoByMemberId(member.getId().toString());

        if (chatRoomMappingInfo != null) {
            String myParticipatedRoomId = chatRoomMappingInfo.getRoomId();
            isParticipate = myParticipatedRoomId.equals(roomId.toString());
        }

        return RoomMapper.toDto(room, member, path, isParticipate, isWaiting);
    }

    public PageResponseDto<List<RoomSimpleResponse>> getRoomSimpleList(int page, int size, Long spotId, List<RoomTagBitMask> tags, Point point, Long radius, Boolean isImminent, RoomSortType sortType) {
        if (spotId != null)
            getSpotService.getSpot(spotId);
        LocalDateTime timeNow = LocalDateTime.now();
        Slice<SearchListResponse> roomSlice = roomRepositoryDSL.findNearRoomsList(spotId, checkTags(tags), isImminent, point, radius, timeNow.minusMinutes(imminentTimeFront), timeNow.plusMinutes(imminentTimeBack), PageRequest.of(page, size), sortType);
        List<RoomSimpleResponse> roomSimpleResponseList = roomSlice.stream().map(RoomMapper::toDto).collect(Collectors.toList());
        return new PageResponseDto<>(PageRequest.of(page, size).getPageNumber(), roomSlice.hasNext(), roomSimpleResponseList);
    }

    public SearchRoomWithRadiusResponses getRadiusRooms(Long spotId, List<RoomTagBitMask> tags, Point searchPoint, Long radius, Boolean isImminent) {
        if (spotId != null)
            getSpotService.getSpot(spotId);
        LocalDateTime timeNow = LocalDateTime.now();
        List<SearchMapResponse> rooms = roomRepositoryDSL.findNearRoomsMap(spotId, checkTags(tags), isImminent, searchPoint, radius, timeNow.minusMinutes(imminentTimeFront), timeNow.plusMinutes(imminentTimeBack));
        List<SearchRoomWithRadiusResponse> roomList = rooms.stream().map(RoomMapper::toDto).toList();
        return new SearchRoomWithRadiusResponses(roomList);
    }

    public SearchRoomIntegrationResponses getRoomIntegration(Long spotId, List<RoomTagBitMask> tags, Point point, Long radius, Boolean isImminent, RoomSortType sortType) {
        if (spotId != null)
            getSpotService.getSpot(spotId);
        LocalDateTime timeNow = LocalDateTime.now();
        List<SearchIntegrationResponse> rooms = roomRepositoryDSL.findNearRoomsIntegration(spotId, checkTags(tags), isImminent, point, radius, timeNow.minusMinutes(imminentTimeFront), timeNow.plusMinutes(imminentTimeBack), sortType);
        List<SearchRoomIntegrationResponse> roomList = rooms.stream().map(RoomMapper::toDto).toList();
        return new SearchRoomIntegrationResponses(roomList);
    }

    public RoomPreviewResponse getRoomPreview(Long roomId) {
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new BaseException(RoomErrorCode.EMPTY_ROOM));
        return RoomMapper.toDto(room);
    }

    private Integer checkTags(List<RoomTagBitMask> tags) {
        Integer tagBitMask = RoomTagBitMaskConverter.convertRoomTagListToBitMask(tags);
        if ((RoomTagBitMask.ONLY_WOMAN.getValue() + RoomTagBitMask.ONLY_MAN.getValue() & tagBitMask) == RoomTagBitMask.ONLY_WOMAN.getValue() + RoomTagBitMask.ONLY_MAN.getValue())
            throw new BaseException(RoomErrorCode.BOTH_GENDER);
        return tagBitMask;
    }
}
