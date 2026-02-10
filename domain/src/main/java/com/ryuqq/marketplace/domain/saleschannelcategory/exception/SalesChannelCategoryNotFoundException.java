package com.ryuqq.marketplace.domain.saleschannelcategory.exception;

public class SalesChannelCategoryNotFoundException extends SalesChannelCategoryException {

    private static final SalesChannelCategoryErrorCode ERROR_CODE =
            SalesChannelCategoryErrorCode.SALES_CHANNEL_CATEGORY_NOT_FOUND;

    public SalesChannelCategoryNotFoundException() {
        super(ERROR_CODE);
    }

    public SalesChannelCategoryNotFoundException(Long id) {
        super(ERROR_CODE, String.format("ID가 %d인 외부 채널 카테고리를 찾을 수 없습니다", id));
    }
}
