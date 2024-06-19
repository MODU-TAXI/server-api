package com.modutaxi.api.domain.participant.service;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.RoomErrorCode;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.participant.entity.Participant;
import com.modutaxi.api.domain.participant.repository.ParticipantRepository;
import com.modutaxi.api.domain.room.entity.Room;
import com.modutaxi.api.domain.room.repository.RoomRepository;
import com.modutaxi.api.domain.roomwaiting.mapper.RoomWaitingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GetParticipantService {

    private final RoomRepository roomRepository;
    private final ParticipantRepository participantRepository;


    /**
     * 방 참가 인원리스트 조회
     */
    public RoomWaitingMapper.MemberRoomInResponseList getParticipateInRoom(Member member, Long roomId){
        Room room = roomRepository.findById(roomId).orElseThrow(
                () -> new BaseException(RoomErrorCode.EMPTY_ROOM));

        List<Participant> participantList = participantRepository.findAllByRoom(room);
        List<Member> memberIdList = new ArrayList<>(participantList).stream().map(Participant::getMember).toList();

        return new RoomWaitingMapper.MemberRoomInResponseList(
                memberIdList.stream()
                        .map(iter -> RoomWaitingMapper.MemberRoomInResponse.toDto(iter, iter.getId().equals(member.getId())))
                        .collect(Collectors.toList()));
    }
}
