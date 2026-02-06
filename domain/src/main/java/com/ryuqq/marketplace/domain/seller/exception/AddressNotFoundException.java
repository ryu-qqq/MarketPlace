package com.ryuqq.marketplace.domain.seller.exception;

/**
 * 주소를 찾을 수 없는 경우 예외.
 *
 * <p>요청한 주소 정보가 존재하지 않을 때 발생합니다.
 */
public class AddressNotFoundException extends SellerException {

    private static final SellerErrorCode ERROR_CODE = SellerErrorCode.ADDRESS_NOT_FOUND;

    public AddressNotFoundException() {
        super(ERROR_CODE);
    }
}
