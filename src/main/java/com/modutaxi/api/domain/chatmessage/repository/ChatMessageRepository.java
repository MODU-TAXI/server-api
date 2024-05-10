package com.modutaxi.api.domain.chatmessage.repository;

import com.modutaxi.api.domain.chatmessage.entity.ChatMessage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

//개발의 편의성 때문에 Jpa로 구현하였지만 몽고로 바꿀 예정
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findAllByRoomIdOrderByCreatedAtAsc(Long roomId);

    void deleteAllByRoomId(Long roomId);
}