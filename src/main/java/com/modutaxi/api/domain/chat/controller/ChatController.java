package com.modutaxi.api.domain.chat.controller;

import com.modutaxi.api.common.auth.jwt.JwtTokenProvider;
import com.modutaxi.api.domain.chat.dto.ChatMessage;
import com.modutaxi.api.domain.chatroom.ChatInfo;
import com.modutaxi.api.domain.chatroom.repository.ChatRoomRepository;
import com.modutaxi.api.domain.chat.service.ChatService;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ChatController {
    private final JwtTokenProvider jwtTokenProvider;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final ChatService chatService;

    // /pub/chat 으로 오는 메세지 핸들링
    @MessageMapping("/chat")
    public void sendMessage(ChatMessage message){

        // TODO: 4/25/24 apic 테스트를 위해 헤더를 ChatMessage 객체에 받음. @Header("token") String token 파라미터로 추가해야함
        System.out.println("왔니?");
        String memberId = jwtTokenProvider.getMemberIdByAccessToken(message.getToken());
        ChatInfo chatInfo = chatRoomRepository.findChatInfoByMemberId(memberId);
        //닉네임 설정
        message.setSender(chatInfo.getNickname());

        System.out.println("member = " + memberId);
        System.out.println("sender = " + message.getSender());
        System.out.println("roomId = " + message.getRoomId());
        System.out.println("content = " + message.getContent());
        System.out.println("type = " + message.getType());

        // TODO: 4/25/24 sender 이름 설정 -> 유저 카운트의 enum 고정값으로 설정하면 될 듯
        // Websocket에 발행된 메시지를 redis로 발행(publish)
        chatService.sendChatMessage(message);
    }
}