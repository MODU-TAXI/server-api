package com.modutaxi.api.domain.chat.repository;

import com.modutaxi.api.domain.chat.service.RedisSubscriber;
import com.modutaxi.api.domain.chatroom.ChatRoom;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ChatRoomRepository {

    private final RedisMessageListenerContainer redisMessageListenerContainer;

    private HashOperations<String, String, ChatRoom> operationHashChatRoom;
    private Map<String, ChannelTopic> topics;

    @PostConstruct
    private void init(){
        topics = new HashMap<>();
        setOperationHashChatRoom(operationHashChatRoom);
    }

    public void setOperationHashChatRoom(
        HashOperations<String, String, ChatRoom> operationHashChatRoom) {
        this.operationHashChatRoom = operationHashChatRoom;
    }
    public void enterChatRoom(RedisSubscriber redisSubscriber, Long roomId){
        ChannelTopic topic = topics.get(String.valueOf(roomId));
        if(topic == null){
            topic = new ChannelTopic("/sub/chat/" + roomId);
            topics.put(String.valueOf(roomId), topic);
        }
        redisMessageListenerContainer.addMessageListener(redisSubscriber, topic);
    }

    public void leaveChatRoom(RedisSubscriber redisSubscriber){
        redisMessageListenerContainer.removeMessageListener(redisSubscriber);
    }
}