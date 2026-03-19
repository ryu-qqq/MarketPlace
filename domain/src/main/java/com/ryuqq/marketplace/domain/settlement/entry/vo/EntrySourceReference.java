package com.ryuqq.marketplace.domain.settlement.entry.vo;

/**
 * 정산 원장의 원천 참조 정보.
 *
 * @param orderItemId 주문 항목 ID (필수)
 * @param claimId 클레임 ID (역분개 시, nullable)
 * @param claimType 클레임 유형 (역분개 시, nullable)
 */
public record EntrySourceReference(String orderItemId, String claimId, String claimType) {

    public EntrySourceReference {
        if (orderItemId == null || orderItemId.isBlank()) {
            throw new IllegalArgumentException("orderItemId는 비어 있을 수 없습니다");
        }
    }

    /** 판매 Entry용 (클레임 없음). */
    public static EntrySourceReference forSales(String orderItemId) {
        return new EntrySourceReference(orderItemId, null, null);
    }

    /** 클레임 역분개 Entry용. */
    public static EntrySourceReference forClaim(
            String orderItemId, String claimId, String claimType) {
        return new EntrySourceReference(orderItemId, claimId, claimType);
    }
}
