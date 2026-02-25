package com.ryuqq.marketplace.application.legacyproduct.dto.command;

import java.util.List;

/**
 * 레거시 상품그룹 수정 Command.
 *
 * <p>productGroupId로 기존 엔티티를 조회한 뒤, 각 필드를 반영하고 Hibernate 더티체킹으로 변경분만 UPDATE합니다.
 *
 * <ul>
 *   <li>1:1 (notice, delivery, description): productGroupId로 조회 → 더티체킹
 *   <li>1:N images: originUrl 매칭 (신규 INSERT / 변경 UPDATE / 미포함 soft delete)
 *   <li>1:N options: productId 매칭 → 더티체킹
 * </ul>
 */
public record LegacyUpdateProductGroupCommand(
        long productGroupId,
        NoticeCommand notice,
        DeliveryCommand delivery,
        String detailDescription,
        List<ImageCommand> images,
        List<OptionCommand> options) {

    public LegacyUpdateProductGroupCommand {
        images = images == null ? List.of() : List.copyOf(images);
        options = options == null ? List.of() : List.copyOf(options);
    }

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

    public record DeliveryCommand(
            String deliveryArea,
            long deliveryFee,
            int deliveryPeriodAverage,
            String returnMethodDomestic,
            String returnCourierDomestic,
            int returnChargeDomestic,
            String returnExchangeAreaDomestic) {}

    public record ImageCommand(String imageType, String imageUrl, String originUrl) {}

    public record OptionCommand(
            Long productId,
            int quantity,
            long additionalPrice,
            List<OptionDetailCommand> optionDetails) {

        public OptionCommand {
            optionDetails = optionDetails == null ? List.of() : List.copyOf(optionDetails);
        }
    }

    public record OptionDetailCommand(
            Long optionGroupId, Long optionDetailId, String optionName, String optionValue) {}
}
