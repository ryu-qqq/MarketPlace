package com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.dto;

import java.time.LocalDateTime;

/**
 * 레거시 상품그룹 기본 정보 Projection DTO.
 *
 * <p>Projections.constructor()로 매핑 (Q클래스 생성 불필요).
 *
 * <p>7개 테이블 JOIN: product_group, seller, brand, category, product_delivery,
 * product_group_detail_description, product_notice
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public record LegacyProductGroupBasicQueryDto(
        // product_group
        long productGroupId,
        String productGroupName,
        long sellerId,
        long brandId,
        long categoryId,
        String optionType,
        String managementType,
        long regularPrice,
        long currentPrice,
        long salePrice,
        long directDiscountPrice,
        int directDiscountRate,
        int discountRate,
        String soldOutYn,
        String displayYn,
        String productCondition,
        String origin,
        String styleCode,
        String insertOperator,
        String updateOperator,
        LocalDateTime insertDate,
        LocalDateTime updateDate,
        // seller
        String sellerName,
        // brand
        String brandName,
        // category
        String categoryPath,
        // delivery
        String deliveryArea,
        Integer deliveryFee,
        Integer deliveryPeriodAverage,
        String returnMethodDomestic,
        String returnCourierDomestic,
        Integer returnChargeDomestic,
        String returnExchangeAreaDomestic,
        // description
        String detailDescription,
        // notice
        String material,
        String color,
        String noticeSize,
        String maker,
        String noticeOrigin,
        String washingMethod,
        String yearMonthDay,
        String assuranceStandard,
        String asPhone) {}
