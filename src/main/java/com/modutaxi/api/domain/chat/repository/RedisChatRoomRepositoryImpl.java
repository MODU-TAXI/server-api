package com.modutaxi.api.domain.chat.repository;

import com.modutaxi.api.common.config.redis.BaseRedisRepository;
import com.modutaxi.api.domain.chat.ChatRoomMappingInfo;
import jakarta.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class RedisChatRoomRepositoryImpl extends BaseRedisRepository implements Serializable {

    // Redis CacheKeys

    private static final String ROOM_IN_LIST = "ROOM_IN_LIST"; //채팅방 참여
    private static final String ROOM_WAITING_LIST = "ROOM_WAITING_LIST"; // 대기열 저장
    public static final String ENTER_INFO = "ENTER_INFO"; // 채팅룸에 입장한 클라이언트의 sessionId와 채팅룸 id를 맵핑한 정보 저장

    private final RedisTemplate<String, String> redisTemplate;

    private final RedisTemplate<String, ChatRoomMappingInfo> chatInfoRedisTemplate;
    private HashOperations<String, String, String> hashOperations;
    private HashOperations<String, String, ChatRoomMappingInfo> chatInfoHashOperations;
    private SetOperations<String, String> waitOperations;
    private SetOperations<String, String> roomInOperations;



    @PostConstruct
    protected void init() {
        classInstance = RedisChatRoomRepositoryImpl.class;
        hashOperations = redisTemplate.opsForHash();
        chatInfoHashOperations = chatInfoRedisTemplate.opsForHash();
        waitOperations = redisTemplate.opsForSet();
        roomInOperations = redisTemplate.opsForSet();
    }


    //대기열에 특정멤버 추가
    public Long addToWaitingList(String roomId, String memberId) {
        return waitOperations.add(ROOM_WAITING_LIST + "_" + roomId, memberId);
    }

    //대기열에서 특정 멤버 삭제
    public Long removeFromWaitingList(String roomId, String memberId) {
        return waitOperations.remove(ROOM_WAITING_LIST + "_" + roomId, memberId);
    }

    //대기열 멤버 목록 조회
    public Set<String> findWaitingList(String roomId) {
        return waitOperations.members(ROOM_WAITING_LIST + "_" + roomId);
    }

    //대기열 안에 특정 멤버가 있는 지 조회
    public boolean findMemberInWaitingList(String roomId, String memberId) {
        return waitOperations.members(ROOM_WAITING_LIST + "_" + roomId).contains(memberId);
    }

    //채팅방에 특정 멤버 추가
    public Long addRoomInMemberList(String roomId, String memberId) {
        return roomInOperations.add(ROOM_IN_LIST + "_" + roomId, memberId);
    }

    //채팅방에서 특정 멤버 삭제
    public Long removeFromRoomInList(String roomId, String memberId) {
        return waitOperations.remove(ROOM_IN_LIST + "_" + roomId, memberId);
    }

    //채팅방 안에 멤버 목록 조회
    public Set<String> findRoomInList(String roomId) {
        return waitOperations.members(ROOM_IN_LIST + "_" + roomId);
    }

    //채팅방 안에 특정 멤버가 있는 지 조회
    public boolean findMemberInRoomInList(String roomId, String memberId) {
        return waitOperations.members(ROOM_IN_LIST + "_" + roomId).contains(memberId);
    }




    // -------------- 소켓 -----------------
    // 멤버와 룸의 매핑.
    public ChatRoomMappingInfo findChatInfoByMemberId(String memberId) {
        if (memberId == null) {
            return null;
        }
        return chatInfoHashOperations.get(ENTER_INFO, memberId);
    }

    public String findMemberBySessionId(String sessionId) {
        if (sessionId == null) {
            return null;
        }
        return (String) hashOperations.get(ENTER_INFO, sessionId);
    }

    public void setUserEnterInfo(String memberId, ChatRoomMappingInfo chatRoomMappingInfo){
        chatInfoHashOperations.put(ENTER_INFO, memberId, chatRoomMappingInfo);
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

}