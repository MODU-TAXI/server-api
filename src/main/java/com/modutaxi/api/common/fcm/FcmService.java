package com.modutaxi.api.common.fcm;

import static com.modutaxi.api.common.constants.ServerConstants.FCM_DEFAULT_TOKEN;

import com.google.firebase.FirebaseException;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.google.gson.Gson;
import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.ChatErrorCode;
import com.modutaxi.api.common.exception.errorcode.MemberErrorCode;
import com.modutaxi.api.domain.chatmessage.dto.ChatMessageRequestDto;
import com.modutaxi.api.domain.chatmessage.entity.MessageType;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.member.repository.MemberRepository;
import com.modutaxi.api.domain.participant.entity.Participant;
import com.modutaxi.api.domain.participant.repository.ParticipantRepository;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    private final RedisFcmRepositoryImpl redisFcmRepository;
    private final ParticipantRepository participantRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void subscribe(Long memberId, Long roomId) {
        String fcmToken = validateAndGetFcmToken(memberId);
        if (!(fcmToken == null || Objects.equals(fcmToken, "") || fcmToken.isEmpty()
            || fcmToken.isBlank())){
            fcmToken = FCM_DEFAULT_TOKEN;
            Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new BaseException(MemberErrorCode.EMPTY_MEMBER)
            );
            member.changeFcmToken(fcmToken);
        }
        try {
            FirebaseMessaging.getInstance()
                .subscribeToTopic(
                    Collections.singletonList(fcmToken), Long.toString(roomId));
            log.info("FCM SUBSCRIBE");
        } catch (FirebaseException e) {
            log.error("FAIL FCM SUBSCRIBE");
            throw new BaseException(ChatErrorCode.FAIL_FCM_SUBSCRIBE);
        }
    }

    public void unsubscribe(Long memberId, Long roomId) {
        String fcmToken = validateAndGetFcmToken(memberId);
        try {
            FirebaseMessaging.getInstance()
                .unsubscribeFromTopic(
                    Collections.singletonList(fcmToken), Long.toString(roomId));
        } catch (FirebaseException e) {
            throw new BaseException(ChatErrorCode.FAIL_FCM_UNSUBSCRIBE);
        }
    }

    public void send(Message message) {
        try {
            FirebaseMessaging.getInstance().send(message);
            Gson gson = new Gson();
            String fcmMessageJson = gson.toJson(message);
            log.info("FCM 메시지: " + fcmMessageJson);
        } catch (FirebaseException e) {
            log.error(ChatErrorCode.FAIL_SEND_MESSAGE.getMessage());
            log.error("message: {}", e.getMessage());
            log.error("localizedMessage: {}", e.getLocalizedMessage());
            log.error("cause: {}", e.getCause().getMessage());
            log.error("message: {}", message.toString());
//            throw new BaseException(ChatErrorCode.FAIL_SEND_MESSAGE);
        }
    }

    /**
     * 채팅 메세지 전송(일반 채팅 & 채팅방 퇴장 & 채팅방 입장)
     */
    public void sendChatMessage(ChatMessageRequestDto chatMessageRequestDto) {
        MessageType messageType = chatMessageRequestDto.getType();

        //일반적인 채팅, 참가, 퇴장, 이미지 전송
        if (messageType.equals(MessageType.CHAT) || messageType.equals(MessageType.JOIN)
            || messageType.equals(MessageType.LEAVE) || messageType.equals(MessageType.IMAGE)) {
            sendMessageExcludeMe(chatMessageRequestDto);

        } else if (messageType.equals(MessageType.CHAT_BOT)) {
            sendMessageForEveryone(chatMessageRequestDto);

        } else if (messageType.equals(MessageType.CALL_TAXI)) {
            sendMessageForEveryone(chatMessageRequestDto);

        } else if (messageType.equals(MessageType.MATCHING_COMPLETE)) {
            sendMessageForManager(chatMessageRequestDto);

        } else if (messageType.equals(MessageType.PAYMENT_REQUEST)) {
            sendMessageForManager(chatMessageRequestDto);

        } else if (messageType.equals(MessageType.PAYMENT_REQUEST_COMPLETE)) {
            sendMessageExcludeMe(chatMessageRequestDto);

        } else if (messageType.equals(MessageType.PAYMENT_COMPLETE)) {
            sendMessageForEveryone(chatMessageRequestDto);

        } else if (messageType.equals(MessageType.PAYMENT_ALL_COMPLETE)) {
            sendMessageForManager(chatMessageRequestDto);

        } else if (messageType.equals(MessageType.PAYMENT_RE_REQUEST)) {
            sendMessageForEveryone(chatMessageRequestDto);
        }
    }

    private void sendMessageForManager(ChatMessageRequestDto chatMessageRequestDto) {
        String fcmToken = redisFcmRepository.findById(
            Long.valueOf(chatMessageRequestDto.getMemberId()));
        if (!(fcmToken == null || Objects.equals(fcmToken, "") || fcmToken.isEmpty()
            || fcmToken.isBlank())) {
            Message message = Message.builder()
                .putData("roomId", Long.toString(chatMessageRequestDto.getRoomId()))
                .putData("messageType", chatMessageRequestDto.getType().toString())
                .putData("message", chatMessageRequestDto.getContent())
                .putData("sender", chatMessageRequestDto.getSender())
                .putData("memberId", chatMessageRequestDto.getMemberId())
                .putData("dateTime", chatMessageRequestDto.getDateTime().toString())
                .setToken(fcmToken)
                .setNotification(Notification.builder()
                    .setTitle("모두의택시")
                    .setBody(chatMessageRequestDto.getContent())
                    .build())
                // APNS 설정 추가
                .setApnsConfig(ApnsConfig.builder()
                    .setAps(Aps.builder()
                        .setContentAvailable(true)
                        .setSound("default")
                        .build())
                    .build())
                .build();
            send(message);
        }
    }

    private void sendMessageForEveryone(ChatMessageRequestDto chatMessageRequestDto) {
        Message message = Message.builder()
            .putData("roomId", Long.toString(chatMessageRequestDto.getRoomId()))
            .putData("messageType", chatMessageRequestDto.getType().toString())
            .putData("message", chatMessageRequestDto.getContent())
            .putData("sender", chatMessageRequestDto.getSender())
            .putData("memberId", chatMessageRequestDto.getMemberId())
            .putData("dateTime", chatMessageRequestDto.getDateTime().toString())
            .setTopic(chatMessageRequestDto.getRoomId().toString())
            .setNotification(Notification.builder()
                .setTitle("모두의택시")
                .setBody(chatMessageRequestDto.getContent())
                .build())
            // APNS 설정 추가
            .setApnsConfig(ApnsConfig.builder()
                .setAps(Aps.builder()
                    .setContentAvailable(true)
                    .setSound("default")
                    .build())
                .build())
            .build();
        send(message);
    }

    private void sendMessageExcludeMe(ChatMessageRequestDto chatMessageRequestDto) {
        List<Participant> participantList =
            participantRepository.findAllByRoomId(chatMessageRequestDto.getRoomId());
        participantList.stream()
            .filter(participant -> !participant.getMember().getId()
                .equals(Long.valueOf(chatMessageRequestDto.getMemberId())))
            .forEach(participant -> {
                String fcmToken = redisFcmRepository.findById(participant.getMember().getId());
                if (!(fcmToken == null || Objects.equals(fcmToken, "") || fcmToken.isEmpty()
                    || fcmToken.isBlank())) {
                    Message message = Message.builder()
                        .putData("roomId", Long.toString(chatMessageRequestDto.getRoomId()))
                        .putData("messageType", chatMessageRequestDto.getType().toString())
                        .putData("message", chatMessageRequestDto.getContent())
                        .putData("sender", chatMessageRequestDto.getSender())
                        .putData("memberId", chatMessageRequestDto.getMemberId())
                        .putData("dateTime", chatMessageRequestDto.getDateTime().toString())
                        .setToken(fcmToken)
                        .setNotification(Notification.builder()
                            .setTitle(chatMessageRequestDto.getSender() + "님")
                            .setBody(chatMessageRequestDto.getType().equals(MessageType.IMAGE)
                                ? "사진" : chatMessageRequestDto.getContent())
                            .setImage(chatMessageRequestDto.getImageUrl())
                            .build())
                        // APNS 설정 추가
                        .setApnsConfig(ApnsConfig.builder()
                            .setAps(Aps.builder()
                                .setContentAvailable(true)
                                .setSound("default")
                                .build())
                            .build())
                        .build();
                    send(message);
                }
            });
    }

    /**
     * 방 정보 변경되었을 때(전체 다)
     */
    public void sendUpdateRoomInfo(Long managerId, Long roomId) {
        Message message = Message.builder()
            .putData("messageType", "ROOM_UPDATE")
            .putData("roomId", Long.toString(roomId))
            .putData("message", "참여해 있는 방 정보가 업데이트 되었습니다.")
            .putData("managerId", managerId.toString())
            .setTopic(roomId.toString())
            .setNotification(Notification.builder()
                .setTitle("모두의택시")
                .setBody("참여해 있는 방 정보가 업데이트 되었습니다.")
                .build())
            // APNS 설정 추가
            .setApnsConfig(ApnsConfig.builder()
                .setAps(Aps.builder()
                    .setContentAvailable(true)
                    .setSound("default")
                    .build())
                .build())
            .build();
        send(message);
    }

    /**
     * 새로운 참여자의 참요 요청 알림, 방장에게
     */
    public void sendNewParticipant(Member roomManager, String roomId, String nickName) {
        String fcmToken = roomManager.getFcmToken();
        if (!(fcmToken == null || Objects.equals(fcmToken, "") || fcmToken.isEmpty()
            || fcmToken.isBlank())) {
            Message message = Message.builder()
                .putData("messageType", "PARTICIPATE_REQUEST")
                .putData("message", nickName + "님이 매칭 대기중이에요!")
                .putData("roomId", roomId)
                .setToken(fcmToken)
                .setNotification(Notification.builder()
                    .setTitle("모두의택시")
                    .setBody(nickName + "님이 매칭 대기중이에요!")
                    .build())
                // APNS 설정 추가
                .setApnsConfig(ApnsConfig.builder()
                    .setAps(Aps.builder()
                        .setContentAvailable(true)
                        .setSound("default")
                        .build())
                    .build())
                .build();
            send(message);
        }
    }

    /**
     * 방 삭제 되었을 때
     */
    public void sendDeleteRoom(Long managerId, Long roomId) {
        Message message = Message.builder()
            .putData("roomId", Long.toString(roomId))
            .putData("messageType", "ROOM_DELETE")
            .putData("message", "방이 삭제 되었습니다.")
            .putData("managerId", managerId.toString())
            .setTopic(roomId.toString())
            .setNotification(Notification.builder()
                .setTitle("모두의택시")
                .setBody("방이 삭제 되었습니다.")
                .build())
            // APNS 설정 추가
            .setApnsConfig(ApnsConfig.builder()
                .setAps(Aps.builder()
                    .setContentAvailable(true)
                    .setSound("default")
                    .build())
                .build())
            .build();
        send(message);
    }

    /**
     * 사용자가 매칭수락 받았을 때 알림
     */
    public void sendPermitParticipate(Member participant, String roomId) {
        String fcmToken = redisFcmRepository.findById(participant.getId());
        if (!(fcmToken == null || Objects.equals(fcmToken, "") || fcmToken.isEmpty()
            || fcmToken.isBlank())) {
            Message message = Message.builder()
                .putData("messageType", "MATCHING_SUCCESS")
                .putData("message", "매칭이 수락되었어요! 지금 바로 채팅을 시작하세요.")
                .putData("roomId", roomId)
                .setToken(fcmToken)
                .setNotification(Notification.builder()
                    .setTitle("모두의택시")
                    .setBody("매칭이 수락되었어요! 지금 바로 채팅을 시작하세요.")
                    .build())
                // APNS 설정 추가
                .setApnsConfig(ApnsConfig.builder()
                    .setAps(Aps.builder()
                        .setContentAvailable(true)
                        .setSound("default")
                        .build())
                    .build())
                .build();
            send(message);
        }
    }

    public String validateAndGetFcmToken(Long memberId) {
        // 캐싱 조회 시도
        String fcmToken = redisFcmRepository.findById(memberId);
        if (fcmToken == null || Objects.equals(fcmToken, "") || fcmToken.isEmpty()) {
            Member member = memberRepository.findByIdAndStatusTrue(memberId)
                .orElseThrow(() -> new BaseException(MemberErrorCode.EMPTY_MEMBER));
            fcmToken = member.getFcmToken();
        }
        return fcmToken;
    }
    // TODO: 5/14/24 토픽 검정 로직
}
