package com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.composite;

import java.time.Instant;

/**
 * OMS 상품 연동상태 enrichment DTO.
 *
 * @param productGroupId 상품그룹 ID
 * @param shopId 샵 ID
 * @param entityStatusName OutboundSyncOutbox status enum name
 * @param processedAt 마지막 처리 시각
 */
public record OmsProductSyncInfoDto(
        long productGroupId, long shopId, String entityStatusName, Instant processedAt) {

    /** (productGroupId, shopId)를 결합한 조회 키. */
    public String compositeKey() {
        return productGroupId + "_" + shopId;
    }

    /** 정적 키 생성 헬퍼. */
    public static String key(long productGroupId, long shopId) {
        return productGroupId + "_" + shopId;
    }
}
