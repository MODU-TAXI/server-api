package com.modutaxi.api.common.util.time;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TimeFormatConverter {
    /**
     * LocalDateTime을 yyyy.MM.dd (E) 형식으로 변환<br>
     * 예시 : 2024.05.05 (일)
     * @return String
     */
    public static String convertTimeToDiaryDate(LocalDateTime time) {
        return time.format(DateTimeFormatter.ofPattern("yyyy.MM.dd (E)").withLocale(Locale.KOREA));
    }
    /**
     * LocalDateTime을 HH:mm 형식으로 변환<br>
     * 예시 : 02:00
     * @return String
     */
    public static String covertTimeToShortClockTime(LocalDateTime time) {
        return time.format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}
