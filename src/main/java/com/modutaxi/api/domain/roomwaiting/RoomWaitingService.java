package com.modutaxi.api.domain.roomwaiting;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.ChatErrorCode;
import com.modutaxi.api.common.exception.errorcode.ParticipateErrorCode;
import com.modutaxi.api.common.exception.errorcode.RoomErrorCode;
import com.modutaxi.api.domain.chat.repository.ChatRoomRepository;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.member.service.GetMemberService;
import com.modutaxi.api.domain.room.entity.Room;
import com.modutaxi.api.domain.room.entity.RoomStatus;
import com.modutaxi.api.domain.room.repository.RoomRepository;
import com.modutaxi.api.domain.roomwaiting.RoomWaitingResponseDto.*;

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
    private final ChatRoomRepository chatRoomRepository;
    private final GetMemberService getMemberService;
    public ApplyResponse applyForParticipate(Member member, String roomId){
        Room room = roomRepository.findById(Long.valueOf(roomId)).orElseThrow(
                () -> new BaseException(RoomErrorCode.EMPTY_ROOM));

        //이미 해당 채팅방에 있을 때
        if(chatRoomRepository.findMemberInRoomInList(roomId, member.getId().toString())){
            throw new BaseException(ParticipateErrorCode.USER_ALREADY_IN_ROOM);
        }

        //이미 다른 방에 들어가 있을 때
        if(chatRoomRepository.findChatInfoByMemberId(member.getId().toString())!=null){
            throw new BaseException(ChatErrorCode.ALREADY_ROOM_IN);
        }

        //이미 해당 대기열에 있을 때
        if(chatRoomRepository.findMemberInWaitingList(roomId, member.getId().toString())){
            throw new BaseException(ParticipateErrorCode.USER_ALREADY_IN_WAITING_LIST);
        }

        //방이 COMPLETE면 에러
        if(room.getRoomStatus().equals(RoomStatus.COMPLETE)){
            throw new BaseException(ParticipateErrorCode.PARTICIPATE_NOT_ALLOW);
        }
        chatRoomRepository.addToWaitingList(roomId, member.getId().toString());
        return new ApplyResponse(true);
    }

    public ApplyResponse acceptForParticipate(Member member, String roomId, String memberId){
        Room room = roomRepository.findById(Long.valueOf(roomId)).orElseThrow(
                () -> new BaseException(RoomErrorCode.EMPTY_ROOM));


        if(chatRoomRepository.findChatInfoByMemberId(memberId)!=null){
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
        if(!chatRoomRepository.findMemberInWaitingList(roomId, memberId)){
            throw new BaseException(ParticipateErrorCode.USER_NOT_IN_ROOM);
        }

        //이미 유저가 해당 방에 존재할 때
        if(chatRoomRepository.findMemberInRoomInList(roomId, memberId)){
            throw new BaseException(ParticipateErrorCode.USER_ALREADY_IN_ROOM);
        }


        // TODO: 5/2/24 String이 아닌 Fcm 알림 전송으로 변경
        //대기열에서 제거
        chatRoomRepository.removeFromWaitingList(roomId, memberId);
        //채팅방에 저장
        chatRoomRepository.addRoomInMemberList(roomId, memberId);

        return new ApplyResponse(true);
    }

    // TODO: 5/2/24 귀찮으니 코드 재사용
    public MemberRoomInResponseList getParticipateInRoom(Long roomId){
        Room room = roomRepository.findById(Long.valueOf(roomId)).orElseThrow(
                () -> new BaseException(RoomErrorCode.EMPTY_ROOM));
        Set<String> memberIdSet = chatRoomRepository.findRoomInList(roomId.toString());

        List<Long> memberIdList = new ArrayList<>(memberIdSet).stream().map(Long::valueOf).toList();
        List<Member> waitingUserList = getMemberService.getMemberList(memberIdList);

        return new MemberRoomInResponseList(
            waitingUserList.stream()
                .map(MemberRoomInResponse::toDto)
                .collect(Collectors.toList()));
    }

    public RoomWaitingResponseList getWaitingList(Long roomId){
        Room room = roomRepository.findById(roomId).orElseThrow(
                () -> new BaseException(RoomErrorCode.EMPTY_ROOM));

        Set<String> memberIdSet = chatRoomRepository.findWaitingList(roomId.toString());

        List<Long> memberIdList = new ArrayList<>(memberIdSet).stream().map(Long::valueOf).toList();
        List<Member> waitingUserList = getMemberService.getMemberList(memberIdList);

        return new RoomWaitingResponseList(
            waitingUserList.stream()
                .map(RoomWaitingResponse::toDto)
                .collect(Collectors.toList()));
    }
}
