package com.ryuqq.marketplace.domain.brandpreset.exception;

import java.util.List;

/** 요청한 내부 브랜드가 존재하지 않을 때 예외. */
public class BrandPresetInternalBrandNotFoundException extends BrandPresetException {

    private static final BrandPresetErrorCode ERROR_CODE =
            BrandPresetErrorCode.BRAND_PRESET_INTERNAL_BRAND_NOT_FOUND;

    public BrandPresetInternalBrandNotFoundException(List<Long> missingIds) {
        super(ERROR_CODE, String.format("존재하지 않는 내부 브랜드 ID: %s", missingIds));
    }
}
