package com.ryuqq.marketplace.application.inboundproduct.dto.response;

import java.util.List;

/**
 * 인바운드 상품 상세 조회 결과 DTO.
 *
 * <p>외부 시스템이 productId 목록을 얻을 수 있도록 인바운드 상태 + 내부 상품 목록을 제공합니다.
 *
 * @param status 인바운드 상품 상태 (RECEIVED, PENDING_MAPPING, MAPPED, CONVERTED, CONVERT_FAILED)
 * @param externalProductCode 외부 상품 코드
 * @param internalProductGroupId 내부 상품 그룹 ID (미변환 시 null)
 * @param products 내부 상품 목록 (미변환 시 빈 리스트)
 */
public record InboundProductDetailResult(
        String status,
        String externalProductCode,
        Long internalProductGroupId,
        List<ProductItem> products) {

    public InboundProductDetailResult {
        products = products != null ? List.copyOf(products) : List.of();
    }

    /**
     * 개별 상품(SKU) 요약 정보.
     *
     * @param productId 상품 ID
     * @param skuCode SKU 코드
     * @param regularPrice 정가
     * @param currentPrice 현재가
     * @param stockQuantity 재고 수량
     * @param sortOrder 정렬 순서
     * @param options 옵션 매핑 목록
     */
    public record ProductItem(
            long productId,
            String skuCode,
            int regularPrice,
            int currentPrice,
            int stockQuantity,
            int sortOrder,
            List<OptionItem> options) {

        public ProductItem {
            options = options != null ? List.copyOf(options) : List.of();
        }
    }

    /**
     * 옵션 매핑 정보.
     *
     * @param optionGroupName 옵션 그룹명
     * @param optionValueName 옵션 값명
     */
    public record OptionItem(String optionGroupName, String optionValueName) {}
}
