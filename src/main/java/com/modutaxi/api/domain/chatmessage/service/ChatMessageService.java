package com.modutaxi.api.domain.chatmessage.service;

import com.modutaxi.api.domain.chatmessage.dto.ChatMessageResponseDto.ChatMessageResponseList;
import com.modutaxi.api.domain.chatmessage.dto.ChatMessageResponseDto.DeleteResponse;
import com.modutaxi.api.domain.chatmessage.entity.ChatMessage;
import com.modutaxi.api.domain.chatmessage.mapper.ChatMessageMapper;
import com.modutaxi.api.domain.chatmessage.repository.ChatMessageRepository;
import com.modutaxi.api.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;

    public ChatMessageResponseList chatMessageResponseDtoList(Member member, Long roomId) {
        List<ChatMessage> chatMessageList = chatMessageRepository.findAllByRoomIdOrderByCreatedAtAsc(roomId);
        return new ChatMessageResponseList(
            chatMessageList.stream()
                .map(ChatMessageMapper::toDto)
                .toList());
    }

    public DeleteResponse deleteChatMessage(Long roomId) {
        chatMessageRepository.deleteAllByRoomId(roomId);
        return new DeleteResponse(true);
    }
}
