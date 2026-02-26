package com.ryuqq.marketplace.application.legacy.shared.dto.composite;

import java.util.List;

/**
 * 세토프 DB 상품(Product) Composite 조회 결과.
 *
 * <p>Product + ProductOption + OptionGroup + OptionDetail + Stock 조인 결과입니다.
 *
 * @param productId 세토프 상품 ID
 * @param productGroupId 세토프 상품그룹 ID
 * @param stockQuantity 재고 수량
 * @param soldOut 품절 여부
 * @param optionMappings resolved된 옵션 매핑 목록
 */
public record LegacyProductCompositeResult(
        long productId,
        long productGroupId,
        int stockQuantity,
        boolean soldOut,
        List<OptionMapping> optionMappings) {

    /**
     * 상품에 매핑된 옵션 정보 (resolved).
     *
     * @param optionGroupId 옵션그룹 ID
     * @param optionDetailId 옵션상세 ID
     * @param optionGroupName 옵션그룹명 (예: "색상")
     * @param optionValue 옵션값 (예: "블랙")
     */
    public record OptionMapping(
            long optionGroupId, long optionDetailId, String optionGroupName, String optionValue) {}
}
