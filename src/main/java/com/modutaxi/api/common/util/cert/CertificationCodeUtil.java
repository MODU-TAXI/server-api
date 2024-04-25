package com.modutaxi.api.common.util.cert;

public class CertificationCodeUtil {
    public static String generateCertificationCode(int length) {
        final String candidateChars = "1234567890";
        String code = "";
        for (int i = 0; i < length; i++) {
            code += candidateChars.charAt((int) (Math.random() * candidateChars.length()));
        }
        return code;
    }
}
