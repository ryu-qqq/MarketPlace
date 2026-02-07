package com.ryuqq.marketplace.domain.seller.exception;

/**
 * 사업자 정보를 찾을 수 없는 경우 예외.
 *
 * <p>셀러의 사업자 정보가 등록되지 않았거나 삭제된 경우 발생합니다.
 */
public class BusinessInfoNotFoundException extends SellerException {

    private static final SellerErrorCode ERROR_CODE = SellerErrorCode.BUSINESS_INFO_NOT_FOUND;

    public BusinessInfoNotFoundException() {
        super(ERROR_CODE);
    }
}
