package com.modutaxi.api.common.util.validator;


import com.modutaxi.api.domain.member.repository.MemberRepository;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NicknameService {

    private static final int NICKNAME_MIN_NUMBER = 1;
    private static final int NICKNAME_MAX_NUMBER = 9;

    private final MemberRepository memberRepository;

    public static String generateNickname() {
        Random random = new Random();
        String adjective = NicknameList.ADJECTIVES[random.nextInt(NicknameList.ADJECTIVES.length)];
        String noun = NicknameList.NOUNS[random.nextInt(NicknameList.NOUNS.length)];
        int number = NICKNAME_MIN_NUMBER + random.nextInt(NICKNAME_MAX_NUMBER); // 1에서 9 사이의 숫자
        return adjective + noun + number;
    }

    public String generateUniqueNickname() {
        String nickname;
        do {
            nickname = generateNickname();
        } while (memberRepository.existsByNickname(nickname));
        return nickname;
    }
}

