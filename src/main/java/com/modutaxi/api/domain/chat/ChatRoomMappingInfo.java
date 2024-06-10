package com.modutaxi.api.domain.chat;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomMappingInfo implements Serializable {
    private String roomId;
    private String nickname;
    private String imageUrl;
}
