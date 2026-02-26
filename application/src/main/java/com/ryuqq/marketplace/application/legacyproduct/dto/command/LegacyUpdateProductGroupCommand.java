package com.ryuqq.marketplace.application.legacyproduct.dto.command;

import java.util.List;

/**
 * 레거시 상품그룹 전체 수정 Command.
 *
 * <p>updateStatus 플래그에 따라 변경된 섹션만 선택적으로 업데이트합니다.
 *
 * <ul>
 *   <li>productStatus → productGroupDetails (상품명, 가격, 상태, 의류상세 등)
 *   <li>noticeStatus → notice (고시정보)
 *   <li>deliveryStatus / refundStatus → delivery (배송/반품정보)
 *   <li>descriptionStatus → detailDescription (상세설명)
 *   <li>imageStatus → images (이미지)
 *   <li>stockOptionStatus → options (옵션/재고)
 * </ul>
 */
public record LegacyUpdateProductGroupCommand(
        long productGroupId,
        ProductGroupDetailsCommand productGroupDetails,
        NoticeCommand notice,
        DeliveryCommand delivery,
        String detailDescription,
        List<ImageCommand> images,
        List<OptionCommand> options,
        UpdateStatusCommand updateStatus) {

    public LegacyUpdateProductGroupCommand {
        images = images == null ? List.of() : List.copyOf(images);
        options = options == null ? List.of() : List.copyOf(options);
    }

    /** 상품그룹 기본정보 (productGroupDetails). */
    public record ProductGroupDetailsCommand(
            String productGroupName,
            String optionType,
            String managementType,
            long regularPrice,
            long currentPrice,
            String soldOutYn,
            String displayYn,
            String productCondition,
            String origin,
            String styleCode,
            long sellerId,
            long categoryId,
            long brandId) {}

    /** 고시정보. */
    public record NoticeCommand(
            String material,
            String color,
            String size,
            String maker,
            String origin,
            String washingMethod,
            String yearMonthDay,
            String assuranceStandard,
            String asPhone) {}

    /** 배송/반품정보. */
    public record DeliveryCommand(
            String deliveryArea,
            long deliveryFee,
            int deliveryPeriodAverage,
            String returnMethodDomestic,
            String returnCourierDomestic,
            int returnChargeDomestic,
            String returnExchangeAreaDomestic) {}

    /** 이미지. */
    public record ImageCommand(String imageType, String imageUrl, String originUrl) {}

    /** 옵션/상품 (SKU 단위). */
    public record OptionCommand(
            Long productId,
            int quantity,
            long additionalPrice,
            List<OptionDetailCommand> optionDetails) {

        public OptionCommand {
            optionDetails = optionDetails == null ? List.of() : List.copyOf(optionDetails);
        }
    }

    /** 옵션 상세. */
    public record OptionDetailCommand(
            Long optionGroupId, Long optionDetailId, String optionName, String optionValue) {}

    /** 수정 대상 플래그. */
    public record UpdateStatusCommand(
            boolean productStatus,
            boolean noticeStatus,
            boolean imageStatus,
            boolean descriptionStatus,
            boolean stockOptionStatus,
            boolean deliveryStatus,
            boolean refundStatus) {}
}
