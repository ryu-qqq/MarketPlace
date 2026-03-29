package com.ryuqq.marketplace.domain.order.id;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/** 주문 번호 Value Object. "ORD-YYYYMMDD-XXXX" 형식입니다. */
public record OrderNumber(String value) {

    private static final String PREFIX = "ORD";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    public OrderNumber {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("OrderNumber 값은 null 또는 빈 문자열일 수 없습니다");
        }
    }

    public static OrderNumber of(String value) {
        return new OrderNumber(value);
    }

    public static OrderNumber generate() {
        String date = LocalDate.now().format(DATE_FMT);
        String seq = String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
        return new OrderNumber(PREFIX + "-" + date + "-" + seq);
    }

    /** 지정 날짜 기준으로 주문번호를 생성합니다. 레거시 이관 시 사용. */
    public static OrderNumber generateWithDate(Instant orderDate) {
        LocalDate date = orderDate.atZone(ZoneId.of("Asia/Seoul")).toLocalDate();
        String dateStr = date.format(DATE_FMT);
        String seq = String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
        return new OrderNumber(PREFIX + "-" + dateStr + "-" + seq);
    }
}
