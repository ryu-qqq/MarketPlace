package com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.dto;

/**
 * 세토프 DB 상품+옵션+재고 flat projection DTO.
 *
 * <p>Product + ProductStock + ProductOption + OptionGroup + OptionDetail 5테이블 조인의 결과 행입니다. 한 상품이 여러
 * 옵션을 가지면 여러 행으로 반환됩니다.
 *
 * @param productId 상품 ID
 * @param productGroupId 상품그룹 ID
 * @param soldOutYn 품절 여부 (Y/N)
 * @param stockQuantity 재고 수량
 * @param optionGroupId 옵션그룹 ID (null 가능 - 옵션 없는 상품)
 * @param optionDetailId 옵션상세 ID (null 가능)
 * @param optionGroupName 옵션그룹명 (null 가능)
 * @param optionValue 옵션값 (null 가능)
 */
public record LegacyProductOptionQueryDto(
        long productId,
        long productGroupId,
        String soldOutYn,
        int stockQuantity,
        Long optionGroupId,
        Long optionDetailId,
        String optionGroupName,
        String optionValue) {}
