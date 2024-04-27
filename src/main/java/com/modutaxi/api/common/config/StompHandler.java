package com.modutaxi.api.common.config;

import com.modutaxi.api.common.auth.jwt.JwtTokenProvider;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.ChatErrorCode;
import com.modutaxi.api.domain.chat.MessageType;
import com.modutaxi.api.domain.chat.dto.ChatMessage;
import com.modutaxi.api.domain.chatroom.ChatInfo;
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

            System.out.println("token = " + token);

            String memberId = jwtTokenProvider.getMemberIdByAccessToken(token);
            System.out.println("memberIdByToken = " + memberId);

            //임시로 세션아이디와 멤버아이디를 매핑.
            //이렇게 해야 다시 SUB으로 갈 때 구독을 할 수 있음.
            chatRoomRepository.setUserInfo(sessionId, memberId);

            if(memberId == null){
                System.out.println("로그인 plz...");
            }
            else System.out.println("토큰 검증 성공!!!");
            //검증 끝

        }

        else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
            //sessionId, roomId 추출
            String sessionId = (String) accessor.getSessionId();
            String destination = (String) message.getHeaders().get("simpDestination");
            System.out.println("destination = " + destination);
            String roomId =
                destination.lastIndexOf('/') == -1 ? null
                    : destination.substring(destination.lastIndexOf("/") + 1);

            if (roomId == null) {
                throw new BaseException(ChatErrorCode.FAULT_ROOM_ID);
            }

            //세션id로 업데이트 하기
            String memberId = chatRoomRepository.findMemberBySessionId(sessionId);
            System.out.println("MemberId : " + memberId);


            if(chatRoomRepository.findChatInfoByMemberId(memberId) != null){
                System.out.println("이미 연결되어있잖아~ = ");
//                chatRoomRepository.removeUserEnterInfo(sessionId);
            }

            ChatInfo chatInfo = new ChatInfo(roomId, "훈" + chatRoomRepository.getUserCount(roomId));
            // 채팅방 연관관계 설정
            chatRoomRepository.setUserEnterInfo(memberId, chatInfo);

            if (chatRoomRepository.getUserCount(roomId) >= 4) {
                throw new BaseException(ChatErrorCode.FULL_CHAT_ROOM);
            }
            System.out.println("Enter possible");


            // 채팅방의 인원수 +1
            chatRoomRepository.plusUserCount(roomId);
            System.out.println("userCount = " + chatRoomRepository.getUserCount(roomId));

            chatService.sendChatMessage(new ChatMessage(Long.valueOf(roomId),MessageType.JOIN,"",
                    chatInfo.getNickname(),""));
        }

        else if (StompCommand.DISCONNECT == accessor.getCommand()) {
            String sessionId = (String) accessor.getSessionId();
            String memberId = chatRoomRepository.findMemberBySessionId(sessionId);
            ChatInfo chatInfo = chatRoomRepository.findChatInfoByMemberId(memberId);

            chatRoomRepository.minusUserCount(chatInfo.getRoomId());
//            System.out.println("count : " + chatRoomRepository.getUserCount(roomId));

            // 클라이언트 퇴장 메시지 발송한다.
            ChatMessage chatMessage = new ChatMessage(Long.valueOf(chatInfo.getRoomId()), MessageType.LEAVE, "",
                chatInfo.getNickname(), "");


            chatService.sendChatMessage(chatMessage);

            // 퇴장한 클라이언트의 roomId 맵핑 정보를 삭제
            chatRoomRepository.removeUserEnterInfo(sessionId, memberId);
        }

        return message;
    }
}