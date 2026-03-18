package com.ryuqq.marketplace.domain.claimsync.id;

/** 클레임 동기화 로그 ID Value Object. DB auto-increment 기반. */
public record ClaimSyncLogId(long value) {

    public static ClaimSyncLogId of(long value) {
        if (value <= 0) {
            throw new IllegalArgumentException("ClaimSyncLogId 값은 0보다 커야 합니다");
        }
        return new ClaimSyncLogId(value);
    }

    public static ClaimSyncLogId forNew() {
        return new ClaimSyncLogId(0L);
    }

    public boolean isNew() {
        return value == 0L;
    }
}
