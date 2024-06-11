package com.modutaxi.api.domain.scheduledmessage.service;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.RoomErrorCode;
import com.modutaxi.api.domain.chat.service.ChatService;
import com.modutaxi.api.domain.chatmessage.dto.ChatMessageRequestDto;
import com.modutaxi.api.domain.chatmessage.entity.MessageType;
import com.modutaxi.api.domain.room.entity.Room;
import com.modutaxi.api.domain.room.repository.RoomRepository;
import com.modutaxi.api.domain.scheduledmessage.repository.ScheduledMessageRepository;
import com.modutaxi.api.domain.scheduledmessage.entity.ScheduledMessage;
import com.modutaxi.api.domain.scheduledmessage.entity.ScheduledMessageStatus;
import com.modutaxi.api.domain.scheduledmessage.mapper.ScheduledMessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class ScheduledMessageService {

    private final TaskScheduler taskScheduler;
    private final ChatService chatService;
    private final RoomRepository roomRepository;
    private final ScheduledMessageRepository scheduledMessageRepository;
    private static final String MATCHING_COMPLETE = "매칭완료 하시겠습니까?";
    private static final String CALL_TAXI = "택시 부르러 가볼까요?";

    private static final long BEFORE_FIVE_MINUTES = 300;
    private static final int NO_DELAY = 0;

    @EventListener(ApplicationReadyEvent.class)
    public void initScheduledMessage() {
        List<ScheduledMessage> scheduledMessageList =
            scheduledMessageRepository.findAllByStatus(ScheduledMessageStatus.PENDING);

        scheduledMessageList.forEach(iter -> {
            if (roomRepository.existsById(iter.getRoomId())) {
                // 메시지 새로 저장
                ScheduledMessage scheduledMessage = scheduledMessageRepository.save(ScheduledMessageMapper
                    .toEntity(iter.getRoomId(), iter.getContent(), iter.getExecuteTime()));

                long delaySeconds = calculateDelaySeconds(LocalDateTime.now(), iter.getExecuteTime());

                if (Objects.equals(scheduledMessage.getContent(), CALL_TAXI)) {
                    delaySeconds = delaySeconds > BEFORE_FIVE_MINUTES + 20 ? delaySeconds - BEFORE_FIVE_MINUTES : NO_DELAY;
                }

                Runnable task = chatBotNotice(iter.getRoomId(), iter.getContent(), scheduledMessage.getId());
                taskScheduler.schedule(task, Instant.now().plusSeconds(delaySeconds));
            }
            scheduledMessageRepository.delete(iter);
        });
        log.info("서버 시작 후 실행되지 않은 메시지가 예약되었습니다.");
    }

    @Transactional
    public void updateScheduledMessageStatus(Long scheduledMessageId) {
        ScheduledMessage scheduledMessage = scheduledMessageRepository.findById(scheduledMessageId).orElseThrow();
        scheduledMessage.scheduledMessageStatusUpdate();
        scheduledMessageRepository.save(scheduledMessage);
    }

    private Runnable chatBotNotice(Long roomId, String content, Long scheduledMessageId) {
        return () -> {
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new BaseException(RoomErrorCode.EMPTY_ROOM));
            updateScheduledMessageStatus(scheduledMessageId);

            ChatMessageRequestDto message = new ChatMessageRequestDto(
                Long.valueOf(roomId), MessageType.CHAT_BOT, content,
                "모두의택시", room.getRoomManager().getId().toString(), LocalDateTime.now(), "");
            chatService.sendChatMessage(message);
            log.info("{}: {}", content, Thread.currentThread().getName());
        };
    }

    @Transactional
    public void addTask(Long roomId, LocalDateTime departureTime) {
        long delaySeconds = calculateDelaySeconds(LocalDateTime.now(), departureTime);

        ScheduledMessage matchingCompleteMessage =
            ScheduledMessageMapper.toEntity(roomId, MATCHING_COMPLETE, departureTime);
        scheduledMessageRepository.save(matchingCompleteMessage);

        Runnable matchingModal = chatBotNotice(roomId, MATCHING_COMPLETE, matchingCompleteMessage.getId());
        taskScheduler.schedule(matchingModal, Instant.now().plusSeconds(delaySeconds));

        ScheduledMessage callTaxiMessage = ScheduledMessageMapper.toEntity(roomId, CALL_TAXI, departureTime);
        scheduledMessageRepository.save(callTaxiMessage);

        Runnable callTaxiTask = chatBotNotice(roomId, CALL_TAXI, callTaxiMessage.getId());
        delaySeconds = delaySeconds > BEFORE_FIVE_MINUTES + 20 ? delaySeconds - BEFORE_FIVE_MINUTES : NO_DELAY;
        taskScheduler.schedule(callTaxiTask, Instant.now().plusSeconds(delaySeconds));
        log.info("{}번 방에 예약메시지가 설정되었습니다.", roomId);
    }

    private long calculateDelaySeconds(LocalDateTime now, LocalDateTime targetDateTime) {
        Duration duration = Duration.between(now, targetDateTime);
        return Math.max(NO_DELAY, duration.getSeconds());
    }
}