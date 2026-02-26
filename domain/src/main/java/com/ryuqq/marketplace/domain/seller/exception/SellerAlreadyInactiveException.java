package com.ryuqq.marketplace.domain.seller.exception;

/**
 * 이미 비활성화된 셀러를 다시 비활성화 시도할 때 예외.
 *
 * <p>이미 비활성(active=false) 상태인 셀러에 대해 deactivate()를 호출할 때 발생합니다.
 */
public class SellerAlreadyInactiveException extends SellerException {

    private static final SellerErrorCode ERROR_CODE = SellerErrorCode.SELLER_ALREADY_INACTIVE;

    public SellerAlreadyInactiveException() {
        super(ERROR_CODE);
    }

    public SellerAlreadyInactiveException(Long sellerId) {
        super(ERROR_CODE, String.format("ID가 %d인 셀러는 이미 비활성화 상태입니다", sellerId));
    }
}
