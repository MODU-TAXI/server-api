package com.modutaxi.api.domain.chat.service;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.RoomErrorCode;
import com.modutaxi.api.domain.chatmessage.dto.ChatMessageRequestDto;
import com.modutaxi.api.domain.chatmessage.entity.MessageType;
import com.modutaxi.api.domain.room.entity.Room;
import com.modutaxi.api.domain.room.repository.RoomRepository;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Slf4j
@Service
public class ChatSchedulerService {

    private static final long BEFORE_FIVE_MINUTES = 300;
    private static final int NO_DELAY = 0;
    private final TaskScheduler taskScheduler;
    private final ChatService chatService;
    private final RoomRepository roomRepository;

    private Runnable chatBotNotice(Long roomId, String content, MessageType type) {
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new BaseException(RoomErrorCode.EMPTY_ROOM));
        return () -> {
            ChatMessageRequestDto message = new ChatMessageRequestDto(
                roomId, type, content, type.getSenderName(),
                room.getRoomManager().getId().toString(), LocalDateTime.now());
            chatService.sendChatMessage(message);
            log.info("{}: {}", content, Thread.currentThread().getName());
        };
    }


    public void addTask(Long roomId, LocalDateTime departureTime) {
        long delaySeconds = calculateDelaySeconds(LocalDateTime.now(), departureTime);

        String content = "팀원들이 다 모였다면,\n'매칭 완료'를 눌러주세요!";
        Runnable matchingModal = chatBotNotice(roomId, content, MessageType.MATCHING_COMPLETE);
        taskScheduler.schedule(matchingModal, Instant.now().plusSeconds(delaySeconds));

        Runnable callTaxiTask = chatBotNotice(roomId, "택시를 불러볼까요?", MessageType.CALL_TAXI);
        delaySeconds =
            delaySeconds > BEFORE_FIVE_MINUTES + 20 ? delaySeconds - BEFORE_FIVE_MINUTES : NO_DELAY;
        taskScheduler.schedule(callTaxiTask, Instant.now().plusSeconds(delaySeconds));
    }

    private long calculateDelaySeconds(LocalDateTime now, LocalDateTime targetDateTime) {
        Duration duration = Duration.between(now, targetDateTime);
        return Math.max(NO_DELAY, duration.getSeconds());
    }
}
