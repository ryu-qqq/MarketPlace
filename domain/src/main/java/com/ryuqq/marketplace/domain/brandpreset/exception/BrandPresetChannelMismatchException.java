package com.ryuqq.marketplace.domain.brandpreset.exception;

/** Shop과 브랜드의 판매채널이 일치하지 않을 때 예외. */
public class BrandPresetChannelMismatchException extends BrandPresetException {

    private static final BrandPresetErrorCode ERROR_CODE =
            BrandPresetErrorCode.BRAND_PRESET_CHANNEL_MISMATCH;

    public BrandPresetChannelMismatchException(Long shopSalesChannelId, Long brandSalesChannelId) {
        super(
                ERROR_CODE,
                String.format(
                        "Shop(salesChannelId: %d)과 SalesChannelBrand(salesChannelId: %d)의"
                                + " 판매채널이 일치하지 않습니다",
                        shopSalesChannelId, brandSalesChannelId));
    }
}
