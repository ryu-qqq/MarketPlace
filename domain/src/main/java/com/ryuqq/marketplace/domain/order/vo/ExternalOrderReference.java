package com.ryuqq.marketplace.domain.order.vo;

import java.time.Instant;

/** 외부몰 주문 참조 정보. OMS에서 수집한 원본 주문의 출처를 기록합니다. */
public record ExternalOrderReference(
        long salesChannelId, long shopId, String externalOrderNo, Instant externalOrderedAt) {

    public ExternalOrderReference {
        if (externalOrderNo == null || externalOrderNo.isBlank()) {
            throw new IllegalArgumentException("외부 주문번호는 필수입니다");
        }
        if (externalOrderedAt == null) {
            throw new IllegalArgumentException("외부 주문시간은 필수입니다");
        }
    }

    public static ExternalOrderReference of(
            long salesChannelId, long shopId, String externalOrderNo, Instant externalOrderedAt) {
        return new ExternalOrderReference(
                salesChannelId, shopId, externalOrderNo, externalOrderedAt);
    }
}
