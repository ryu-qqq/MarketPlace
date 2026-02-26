package com.ryuqq.marketplace.domain.inboundproduct.vo;

/** InboundProduct 수정 데이터 VO. 재수신 시 변경 감지에 사용. */
public record InboundProductUpdateData(
        String productName,
        String externalBrandCode,
        String externalCategoryCode,
        int regularPrice,
        int currentPrice,
        String optionType,
        String descriptionHtml,
        InboundProductPayload payload) {

    public static InboundProductUpdateData of(
            String productName,
            String externalBrandCode,
            String externalCategoryCode,
            int regularPrice,
            int currentPrice,
            String optionType,
            String descriptionHtml,
            InboundProductPayload payload) {
        return new InboundProductUpdateData(
                productName,
                externalBrandCode,
                externalCategoryCode,
                regularPrice,
                currentPrice,
                optionType,
                descriptionHtml,
                payload);
    }
}
