package com.ryuqq.marketplace.domain.brandmapping.exception;

/** 해당 외부 브랜드에 이미 매핑이 존재할 때 예외. */
public class BrandMappingDuplicateException extends BrandMappingException {

    private static final BrandMappingErrorCode ERROR_CODE =
            BrandMappingErrorCode.BRAND_MAPPING_DUPLICATE;

    public BrandMappingDuplicateException() {
        super(ERROR_CODE);
    }

    public BrandMappingDuplicateException(Long salesChannelBrandId) {
        super(ERROR_CODE, String.format("외부 브랜드 ID '%d'에 이미 매핑이 존재합니다", salesChannelBrandId));
    }
}
