package com.modutaxi.api.common.config.websocket;

import com.modutaxi.api.common.auth.jwt.JwtTokenProvider;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.StompErrorCode;
import com.modutaxi.api.common.fcm.FcmService;
import com.modutaxi.api.domain.chatmessage.dto.ChatMessageRequestDto;
import com.modutaxi.api.domain.chatmessage.entity.MessageType;
import com.modutaxi.api.domain.chat.ChatRoomMappingInfo;
import com.modutaxi.api.domain.chat.repository.RedisChatRoomRepositoryImpl;
import com.modutaxi.api.domain.chat.service.ChatService;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.member.repository.MemberRepository;
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
    private final MemberRepository memberRepository;

    private static final int FULL_MEMBER = 4;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        //웹소켓 연결 요청
        if (StompCommand.CONNECT == accessor.getCommand()) {

            String sessionId = accessor.getSessionId();
            String token = accessor.getFirstNativeHeader("token");

            jwtTokenProvider.validateAccessToken(token);

            String memberId = jwtTokenProvider.getMemberIdByToken(token);
            redisChatRoomRepositoryImpl.setUserInfo(sessionId, memberId);
            log.info("Socket Connect");
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
            Member member = memberRepository.findById(Long.valueOf(memberId)).orElseThrow(
                    () -> new BaseException(StompErrorCode.EMPTY_MEMBER));

            ChatRoomMappingInfo chatRoomMappingInfo = redisChatRoomRepositoryImpl.findChatInfoByMemberId(memberId);


            //roomId가 안들어왔으면 에러
            if (roomId == null || roomId == "") {
                log.error("구독요청 \"sub/chat/{roomId}\" 에서 roomId가 들어오지 않았습니다.");
                throw new BaseException(StompErrorCode.ROOM_ID_IS_NULL);
            }

            //없는 방 연결하려 할 때 에러
            Room room = roomRepository.findById(Long.valueOf(roomId)).orElseThrow(
                    () -> new BaseException(StompErrorCode.FAULT_ROOM_ID));

            //이미 연결된 방이 있는데 애꿎은 방을 들어가려고 하면 에러
            //연결되어 있는 방이 존재하면서 && 요청으로 들어온 roomId가 연결되어 있는 방과 다를 때
            if (chatRoomMappingInfo != null && !roomId.equals(chatRoomMappingInfo.getRoomId())) {
                log.error("사용자 ID: {}님은 현재 {}번 방에 참여해 있지만, 참여요청이 들어온 방은 {}번방 입니다. ",
                        memberId, chatRoomMappingInfo.getRoomId(), roomId);
                throw new BaseException(StompErrorCode.ALREADY_ROOM_IN);
            }

            if (room.getCurrentHeadcount() >= FULL_MEMBER) {
                log.error("참여하려고 하는 {}방의 인원수가 4명으로 만석입니다. 따라서 방에 참가할 수 없습니다.", roomId);
                throw new BaseException(StompErrorCode.FULL_CHAT_ROOM);
            }

            String nickName = member.getNickname();

            if (chatRoomMappingInfo == null) {
                chatRoomMappingInfo = new ChatRoomMappingInfo(roomId, nickName);
                redisChatRoomRepositoryImpl.setUserEnterInfo(memberId, chatRoomMappingInfo);

                room.plusCurrentHeadCount();
                ChatMessageRequestDto joinMessage = new ChatMessageRequestDto(
                        Long.valueOf(roomId), MessageType.JOIN, nickName + "님이 들어왔습니다.",
                        chatRoomMappingInfo.getNickname(), memberId, LocalDateTime.now(), "");

                fcmService.subscribe(Long.valueOf(memberId), Long.valueOf(roomId));
                chatService.sendChatMessage(joinMessage);
            }
            log.info("SUBSCRIBE");
        } else if (StompCommand.DISCONNECT == accessor.getCommand()) {
            String sessionId = accessor.getSessionId();
            // 세션에 대한 정보 삭제
            redisChatRoomRepositoryImpl.removeUserBySessionIdEnterInfo(sessionId);
        }

        return message;
    }
}