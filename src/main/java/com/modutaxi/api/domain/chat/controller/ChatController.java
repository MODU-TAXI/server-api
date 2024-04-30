package com.modutaxi.api.domain.chat.controller;

import com.modutaxi.api.common.auth.CurrentMember;
import com.modutaxi.api.common.auth.jwt.JwtTokenProvider;
import com.modutaxi.api.domain.chatmessage.dto.ChatMessageRequestDto;
import com.modutaxi.api.domain.chatmessage.entity.ChatMessage;
import com.modutaxi.api.domain.chatmessage.repository.ChatMessageRepository;
import com.modutaxi.api.domain.chat.ChatRoomMappingInfo;
import com.modutaxi.api.domain.chat.repository.ChatRoomRepository;
import com.modutaxi.api.domain.chat.service.ChatService;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.room.entity.Room;
import com.modutaxi.api.domain.room.repository.RoomRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Tag(name = "pub/sub 관련 API")
public class ChatController {

    private final JwtTokenProvider jwtTokenProvider;
    private final ChatRoomRepository chatRoomRepository;
    private final RoomRepository roomRepository;
    private final ChatService chatService;
    private final ChatMessageRepository chatMessageRepository;

    // /pub/chat 으로 오는 메세지 핸들링
    @MessageMapping("/chat")
    public void sendMessage(ChatMessageRequestDto message) {
        // TODO: apic 테스트를 위해 헤더를 ChatMessageRequestDto 객체에 받음. @Header("token") String token 파라미터로 추가해야함
        String memberId = jwtTokenProvider.getMemberIdByAccessToken(message.getToken());
        ChatRoomMappingInfo chatRoomMappingInfo = chatRoomRepository.findChatInfoByMemberId(memberId);
        //닉네임 설정
        message.setSender(chatRoomMappingInfo.getNickname());

        // Websocket에 발행된 메시지를 redis로 발행(publish)
        chatService.sendChatMessage(message);
        Room room = roomRepository.findById(Long.valueOf(chatRoomMappingInfo.getRoomId())).orElseThrow();

        //메세지 리퍼지토리에 저장
        chatMessageRepository.save(ChatMessage.toEntity(message, room));
    }

    @Operation(summary = "해당 모집방에 참여 가능한 지")
    @GetMapping("/chat/{roomId}")
    public ResponseEntity<String> chatRoomEnterPossible(@CurrentMember Member member, @PathVariable Long roomId){
        return ResponseEntity.ok(chatService.chatRoomEnterPossible(member, roomId));
    }

    @Operation(summary = "채팅 정보")
    @GetMapping("/chat-info")
    public ResponseEntity<Long> getChatRoomMappingInfo(@CurrentMember Member member){
        return ResponseEntity.ok(chatService.getChatRoomInfo(member));
    }

    @Operation(summary = "채팅방 매핑정보 삭제", description = "해당 로직은 채팅방 퇴장 시 이루어진다.")
    @DeleteMapping("/chat-info")
    public ResponseEntity<String> deleteChatRoomInfo(@CurrentMember Member member) {
        return ResponseEntity.ok(chatService.deleteChatRoomInfo(member));
    }



}
