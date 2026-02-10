package com.ryuqq.marketplace.domain.saleschannelbrand.exception;

public class SalesChannelBrandCodeDuplicateException extends SalesChannelBrandException {

    private static final SalesChannelBrandErrorCode ERROR_CODE =
            SalesChannelBrandErrorCode.SALES_CHANNEL_BRAND_CODE_DUPLICATE;

    public SalesChannelBrandCodeDuplicateException() {
        super(ERROR_CODE);
    }

    public SalesChannelBrandCodeDuplicateException(String code) {
        super(ERROR_CODE, String.format("외부 브랜드 코드 '%s'은(는) 이미 존재합니다", code));
    }
}
