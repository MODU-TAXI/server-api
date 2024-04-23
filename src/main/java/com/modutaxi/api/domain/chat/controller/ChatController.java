package com.modutaxi.api.domain.chat.controller;

import com.modutaxi.api.domain.chat.dto.ChatMessage;
import com.modutaxi.api.domain.chat.service.RedisPublisher;
import com.modutaxi.api.domain.chat.service.RedisSubscriber;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ChatController {
    private final RedisPublisher redisPublisher;
    private final RedisSubscriber redisSubscriber;

    @MessageMapping("/chat")
    public void sendMessage(@RequestBody ChatMessage chatMessage){
        redisPublisher.publish(redisSubscriber, chatMessage);
    }
}