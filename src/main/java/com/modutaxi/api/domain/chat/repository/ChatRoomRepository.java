package com.modutaxi.api.domain.chat.repository;

import com.amazonaws.services.cloudformation.model.StackInstance;
import com.modutaxi.api.common.config.redis.BaseRedisRepository;
import com.modutaxi.api.domain.chat.service.RedisSubscriber;
import com.modutaxi.api.domain.chatroom.ChatRoom;
import com.modutaxi.api.domain.member.repository.RedisRTKRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.PartialUpdate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ChatRoomRepository extends BaseRedisRepository implements Serializable {

    // Redis CacheKeys
    private static final String CHAT_ROOMS = "CHAT_ROOM"; // 채팅룸 저장
    public static final String ENTER_INFO = "ENTER_INFO"; // 채팅룸에 입장한 클라이언트의 sessionId와 채팅룸 id를 맵핑한 정보 저장
    private static final String USER_COUNT = "USER_COUNT"; // 유저 수

    private final RedisTemplate<String, String> redisTemplate;
    private ValueOperations<String, String> valueOperations;
    private HashOperations<String, String, String> hashOperations;



    @PostConstruct
    protected void init() {
        classInstance = ChatRoomRepository.class;
        valueOperations = redisTemplate.opsForValue();
        hashOperations = redisTemplate.opsForHash();
    }

    public String findById(String sessionId) {
        System.out.println("sessionId = " + sessionId);
        if (sessionId == null) {
            return null;
        }
        return (String) valueOperations.get(ENTER_INFO + ":"+ sessionId);
    }



    public void setUserEnterInfo(String sessionId, String roomId){
        hashOperations.put(ENTER_INFO, sessionId, roomId);
    }

    public void removeUserEnterInfo(String sessionId){
        hashOperations.delete(ENTER_INFO,sessionId);
    }


    // 채팅방 유저수 조회
    public long getUserCount(String roomId) {
        return Long.valueOf(Optional.ofNullable(valueOperations.get(USER_COUNT + "_" + roomId)).orElse("0"));
    }

    // 채팅방에 입장한 유저수 +1
    public long plusUserCount(String roomId) {
        return Optional.ofNullable(valueOperations.increment(USER_COUNT + "_" + roomId)).orElse(0L);
    }

    // 채팅방에 입장한 유저수 -1
    public long minusUserCount(String roomId) {
        return Optional.ofNullable(valueOperations.decrement(USER_COUNT + "_" + roomId)).filter(count -> count > 0).orElse(0L);
    }
}