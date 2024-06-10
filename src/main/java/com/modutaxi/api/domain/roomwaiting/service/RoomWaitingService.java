package com.modutaxi.api.domain.roomwaiting.service;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.ChatErrorCode;
import com.modutaxi.api.common.exception.errorcode.MemberErrorCode;
import com.modutaxi.api.common.exception.errorcode.ParticipateErrorCode;
import com.modutaxi.api.common.exception.errorcode.RoomErrorCode;
import com.modutaxi.api.common.fcm.FcmService;
import com.modutaxi.api.domain.chat.ChatRoomMappingInfo;
import com.modutaxi.api.domain.chat.repository.RedisChatRoomRepositoryImpl;
import com.modutaxi.api.domain.chat.service.ChatService;
import com.modutaxi.api.domain.chatmessage.dto.ChatMessageRequestDto;
import com.modutaxi.api.domain.chatmessage.entity.MessageType;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.member.repository.MemberRepository;
import com.modutaxi.api.domain.member.service.GetMemberService;
import com.modutaxi.api.domain.room.entity.Room;
import com.modutaxi.api.domain.room.entity.RoomStatus;
import com.modutaxi.api.domain.room.repository.RoomRepository;
import com.modutaxi.api.domain.roomwaiting.mapper.RoomWaitingMapper.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomWaitingService {
    private final RoomRepository roomRepository;
    private final RedisChatRoomRepositoryImpl redisChatRoomRepositoryImpl;
    private final GetMemberService getMemberService;
    private final FcmService fcmService;
    private final MemberRepository memberRepository;
    private final ChatService chatService;

    /**
     * 방 참가 신청
     */
    public ApplyResponse applyForParticipate(Member member, String roomId){
        if (member.isBlocked()) {
            throw new BaseException(MemberErrorCode.BLOCKED_MEMBER);
        }

        Room room = roomRepository.findById(Long.valueOf(roomId)).orElseThrow(
                () -> new BaseException(RoomErrorCode.EMPTY_ROOM));

        //이미 해당 채팅방에 있을 때
        if(redisChatRoomRepositoryImpl.findMemberInRoomInList(roomId, member.getId().toString())){
            throw new BaseException(ParticipateErrorCode.USER_ALREADY_IN_ROOM);
        }

        //이미 다른 방에 들어가 있을 때
        if(redisChatRoomRepositoryImpl.findChatInfoByMemberId(member.getId().toString())!=null){
            throw new BaseException(ChatErrorCode.ALREADY_ROOM_IN);
        }

        //이미 해당 대기열에 있을 때
        if(redisChatRoomRepositoryImpl.findMemberInWaitingList(roomId, member.getId().toString())){
            throw new BaseException(ParticipateErrorCode.USER_ALREADY_IN_WAITING_LIST);
        }

        //방이 COMPLETE면 에러
        if(room.getRoomStatus().equals(RoomStatus.COMPLETE)){
            throw new BaseException(ParticipateErrorCode.PARTICIPATE_NOT_ALLOW);
        }
        redisChatRoomRepositoryImpl.addToWaitingList(roomId, member.getId().toString());
        fcmService.sendNewParticipant(room.getRoomManager(), roomId);
        return new ApplyResponse(true);
    }

    /**
     * 방 참가 수락
     */
    public ApplyResponse acceptForParticipate(Member member, String roomId, String memberId){
        Room room = roomRepository.findById(Long.valueOf(roomId)).orElseThrow(
                () -> new BaseException(RoomErrorCode.EMPTY_ROOM));

        Member participant = memberRepository.findByIdAndStatusTrue(Long.valueOf(memberId))
            .orElseThrow(() -> new BaseException(MemberErrorCode.EMPTY_MEMBER));

        if(redisChatRoomRepositoryImpl.findChatInfoByMemberId(memberId)!=null){
            throw new BaseException(ChatErrorCode.ALREADY_ROOM_IN);
        }

        //방 매니저가 아닌 사용자의 허락은 에러
        if(!room.getRoomManager().getId().equals(member.getId())){
            throw new BaseException(ParticipateErrorCode.YOUR_NOT_ROOM_MANAGER);
        }

        //방 자체 상태가 COMPLETE면 에러
        if(room.getRoomStatus().equals(RoomStatus.COMPLETE)){
            throw new BaseException(ParticipateErrorCode.PARTICIPATE_NOT_ALLOW);
        }

        // 대기열에 특정 사용자가 존재하지 않으면 에러
        if(!redisChatRoomRepositoryImpl.findMemberInWaitingList(roomId, memberId)){
            throw new BaseException(ParticipateErrorCode.USER_NOT_IN_ROOM);
        }

        //이미 유저가 해당 방에 존재할 때
        if(redisChatRoomRepositoryImpl.findMemberInRoomInList(roomId, memberId)){
            throw new BaseException(ParticipateErrorCode.USER_ALREADY_IN_ROOM);
        }

        //대기열에서 제거
        redisChatRoomRepositoryImpl.removeFromWaitingList(roomId, memberId);
        //채팅방에 저장
        redisChatRoomRepositoryImpl.addRoomInMemberList(roomId, memberId);

        //매핑 정보 저장
        ChatRoomMappingInfo chatRoomMappingInfo = new ChatRoomMappingInfo(roomId, participant.getNickname());
        redisChatRoomRepositoryImpl.setUserEnterInfo(memberId, chatRoomMappingInfo);

        //fcm구독
        fcmService.subscribe(Long.valueOf(memberId), Long.valueOf(roomId));
        //참가 수락되었다는 메세지 본인에게 전송
        fcmService.sendPermitParticipate(participant, roomId);

        //방 팀원들에게 참가했다는 메세지 보내기
        chatService.sendChatMessage(new ChatMessageRequestDto(
                Long.valueOf(roomId), MessageType.JOIN, participant.getNickname() + "님이 들어왔습니다.",
                chatRoomMappingInfo.getNickname(), memberId, LocalDateTime.now(), ""));

        return new ApplyResponse(true);
    }

    /**
     * 방 참가 인원리스트 조회
     */
    public MemberRoomInResponseList getParticipateInRoom(Member member, Long roomId){
        Room room = roomRepository.findById(roomId).orElseThrow(
                () -> new BaseException(RoomErrorCode.EMPTY_ROOM));
        Set<String> memberIdSet = redisChatRoomRepositoryImpl.findRoomInList(roomId.toString());

        List<Long> memberIdList = new ArrayList<>(memberIdSet).stream().map(Long::valueOf).toList();
        List<Member> waitingUserList = getMemberService.getMemberList(memberIdList);

        return new MemberRoomInResponseList(
            waitingUserList.stream()
                .map(iter -> MemberRoomInResponse.toDto(iter, iter.getId().equals(member.getId())))
                .collect(Collectors.toList()));
    }

    /**
     * 매칭 대기 인원리스트 조회
     */
    public RoomWaitingResponseList getWaitingList(Member member, Long roomId){
        Room room = roomRepository.findById(roomId).orElseThrow(
                () -> new BaseException(RoomErrorCode.EMPTY_ROOM));

        Set<String> memberIdSet = redisChatRoomRepositoryImpl.findWaitingList(roomId.toString());

        List<Long> memberIdList = new ArrayList<>(memberIdSet).stream().map(Long::valueOf).toList();
        List<Member> waitingUserList = getMemberService.getMemberList(memberIdList);

        return new RoomWaitingResponseList(
            waitingUserList.stream()
                .map(iter -> RoomWaitingResponse.toDto(iter, iter.getId().equals(member.getId())))
                .collect(Collectors.toList()));
    }
}
