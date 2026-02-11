package com.ryuqq.marketplace.domain.categorypreset.exception;

/** Shop과 카테고리의 판매채널이 일치하지 않을 때 예외. */
public class CategoryPresetChannelMismatchException extends CategoryPresetException {

    private static final CategoryPresetErrorCode ERROR_CODE =
            CategoryPresetErrorCode.CATEGORY_PRESET_CHANNEL_MISMATCH;

    public CategoryPresetChannelMismatchException(
            Long shopSalesChannelId, Long categorySalesChannelId) {
        super(
                ERROR_CODE,
                String.format(
                        "Shop(salesChannelId: %d)과 SalesChannelCategory(salesChannelId: %d)의"
                                + " 판매채널이 일치하지 않습니다",
                        shopSalesChannelId, categorySalesChannelId));
    }
}
