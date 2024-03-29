package com.modutaxi.api.domain.mail.vo;

public class MailTemplate {
    public static String getCertMailContent(String email, String certCode) {
        return "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "</head>\n" +
            "<body>\n" +
            "<div style=\"background-color: #3ed0ac; height: 150px; display: flex; align-items: center;\">\n" +
            "    <span style=\"font-family: Gill Sans; font-weight: bold; font-size: 50px; color: #ffffff; text-shadow: 0px 4px 4px #404040; position: relative; left: 20px\">\n" +
            "        모두의 택시 \uD83D\uDE95\n" +
            "    </span>\n" +
            "</div>\n" +
            "<p class=\"p\">\n" +
            "    <span style=\"font-family: Gill Sans; font-weight: bolder; font-size: 25px; position: relative; left: 10px\">\n" +
            "        모두의 택시 로그인을 위한 인증번호입니다.<br>\n" +
            "    </span>\n" +
            "    <span style=\"font-size: 10px\"><br></span>\n" +
            "    <span style=\"font-family: Gill Sans; font-weight: normal; position: relative; left: 10px\">\n" +
            "        아래 인증번호를 확인하여 로그인을 진행해 주세요.\n" +
            "    </span>\n" +
            "</p>\n" +
            "<div style=\"position: relative; left: 10px; width: 500px\">\n" +
            "    <hr>\n" +
            "    <div>\n" +
            "        <span style=\"font-family: Gill Sans; font-weight: bolder; font-size: 15px;\">\n" +
            "            이메일 계정\n" +
            "            <span style=\"color: #3ed0ac; text-decoration: underline; text-decoration-thickness: 2px; position: relative; left: 1ch\">\n" +
            "                " + email + "\n" +
            "            </span>\n" +
            "        </span>\n" +
            "    </div>\n" +
            "    <br>\n" +
            "    <div>\n" +
            "        <span style=\"font-family: Gill Sans; font-weight: bolder; font-size: 15px;\">\n" +
            "            인증번호\n" +
            "            <span style=\"font-weight: normal; position: relative; left: 3ch\">\n" +
            "                " + certCode + "\n" +
            "            </span>\n" +
            "        </span>\n" +
            "    </div>\n" +
            "    <hr>\n" +
            "</div>\n" +
            "<p style=\"position: relative; left: 10px;\">\n" +
            "    <span style=\"font-family: Gill Sans; font-weight: normal;\">\n" +
            "        <br>\n" +
            "        본 메일은 발신전용입니다.<br>\n" +
            "        모두의 택시 이메일 인증 관련하여 궁금한 점이 있으시면\n" +
            "        <a href=\"mailto:ghfkddl706@me.com?subject=모두의 택시 이메일 인증 관련 문의\" style=\"font-weight: bold\">\n" +
            "            카카오채널\n" +
            "        </a>\n" +
            "        로 문의 해주세요.\n" +
            "    </span>\n" +
            "</p>\n" +
            "</body>\n" +
            "</html>";
    }
}
