package com.ryuqq.marketplace.domain.categorypreset.exception;

/** 판매채널 카테고리를 찾을 수 없을 때 예외. */
public class CategoryPresetSalesChannelCategoryNotFoundException extends CategoryPresetException {

    private static final CategoryPresetErrorCode ERROR_CODE =
            CategoryPresetErrorCode.CATEGORY_PRESET_SALES_CHANNEL_CATEGORY_NOT_FOUND;

    public CategoryPresetSalesChannelCategoryNotFoundException(String categoryCode) {
        super(ERROR_CODE, String.format("카테고리 코드 '%s'에 해당하는 판매채널 카테고리를 찾을 수 없습니다", categoryCode));
    }
}
