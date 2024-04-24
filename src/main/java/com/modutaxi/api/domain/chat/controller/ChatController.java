package com.modutaxi.api.domain.chat.controller;

import com.modutaxi.api.common.auth.CurrentMember;
import com.modutaxi.api.common.auth.jwt.JwtTokenProvider;
import com.modutaxi.api.domain.chat.MessageType;
import com.modutaxi.api.domain.chat.dto.ChatMessage;
import com.modutaxi.api.domain.chat.repository.ChatRoomRepository;
import com.modutaxi.api.domain.chat.service.ChatService;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ChatController {
    private final JwtTokenProvider jwtTokenProvider;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final ChatService chatService;
    @MessageMapping("/chat")
    public void sendMessage(ChatMessage message, @Header("token") String token){
        System.out.println("왔니?");
        String memberId = jwtTokenProvider.getMemberIdByAccessToken(token);
        Member member = memberRepository.findByIdAndStatusTrue(Long.valueOf(memberId)).orElseThrow();
        System.out.println("member = " + member.getId());
        System.out.println("sender = " + message.getSender());
        System.out.println("roomId = " + message.getRoomId());
        System.out.println("content = " + message.getContent());
        System.out.println("type = " + message.getType());


//        // 로그인 회원 정보로 대화명 설정
//        message.setSender(member.getName());
//        // Websocket에 발행된 메시지를 redis로 발행(publish)
        chatService.sendChatMessage(message);
    }
}