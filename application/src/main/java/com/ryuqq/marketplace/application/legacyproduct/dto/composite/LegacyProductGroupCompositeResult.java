package com.ryuqq.marketplace.application.legacyproduct.dto.composite;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 세토프 DB 상품그룹 Composite 조회 결과.
 *
 * <p>ProductGroup + Seller + Brand + Category + Delivery + Description + Notice + Image 조인 결과입니다.
 * Product(SKU) 데이터는 제외됩니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public record LegacyProductGroupCompositeResult(
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
        List<ImageInfo> images,
        String detailDescription,
        NoticeInfo notice,
        DeliveryInfo delivery) {

    public record ImageInfo(String imageType, String imageUrl) {}

    public record NoticeInfo(
            String material,
            String color,
            String size,
            String maker,
            String origin,
            String washingMethod,
            String yearMonthDay,
            String assuranceStandard,
            String asPhone) {}

    public record DeliveryInfo(
            String deliveryArea,
            Integer deliveryFee,
            Integer deliveryPeriodAverage,
            String returnMethodDomestic,
            String returnCourierDomestic,
            Integer returnChargeDomestic,
            String returnExchangeAreaDomestic) {}
}
