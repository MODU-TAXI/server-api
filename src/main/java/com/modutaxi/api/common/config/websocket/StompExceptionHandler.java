package com.modutaxi.api.common.config.websocket;

import com.modutaxi.api.common.auth.PrincipalDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class StompExceptionHandler extends StompSubProtocolErrorHandler {
    private static final byte[] EMPTY_PAYLOAD = new byte[0];

    public StompExceptionHandler() {
        super();
    }

    @Override
    public Message<byte[]> handleClientMessageProcessingError(@Nullable Message<byte[]> clientMessage, Throwable ex) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        //메세지 생성
        accessor.setMessage(ex.getMessage());
        accessor.setLeaveMutable(true);

        StompHeaderAccessor clientHeaderAccessor = null;
        if (clientMessage != null) {
            clientHeaderAccessor = MessageHeaderAccessor.getAccessor(clientMessage, StompHeaderAccessor.class);
            if (clientHeaderAccessor != null) {
                String receiptId = clientHeaderAccessor.getReceipt();
                if (receiptId != null) {
                    accessor.setReceiptId(receiptId);
                }
            }
        }

        return handleInternal(accessor, EMPTY_PAYLOAD, ex, clientHeaderAccessor);
    }
    @Override
    protected Message<byte[]> handleInternal(StompHeaderAccessor errorHeaderAccessor, byte[] errorPayload,
                                             @Nullable Throwable cause, @Nullable StompHeaderAccessor clientHeaderAccessor) {
        String errorCause = "";

        if (cause != null) {
            errorCause = (cause.getCause() != null) ? cause.getCause().toString() : "No exception";
        }

        log.error(errorCause);
        String fullErrorMessage = extractErrorCode(errorCause);

        byte[] newPayload = fullErrorMessage.getBytes(StandardCharsets.UTF_8);

        return MessageBuilder.createMessage(newPayload, errorHeaderAccessor.getMessageHeaders());
    }

    private String extractErrorCode(String input) {
        String[] parts = input.split(":");
        // 콜론 다음 부분의 앞뒤 공백을 제거하여 반환
        if (parts.length > 1) {
            return parts[1].trim();
        } else {
            // ':' 문자가 없는 경우 빈 문자열 반환
            return "No exception";
        }
    }
}
