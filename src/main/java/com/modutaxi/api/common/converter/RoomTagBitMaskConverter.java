package com.modutaxi.api.common.converter;

import com.modutaxi.api.domain.room.entity.RoomTagBitMask;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component("roomConverter")
public class RoomTagBitMaskConverter {
    public static int convertRoomTagListToBitMask(List<RoomTagBitMask> roomTagList){
        int bit = 0;
        for(RoomTagBitMask roomTag : roomTagList){
            bit |= roomTag.getValue();
        }
        return bit;
    }

    public static List<RoomTagBitMask> convertBitMaskToRoomTagList(int bitMask){
        List<RoomTagBitMask> roomTagList = new ArrayList<>();
        for(RoomTagBitMask roomTag : RoomTagBitMask.values()){
            if((bitMask & roomTag.getValue()) != 0){
                roomTagList.add(roomTag);
            }
        }
        return roomTagList;
    }
}
