package com.ryuqq.marketplace.domain.refund.vo;

import java.time.Instant;

/** 환불 보류 정보 Value Object. */
public record HoldInfo(String holdReason, Instant holdAt) {

    public HoldInfo {
        if (holdReason == null || holdReason.isBlank()) {
            throw new IllegalArgumentException("보류 사유는 null 또는 빈 문자열일 수 없습니다");
        }
        if (holdAt == null) {
            throw new IllegalArgumentException("보류 시각은 null일 수 없습니다");
        }
    }

    public static HoldInfo of(String holdReason, Instant holdAt) {
        return new HoldInfo(holdReason, holdAt);
    }
}
