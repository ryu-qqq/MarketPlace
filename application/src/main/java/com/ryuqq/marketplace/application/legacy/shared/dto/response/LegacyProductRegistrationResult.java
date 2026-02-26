package com.ryuqq.marketplace.application.legacy.shared.dto.response;

import java.util.List;

/**
 * 레거시 상품 등록 결과 DTO.
 *
 * <p>luxurydb에 저장된 상품그룹 PK와 상품 PK 목록을 담아 반환합니다.
 *
 * @param productGroupId luxurydb 상품그룹 PK
 * @param sellerId 셀러 ID
 * @param productIds luxurydb 상품 PK 목록
 */
public record LegacyProductRegistrationResult(
        long productGroupId, long sellerId, List<Long> productIds) {

    public LegacyProductRegistrationResult {
        productIds = productIds != null ? List.copyOf(productIds) : List.of();
    }
}
