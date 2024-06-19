package com.modutaxi.api.domain.roomwaiting.service;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.ChatErrorCode;
import com.modutaxi.api.common.exception.errorcode.MemberErrorCode;
import com.modutaxi.api.common.exception.errorcode.ParticipateErrorCode;
import com.modutaxi.api.common.exception.errorcode.RoomErrorCode;
import com.modutaxi.api.common.fcm.FcmService;
import com.modutaxi.api.domain.chat.ChatRoomMappingInfo;
import com.modutaxi.api.domain.chat.dto.ChatResponseDto;
import com.modutaxi.api.domain.chat.dto.ChatResponseDto.DeleteResponse;
import com.modutaxi.api.domain.chat.repository.RedisChatRoomRepositoryImpl;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.member.repository.MemberRepository;
import com.modutaxi.api.domain.room.entity.Room;
import com.modutaxi.api.domain.room.entity.RoomStatus;
import com.modutaxi.api.domain.room.repository.RoomRepository;
import com.modutaxi.api.domain.roomwaiting.entity.RoomWaiting;
import com.modutaxi.api.domain.roomwaiting.repository.RoomWaitingRepository;
import com.modutaxi.api.domain.roomwaiting.mapper.RoomWaitingMapper;
import com.modutaxi.api.domain.roomwaiting.mapper.RoomWaitingMapper.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoomWaitingService {
    private final RoomRepository roomRepository;
    private final RedisChatRoomRepositoryImpl redisChatRoomRepositoryImpl;
    private final FcmService fcmService;
    private final MemberRepository memberRepository;
    private final RoomWaitingRepository roomWaitingRepository;

    /**
     * 방 참가 신청 - 대기열 등
     */
    @Transactional
    public ApplyResponse applyForParticipate(Long memberId, String roomId){
        Member member = memberRepository.findByIdAndStatusTrue(memberId).orElseThrow(()
                -> new BaseException(MemberErrorCode.EMPTY_MEMBER));

        if (member.isBlocked()) {
            throw new BaseException(MemberErrorCode.BLOCKED_MEMBER);
        }

        Room room = roomRepository.findById(Long.valueOf(roomId)).orElseThrow(
                () -> new BaseException(RoomErrorCode.EMPTY_ROOM));

        ChatRoomMappingInfo chatRoomMappingInfo = redisChatRoomRepositoryImpl.findChatInfoByMemberId(member.getId().toString());

        // 이미 해당 채팅방에 있을 때
        if(chatRoomMappingInfo.getRoomId().equals(roomId)){
            throw new BaseException(ParticipateErrorCode.USER_ALREADY_IN_ROOM);
        }

        // 이미 해당 대기열에 있을 때
        if(roomWaitingRepository.existsByMemberAndRoom(member,  room)){
            throw new BaseException(ParticipateErrorCode.USER_ALREADY_IN_WAITING_LIST);
        }

        // 자기 방이 아닌 다른 방에 들어가 있을 때
        if(chatRoomMappingInfo.getRoomId() != null){
            throw new BaseException(ChatErrorCode.ALREADY_ROOM_IN);
        }

        // 방이 COMPLETE면 에러
        if(room.getRoomStatus().equals(RoomStatus.COMPLETE)){
            throw new BaseException(ParticipateErrorCode.PARTICIPATE_NOT_ALLOW);
        }

        roomWaitingRepository.save(RoomWaitingMapper.toEntity(member, room));
        fcmService.sendNewParticipant(room.getRoomManager(), roomId);
        return new ApplyResponse(true);
    }


    /**
     * 매칭 대기 인원리스트 조회
     */
    public RoomWaitingResponseList getWaitingList(Long memberId, Long roomId){
        Member member = memberRepository.findByIdAndStatusTrue(memberId).orElseThrow(()
        -> new BaseException(MemberErrorCode.EMPTY_MEMBER));
        Room room = roomRepository.findById(roomId).orElseThrow(
                () -> new BaseException(RoomErrorCode.EMPTY_ROOM));

        List<RoomWaiting> waitingList = roomWaitingRepository.findAllByRoomId(roomId);

        List<Member> memberIdList = new ArrayList<>(waitingList).stream().map(RoomWaiting::getMember).toList();

        return new RoomWaitingMapper.RoomWaitingResponseList(
                memberIdList.stream()
                        .map(iter -> RoomWaitingMapper.RoomWaitingResponse.toDto(iter, iter.getId().equals(member.getId())))
                        .collect(Collectors.toList()));
    }

    /**
     * 대기열 퇴장
     */
    @Transactional
    public DeleteResponse leaveRoomWaiting(Long memberId, Long roomId) {
        Member member = memberRepository.findByIdAndStatusTrue(memberId).orElseThrow(()
                -> new BaseException(MemberErrorCode.EMPTY_MEMBER));

        Room room = roomRepository.findById(roomId).orElseThrow(()
                -> new BaseException(RoomErrorCode.EMPTY_ROOM));

        List<RoomWaiting> waitingList = roomWaitingRepository.findAllByRoomId(roomId);


        boolean isMemberInWaitingList = waitingList.stream()
                .anyMatch(waiting -> waiting.getMember().getId().equals(member.getId()));

        if (!isMemberInWaitingList) {
            throw new BaseException(ParticipateErrorCode.USER_NOT_IN_ROOM_WAITING);
        }

        roomWaitingRepository.deleteByMemberAndRoom(member, room);
        return new ChatResponseDto.DeleteResponse(true);
    }
}
