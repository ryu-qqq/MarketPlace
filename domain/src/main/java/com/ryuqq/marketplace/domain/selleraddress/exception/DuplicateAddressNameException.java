package com.ryuqq.marketplace.domain.selleraddress.exception;

/** 동일 셀러·배송지 타입 내 배송지 이름 중복 시 예외. */
public class DuplicateAddressNameException extends SellerAddressException {

    private static final SellerAddressErrorCode ERROR_CODE =
            SellerAddressErrorCode.DUPLICATE_ADDRESS_NAME;

    public DuplicateAddressNameException() {
        super(ERROR_CODE);
    }
}
