package com.ryuqq.marketplace.domain.exchange.id;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/** 교환 클레임 번호 Value Object. "EXC-YYYYMMDD-XXXX" 형식입니다. */
public record ExchangeClaimNumber(String value) {

    private static final String PREFIX = "EXC";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    public ExchangeClaimNumber {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ExchangeClaimNumber 값은 null 또는 빈 문자열일 수 없습니다");
        }
    }

    public static ExchangeClaimNumber of(String value) {
        return new ExchangeClaimNumber(value);
    }

    public static ExchangeClaimNumber generate() {
        String date = LocalDate.now().format(DATE_FMT);
        String seq = String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
        return new ExchangeClaimNumber(PREFIX + "-" + date + "-" + seq);
    }
}
