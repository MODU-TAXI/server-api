package com.modutaxi.api.domain.scheduledmessage.service;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.RoomErrorCode;
import com.modutaxi.api.domain.alarm.entity.AlarmType;
import com.modutaxi.api.domain.alarm.service.RegisterAlarmService;
import com.modutaxi.api.domain.chat.service.ChatService;
import com.modutaxi.api.domain.chatmessage.dto.ChatMessageRequestDto;
import com.modutaxi.api.domain.chatmessage.entity.MessageType;
import com.modutaxi.api.domain.room.entity.Room;
import com.modutaxi.api.domain.room.entity.RoomStatus;
import com.modutaxi.api.domain.room.repository.RoomRepository;
import com.modutaxi.api.domain.scheduledmessage.entity.ScheduledMessage;
import com.modutaxi.api.domain.scheduledmessage.entity.ScheduledMessageStatus;
import com.modutaxi.api.domain.scheduledmessage.mapper.ScheduledMessageMapper;
import com.modutaxi.api.domain.scheduledmessage.repository.ScheduledMessageRepository;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ScheduledMessageService {

    private static final String MATCHING_COMPLETE = "매칭완료 하시겠습니까?";
    private static final String CALL_TAXI = "택시 부르러 가볼까요?";
    private static final long BEFORE_FIVE_MINUTES = 300;
    private static final int NO_DELAY = 0;

    @Qualifier("taskScheduler")
    private final TaskScheduler taskScheduler;
    private final ChatService chatService;
    private final RegisterAlarmService registerAlarmService;
    private final RoomRepository roomRepository;
    private final ScheduledMessageRepository scheduledMessageRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void initScheduledMessage() {
        List<ScheduledMessage> scheduledMessageList =
            scheduledMessageRepository.findAllByStatus(ScheduledMessageStatus.PENDING);

        scheduledMessageList.forEach(iter -> {
            if (roomRepository.existsById(iter.getRoomId())) {
                // 메시지 새로 저장
                ScheduledMessage scheduledMessage = scheduledMessageRepository.save(
                    ScheduledMessageMapper
                        .toEntity(iter.getRoomId(), iter.getContent(), iter.getExecuteTime(),
                            iter.getType()));

                long delaySeconds = calculateDelaySeconds(LocalDateTime.now(),
                    iter.getExecuteTime());

                if (Objects.equals(scheduledMessage.getContent(), CALL_TAXI)) {
                    delaySeconds =
                        delaySeconds > BEFORE_FIVE_MINUTES + 20 ? delaySeconds - BEFORE_FIVE_MINUTES
                            : NO_DELAY;
                }

                Runnable task = chatBotNotice(iter.getRoomId(), iter.getContent(),
                    scheduledMessage.getId(), iter.getType());
                taskScheduler.schedule(task, Instant.now().plusSeconds(delaySeconds));
            }
            scheduledMessageRepository.delete(iter);
        });
        log.info("서버 시작 후 실행되지 않은 메시지가 예약되었습니다.");
    }

    @Transactional
    public void updateScheduledMessageStatus(Long scheduledMessageId) {
        ScheduledMessage scheduledMessage = scheduledMessageRepository.findById(scheduledMessageId)
            .orElseThrow();
        scheduledMessage.scheduledMessageStatusUpdate();
        scheduledMessageRepository.save(scheduledMessage);
    }

    private Runnable chatBotNotice(Long roomId, String content, Long scheduledMessageId,
        MessageType type) {
        return () -> {
            Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new BaseException(RoomErrorCode.EMPTY_ROOM));
            if (room.getRoomStatus().equals(RoomStatus.PROCEEDING)) {
                updateScheduledMessageStatus(scheduledMessageId);

                ChatMessageRequestDto message = new ChatMessageRequestDto(
                    roomId, type, content,
                    type.getSenderName(), room.getRoomManager().getId().toString(),
                    LocalDateTime.now(),
                    "");
                chatService.sendChatMessage(message);
                // 타입이 매칭 완료라면 알림 저장
                if (type == MessageType.MATCHING_COMPLETE) {
                    registerAlarmService.registerAlarm(AlarmType.MATCHING_COMPLETE, roomId,
                        room.getRoomManager().getId());
                }

                log.info("{}: {}", content, Thread.currentThread().getName());
            }
        };
    }

    @Transactional
    public void addTask(Long roomId, LocalDateTime departureTime) {
        long delaySeconds = calculateDelaySeconds(LocalDateTime.now(), departureTime);

        ScheduledMessage matchingCompleteMessage =
            ScheduledMessageMapper.toEntity(roomId, MATCHING_COMPLETE, departureTime,
                MessageType.MATCHING_COMPLETE);
        scheduledMessageRepository.save(matchingCompleteMessage);

        Runnable matchingModal = chatBotNotice(roomId, MATCHING_COMPLETE,
            matchingCompleteMessage.getId(), MessageType.MATCHING_COMPLETE);
        taskScheduler.schedule(matchingModal, Instant.now().plusSeconds(delaySeconds));

        ScheduledMessage callTaxiMessage = ScheduledMessageMapper.toEntity(roomId, CALL_TAXI,
            departureTime, MessageType.CALL_TAXI);
        scheduledMessageRepository.save(callTaxiMessage);

        Runnable callTaxiTask = chatBotNotice(roomId, CALL_TAXI, callTaxiMessage.getId(),
            MessageType.CALL_TAXI);
        delaySeconds =
            delaySeconds > BEFORE_FIVE_MINUTES + 20 ? delaySeconds - BEFORE_FIVE_MINUTES : NO_DELAY;
        taskScheduler.schedule(callTaxiTask, Instant.now().plusSeconds(delaySeconds));
        log.info("{}번 방에 예약메시지가 설정되었습니다.", roomId);
    }

    private long calculateDelaySeconds(LocalDateTime now, LocalDateTime targetDateTime) {
        Duration duration = Duration.between(now, targetDateTime);
        return Math.max(NO_DELAY, duration.getSeconds());
    }
}