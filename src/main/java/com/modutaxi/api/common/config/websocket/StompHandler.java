package com.modutaxi.api.common.config.websocket;

import com.modutaxi.api.common.auth.jwt.JwtTokenProvider;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.ChatErrorCode;
import com.modutaxi.api.common.exception.errorcode.RoomErrorCode;
import com.modutaxi.api.common.fcm.FcmService;
import com.modutaxi.api.domain.chatmessage.dto.ChatMessageRequestDto;
import com.modutaxi.api.domain.chatmessage.entity.MessageType;
import com.modutaxi.api.domain.chat.ChatRoomMappingInfo;
import com.modutaxi.api.domain.chat.ChatNickName;
import com.modutaxi.api.domain.chat.repository.RedisChatRoomRepositoryImpl;
import com.modutaxi.api.domain.chat.service.ChatService;
import com.modutaxi.api.domain.room.entity.Room;
import com.modutaxi.api.domain.room.repository.RoomRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisChatRoomRepositoryImpl redisChatRoomRepositoryImpl;
    private final ChatService chatService;
    private final RoomRepository roomRepository;
    private final FcmService fcmService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        //웹소켓 연결 요청
        if (StompCommand.CONNECT == accessor.getCommand()) {

            String sessionId = accessor.getSessionId();
            String token = accessor.getFirstNativeHeader("token");

            String memberId = jwtTokenProvider.getMemberIdByToken(token);
            redisChatRoomRepositoryImpl.setUserInfo(sessionId, memberId);
        }

        //구독 요청
        else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
            log.info("구독 요청");
            String sessionId = accessor.getSessionId();
            String destination = (String) message.getHeaders().get("simpDestination");

            String roomId =
                    destination.lastIndexOf('/') == -1 ? null
                            : destination.substring(destination.lastIndexOf("/") + 1);

            String memberId = redisChatRoomRepositoryImpl.findMemberBySessionId(sessionId);

            ChatRoomMappingInfo chatRoomMappingInfo = redisChatRoomRepositoryImpl.findChatInfoByMemberId(memberId);


            //roomId가 안들어왔으면 컷
            if (roomId == null) {
                log.error("구독요청 \"sub/chat/{roomId}\" 에서 roomId가 들어오지 않았습니다.");
                throw new BaseException(ChatErrorCode.FAULT_ROOM_ID);
            }

            //없는 방 연결하려 할 때 예외
            Room room = roomRepository.findById(Long.valueOf(roomId)).orElseThrow(
                    () -> new BaseException(RoomErrorCode.EMPTY_ROOM));

            //이미 연결된 방이 있는데 애꿎은 방을 들어가려고 하면 컷
            //연결되어 있는 방이 존재하면서 && 요청으로 들어온 roomId가 연결되어 있는 방과 다를 때
            if (chatRoomMappingInfo != null && !roomId.equals(chatRoomMappingInfo.getRoomId())) {
                log.error("사용자 ID: {}님은 현재 {}번 방에 참여해 있지만, 참여요청이 들어온 방은 {}번방 입니다. ",
                        memberId, chatRoomMappingInfo.getRoomId(), roomId);
                throw new BaseException(ChatErrorCode.ALREADY_ROOM_IN);
            }

            String nickName = chatRoomMappingInfo != null ? chatRoomMappingInfo.getNickname() : setNickName(roomId);
            int count = ChatNickName.valueOf(nickName).getValue();

            if (redisChatRoomRepositoryImpl.getUserCount(roomId) >= 15) {
                log.error("참여하려고 하는 {}방의 인원수가 4명으로 만석입니다. 따라서 방에 참가할 수 없습니다.", roomId);
                throw new BaseException(ChatErrorCode.FULL_CHAT_ROOM);
            }

            if (chatRoomMappingInfo == null) {
                chatRoomMappingInfo = new ChatRoomMappingInfo(roomId, nickName);
                redisChatRoomRepositoryImpl.setUserEnterInfo(memberId, chatRoomMappingInfo);
                // 채팅방의 인원수 체크
                redisChatRoomRepositoryImpl.plusUserCount(roomId, count);
                ChatMessageRequestDto joinMessage = new ChatMessageRequestDto(
                        Long.valueOf(roomId), MessageType.JOIN, nickName + "님이 들어왔습니다.",
                        chatRoomMappingInfo.getNickname(), memberId, LocalDateTime.now());

                chatService.sendChatMessage(joinMessage);
                fcmService.subscribe(Long.valueOf(memberId), Long.valueOf(roomId));
            }
        } else if (StompCommand.DISCONNECT == accessor.getCommand()) {
            String sessionId = accessor.getSessionId();
            // 세션에 대한 정보 삭제
            redisChatRoomRepositoryImpl.removeUserBySessionIdEnterInfo(sessionId);
        }

        return message;
    }

    private String setNickName(String roomId) {
        Long count = redisChatRoomRepositoryImpl.getUserCount(roomId);
        int emptyBitIndex = findEmptyBitIndex(count);

        if (emptyBitIndex != -1) {
            // 해당하는 비트 인덱스에 맞는 ChatNickName 할당
            return ChatNickName.values()[emptyBitIndex].name();
        } else {
            // 더 이상 할당 가능한 닉네임이 없을 때의 처리
            return "nickname";
        }
    }

    // 비어있는 비트 인덱스 찾는 메소드
    private int findEmptyBitIndex(Long count) {
        // 비트를 확인할 수 있는 최대 인덱스
        int maxBitIndex = ChatNickName.values().length - 1;

        for (int i = 0; i <= maxBitIndex; i++) {
            int bitValue = ChatNickName.values()[i].getValue();
            // count와 해당 비트값을 AND 연산하여 비트가 비어있는지 확인
            if ((count & bitValue) == 0) {
                return i; // 비어있는 비트 인덱스 반환
            }
        }
        return -1; // 비어있는 비트가 없는 경우
    }
}