package com.ryuqq.marketplace.domain.brandpreset.exception;

/** 판매채널 브랜드를 찾을 수 없을 때 예외. */
public class BrandPresetSalesChannelBrandNotFoundException extends BrandPresetException {

    private static final BrandPresetErrorCode ERROR_CODE =
            BrandPresetErrorCode.BRAND_PRESET_SALES_CHANNEL_BRAND_NOT_FOUND;

    public BrandPresetSalesChannelBrandNotFoundException(Long salesChannelBrandId) {
        super(ERROR_CODE, String.format("판매채널 브랜드를 찾을 수 없습니다 (id: %d)", salesChannelBrandId));
    }
}
