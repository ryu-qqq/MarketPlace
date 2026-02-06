package com.ryuqq.marketplace.domain.brand.exception;

/** 브랜드 코드 중복 예외. */
public class BrandCodeDuplicateException extends BrandException {

    private static final BrandErrorCode ERROR_CODE = BrandErrorCode.BRAND_CODE_DUPLICATE;

    public BrandCodeDuplicateException() {
        super(ERROR_CODE);
    }

    public BrandCodeDuplicateException(String code) {
        super(ERROR_CODE, String.format("브랜드 코드 '%s'가 이미 존재합니다", code));
    }
}
