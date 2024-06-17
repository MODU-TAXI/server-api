package com.modutaxi.api.domain.chat.controller;

import com.modutaxi.api.common.auth.CurrentMember;
import com.modutaxi.api.common.auth.jwt.JwtTokenProvider;
import com.modutaxi.api.domain.chat.dto.ChatResponseDto.ChatMappingResponse;
import com.modutaxi.api.domain.chat.dto.ChatResponseDto.DeleteResponse;
import com.modutaxi.api.domain.chat.dto.ChatResponseDto.EnterableResponse;
import com.modutaxi.api.domain.chatmessage.dto.ChatMessageRequestDto;
import com.modutaxi.api.domain.chat.ChatRoomMappingInfo;
import com.modutaxi.api.domain.chat.repository.RedisChatRoomRepositoryImpl;
import com.modutaxi.api.domain.chat.service.ChatService;
import com.modutaxi.api.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestController
@Tag(name = "pub/sub 관련 API")
public class ChatController {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisChatRoomRepositoryImpl redisChatRoomRepositoryImpl;
    private final ChatService chatService;


    // /pub/chat 으로 오는 메세지 핸들링
    @MessageMapping("/chat")
    public void sendMessage(ChatMessageRequestDto message) {

        String memberId = message.getMemberId();
        ChatRoomMappingInfo chatRoomMappingInfo = redisChatRoomRepositoryImpl.findChatInfoByMemberId(memberId);

        message.setSender(chatRoomMappingInfo.getNickname());
        message.setMemberId(memberId);
        message.setDateTime(LocalDateTime.now());


        // Websocket에 발행된 메시지를 redis로 발행(publish)
        chatService.sendChatMessage(message);
    }

    @Operation(summary = "해당 모집방에 참여 가능한 지")
    @GetMapping("/api/rooms/{roomId}/enterable")
    public ResponseEntity<EnterableResponse> chatRoomEnterPossible(@CurrentMember Member member, @PathVariable Long roomId){
        return ResponseEntity.ok(chatService.chatRoomEnterPossible(member, roomId));
    }

    @Operation(summary = "채팅 정보")
    @GetMapping("/api/chats/info")
    public ResponseEntity<ChatMappingResponse> getChatRoomMappingInfo(@CurrentMember Member member){
        return ResponseEntity.ok(chatService.getChatRoomInfo(member));
    }

    @Operation(summary = "퇴장!! 채팅방 매핑정보 삭제", description = "해당 로직은 채팅방 퇴장 시 이루어진다.")
    @DeleteMapping("/api/chats/info")
    public ResponseEntity<DeleteResponse> deleteChatRoomInfo(@CurrentMember Member member) {
        return ResponseEntity.ok(chatService.leaveRoomAndDeleteChatRoomInfo(member));
    }

    // TODO: 6/14/24 매핑정보 삭제 에러 이슈로 만든 TEST API

    @Operation(summary = "매핑정보 삭제", description = "매핑정보 삭제 에러 이슈로 만든 TEST API")
    @DeleteMapping("/api/mapping/info")
    public ResponseEntity<DeleteResponse> deleteMappingInfo(@CurrentMember Member member) {
        return ResponseEntity.ok(chatService.deleteMapping(member));
    }

}
