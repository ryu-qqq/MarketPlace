package com.ryuqq.marketplace.domain.refund.id;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/** 환불 클레임 번호 Value Object. "RFD-YYYYMMDD-XXXX" 형식입니다. */
public record RefundClaimNumber(String value) {

    private static final String PREFIX = "RFD";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    public RefundClaimNumber {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("RefundClaimNumber 값은 null 또는 빈 문자열일 수 없습니다");
        }
    }

    public static RefundClaimNumber of(String value) {
        return new RefundClaimNumber(value);
    }

    public static RefundClaimNumber generate() {
        String date = LocalDate.now().format(DATE_FMT);
        String seq = String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
        return new RefundClaimNumber(PREFIX + "-" + date + "-" + seq);
    }
}
