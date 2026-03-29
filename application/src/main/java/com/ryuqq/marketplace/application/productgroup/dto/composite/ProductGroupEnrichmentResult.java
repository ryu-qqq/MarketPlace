package com.ryuqq.marketplace.application.productgroup.dto.composite;

import java.util.List;

/**
 * 상품 그룹 enrichment 결과 DTO.
 *
 * <p>목록 조회에서 기본 Composition 쿼리 이후, productGroupIds IN 쿼리로 한 번에 가격 요약 + 옵션 요약을 가져오는 통합 enrichment
 * 결과입니다.
 *
 * @param productGroupId 상품 그룹 ID
 * @param minPrice 최저 실판매가 (effectivePrice 기준)
 * @param maxPrice 최고 실판매가 (effectivePrice 기준)
 * @param maxDiscountRate 최대 할인율
 * @param optionGroups 옵션 그룹 요약 목록 (예: [{색상, [블랙, 화이트]}, {사이즈, [S, M, L]}])
 */
public record ProductGroupEnrichmentResult(
        Long productGroupId,
        int minPrice,
        int maxPrice,
        int regularPrice,
        int salePrice,
        int maxDiscountRate,
        List<OptionGroupSummaryResult> optionGroups) {

    public ProductGroupEnrichmentResult {
        optionGroups = optionGroups != null ? List.copyOf(optionGroups) : List.of();
    }
}
