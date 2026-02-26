package com.ryuqq.marketplace.application.productgroup.dto.result;

import java.util.List;

/**
 * 상품 그룹 등록 결과.
 *
 * @param productGroupId 생성된 상품 그룹 ID
 * @param productIds 생성된 상품(SKU) ID 목록
 */
public record ProductGroupRegistrationResult(long productGroupId, List<Long> productIds) {

    public ProductGroupRegistrationResult {
        productIds = List.copyOf(productIds);
    }
}
