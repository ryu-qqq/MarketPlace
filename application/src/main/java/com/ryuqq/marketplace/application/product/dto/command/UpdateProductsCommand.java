package com.ryuqq.marketplace.application.product.dto.command;

import java.util.List;

/**
 * 상품(SKU) 일괄 수정 Command.
 *
 * <p>옵션 구조 + 상품(가격/재고/SKU/정렬/옵션매핑)을 함께 수정합니다.
 *
 * <p>optionGroups의 ID 기반 diff로 SellerOption을 먼저 수정한 후, resolvedActiveValueIds를 기반으로 Product의
 * optionMappings를 갱신합니다.
 *
 * @param productGroupId 상품 그룹 ID
 * @param optionGroups 옵션 그룹 수정 데이터 목록
 * @param products 수정할 상품 데이터 목록
 */
public record UpdateProductsCommand(
        long productGroupId, List<OptionGroupData> optionGroups, List<ProductData> products) {

    /**
     * 개별 상품 수정 데이터.
     *
     * <p>productId가 null이면 신규 Product, non-null이면 기존 Product를 의미합니다.
     *
     * <p>salePrice와 discountRate는 도메인 내부에서 자동 계산됩니다.
     *
     * @param productId 수정 대상 상품 ID (nullable: null이면 신규)
     * @param skuCode SKU 코드
     * @param regularPrice 정가
     * @param currentPrice 판매가
     * @param stockQuantity 재고 수량
     * @param sortOrder 정렬 순서
     * @param selectedOptions 이름 기반 옵션 선택 목록
     */
    public record ProductData(
            Long productId,
            String skuCode,
            int regularPrice,
            int currentPrice,
            int stockQuantity,
            int sortOrder,
            List<SelectedOption> selectedOptions) {

        public ProductData {
            selectedOptions = List.copyOf(selectedOptions);
        }
    }

    /**
     * 옵션 그룹 수정 데이터.
     *
     * @param sellerOptionGroupId 기존 옵션 그룹 ID (nullable: null이면 신규)
     * @param optionGroupName 옵션 그룹명
     * @param canonicalOptionGroupId 표준 옵션 그룹 ID
     * @param optionValues 옵션 값 목록
     */
    public record OptionGroupData(
            Long sellerOptionGroupId,
            String optionGroupName,
            Long canonicalOptionGroupId,
            List<OptionValueData> optionValues) {}

    /**
     * 옵션 값 수정 데이터.
     *
     * @param sellerOptionValueId 기존 옵션 값 ID (nullable: null이면 신규)
     * @param optionValueName 옵션 값명
     * @param canonicalOptionValueId 표준 옵션 값 ID
     * @param sortOrder 정렬 순서
     */
    public record OptionValueData(
            Long sellerOptionValueId,
            String optionValueName,
            Long canonicalOptionValueId,
            int sortOrder) {}
}
