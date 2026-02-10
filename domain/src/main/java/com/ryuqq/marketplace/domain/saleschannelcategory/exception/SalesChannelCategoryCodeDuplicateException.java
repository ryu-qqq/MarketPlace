package com.ryuqq.marketplace.domain.saleschannelcategory.exception;

public class SalesChannelCategoryCodeDuplicateException extends SalesChannelCategoryException {

    private static final SalesChannelCategoryErrorCode ERROR_CODE =
            SalesChannelCategoryErrorCode.SALES_CHANNEL_CATEGORY_CODE_DUPLICATE;

    public SalesChannelCategoryCodeDuplicateException() {
        super(ERROR_CODE);
    }

    public SalesChannelCategoryCodeDuplicateException(String code) {
        super(ERROR_CODE, String.format("외부 카테고리 코드 '%s'은(는) 이미 존재합니다", code));
    }
}
