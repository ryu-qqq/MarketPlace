package com.ryuqq.marketplace.application.legacyproduct.dto.response;

import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import java.util.List;

/**
 * 레거시 상품 등록 결과 DTO.
 *
 * <p>상품 그룹과 활성화된 상품 목록만 담아 반환합니다.
 *
 * @param productGroup 상품 그룹 (변환 대기 시 null)
 * @param products 활성화된 상품 목록
 */
public record LegacyProductRegistrationResult(ProductGroup productGroup, List<Product> products) {

    public LegacyProductRegistrationResult {
        products = products != null ? List.copyOf(products) : List.of();
    }
}
