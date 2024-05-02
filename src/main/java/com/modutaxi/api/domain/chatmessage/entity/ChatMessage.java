package com.modutaxi.api.domain.chatmessage.entity;

import com.modutaxi.api.common.entity.BaseTime;
import com.modutaxi.api.domain.chatmessage.dto.ChatMessageRequestDto;
import com.modutaxi.api.domain.room.entity.Room;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PersistenceUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ChatMessage extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    private MessageType messageType;

    private String content;

    private String sender;

    private String memberId;

    public static ChatMessage toEntity(ChatMessageRequestDto messageRequestDto, Room room){
        return ChatMessage.builder()
                .room(room)
                .messageType(messageRequestDto.getType())
                .content(messageRequestDto.getContent())
                .sender(messageRequestDto.getSender())
                .memberId(messageRequestDto.getMemberId())
                .build();
    }
}