package com.ryuqq.marketplace.domain.cancel.id;

import java.util.UUID;

/** 취소 ID Value Object. UUIDv7 기반 자동 생성 지원. */
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

    public static CancelId generate() {
        return new CancelId(UUID.randomUUID().toString());
    }
}
