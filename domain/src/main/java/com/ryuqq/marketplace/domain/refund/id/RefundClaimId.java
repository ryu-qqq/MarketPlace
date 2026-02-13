package com.ryuqq.marketplace.domain.refund.id;

/** 환불 클레임 ID Value Object. 외부에서 UUIDv7을 주입받습니다. */
public record RefundClaimId(String value) {

    public static RefundClaimId of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("RefundClaimId 값은 null 또는 빈 문자열일 수 없습니다");
        }
        return new RefundClaimId(value);
    }

    public static RefundClaimId forNew(String value) {
        return of(value);
    }
}
