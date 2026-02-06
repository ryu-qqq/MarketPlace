package com.ryuqq.marketplace.domain.selleraddress.exception;

/** 기본 주소를 삭제할 수 없는 경우 예외. */
public class CannotDeleteDefaultAddressException extends SellerAddressException {

    private static final SellerAddressErrorCode ERROR_CODE =
            SellerAddressErrorCode.CANNOT_DELETE_DEFAULT_ADDRESS;

    public CannotDeleteDefaultAddressException() {
        super(ERROR_CODE);
    }
}
