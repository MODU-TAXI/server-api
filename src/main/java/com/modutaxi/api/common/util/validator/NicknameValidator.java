package com.modutaxi.api.common.util.validator;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.MemberErrorCode;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class NicknameValidator {

    private static final Pattern VALID_PATTERN = Pattern.compile(
        "^(?=.*[a-zA-Z가-힣])[a-zA-Z가-힣0-9]+$");


    public static void validate(String nickname) {
        // 아무것도 입력되지 않았을 때, 2자 미만일 때
        if (nickname == null || nickname.trim().isEmpty() || nickname.length() < 2) {
            throw new BaseException(MemberErrorCode.TOO_SHORT_NICKNAME);
        }
        // 최대 길이 제한
        if (nickname.length() > 12) {
            throw new BaseException(MemberErrorCode.TOO_LONG_NICKNAME);
        }
        // 공백 또는 특수 문자 포함 (한글, 영어, 숫자만 사용할 수 있어요!)
        if (!VALID_PATTERN.matcher(nickname).matches() || nickname.contains(" ")) {
            throw new BaseException(MemberErrorCode.INVALID_NICKNAME);
        }
        // 비속어 포함
        for (String profanity : ProfanityList.PROFANITIES) {
            if (nickname.toLowerCase().contains(profanity.toLowerCase())) {
                throw new BaseException(MemberErrorCode.INAPPROPRIATE_WORD_NICKNAME);
            }
        }
    }
}
