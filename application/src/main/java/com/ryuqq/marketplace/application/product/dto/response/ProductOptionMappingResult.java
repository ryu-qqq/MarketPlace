package com.ryuqq.marketplace.application.product.dto.response;

import com.ryuqq.marketplace.domain.product.aggregate.ProductOptionMapping;

/** 상품-옵션값 매핑 조회 결과 DTO. */
public record ProductOptionMappingResult(
        Long id,
        Long productId,
        Long sellerOptionValueId,
        String optionGroupName,
        String optionValueName) {

    /** 도메인 객체에서 변환 (옵션 이름 미해석). */
    public static ProductOptionMappingResult from(ProductOptionMapping mapping) {
        return new ProductOptionMappingResult(
                mapping.idValue(),
                mapping.productIdValue(),
                mapping.sellerOptionValueIdValue(),
                null,
                null);
    }

    /** Composite 쿼리에서 옵션 이름이 해석된 결과로 생성. */
    public static ProductOptionMappingResult withOptionNames(
            Long id,
            Long productId,
            Long sellerOptionValueId,
            String optionGroupName,
            String optionValueName) {
        return new ProductOptionMappingResult(
                id, productId, sellerOptionValueId, optionGroupName, optionValueName);
    }
}
