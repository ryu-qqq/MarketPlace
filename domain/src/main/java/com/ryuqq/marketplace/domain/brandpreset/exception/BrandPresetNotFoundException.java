package com.ryuqq.marketplace.domain.brandpreset.exception;

/** 브랜드 프리셋을 찾을 수 없을 때 예외. */
public class BrandPresetNotFoundException extends BrandPresetException {

    private static final BrandPresetErrorCode ERROR_CODE =
            BrandPresetErrorCode.BRAND_PRESET_NOT_FOUND;

    public BrandPresetNotFoundException() {
        super(ERROR_CODE);
    }

    public BrandPresetNotFoundException(Long brandPresetId) {
        super(ERROR_CODE, String.format("브랜드 프리셋을 찾을 수 없습니다 (id: %d)", brandPresetId));
    }
}
