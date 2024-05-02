package com.modutaxi.api.common.fcm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.ChatErrorCode;
import com.modutaxi.api.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    private final RedisFcmRepositoryImpl redisFcmRepository;

    public void subscribe(Member member, Long chatroomId) {
        String fcmToken = redisFcmRepository.findById(member.getId());
        if (fcmToken == null) throw new BaseException(ChatErrorCode.INVALID_FCM_TOKEN);

        try {
            FirebaseMessaging.getInstance()
                    .subscribeToTopic(
                            Collections.singletonList(fcmToken), Long.toString(chatroomId));
        } catch (FirebaseMessagingException e) {
            throw new BaseException(ChatErrorCode.FAIL_FCM_SUBSCRIBE);
        }
    }

}
