package com.modutaxi.api.domain.chat.controller;

import com.modutaxi.api.common.auth.CurrentMember;
import com.modutaxi.api.common.auth.jwt.JwtTokenProvider;
import com.modutaxi.api.domain.chat.dto.ChatResponseDto.ChatMappingResponse;
import com.modutaxi.api.domain.chat.dto.ChatResponseDto.DeleteResponse;
import com.modutaxi.api.domain.chat.dto.ChatResponseDto.EnterableResponse;
import com.modutaxi.api.domain.chatmessage.dto.ChatMessageRequestDto;
import com.modutaxi.api.domain.chatmessage.mapper.ChatMessageMapper;
import com.modutaxi.api.domain.chatmessage.repository.ChatMessageRepository;
import com.modutaxi.api.domain.chat.ChatRoomMappingInfo;
import com.modutaxi.api.domain.chat.repository.RedisChatRoomRepositoryImpl;
import com.modutaxi.api.domain.chat.service.ChatService;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.room.entity.Room;
import com.modutaxi.api.domain.room.repository.RoomRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
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
    private final RoomRepository roomRepository;
    private final ChatService chatService;
    private final ChatMessageRepository chatMessageRepository;


    // /pub/chat 으로 오는 메세지 핸들링
    @MessageMapping("/chat")
    public void sendMessage(ChatMessageRequestDto message, @Header("token") String token ) {

        String memberId = jwtTokenProvider.getMemberIdByToken(token);
        ChatRoomMappingInfo chatRoomMappingInfo = redisChatRoomRepositoryImpl.findChatInfoByMemberId(memberId);

        message.setSender(chatRoomMappingInfo.getNickname());
        message.setMemberId(memberId);
        message.setDateTime(LocalDateTime.now());


        // Websocket에 발행된 메시지를 redis로 발행(publish)
        chatService.sendChatMessage(message);

        // TODO: 5/2/24 mongoDB로 변경해야함 -> 시간 비교해서 리팩터링
        Room room = roomRepository.findById(Long.valueOf(chatRoomMappingInfo.getRoomId())).orElseThrow();

        //메세지 리퍼지토리에 저장
        chatMessageRepository.save(ChatMessageMapper.toEntity(message, room));
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



}
