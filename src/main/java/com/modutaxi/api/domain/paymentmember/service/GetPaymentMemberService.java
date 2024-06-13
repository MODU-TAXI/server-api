package com.modutaxi.api.domain.paymentmember.service;

import static com.modutaxi.api.domain.paymentmember.mapper.PaymentMemberMapper.toDto;

import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.paymentmember.dto.PaymentMemberResponseDto.PaymentMemberListResponse;
import com.modutaxi.api.domain.paymentmember.dto.PaymentMemberResponseDto.PaymentMemberResponse;
import com.modutaxi.api.domain.paymentmember.entity.PaymentMember;
import com.modutaxi.api.domain.paymentmember.repository.PaymentMemberRepository;
import com.modutaxi.api.domain.paymentroom.entity.PaymentRoom;
import com.modutaxi.api.domain.paymentroom.service.GetPaymentRoomService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetPaymentMemberService {

    private final PaymentMemberRepository paymentMemberRepository;
    private final GetPaymentRoomService getPaymentRoomService;

    public PaymentMemberListResponse getPaymentMembers(Member member, Long roomId) {
        // 1. roomId로 정산방 가져오기
        PaymentRoom paymentRoom = getPaymentRoomService.getPaymentRoomByRoomId(roomId);
        // 2. 정산방으로 정산 멤버들 가져오기
        List<PaymentMember> paymentMembers = paymentMemberRepository.findAllByPaymentRoom(
            paymentRoom);
        // 3. PaymentMember -> Dto 변환
        List<PaymentMemberResponse> participantList = paymentMembers.stream()
            .map(paymentMember -> toDto(paymentMember, member.getId()))
            .collect(Collectors.toList());
        return new PaymentMemberListResponse(participantList);
    }

}
