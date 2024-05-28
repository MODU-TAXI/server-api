package com.modutaxi.api.common.fcm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.google.gson.Gson;
import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.ChatErrorCode;
import com.modutaxi.api.domain.chatmessage.dto.ChatMessageRequestDto;
import com.modutaxi.api.domain.chatmessage.entity.MessageType;
import com.modutaxi.api.domain.member.entity.Member;

import java.util.Collections;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    private final RedisFcmRepositoryImpl redisFcmRepository;

    public void subscribe(Long memberId, Long roomId) {
        String fcmToken = validateAndGetFcmToken(memberId);
        try {
            FirebaseMessaging.getInstance()
                    .subscribeToTopic(
                            Collections.singletonList(fcmToken), Long.toString(roomId));
        } catch (FirebaseMessagingException e) {
            throw new BaseException(ChatErrorCode.FAIL_FCM_SUBSCRIBE);
        }
    }

    public void unsubscribe(Long memberId, Long roomId) {
        String fcmToken = validateAndGetFcmToken(memberId);
        try {
            FirebaseMessaging.getInstance()
                    .unsubscribeFromTopic(
                            Collections.singletonList(fcmToken), Long.toString(roomId));
        } catch (FirebaseMessagingException e) {
            throw new BaseException(ChatErrorCode.FAIL_FCM_UNSUBSCRIBE);
        }
    }

    public void send(Message message) {
        try {
            FirebaseMessaging.getInstance().send(message);
            Gson gson = new Gson();
            String fcmMessageJson = gson.toJson(message);
            log.info("FCM 메시지: " + fcmMessageJson);
        } catch (FirebaseMessagingException e) {
            throw new BaseException(ChatErrorCode.FAIL_SEND_MESSAGE);
        }
    }

    // TODO: 5/14/24 프론트 측 테스트를 위한 메서드입니다.
    public void testSend(Member member){
        String fcmToken = validateAndGetFcmToken(member.getId());
        Message message = Message.builder()
                .putData("messageType", "TEST")
                .putData("message", "테스트메세지")
                .setToken(fcmToken)
                .setNotification(Notification.builder()
                        .setTitle("모두의택시")
                        .setBody("테스트메세지")
                        .build())
                .build();
        send(message);
    }

    /**
     * 채팅 메세지 전송(일반 채팅 & 채팅방 퇴장 & 채팅방 입장)
     */
    public void sendChatMessage(ChatMessageRequestDto chatMessageRequestDto) {
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
                        .setBody(chatMessageRequestDto.getType().equals(MessageType.IMAGE)
                                ? "사진" : chatMessageRequestDto.getContent())
                        .build())
                .build();
        send(message);
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
                .build();
        send(message);
    }


    /**
     * 새로운 참여자의 참요 요청 알림, 방장에게
     */
    public void sendNewParticipant(Member roomManager, String roomId) {
        String fcmToken = validateAndGetFcmToken(roomManager.getId());
        Message message = Message.builder()
                .putData("messageType", "PARTICIPATE_REQUEST")
                .putData("message", "새로운 참가자의 참가 요청이 들어왔습니다.")
                .putData("roomId", roomId)
                .setToken(fcmToken)
                .setNotification(Notification.builder()
                        .setTitle("모두의택시")
                        .setBody("새로운 참가자의 참가 요청이 들어왔습니다.")
                        .build())
                .build();
        send(message);
    }

    /**
     * 매칭 완료 알림(전체 다에게)
     */
    // TODO: 5/12/24 아직 쓰는 곳 없음. API 만들어야 함.
    public void sendSuccessMatching(Long managerId, Long roomId) {
        Message message = Message.builder()
                .putData("roomId", Long.toString(roomId))
                .putData("messageType", "MATCHING_COMPLETE")
                .putData("message", "방 매칭이 완료되었습니다.")
                .putData("managerId", managerId.toString())
                .setTopic(roomId.toString())
                .setNotification(Notification.builder()
                        .setTitle("모두의택시")
                        .setBody("방 매칭이 완료되었습니다.")
                        .build())
                .build();
        send(message);
    }

    /**
     * 정산 요청
     * TODO: 5/12/24 아직 쓰는 곳 없음. API 만들어야 함.
     */
    public void sendRequestPayment(Member member, Long roomId, int bill) {
        String fcmToken = validateAndGetFcmToken(member.getId());
        Message message = Message.builder()
                .putData("roomId", Long.toString(roomId))
                .putData("messageType", "REMIT_REQUEST")
                .putData("message", "정산해주세요.")
                .putData("bill", String.valueOf(bill))
                .setTopic(roomId.toString())
                .setNotification(Notification.builder()
                        .setTitle("모두의택시")
                        .setBody("정산해주세요.")
                        .build())
                .build();
        send(message);
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
                .build();
        send(message);
    }

    /**
     * 사용자가 매칭수락 받았을 때 알림
     */
    public void sendPermitParticipate(Member participant, String roomId) {
        String fcmToken = validateAndGetFcmToken(participant.getId());
        Message message = Message.builder()
                .putData("messageType", "MATCHING_SUCCESS")
                .putData("message", "방 매칭에 성공했습니다.")
                .putData("roomId", roomId)
                .setToken(fcmToken)
                .setNotification(Notification.builder()
                        .setTitle("모두의택시")
                        .setBody("방 매칭에 성공했습니다.")
                        .build())
                .build();
        send(message);
    }

    /**
     * 출발 시각이 되었을 때(스프링 스케줄러 cron식으로 보내기)
     */
    public void sendDepartureTime(Member member, Long roomId) {
        String fcmToken = validateAndGetFcmToken(member.getId());
        Message message = Message.builder()
                .putData("roomId", Long.toString(roomId))
                .putData("messageType", "TIME_TO_DEPART")
                .putData("message", "예정되어 있던 출발 시간이 되었습니다.")
                .setToken(fcmToken)
                .setNotification(Notification.builder()
                        .setTitle("모두의택시")
                        .setBody("예정되어 있던 출발 시간이 되었습니다.")
                        .build())
                .build();
        send(message);
    }

    /**
     * 출발 10(임시)분 전
     */
    public void send10MinutesBeforeDepartureTime(Member member, Long roomId) {
        String fcmToken = validateAndGetFcmToken(member.getId());
        Message message = Message.builder()
                .putData("roomId", Long.toString(roomId))
                .putData("messageType", "DEPART_10_MINUTES_AGO")
                .putData("message", "출발 10분 전입니다.")
                .setToken(fcmToken)
                .setNotification(Notification.builder()
                        .setTitle("모두의택시")
                        .setBody("출발 10분 전입니다.")
                        .build())
                .build();
        send(message);
    }


    public String validateAndGetFcmToken(Long memberId) {
        String fcmToken = redisFcmRepository.findById(memberId);
        if (fcmToken == null) {
            throw new BaseException(ChatErrorCode.INVALID_FCM_TOKEN);
        }
        return fcmToken;
    }
    // TODO: 5/14/24 토픽 검정 로직
}
