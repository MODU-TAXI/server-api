package com.modutaxi.api.domain.chatmessage.repository;

import com.modutaxi.api.domain.chatmessage.entity.ChatMessage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("SELECT cm, m.imageUrl " +
        "FROM ChatMessage cm " +
        "JOIN Member m ON cm.memberId = m.id " +
        "WHERE cm.roomId = :roomId " +
        "ORDER BY cm.createdAt ASC")
    List<Object[]> findAllByRoomIdWithMemberImageUrl(Long roomId);

    void deleteAllByRoomId(Long roomId);
}