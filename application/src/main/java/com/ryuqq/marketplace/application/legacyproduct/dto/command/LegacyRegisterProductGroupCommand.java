package com.ryuqq.marketplace.application.legacyproduct.dto.command;

import java.util.List;

/**
 * 레거시 상품그룹 등록 Command.
 *
 * <p>luxurydb에 직접 INSERT하기 위한 전체 데이터를 포함합니다. 내부 record로 하위 테이블 데이터를 그룹화합니다.
 */
public record LegacyRegisterProductGroupCommand(
        long sellerId,
        long brandId,
        long categoryId,
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
        NoticeCommand notice,
        DeliveryCommand delivery,
        List<ImageCommand> images,
        String detailDescription,
        List<OptionCommand> options) {

    public LegacyRegisterProductGroupCommand {
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
            int quantity, long additionalPrice, List<OptionDetailCommand> optionDetails) {

        public OptionCommand {
            optionDetails = optionDetails == null ? List.of() : List.copyOf(optionDetails);
        }
    }

    public record OptionDetailCommand(String optionName, String optionValue) {}
}
