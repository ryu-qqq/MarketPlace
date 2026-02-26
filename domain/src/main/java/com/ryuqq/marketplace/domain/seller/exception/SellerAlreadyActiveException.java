package com.ryuqq.marketplace.domain.seller.exception;

/**
 * 이미 활성화된 셀러를 다시 활성화 시도할 때 예외.
 *
 * <p>이미 활성(active=true) 상태인 셀러에 대해 activate()를 호출할 때 발생합니다.
 */
public class SellerAlreadyActiveException extends SellerException {

    private static final SellerErrorCode ERROR_CODE = SellerErrorCode.SELLER_ALREADY_ACTIVE;

    public SellerAlreadyActiveException() {
        super(ERROR_CODE);
    }

    public SellerAlreadyActiveException(Long sellerId) {
        super(ERROR_CODE, String.format("ID가 %d인 셀러는 이미 활성화 상태입니다", sellerId));
    }
}
