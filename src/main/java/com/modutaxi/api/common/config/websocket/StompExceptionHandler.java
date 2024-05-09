package com.modutaxi.api.common.config.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

@Slf4j
@Component
public class StompExceptionHandler extends StompSubProtocolErrorHandler {
  private static final byte[] EMPTY_PAYLOAD = new byte[0];

  public StompExceptionHandler(){
    super();
  }

  @Override
  public Message<byte[]> handleClientMessageProcessingError(@Nullable Message<byte[]> clientMessage, Throwable ex) {
    StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
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
    log.error("소켓 예외 발생\n {}\n {}\n",accessor, String.valueOf(ex));
    return handleInternal(accessor, EMPTY_PAYLOAD, ex, clientHeaderAccessor);
  }

}
