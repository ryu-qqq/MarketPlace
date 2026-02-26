package com.ryuqq.marketplace.application.product.dto.command;

import java.util.List;

/**
 * 상품(SKU) 배치 상태 변경 Command (ProductGroup 단위).
 *
 * @param sellerId 셀러 ID (소유권 검증용)
 * @param productGroupId 상품 그룹 ID
 * @param productIds 상품 ID 목록
 * @param targetStatus 변경할 상태 ("ACTIVE", "INACTIVE", "SOLDOUT")
 */
public record BatchChangeProductStatusCommand(
        long sellerId, long productGroupId, List<Long> productIds, String targetStatus) {}
