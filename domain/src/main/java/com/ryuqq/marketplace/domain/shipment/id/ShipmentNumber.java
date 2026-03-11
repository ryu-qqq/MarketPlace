package com.ryuqq.marketplace.domain.shipment.id;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/** 배송 번호 Value Object. "SHP-YYYYMMDD-XXXX" 형태. */
public record ShipmentNumber(String value) {

    private static final String PREFIX = "SHP";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static ShipmentNumber of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ShipmentNumber 값은 null 또는 빈 문자열일 수 없습니다");
        }
        return new ShipmentNumber(value);
    }

    public static ShipmentNumber generate() {
        String date = LocalDate.now().format(DATE_FMT);
        String seq = String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
        return new ShipmentNumber(PREFIX + "-" + date + "-" + seq);
    }
}
