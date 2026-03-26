package com.ryuqq.marketplace.domain.claimhistory.id;

import java.util.UUID;

/** 클레임 이력 ID Value Object. UUIDv7 기반 자동 생성 지원. */
public record ClaimHistoryId(String value) {

    public static ClaimHistoryId of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ClaimHistoryId 값은 null 또는 빈 문자열일 수 없습니다");
        }
        return new ClaimHistoryId(value);
    }

    public static ClaimHistoryId forNew(String value) {
        return of(value);
    }

    public static ClaimHistoryId generate() {
        return new ClaimHistoryId(UUID.randomUUID().toString());
    }
}
