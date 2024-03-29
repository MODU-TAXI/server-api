package com.modutaxi.api.domain.mail.vo;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum MailDomain {
    INHA_UNIVERSITY_1("inha.edu"),
    INHA_UNIVERSITY_2("inha.ac.kr"),
    INHA_TECHNICAL_COLLEGE_1("itc.ac.kr"),
    INHA_TECHNICAL_COLLEGE_2("inhatc.ac.kr"),
    ;
    private final String domain;
    public static Boolean isExistDomain(String domain){
        for(MailDomain mailDomain : MailDomain.values()){
            if(mailDomain.domain.equals(domain)){
                return true;
            }
        }
        return false;
    }
}
