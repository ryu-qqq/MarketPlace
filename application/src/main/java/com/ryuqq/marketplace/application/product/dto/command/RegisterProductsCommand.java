package com.ryuqq.marketplace.application.product.dto.command;

import java.util.List;

/**
 * 상품(SKU) 일괄 등록 Command.
 *
 * <p>ProductGroup 등록/수정 시 Products를 함께 생성할 때 사용합니다. 각 ProductData의 selectedOptions가 이름 기반으로 옵션을
 * 지정하며, Coordinator에서 실제 SellerOptionValueId로 resolve합니다.
 *
 * @param productGroupId 상품 그룹 ID
 * @param products 상품 데이터 목록
 */
public record RegisterProductsCommand(long productGroupId, List<ProductData> products) {

    /**
     * 개별 상품 생성 데이터.
     *
     * <p>salePrice와 discountRate는 도메인 내부에서 자동 계산됩니다.
     *
     * @param skuCode SKU 코드
     * @param regularPrice 정가
     * @param currentPrice 판매가
     * @param stockQuantity 재고 수량
     * @param sortOrder 정렬 순서
     * @param selectedOptions 이름 기반 옵션 선택 목록
     */
    public record ProductData(
            String skuCode,
            int regularPrice,
            int currentPrice,
            int stockQuantity,
            int sortOrder,
            List<SelectedOption> selectedOptions) {}
}
