package com.ryuqq.marketplace.domain.legacy.productgroup.vo;

/**
 * 레거시(세토프) 상품그룹 기본정보 업데이트 데이터 VO.
 *
 * <p>productGroup 테이블의 기본정보(상품명, 가격, 상태, 의류상세 등)를 일괄 업데이트할 때 사용합니다.
 */
public record LegacyProductGroupUpdateData(
        String productGroupName,
        long sellerId,
        long brandId,
        long categoryId,
        OptionType optionType,
        ManagementType managementType,
        long regularPrice,
        long currentPrice,
        String soldOutYn,
        String displayYn,
        ProductCondition productCondition,
        Origin origin,
        String styleCode) {}
