package com.ryuqq.marketplace.domain.order.id;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/** 결제 번호 Value Object. "PAY-YYYYMMDD-XXXX" 형식입니다. */
public record PaymentNumber(String value) {

    private static final String PREFIX = "PAY";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    public PaymentNumber {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("PaymentNumber 값은 null 또는 빈 문자열일 수 없습니다");
        }
    }

    public static PaymentNumber of(String value) {
        return new PaymentNumber(value);
    }

    public static PaymentNumber generate() {
        String date = LocalDate.now().format(DATE_FMT);
        String seq = String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
        return new PaymentNumber(PREFIX + "-" + date + "-" + seq);
    }
}
