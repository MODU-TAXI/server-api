package com.modutaxi.api.domain.chat.service;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.ChatErrorCode;
import com.modutaxi.api.common.exception.errorcode.RoomErrorCode;
import com.modutaxi.api.domain.chat.dto.ChatResponseDto.ChatMappingResponse;
import com.modutaxi.api.domain.chatmessage.dto.ChatMessageResponseDto;
import com.modutaxi.api.domain.chatmessage.entity.MessageType;
import com.modutaxi.api.domain.chatmessage.dto.ChatMessageRequestDto;
import com.modutaxi.api.domain.chat.ChatRoomMappingInfo;
import com.modutaxi.api.domain.chat.ChatNickName;
import com.modutaxi.api.domain.chat.repository.ChatRoomRepository;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.room.entity.Room;
import com.modutaxi.api.domain.room.repository.RoomRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatService {
    private final ChannelTopic channelTopic;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChatRoomRepository chatRoomRepository;
    private final RoomRepository roomRepository;
    /**
     * 채팅방에 메시지 발송할 수 있도록
     */
    public void sendChatMessage(ChatMessageRequestDto chatMessageRequestDto) {

        if (chatMessageRequestDto.getType().equals(MessageType.JOIN)) {
            chatMessageRequestDto.setContent(chatMessageRequestDto.getSender() + "님이 들어왔습니다.");
        } else if (chatMessageRequestDto.getType().equals(MessageType.LEAVE)) {
            chatMessageRequestDto.setContent(chatMessageRequestDto.getSender() + "님이 나갔습니다.");
        }

        redisTemplate.convertAndSend(channelTopic.getTopic(),
                ChatMessageResponseDto.requestDtoToResponseDto(chatMessageRequestDto));
    }

    public String deleteChatRoomInfo(Member member){
        ChatRoomMappingInfo chatRoomMappingInfo = chatRoomRepository.findChatInfoByMemberId(member.getId().toString());

        if(chatRoomMappingInfo == null){
            throw new BaseException(ChatErrorCode.ALREADY_ROOM_OUT);
        }

        Room room = roomRepository.findById(Long.valueOf(chatRoomMappingInfo.getRoomId())).orElseThrow(
                () -> new BaseException(RoomErrorCode.EMPTY_ROOM)
        );

        if(room.getRoomManager().equals(member)){
            throw new BaseException(RoomErrorCode.JUST_DELETE);
        }

        int count = ChatNickName.valueOf(chatRoomMappingInfo.getNickname()).getValue();
        // 클라이언트 퇴장 메시지 발송한다.
        ChatMessageRequestDto chatMessageRequestDto = new ChatMessageRequestDto(Long.valueOf(
                chatRoomMappingInfo.getRoomId()), MessageType.LEAVE, "",
                chatRoomMappingInfo.getNickname(), member.getId().toString(), LocalDateTime.now());

        sendChatMessage(chatMessageRequestDto);

        chatRoomRepository.removeUserByMemberIdEnterInfo(member.getId().toString());
        chatRoomRepository.minusUserCount(chatRoomMappingInfo.getRoomId(), count);

        return "매핑 정보가 삭제 되었습니다.";
    }

    public String chatRoomEnterPossible(Member member, Long roomId){
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new BaseException(
                RoomErrorCode.EMPTY_ROOM));

        if(chatRoomRepository.findChatInfoByMemberId(member.getId().toString()) != null){
            return "이미 방 참가해있잖아.";
        }

        if(chatRoomRepository.getUserCount(roomId.toString()) >= 15){
            return "인원 꽉 찼슈";
        }

        return "참가가능";
    }

    public ChatMappingResponse getChatRoomInfo(Member member){
        ChatRoomMappingInfo chatInfo = chatRoomRepository.findChatInfoByMemberId(member.getId().toString());
        if(chatInfo == null){
            return new ChatMappingResponse("null",member.getId().toString());
        }
        return new ChatMappingResponse(chatInfo.getRoomId(),member.getId().toString());
    }
}
