package com.ryuqq.marketplace.domain.inboundproduct.vo;

import java.util.List;

/**
 * 인바운드 상품의 정형화된 페이로드 VO.
 *
 * <p>rawPayloadJson(비정형 JSON)을 대체하여 이미지, 옵션, 상품, 고시정보를 정형 필드로 관리합니다. 내부 상품 등록
 * API(RegisterProductGroupApiRequest)와 동일한 구조를 따릅니다.
 */
public record InboundProductPayload(
        List<InboundImageData> images,
        List<InboundOptionGroupData> optionGroups,
        List<InboundProductData> products,
        List<InboundNoticeEntry> noticeEntries) {

    public InboundProductPayload {
        images = images != null ? List.copyOf(images) : List.of();
        optionGroups = optionGroups != null ? List.copyOf(optionGroups) : List.of();
        products = products != null ? List.copyOf(products) : List.of();
        noticeEntries = noticeEntries != null ? List.copyOf(noticeEntries) : List.of();
    }

    public record InboundImageData(String imageType, String originUrl, int sortOrder) {}

    public record InboundOptionGroupData(
            String optionGroupName, String inputType, List<InboundOptionValueData> optionValues) {

        public InboundOptionGroupData {
            optionValues = optionValues != null ? List.copyOf(optionValues) : List.of();
        }

        public record InboundOptionValueData(String optionValueName, int sortOrder) {}
    }

    public record InboundProductData(
            String skuCode,
            int regularPrice,
            int currentPrice,
            int stockQuantity,
            int sortOrder,
            List<InboundSelectedOption> selectedOptions) {

        public InboundProductData {
            selectedOptions = selectedOptions != null ? List.copyOf(selectedOptions) : List.of();
        }

        public record InboundSelectedOption(String optionGroupName, String optionValueName) {}
    }

    public record InboundNoticeEntry(String fieldCode, String fieldValue) {}
}
