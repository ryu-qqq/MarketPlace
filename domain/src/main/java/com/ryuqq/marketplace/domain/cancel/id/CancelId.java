package com.ryuqq.marketplace.domain.cancel.id;

/** 취소 ID Value Object. 외부에서 UUIDv7을 주입받습니다. */
public record CancelId(String value) {

    public static CancelId of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("CancelId 값은 null 또는 빈 문자열일 수 없습니다");
        }
        return new CancelId(value);
    }

    public static CancelId forNew(String value) {
        return of(value);
    }
}
