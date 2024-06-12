package com.modutaxi.api.common.util.string;

public class StringConverter {

    public static String convertNameMosaic(String name) {
        if (name == null || name.length() <= 1) {
            return name; // 이름이 null 이거나 길이가 한 글자 이하인 경우는 그대로 반환
        } else if (name.length() == 2) {
            return name.charAt(0) + "*"; // 이름이 두 글자인 경우 첫 글자만 남기고 *로 대체
        } else {
            return name.charAt(0) // 첫 글자 추가
                + "*".repeat(name.length() - 2) // 중간 글자들은 *로 대체
                + name.charAt(name.length() - 1);
        }
    }
}
