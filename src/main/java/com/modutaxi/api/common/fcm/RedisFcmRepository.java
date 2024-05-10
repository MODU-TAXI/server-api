package com.modutaxi.api.common.fcm;

public interface RedisFcmRepository {

    void save(Long memberId, String fcmToken);

    String findById(Long memberId);
}
