package com.ryuqq.marketplace.domain.brandmapping.exception;

/** 브랜드 매핑을 찾을 수 없을 때 예외. */
public class BrandMappingNotFoundException extends BrandMappingException {

    private static final BrandMappingErrorCode ERROR_CODE =
            BrandMappingErrorCode.BRAND_MAPPING_NOT_FOUND;

    public BrandMappingNotFoundException() {
        super(ERROR_CODE);
    }

    public BrandMappingNotFoundException(Long brandMappingId) {
        super(ERROR_CODE, String.format("브랜드 매핑을 찾을 수 없습니다 (id: %d)", brandMappingId));
    }

    public BrandMappingNotFoundException(Long salesChannelId, Long internalBrandId) {
        super(
                ERROR_CODE,
                String.format(
                        "브랜드 매핑을 찾을 수 없습니다 (salesChannelId: %d, internalBrandId: %d)",
                        salesChannelId, internalBrandId));
    }
}
