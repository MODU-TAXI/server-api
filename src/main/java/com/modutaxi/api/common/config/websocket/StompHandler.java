package com.modutaxi.api.common.config.websocket;

import com.modutaxi.api.common.auth.jwt.JwtTokenProvider;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.ChatErrorCode;
import com.modutaxi.api.domain.chatmessage.dto.ChatMessageRequestDto;
import com.modutaxi.api.domain.chatmessage.entity.MessageType;
import com.modutaxi.api.domain.chatmessage.service.ChatMessageService;
import com.modutaxi.api.domain.chat.ChatRoomMappingInfo;
import com.modutaxi.api.domain.chat.ChatNickName;
import com.modutaxi.api.domain.chat.repository.ChatRoomRepository;
import com.modutaxi.api.domain.chat.service.ChatService;
import com.modutaxi.api.domain.member.repository.MemberRepository;
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
    private final ChatRoomRepository chatRoomRepository;
    private final ChatService chatService;
//    private final StompErrorHandler stompErrorHandler;



    // websocket을 통해 들어온 요청이 처리 되기전 실행된다.
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        System.out.println("---------------------------------------");
        System.out.println("Command: " + accessor.getCommand());
        System.out.println("Destination: " + accessor.getDestination());
        System.out.println("Message Headers: " + accessor.getMessageHeaders());
        System.out.println("First Native Header: " + accessor.getFirstNativeHeader("token"));
        System.out.println("Session ID: " + accessor.getSessionId());
        System.out.println("User: " + accessor.getUser());
        System.out.println("Subscription ID: " + accessor.getSubscriptionId());

        //웹소켓 연결 요청
        if (StompCommand.CONNECT == accessor.getCommand()) {

            String sessionId = (String) accessor.getSessionId();
            String token = accessor.getFirstNativeHeader("token");
            String memberId = jwtTokenProvider.getMemberIdByAccessToken(token);

            //임시로 세션아이디와 멤버아이디를 매핑.
            //이렇게 해야 다시 SUB으로 갈 때 구독을 할 수 있음.
            chatRoomRepository.setUserInfo(sessionId, memberId);
        }

        //구독 요청
        else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
            String sessionId = (String) accessor.getSessionId();
            String destination = (String) message.getHeaders().get("simpDestination");

            String roomId =
                destination.lastIndexOf('/') == -1 ? null
                    : destination.substring(destination.lastIndexOf("/") + 1);

            String memberId = chatRoomRepository.findMemberBySessionId(sessionId);

            ChatRoomMappingInfo chatRoomMappingInfo = chatRoomRepository.findChatInfoByMemberId(memberId);

            //roomId가 안들어왔으면 컷
            if (roomId == null) {
                throw new BaseException(ChatErrorCode.FAULT_ROOM_ID);
            }

            //이미 연결된 방이 있는데 애꿎은 방을 들어가려고 하면 컷
            //연결되어 있는 방이 존재하면서 && 요청으로 들어온 roomId가 연결되어 있는 방과 다를 때
            if (chatRoomMappingInfo != null && !roomId.equals(chatRoomMappingInfo.getRoomId())) {
                throw new BaseException(ChatErrorCode.ALREADY_ROOM_IN);
            }

            String nickName = chatRoomMappingInfo != null ? chatRoomMappingInfo.getNickname() : setNickName(roomId);
            int count = ChatNickName.valueOf(nickName).getValue();

            if (chatRoomRepository.getUserCount(roomId) >= 15) {
                throw new BaseException(ChatErrorCode.FULL_CHAT_ROOM);
            }

            if(chatRoomMappingInfo == null){
                chatRoomMappingInfo = new ChatRoomMappingInfo(roomId, nickName);
                chatRoomRepository.setUserEnterInfo(memberId, chatRoomMappingInfo);
                // 채팅방의 인원수 체크
                chatRoomRepository.plusUserCount(roomId, count);
                chatService.sendChatMessage(new ChatMessageRequestDto(Long.valueOf(roomId),MessageType.JOIN,"",
                        chatRoomMappingInfo.getNickname(),memberId));
            }
        }

        else if (StompCommand.DISCONNECT == accessor.getCommand()) {
            String sessionId = (String) accessor.getSessionId();
            // 세션에 대한 정보 삭제
            chatRoomRepository.removeUserBySessionIdEnterInfo(sessionId);
            //웹소켓은 끊기면 걍 끊긴거다~
            //대신 방 정보 매핑 삭제는 따로 API 호출을 해주어야 하는 것~
        }

        return message;
    }
    private String setNickName(String roomId){
        Long count = chatRoomRepository.getUserCount(roomId);
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