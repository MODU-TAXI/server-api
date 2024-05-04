package com.modutaxi.api.domain.chatmessage.controller;

import com.modutaxi.api.common.auth.CurrentMember;
import com.modutaxi.api.domain.chatmessage.dto.ChatMessageResponseDto.ChatMessageResponseList;
import com.modutaxi.api.domain.chatmessage.dto.ChatMessageResponseDto.DeleteResponse;
import com.modutaxi.api.domain.chatmessage.service.ChatMessageService;
import com.modutaxi.api.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "채팅 메세지 API")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @Operation(summary = "오갔던 채팅 전부 조회")
    @GetMapping("/api/chats/rooms/{roomId}/messages")
    public ResponseEntity<ChatMessageResponseList> getChatMessage(@CurrentMember Member member,
                                                                  @PathVariable Long roomId) {
        return ResponseEntity.ok(chatMessageService.chatMessageResponseDtoList(member, roomId));
    }

    @Operation(summary = "오갔던 채팅 메세지 전부 삭제")
    @DeleteMapping("/api/chats/rooms/{roomId}/messages")
    public ResponseEntity<DeleteResponse> deleteChatMessage(@PathVariable Long roomId){
        return ResponseEntity.ok(chatMessageService.deleteChatMessage(roomId));
    }

}
