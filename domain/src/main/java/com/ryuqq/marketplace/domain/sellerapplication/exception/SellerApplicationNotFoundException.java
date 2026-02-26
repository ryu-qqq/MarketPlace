package com.ryuqq.marketplace.domain.sellerapplication.exception;

/**
 * 입점 신청을 찾을 수 없는 경우 예외.
 *
 * <p>요청한 ID에 해당하는 입점 신청이 존재하지 않을 때 발생합니다.
 */
public class SellerApplicationNotFoundException extends SellerApplicationException {

    private static final SellerApplicationErrorCode ERROR_CODE =
            SellerApplicationErrorCode.SELLER_APPLICATION_NOT_FOUND;

    public SellerApplicationNotFoundException() {
        super(ERROR_CODE);
    }

    public SellerApplicationNotFoundException(Long sellerApplicationId) {
        super(ERROR_CODE, String.format("ID가 %d인 입점 신청을 찾을 수 없습니다", sellerApplicationId));
    }
}
