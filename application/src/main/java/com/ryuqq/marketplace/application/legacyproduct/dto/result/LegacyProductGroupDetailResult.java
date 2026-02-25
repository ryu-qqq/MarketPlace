package com.ryuqq.marketplace.application.legacyproduct.dto.result;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 레거시 상품그룹 상세 조회 결과.
 *
 * <p>세토프 DB에서 조회한 상품그룹 상세를 레거시 API 응답에 적합한 형태로 가공한 결과입니다. 세토프 OMS ProductGroupFetchResponse와 동일한
 * 데이터를 포함합니다.
 */
public record LegacyProductGroupDetailResult(
        long productGroupId,
        String productGroupName,
        long sellerId,
        String sellerName,
        long brandId,
        String brandName,
        long categoryId,
        String categoryPath,
        String optionType,
        String managementType,
        long regularPrice,
        long currentPrice,
        long salePrice,
        long directDiscountPrice,
        int directDiscountRate,
        int discountRate,
        boolean soldOut,
        boolean displayed,
        String productCondition,
        String origin,
        String styleCode,
        String insertOperator,
        String updateOperator,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LegacyNoticeResult notice,
        List<LegacyImageResult> images,
        String detailDescription,
        LegacyDeliveryResult delivery,
        List<LegacyProductResult> products) {

    public record LegacyNoticeResult(
            String material,
            String color,
            String size,
            String maker,
            String origin,
            String washingMethod,
            String yearMonthDay,
            String assuranceStandard,
            String asPhone) {}

    public record LegacyImageResult(String imageType, String imageUrl) {}

    public record LegacyDeliveryResult(
            String deliveryArea,
            Integer deliveryFee,
            Integer deliveryPeriodAverage,
            String returnMethodDomestic,
            String returnCourierDomestic,
            Integer returnChargeDomestic,
            String returnExchangeAreaDomestic) {}

    public record LegacyProductResult(
            long productId,
            int stockQuantity,
            boolean soldOut,
            List<LegacyOptionMappingResult> options) {}

    public record LegacyOptionMappingResult(
            long optionGroupId, long optionDetailId, String optionGroupName, String optionValue) {}
}
