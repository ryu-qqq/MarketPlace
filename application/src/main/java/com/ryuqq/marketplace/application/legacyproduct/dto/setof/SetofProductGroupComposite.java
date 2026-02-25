package com.ryuqq.marketplace.application.legacyproduct.dto.setof;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 세토프 DB에서 조회한 상품그룹 전체 복합 데이터.
 *
 * <p>LEGACY_IMPORTED 폴백 경로 및 배치 전환에서 사용됩니다.
 */
public record SetofProductGroupComposite(
        ProductGroupData productGroup,
        List<ProductData> products,
        List<ProductOptionData> productOptions,
        Map<Long, Integer> stockByProductId,
        List<ImageData> images,
        DetailDescriptionData detailDescription,
        NoticeData notice,
        DeliveryData delivery,
        List<OptionGroupData> optionGroups,
        List<OptionDetailData> optionDetails) {

    public SetofProductGroupComposite {
        products = List.copyOf(products);
        productOptions = List.copyOf(productOptions);
        stockByProductId = Map.copyOf(stockByProductId);
        images = List.copyOf(images);
        optionGroups = List.copyOf(optionGroups);
        optionDetails = List.copyOf(optionDetails);
    }

    public record ProductGroupData(
            long id,
            String externalProductUuid,
            String productGroupName,
            long sellerId,
            long brandId,
            long categoryId,
            String optionType,
            long regularPrice,
            long currentPrice,
            long salePrice,
            int directDiscountRate,
            long directDiscountPrice,
            int discountRate,
            String soldOutYn,
            String displayYn,
            String productCondition,
            String origin,
            String styleCode,
            String managementType,
            String deleteYn,
            LocalDateTime insertDate,
            LocalDateTime updateDate) {}

    public record ProductData(
            long id,
            long productGroupId,
            String soldOutYn,
            String displayYn,
            String deleteYn,
            LocalDateTime insertDate,
            LocalDateTime updateDate) {}

    public record ProductOptionData(
            long id,
            long productId,
            long optionGroupId,
            long optionDetailId,
            long additionalPrice) {}

    public record ImageData(
            long id,
            long productGroupId,
            String productGroupImageType,
            String imageUrl,
            String originUrl,
            Long displayOrder) {}

    public record DetailDescriptionData(
            long productGroupId, String productGroupImageType, String imageUrl, String imageUrls) {}

    public record NoticeData(
            long productGroupId,
            String material,
            String color,
            String size,
            String maker,
            String origin,
            String washingMethod,
            String yearMonthDay,
            String assuranceStandard,
            String asPhone) {}

    public record DeliveryData(
            long productGroupId,
            String deliveryArea,
            Integer deliveryFee,
            Integer deliveryPeriodAverage,
            String returnMethodDomestic,
            String returnCourierDomestic,
            Integer returnChargeDomestic,
            String returnExchangeAreaDomestic) {}

    public record OptionGroupData(long id, String optionName) {}

    public record OptionDetailData(long id, long optionGroupId, String optionValue) {}
}
