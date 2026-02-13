package com.ryuqq.marketplace.application.productgroup.dto.response;

import com.ryuqq.marketplace.application.product.dto.response.ProductDetailResult;
import java.util.List;

/**
 * 옵션 구조 + 상품(SKU) 통합 결과 DTO.
 *
 * <p>옵션 그룹 구조 정의(optionGroups)와 각 SKU의 옵션 매핑이 resolved된 상품 목록(products)을 하나로 묶어 제공합니다.
 *
 * @param optionGroups 셀러 옵션 그룹 구조 (예: [{색상, [블랙, 화이트]}, {사이즈, [S, M, L]}])
 * @param products 상품(SKU) 목록 (각 상품에 optionGroupName + optionValueName이 resolved)
 */
public record ProductOptionMatrixResult(
        List<SellerOptionGroupResult> optionGroups, List<ProductDetailResult> products) {

    public ProductOptionMatrixResult {
        optionGroups = optionGroups != null ? List.copyOf(optionGroups) : List.of();
        products = products != null ? List.copyOf(products) : List.of();
    }
}
