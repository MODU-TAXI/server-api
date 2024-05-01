package com.modutaxi.api.domain.chatmessage.service;

import com.modutaxi.api.domain.chatmessage.dto.ChatMessageResponseDto;
import com.modutaxi.api.domain.chatmessage.entity.ChatMessage;
import com.modutaxi.api.domain.chatmessage.repository.ChatMessageRepository;
import com.modutaxi.api.domain.member.entity.Member;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    public List<ChatMessageResponseDto> chatMessageResponseDtoList(Member member, Long roomId){
        List<ChatMessage> chatMessageList = chatMessageRepository.findAllByRoomIdOrderByCreatedAtAsc(roomId);
        return chatMessageList.stream()
                .map(ChatMessageResponseDto::entityToDto)
                .toList();
    }

    public String deleteChatMessage(Long roomId){
        chatMessageRepository.deleteAllByRoomId(roomId);
        return "싸그리 다 삭제~ 응~";
    }
}
