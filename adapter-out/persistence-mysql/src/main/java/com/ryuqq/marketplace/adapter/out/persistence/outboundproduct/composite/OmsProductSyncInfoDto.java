package com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.composite;

import java.time.Instant;

/**
 * OMS 상품 연동상태 enrichment DTO.
 *
 * @param productGroupId 상품그룹 ID
 * @param entityStatusName OutboundSyncOutbox status enum name
 * @param processedAt 마지막 처리 시각
 */
public record OmsProductSyncInfoDto(
        long productGroupId, String entityStatusName, Instant processedAt) {}
