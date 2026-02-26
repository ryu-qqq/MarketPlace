package com.ryuqq.marketplace.domain.saleschannelbrand.exception;

public class SalesChannelBrandNotFoundException extends SalesChannelBrandException {

    private static final SalesChannelBrandErrorCode ERROR_CODE =
            SalesChannelBrandErrorCode.SALES_CHANNEL_BRAND_NOT_FOUND;

    public SalesChannelBrandNotFoundException() {
        super(ERROR_CODE);
    }

    public SalesChannelBrandNotFoundException(Long id) {
        super(ERROR_CODE, String.format("ID가 %d인 외부 채널 브랜드를 찾을 수 없습니다", id));
    }
}
