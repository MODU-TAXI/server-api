package com.modutaxi.api.domain.history.service;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.HistoryErrorCode;
import com.modutaxi.api.common.exception.errorcode.RoomErrorCode;
import com.modutaxi.api.domain.history.dto.HistoryResponseDto.*;
import com.modutaxi.api.domain.history.entity.History;
import com.modutaxi.api.domain.history.mapper.HistoryMapper;
import com.modutaxi.api.domain.history.repository.HistoryRepository;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.room.entity.Room;
import com.modutaxi.api.domain.room.repository.RoomRepository;
import com.modutaxi.api.domain.roomwaiting.mapper.RoomWaitingMapper;
import com.modutaxi.api.domain.roomwaiting.service.RoomWaitingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class HistoryService {
    private final HistoryRepository historyRepository;
    private final RoomRepository roomRepository;
    private final RoomWaitingService roomWaitingService;

    public HistoryDetailResponse getHistoryDetail(Member member, Long historyId) {
        History history = historyRepository.findById(historyId)
            .orElseThrow(() -> new BaseException(HistoryErrorCode.EMPTY_HISTORY));

        Room room = roomRepository.findById(history.getRoom().getId())
            .orElseThrow(() -> new BaseException(RoomErrorCode.EMPTY_ROOM));

        RoomWaitingMapper.MemberRoomInResponseList memberRoomInResponseList =
            roomWaitingService.getParticipateInRoom(member, room.getId());

        return HistoryMapper.toDto(history, memberRoomInResponseList);
    }
}
