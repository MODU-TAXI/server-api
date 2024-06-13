package com.modutaxi.api.domain.paymentroom.service;

import static com.modutaxi.api.domain.paymentroom.mapper.PaymentRoomMapper.toDto;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.PaymentErrorCode;
import com.modutaxi.api.common.exception.errorcode.RoomErrorCode;
import com.modutaxi.api.domain.account.entity.Account;
import com.modutaxi.api.domain.account.repository.AccountRepository;
import com.modutaxi.api.domain.paymentroom.dto.PaymentRoomResponseDto.PaymentRoomResponse;
import com.modutaxi.api.domain.paymentroom.entity.PaymentRoom;
import com.modutaxi.api.domain.paymentroom.repository.PaymentRoomRepository;
import com.modutaxi.api.domain.room.entity.Room;
import com.modutaxi.api.domain.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetPaymentRoomService {

    private final PaymentRoomRepository paymentRoomRepository;
    private final RoomRepository roomRepository;
    private final AccountRepository accountRepository;

    public PaymentRoomResponse getPaymentRoom(Long roomId) {
        PaymentRoom paymentRoom = getPaymentRoomByRoomId(roomId);
        Account account = accountRepository.findById(paymentRoom.getAccountId())
            .orElseThrow(() -> new BaseException(PaymentErrorCode.INVALID_ACCOUNT));
        return toDto(paymentRoom, account);
    }

    public PaymentRoom getPaymentRoomByRoomId(Long roomId) {
        // 1. 방 조회
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new BaseException(RoomErrorCode.EMPTY_ROOM));
        // 2. 정산방 조회
        return paymentRoomRepository.findByRoomId(room.getId())
            .orElseThrow(() -> new BaseException(PaymentErrorCode.INVALID_PAYMENT_ROOM));
    }
}
