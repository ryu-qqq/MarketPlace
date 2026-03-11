package com.ryuqq.marketplace.domain.order.vo;

import java.time.Instant;

/**
 * 외부몰 주문 참조 정보. OMS에서 수집한 원본 주문의 출처를 기록합니다.
 *
 * @param salesChannelId 판매채널 ID
 * @param shopId 샵 ID
 * @param shopCode 샵 코드 (nullable, 예: "NAVER", "COUPANG")
 * @param shopName 샵 이름 (nullable, 예: "네이버 스마트스토어")
 * @param externalOrderNo 외부 주문번호
 * @param externalOrderedAt 외부 주문시간
 */
public record ExternalOrderReference(
        long salesChannelId,
        long shopId,
        String shopCode,
        String shopName,
        String externalOrderNo,
        Instant externalOrderedAt) {

    public ExternalOrderReference {
        if (externalOrderNo == null || externalOrderNo.isBlank()) {
            throw new IllegalArgumentException("외부 주문번호는 필수입니다");
        }
        if (externalOrderedAt == null) {
            throw new IllegalArgumentException("외부 주문시간은 필수입니다");
        }
    }

    public static ExternalOrderReference of(
            long salesChannelId,
            long shopId,
            String shopCode,
            String shopName,
            String externalOrderNo,
            Instant externalOrderedAt) {
        return new ExternalOrderReference(
                salesChannelId, shopId, shopCode, shopName, externalOrderNo, externalOrderedAt);
    }
}
