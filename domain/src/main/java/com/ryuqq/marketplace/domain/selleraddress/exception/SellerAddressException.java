package com.ryuqq.marketplace.domain.selleraddress.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;

/** 셀러 주소 도메인 예외. */
public class SellerAddressException extends DomainException {

    public SellerAddressException(SellerAddressErrorCode errorCode) {
        super(errorCode);
    }

    public SellerAddressException(SellerAddressErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public SellerAddressException(SellerAddressErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
