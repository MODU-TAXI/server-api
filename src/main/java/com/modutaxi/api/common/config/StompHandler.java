package com.modutaxi.api.common.config;

import com.modutaxi.api.common.auth.jwt.JwtTokenProvider;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.ChatErrorCode;
import com.modutaxi.api.domain.chat.MessageType;
import com.modutaxi.api.domain.chat.dto.ChatMessage;
import com.modutaxi.api.domain.chatroom.ChatInfo;
import com.modutaxi.api.domain.chatroom.ChatNickName;
import com.modutaxi.api.domain.chatroom.repository.ChatRoomRepository;
import com.modutaxi.api.domain.chat.service.ChatService;
import com.modutaxi.api.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final ChatService chatService;



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

            //roomId가 안들어왔으면 컷
            if (roomId == null) {
                throw new BaseException(ChatErrorCode.FAULT_ROOM_ID);
            }

            //memberId 가져오기
            String memberId = chatRoomRepository.findMemberBySessionId(sessionId);


            if(chatRoomRepository.findChatInfoByMemberId(memberId) != null){
                System.out.println("이미 연결되어있잖아~ 쉐끼야");
//                throw new BaseException(ChatErrorCode.ALREADY_ROOM_IN);
                chatRoomRepository.removeUserBySessionIdEnterInfo(sessionId);
                chatRoomRepository.setUserInfo(sessionId, memberId);
            }

            String nickName = setNickName(roomId);
            System.out.println("nickName = " + nickName);
            int count = ChatNickName.valueOf(nickName).getValue();

            ChatInfo chatInfo = new ChatInfo(roomId, nickName);

            // 채팅방 연관관계 설정
            chatRoomRepository.setUserEnterInfo(memberId, chatInfo);

            if (chatRoomRepository.getUserCount(roomId) >= 15) {
                throw new BaseException(ChatErrorCode.FULL_CHAT_ROOM);
            }
            System.out.println("Enter possible");


            // 채팅방의 인원수 +1
            chatRoomRepository.plusUserCount(roomId, count);
            System.out.println("userCount = " + chatRoomRepository.getUserCount(roomId));

            chatService.sendChatMessage(new ChatMessage(Long.valueOf(roomId),MessageType.JOIN,"",
                    chatInfo.getNickname(),""));
        }

        else if (StompCommand.DISCONNECT == accessor.getCommand()) {
            String sessionId = (String) accessor.getSessionId();
            String memberId = chatRoomRepository.findMemberBySessionId(sessionId);
            ChatInfo chatInfo = chatRoomRepository.findChatInfoByMemberId(memberId);
            int count = ChatNickName.valueOf(chatInfo.getNickname()).getValue();
            chatRoomRepository.minusUserCount(chatInfo.getRoomId(), count);
//            System.out.println("count : " + chatRoomRepository.getUserCount(roomId));

            // 클라이언트 퇴장 메시지 발송한다.
            ChatMessage chatMessage = new ChatMessage(Long.valueOf(chatInfo.getRoomId()), MessageType.LEAVE, "",
                chatInfo.getNickname(), "");


            chatService.sendChatMessage(chatMessage);

            System.out.println("왜안돼??????"+memberId);
            // 퇴장한 클라이언트의 roomId 맵핑 정보를 삭제
            chatRoomRepository.removeUserBySessionIdEnterInfo(sessionId);
            chatRoomRepository.removeUserByMemberIdEnterInfo(memberId);

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