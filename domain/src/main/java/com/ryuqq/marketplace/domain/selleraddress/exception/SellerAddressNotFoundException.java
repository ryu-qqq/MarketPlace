package com.ryuqq.marketplace.domain.selleraddress.exception;

/** 셀러 주소를 찾을 수 없는 경우 예외. */
public class SellerAddressNotFoundException extends SellerAddressException {

    private static final SellerAddressErrorCode ERROR_CODE =
            SellerAddressErrorCode.SELLER_ADDRESS_NOT_FOUND;

    public SellerAddressNotFoundException() {
        super(ERROR_CODE);
    }
}
