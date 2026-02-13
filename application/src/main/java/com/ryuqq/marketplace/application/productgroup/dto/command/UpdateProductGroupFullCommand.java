package com.ryuqq.marketplace.application.productgroup.dto.command;

import java.util.List;

/**
 * 상품 그룹 전체 수정 Command.
 *
 * <p>ProductGroup + Description + Notice + Products 전체를 수정합니다.
 *
 * <p>Products는 기존 Product를 soft delete하고 새로 생성하는 전략을 사용합니다.
 */
public record UpdateProductGroupFullCommand(
        long productGroupId,
        String productGroupName,
        long brandId,
        long categoryId,
        long shippingPolicyId,
        long refundPolicyId,
        List<ImageCommand> images,
        List<OptionGroupCommand> optionGroups,
        List<ProductCommand> products,
        DescriptionCommand description,
        NoticeCommand notice) {

    /** 이미지 Command. */
    public record ImageCommand(String imageType, String originUrl, int sortOrder) {}

    /** 옵션 그룹 Command. */
    public record OptionGroupCommand(
            String optionGroupName,
            Long canonicalOptionGroupId,
            List<OptionValueCommand> optionValues) {}

    /** 옵션 값 Command. */
    public record OptionValueCommand(
            String optionValueName, Long canonicalOptionValueId, int sortOrder) {}

    /** 상품 Command. */
    public record ProductCommand(
            String skuCode,
            int regularPrice,
            int currentPrice,
            int salePrice,
            int discountRate,
            int stockQuantity,
            int sortOrder,
            List<Integer> optionIndices) {}

    /** 상세설명 Command. */
    public record DescriptionCommand(
            String content, List<DescriptionImageCommand> descriptionImages) {}

    /** 상세설명 이미지 Command. */
    public record DescriptionImageCommand(String originUrl, int sortOrder) {}

    /** 고시정보 Command. */
    public record NoticeCommand(long noticeCategoryId, List<NoticeEntryCommand> entries) {}

    /** 고시정보 항목 Command. */
    public record NoticeEntryCommand(long noticeFieldId, String fieldValue) {}
}
