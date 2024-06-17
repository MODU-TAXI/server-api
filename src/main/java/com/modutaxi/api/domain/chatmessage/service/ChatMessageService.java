package com.modutaxi.api.domain.chatmessage.service;

import com.modutaxi.api.domain.chatmessage.dto.ChatMessageResponseDto.ChatMessageResponse;
import com.modutaxi.api.domain.chatmessage.dto.ChatMessageResponseDto.ChatMessageResponseList;
import com.modutaxi.api.domain.chatmessage.dto.ChatMessageResponseDto.DeleteResponse;
import com.modutaxi.api.domain.chatmessage.entity.ChatMessage;
import com.modutaxi.api.domain.chatmessage.mapper.ChatMessageMapper;
import com.modutaxi.api.domain.chatmessage.repository.ChatMessageRepository;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.member.repository.MemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.modutaxi.api.common.constants.ServerConstants.BASIC_PROFILE_IMAGE_URL;

@Service
@AllArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private static final int CHAT_MASSAGE = 0;
    private static final int IMAGE_URL = 1;


    public ChatMessageResponseList chatMessageResponseDtoList(Long roomId) {
        List<Object[]> chatMessageList =
            chatMessageRepository.findAllByRoomIdWithMemberImageUrl(roomId);

        List<ChatMessageResponse> chatMessageResponseList =
            chatMessageList.stream()
                .map(iter -> {
                    ChatMessage cm = (ChatMessage) iter[CHAT_MASSAGE];
                    String imageUrl = (String) iter[IMAGE_URL];
                    if(imageUrl == null) imageUrl = BASIC_PROFILE_IMAGE_URL;
                    return ChatMessageMapper.toDto(cm, imageUrl);
                })
                .toList();
        return new ChatMessageResponseList(chatMessageResponseList);
    }

    public DeleteResponse deleteChatMessage(Long roomId) {
        chatMessageRepository.deleteAllByRoomId(roomId);
        return new DeleteResponse(true);
    }
}
