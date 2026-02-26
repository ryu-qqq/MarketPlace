package com.ryuqq.marketplace.domain.claim.id;

/** 클레임 배송(수거) ID. */
public record ClaimShipmentId(String value) {

    public static ClaimShipmentId of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ClaimShipmentId 값은 null 또는 빈 문자열일 수 없습니다");
        }
        return new ClaimShipmentId(value);
    }

    public static ClaimShipmentId forNew(String value) {
        return of(value);
    }
}
