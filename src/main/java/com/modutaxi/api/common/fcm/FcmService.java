package com.modutaxi.api.common.fcm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.gson.Gson;
import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.ChatErrorCode;
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

    public void subscribe(Member member, Long chatroomId) {
        String fcmToken = validateAndGetFcmToken(member.getId());
        try {
            FirebaseMessaging.getInstance()
                .subscribeToTopic(
                    Collections.singletonList(fcmToken), Long.toString(chatroomId));
        } catch (FirebaseMessagingException e) {
            throw new BaseException(ChatErrorCode.FAIL_FCM_SUBSCRIBE);
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
     * 채팅 알림
     * TODO: 파라미터는 적절히 바꿔주세요.
     */
    public void sendChatMessage(Member member,
        Long chatroomId, String nickname, String content, String createAt) {
        String fcmToken = validateAndGetFcmToken(member.getId());
        Message message = Message.builder()
            .putData("type", "MESSAGE")
            .putData("chatroomId", Long.toString(chatroomId))
            .putData("message", content)
            .putData("nickName", nickname)
            .putData("createAt", createAt)
            .setToken(fcmToken)
            .build();
        send(message);
    }

    /**
     * 새로운 참여자 알림
     * TODO: 파라미터는 적절히 바꿔주세요.
     */
    public void sendNewParticipant(Member member, Long chatroomId, String createAt) {
        String fcmToken = validateAndGetFcmToken(member.getId());
        Message message = Message.builder()
            .putData("type", "NEW_PARTICIPANT")
            .putData("chatroomId", Long.toString(chatroomId))
            .putData("createAt", createAt)
            .putData("message", "새로운 참여자 입장 키야아아!!")
            .setToken(fcmToken)
            .build();
        send(message);
    }

    /**
     * 매칭 완료 알림
     * TODO: 파라미터는 적절히 바꿔주세요.
     */
    public void sendSuccessMatching(Member member, Long chatroomId) {
        String fcmToken = validateAndGetFcmToken(member.getId());
        Message message = Message.builder()
            .putData("chatroomId", Long.toString(chatroomId))
            .putData("type", "SUCCESS_MATCHING")
            .putData("message", "어디어디 방 매칭에 성공했어요.")
            .setToken(fcmToken)
            .build();
        send(message);
    }

    /**
     * (방장) 새로운 사용자 매칭 등록 알림
     * TODO: 파라미터는 적절히 바꿔주세요.
     */
    public void sendNewMatching(Member member, Long chatroomId) {
        String fcmToken = validateAndGetFcmToken(member.getId());
        Message message = Message.builder()
            .putData("chatroomId", Long.toString(chatroomId))
            .putData("type", "NEW_MATCHING")
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
}
