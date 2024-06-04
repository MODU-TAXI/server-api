package com.modutaxi.api.domain.chat.service;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.RoomErrorCode;
import com.modutaxi.api.domain.chatmessage.dto.ChatMessageRequestDto;
import com.modutaxi.api.domain.chatmessage.entity.MessageType;
import com.modutaxi.api.domain.chatmessage.mapper.ChatMessageMapper;
import com.modutaxi.api.domain.chatmessage.repository.ChatMessageRepository;
import com.modutaxi.api.domain.room.entity.Room;
import com.modutaxi.api.domain.room.repository.RoomRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

@AllArgsConstructor
@Slf4j
@Service
public class ChatSchedulerService {

    private final TaskScheduler taskScheduler;
    private final ChatService chatService;

    // TODO: 6/4/24 전체적으로 메세지 저장로직을 전송 로직과 묶기
    private final ChatMessageRepository chatMessageRepository;
    private final RoomRepository roomRepository;

    private static final long BEFORE_FIVE_MINUTES = 300;
    private static final int NO_DELAY = 0;

    private Runnable chatBotNotice(Long roomId, String content) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new BaseException(RoomErrorCode.EMPTY_ROOM));
        return () -> {
            ChatMessageRequestDto message = new ChatMessageRequestDto(
                Long.valueOf(roomId), MessageType.CHAT_BOT, content,
                "모두의택시", room.getRoomManager().getId().toString(), LocalDateTime.now());
            chatService.sendChatMessage(message);
            chatMessageRepository.save(ChatMessageMapper.toEntity(message, room));
            log.info("{}: {}", content, Thread.currentThread().getName());
        };
    }


    public void addTask(Long roomId, LocalDateTime departureTime) {
        long delaySeconds = calculateDelaySeconds(LocalDateTime.now(), departureTime);

        Runnable matchingModal = chatBotNotice(roomId, "매칭완료 하시겠습니까?");
        taskScheduler.schedule(matchingModal, Instant.now().plusSeconds(delaySeconds));

        Runnable callTaxiTask = chatBotNotice(roomId, "택시 부르러 가볼까요?");
        delaySeconds = delaySeconds > BEFORE_FIVE_MINUTES + 20 ? delaySeconds - BEFORE_FIVE_MINUTES : NO_DELAY;
        taskScheduler.schedule(callTaxiTask, Instant.now().plusSeconds(delaySeconds));
    }

    private long calculateDelaySeconds(LocalDateTime now, LocalDateTime targetDateTime) {
        Duration duration = Duration.between(now, targetDateTime);
        return Math.max(NO_DELAY, duration.getSeconds());
    }
}
