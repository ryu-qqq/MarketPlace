package com.ryuqq.marketplace.domain.seller.exception;

/**
 * 비활성화된 셀러에 대한 작업 시 예외.
 *
 * <p>비활성화된 셀러에 대해 허용되지 않는 작업을 시도할 때 발생합니다.
 */
public class SellerInactiveException extends SellerException {

    private static final SellerErrorCode ERROR_CODE = SellerErrorCode.SELLER_INACTIVE;

    public SellerInactiveException() {
        super(ERROR_CODE);
    }
}
