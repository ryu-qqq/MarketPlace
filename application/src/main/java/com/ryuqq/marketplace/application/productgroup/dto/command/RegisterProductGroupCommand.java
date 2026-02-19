package com.ryuqq.marketplace.application.productgroup.dto.command;

import com.ryuqq.marketplace.application.product.dto.command.SelectedOption;
import java.util.List;

/**
 * 상품 그룹 등록 Command.
 *
 * <p>ProductGroup + Description + Notice + Products를 한번에 등록합니다.
 */
public record RegisterProductGroupCommand(
        long sellerId,
        long brandId,
        long categoryId,
        long shippingPolicyId,
        long refundPolicyId,
        String productGroupName,
        String optionType,
        List<ImageCommand> images,
        List<OptionGroupCommand> optionGroups,
        List<ProductCommand> products,
        DescriptionCommand description,
        NoticeCommand notice) {

    public record ImageCommand(String imageType, String originUrl, int sortOrder) {}

    public record OptionGroupCommand(
            String optionGroupName,
            Long canonicalOptionGroupId,
            String inputType,
            List<OptionValueCommand> optionValues) {}

    public record OptionValueCommand(
            String optionValueName, Long canonicalOptionValueId, int sortOrder) {}

    public record ProductCommand(
            String skuCode,
            int regularPrice,
            int currentPrice,
            int stockQuantity,
            int sortOrder,
            List<SelectedOption> selectedOptions) {}

    public record DescriptionCommand(
            String content, List<DescriptionImageCommand> descriptionImages) {}

    public record DescriptionImageCommand(String originUrl, int sortOrder) {}

    public record NoticeCommand(long noticeCategoryId, List<NoticeEntryCommand> entries) {}

    public record NoticeEntryCommand(long noticeFieldId, String fieldValue) {}
}
