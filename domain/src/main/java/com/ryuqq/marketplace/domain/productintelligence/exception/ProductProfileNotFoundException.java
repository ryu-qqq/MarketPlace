package com.ryuqq.marketplace.domain.productintelligence.exception;

/**
 * 상품 프로파일을 찾을 수 없는 경우 예외.
 *
 * <p>요청한 ID에 해당하는 프로파일이 존재하지 않을 때 발생합니다.
 */
public class ProductProfileNotFoundException extends ProductIntelligenceException {

    private static final ProductIntelligenceErrorCode ERROR_CODE =
            ProductIntelligenceErrorCode.PROFILE_NOT_FOUND;

    public ProductProfileNotFoundException(Long profileId) {
        super(ERROR_CODE, String.format("ID가 %d인 상품 프로파일을 찾을 수 없습니다", profileId));
    }
}
