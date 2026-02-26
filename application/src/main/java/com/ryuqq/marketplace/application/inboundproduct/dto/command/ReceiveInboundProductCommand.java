package com.ryuqq.marketplace.application.inboundproduct.dto.command;

import java.util.List;

/**
 * 인바운드 상품 수신 Command.
 *
 * <p>내부 상품 등록({@code RegisterProductGroupCommand})과 동일한 구조로 이미지/옵션/상품/설명/고시정보를 직접 필드로 관리합니다. 인바운드
 * 고유 필드(inboundSourceId, externalProductCode, externalBrandCode, externalCategoryCode)만 추가됩니다.
 */
public record ReceiveInboundProductCommand(
        long inboundSourceId,
        String externalProductCode,
        String productName,
        String externalBrandCode,
        String externalCategoryCode,
        long sellerId,
        int regularPrice,
        int currentPrice,
        String optionType,
        List<ImageCommand> images,
        List<OptionGroupCommand> optionGroups,
        List<ProductCommand> products,
        DescriptionCommand description,
        NoticeCommand notice) {

    public record ImageCommand(String imageType, String originUrl, int sortOrder) {}

    public record OptionGroupCommand(
            String optionGroupName, String inputType, List<OptionValueCommand> optionValues) {}

    public record OptionValueCommand(String optionValueName, int sortOrder) {}

    public record ProductCommand(
            String skuCode,
            int regularPrice,
            int currentPrice,
            int stockQuantity,
            int sortOrder,
            List<SelectedOptionCommand> selectedOptions) {}

    public record SelectedOptionCommand(String optionGroupName, String optionValueName) {}

    public record DescriptionCommand(String content) {}

    public record NoticeCommand(List<NoticeEntryCommand> entries) {}

    public record NoticeEntryCommand(String fieldCode, String fieldValue) {}
}
