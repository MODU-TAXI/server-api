package com.modutaxi.api.domain.chatmessage.entity;

import com.modutaxi.api.common.entity.BaseTime;
import com.modutaxi.api.domain.chatmessage.dto.ChatMessageRequestDto;
import com.modutaxi.api.domain.room.entity.Room;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;

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
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Room room;

    private MessageType messageType;

    private String content;

    private String sender;

    private String memberId;

    private LocalDateTime dateTime;
}