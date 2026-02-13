package com.ryuqq.marketplace.domain.cancel.id;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/** 취소 번호 Value Object. "CAN-YYYYMMDD-XXXX" 형식입니다. */
public record CancelNumber(String value) {

    private static final String PREFIX = "CAN";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    public CancelNumber {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("CancelNumber 값은 null 또는 빈 문자열일 수 없습니다");
        }
    }

    public static CancelNumber of(String value) {
        return new CancelNumber(value);
    }

    public static CancelNumber generate() {
        String date = LocalDate.now().format(DATE_FMT);
        String seq = String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
        return new CancelNumber(PREFIX + "-" + date + "-" + seq);
    }
}
