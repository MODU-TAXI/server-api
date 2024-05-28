package com.modutaxi.api.common.config.websocket;

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

        String errorMessage =  "규정되지 않은 에러입니다.";
        String errorCause = "No exception";

        if (cause != null) {
            errorMessage = cause.getMessage();
            errorCause = (cause.getCause() != null) ? cause.getCause().toString() : "null";
            errorHeaderAccessor.setHeader("error-message", errorMessage);
            errorHeaderAccessor.setHeader("error-cause", errorCause);
        }

//        assert cause != null;
//        System.out.println("cause = " + cause.getCause());
        // 예외 메시지를 포함한 새로운 payload 생성

        String fullErrorMessage = "Error: " + errorMessage + "\nCause: " + errorCause;
        byte[] newPayload = fullErrorMessage.getBytes(StandardCharsets.UTF_8);

        log.error("payload: " + errorPayload  + "\n" + "cause: " + cause.getMessage() + ", " + cause.getCause());
        return MessageBuilder.createMessage(newPayload, errorHeaderAccessor.getMessageHeaders());
    }
}
