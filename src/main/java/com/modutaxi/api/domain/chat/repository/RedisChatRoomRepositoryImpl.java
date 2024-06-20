package com.modutaxi.api.domain.chat.repository;

import com.modutaxi.api.common.config.redis.BaseRedisRepository;
import com.modutaxi.api.domain.chat.ChatRoomMappingInfo;
import com.modutaxi.api.domain.participant.entity.Participant;
import com.modutaxi.api.domain.participant.repository.ParticipantRepository;
import jakarta.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Optional;
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

    public static final String ENTER_INFO = "ENTER_INFO"; // 채팅룸에 입장한 클라이언트의 sessionId와 채팅룸 id를 맵핑한 정보 저장

    private final RedisTemplate<String, String> redisTemplate;

    private final RedisTemplate<String, ChatRoomMappingInfo> chatInfoRedisTemplate;
    private HashOperations<String, String, String> hashOperations;
    private HashOperations<String, String, ChatRoomMappingInfo> chatInfoHashOperations;
    private final ParticipantRepository participantRepository;



    @PostConstruct
    protected void init() {
        classInstance = RedisChatRoomRepositoryImpl.class;
        hashOperations = redisTemplate.opsForHash();
        chatInfoHashOperations = chatInfoRedisTemplate.opsForHash();
    }


    // -------------- 소켓 -----------------
    // 멤버와 룸의 매핑.
    public ChatRoomMappingInfo findChatInfoByMemberId(String memberId) {
        if (memberId == null) {
            return null;
        }
        ChatRoomMappingInfo chatRoomMappingInfo = chatInfoHashOperations.get(ENTER_INFO, memberId);
        if(chatRoomMappingInfo == null){
            Optional<Participant> participant
                = participantRepository.findByMemberId(Long.valueOf(memberId));
            if(participant.isEmpty()) return null;
            chatRoomMappingInfo =
                new ChatRoomMappingInfo(
                    participant.get().getRoom().getId().toString(),
                    participant.get().getMember().getNickname());
        }
        return chatRoomMappingInfo;
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