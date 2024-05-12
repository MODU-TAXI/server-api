package com.modutaxi.api.common.fcm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.gson.Gson;
import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.ChatErrorCode;
import com.modutaxi.api.domain.chatmessage.dto.ChatMessageRequestDto;
import com.modutaxi.api.domain.chatmessage.entity.ChatMessage;
import com.modutaxi.api.domain.member.entity.Member;

import java.util.Collections;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    private final FirebaseMessaging firebaseMessaging;
    private final RedisFcmRepositoryImpl redisFcmRepository;

    public void subscribe(Member member, Long roomId) {
        String fcmToken = validateAndGetFcmToken(member.getId());
        try {
            FirebaseMessaging.getInstance()
                    .subscribeToTopic(
                            Collections.singletonList(fcmToken), Long.toString(roomId));
        } catch (FirebaseMessagingException e) {
            throw new BaseException(ChatErrorCode.FAIL_FCM_SUBSCRIBE);
        }
    }

    public void unsubscribe(Member member, Long roomId) {
        String fcmToken = validateAndGetFcmToken(member.getId());
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
            firebaseMessaging.getInstance().send(message);
            Gson gson = new Gson();
            String fcmMessageJson = gson.toJson(message);
            log.info("FCM 메시지: " + fcmMessageJson);
        } catch (FirebaseMessagingException e) {
            throw new BaseException(ChatErrorCode.FAIL_SEND_MESSAGE);
        }
    }

    /**
     * 채팅 메세지 전송, 토큰으로만
     */
    // TODO: 5/12/24 사용안할 수도
    public void sendChatMessage(Member member,
                                ChatMessageRequestDto chatMessageRequestDto) {
        String fcmToken = validateAndGetFcmToken(member.getId());

        Message message = Message.builder()
                .putData("roomId", chatMessageRequestDto.getRoomId().toString())
                .putData("MessageType", chatMessageRequestDto.getType().toString())
                .putData("content", chatMessageRequestDto.getContent())
                .putData("sender", chatMessageRequestDto.getSender())
                .putData("memberId", chatMessageRequestDto.getMemberId())
                .putData("dateTime", chatMessageRequestDto.getDateTime().toString())
                .setToken(fcmToken)
                .build();
        send(message);
    }

    /**
     * 채팅 메세지 전송, 누군가 채팅방 나갔을 때, 누군가 채팅방 구독했을 때(들어왔을 때)
     */
    public void sendChatMessage(ChatMessageRequestDto chatMessageRequestDto) {
        Message message = Message.builder()
                .putData("roomId", chatMessageRequestDto.getRoomId().toString())
                .putData("MessageType", chatMessageRequestDto.getType().toString())
                .putData("content", chatMessageRequestDto.getContent())
                .putData("sender", chatMessageRequestDto.getSender())
                .putData("memberId", chatMessageRequestDto.getMemberId())
                .putData("dateTime", chatMessageRequestDto.getDateTime().toString())
                .setTopic(chatMessageRequestDto.getRoomId().toString())
                .build();
        send(message);
    }

    /**
     * 방 정보 변경되었을 때(전체 다)
     */
    public void sendUpdateRoomInfo(Long managerId, Long roomId) {
        Message message = Message.builder()
                .putData("MessageType", "MESSAGE")
                .putData("roomId", Long.toString(roomId))
                .putData("message", "참여해 있는 방 정보가 업데이트 되었습니다.")
                .putData("managerId", managerId.toString())
                .setTopic(roomId.toString())
                .build();
        send(message);
    }


    /**
     * 새로운 참여자의 참요 요청 알림, 방장에게
     */
    public void sendNewParticipant(Member roomManager, String roomId) {
        String fcmToken = validateAndGetFcmToken(roomManager.getId());
        Message message = Message.builder()
                .putData("MessageType", "PARTICIPATION_REQUEST")
                .putData("message", "새로운 참가 요청이 들어왔습니다.")
                .putData("roomId", roomId)
                .setToken(fcmToken)
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
                .putData("MessageType", "SUCCESS_MATCHING")
                .putData("message", "어디어디 방 매칭에 성공했어요.")
                .putData("managerId", managerId.toString())
                .setTopic(roomId.toString())
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
                .putData("MessageType", "REQUEST_REMIT")
                .putData("message", "정산해주세요!!")
                .putData("bill", String.valueOf(bill))
                .setTopic(roomId.toString())
                .build();
        send(message);
    }

    /**
     * 방 삭제 되었을 때(방장을 제외한 전부)
     */
    public void sendDeleteRoom(Long managerId, Long roomId) {
        Message message = Message.builder()
                .putData("roomId", Long.toString(roomId))
                .putData("MessageType", "SUCCESS_MATCHING")
                .putData("message", "어디어디 방 매칭에 성공했어요.")
                .putData("managerId", managerId.toString())
                .setTopic(roomId.toString())
                .build();
        send(message);
    }

    /**
     * 사용자 매칭수락 받았을 때 알림
     */
    public void sendPermitParticipate(Member participant, String chatroomId) {
        String fcmToken = validateAndGetFcmToken(participant.getId());
        Message message = Message.builder()
                .putData("MessageType", "SUCCESS_MATCHING")
                .putData("message", "방 매칭에 성공했어요.")
                .putData("roomId", chatroomId)
                .setToken(fcmToken)
                .build();
        send(message);
    }

    /**
     * 출발 시각이 되었을 때(스프링 스케줄러 cron식으로 보내기)
     */
    public void sendDepartureTime(Member member, Long chatroomId) {
        String fcmToken = validateAndGetFcmToken(member.getId());
        Message message = Message.builder()
                .putData("chatroomId", Long.toString(chatroomId))
                .putData("MessageType", "NEW_MATCHING")
                .putData("message", "어디어디 방 매칭에 성공했어요.")
                .setToken(fcmToken)
                .build();
        send(message);
    }

    /**
     * 출발 10(임시)분 전
     */
    public void send10MinutesBeforeDepartureTime(Member member, Long chatroomId) {
        String fcmToken = validateAndGetFcmToken(member.getId());
        Message message = Message.builder()
                .putData("chatroomId", Long.toString(chatroomId))
                .putData("MessageType", "NEW_MATCHING")
                .putData("message", "어디어디 방 매칭에 성공했어요.")
                .setToken(fcmToken)
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
    // TODO: 5/12/24 토픽 검정 로직도 필요한가?
}
