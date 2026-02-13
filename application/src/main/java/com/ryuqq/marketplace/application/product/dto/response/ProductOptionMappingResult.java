package com.ryuqq.marketplace.application.product.dto.response;

import com.ryuqq.marketplace.domain.product.aggregate.ProductOptionMapping;

/** 상품-옵션값 매핑 조회 결과 DTO. */
public record ProductOptionMappingResult(Long id, Long productId, Long sellerOptionValueId) {

    public static ProductOptionMappingResult from(ProductOptionMapping mapping) {
        return new ProductOptionMappingResult(
                mapping.idValue(), mapping.productIdValue(), mapping.sellerOptionValueIdValue());
    }
}
