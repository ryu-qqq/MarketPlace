package com.ryuqq.marketplace.domain.legacyconversion.aggregate;

/**
 * 레거시 셀러 ID 매핑.
 *
 * <p>luxurydb seller_id와 market sellers.id 간의 매핑을 저장합니다.
 * N:1 매핑 가능 (레거시에서 같은 셀러를 여러 계정으로 운영하는 케이스 존재).
 */
public class LegacySellerIdMapping {

    private final Long id;
    private final long legacySellerId;
    private final long internalSellerId;
    private final String sellerName;

    private LegacySellerIdMapping(
            Long id, long legacySellerId, long internalSellerId, String sellerName) {
        this.id = id;
        this.legacySellerId = legacySellerId;
        this.internalSellerId = internalSellerId;
        this.sellerName = sellerName;
    }

    public static LegacySellerIdMapping reconstitute(
            Long id, long legacySellerId, long internalSellerId, String sellerName) {
        return new LegacySellerIdMapping(id, legacySellerId, internalSellerId, sellerName);
    }

    public Long id() {
        return id;
    }

    public long legacySellerId() {
        return legacySellerId;
    }

    public long internalSellerId() {
        return internalSellerId;
    }

    public String sellerName() {
        return sellerName;
    }
}
