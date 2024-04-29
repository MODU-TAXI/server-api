package com.modutaxi.api.domain.chatroom.repository;

import com.modutaxi.api.common.config.redis.BaseRedisRepository;
import com.modutaxi.api.domain.chatroom.ChatInfo;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.io.Serializable;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ChatRoomRepository extends BaseRedisRepository implements Serializable {

    // Redis CacheKeys
    private static final String CHAT_ROOMS = "CHAT_ROOM"; // 채팅룸 저장
    public static final String ENTER_INFO = "ENTER_INFO"; // 채팅룸에 입장한 클라이언트의 sessionId와 채팅룸 id를 맵핑한 정보 저장
    private static final String USER_COUNT = "USER_COUNT"; // 유저 수

    private final RedisTemplate<String, String> redisTemplate;

    private final RedisTemplate<String, ChatInfo> chatInfoRedisTemplate;
    private ValueOperations<String, String> valueOperations;
    private HashOperations<String, String, String> hashOperations;
    private HashOperations<String, String, ChatInfo> chatInfoHashOperations;



    @PostConstruct
    protected void init() {
        classInstance = ChatRoomRepository.class;
        valueOperations = redisTemplate.opsForValue();
        hashOperations = redisTemplate.opsForHash();
        chatInfoHashOperations = chatInfoRedisTemplate.opsForHash();
    }

    // 멤버와 룸의 매핑.
    public ChatInfo findChatInfoByMemberId(String memberId) {
        if (memberId == null) {
            return null;
        }
        return (ChatInfo) chatInfoHashOperations.get(ENTER_INFO, memberId);
    }

    public String findMemberBySessionId(String sessionId) {
        if (sessionId == null) {
            return null;
        }
        return (String) hashOperations.get(ENTER_INFO, sessionId);
    }

    public void setUserEnterInfo(String memberId, ChatInfo chatInfo){
        chatInfoHashOperations.put(ENTER_INFO, memberId, chatInfo);
    }

    public void setUserInfo(String sessionId, String memberId){
        hashOperations.put(ENTER_INFO, sessionId, memberId);
    }

    public void removeUserBySessionIdEnterInfo(String sessionId){
        hashOperations.delete(ENTER_INFO,sessionId);
    }

    public void removeUserByMemberIdEnterInfo(String memberId){
        chatInfoHashOperations.delete(ENTER_INFO,memberId);
    }


    // 채팅방 유저수 조회
    public long getUserCount(String roomId) {
        return Long.valueOf(Optional.ofNullable(valueOperations.get(USER_COUNT + "_" + roomId)).orElse("0"));
    }

    // 채팅방에 입장한 유저수 +1
    public long plusUserCount(String roomId, int count) {
        return Optional.ofNullable(valueOperations.increment(USER_COUNT + "_" + roomId, count)).orElse(0L);
    }

    // 채팅방에 입장한 유저수 -1
    public long minusUserCount(String roomId, int count) {
        return Optional.ofNullable(valueOperations.decrement(USER_COUNT + "_" + roomId, count)).orElse(0L);
    }
}