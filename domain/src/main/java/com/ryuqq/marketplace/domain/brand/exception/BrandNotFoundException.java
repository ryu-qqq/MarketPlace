package com.ryuqq.marketplace.domain.brand.exception;

/**
 * 브랜드를 찾을 수 없는 경우 예외.
 *
 * <p>요청한 ID에 해당하는 브랜드가 존재하지 않을 때 발생합니다.
 */
public class BrandNotFoundException extends BrandException {

    private static final BrandErrorCode ERROR_CODE = BrandErrorCode.BRAND_NOT_FOUND;

    public BrandNotFoundException() {
        super(ERROR_CODE);
    }

    public BrandNotFoundException(Long brandId) {
        super(ERROR_CODE, String.format("ID가 %d인 브랜드를 찾을 수 없습니다", brandId));
    }

    public BrandNotFoundException(String brandCode) {
        super(ERROR_CODE, String.format("코드가 %s인 브랜드를 찾을 수 없습니다", brandCode));
    }
}
