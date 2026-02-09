package com.ryuqq.marketplace.domain.shop.exception;

/**
 * 외부몰을 찾을 수 없는 경우 예외.
 *
 * <p>요청한 ID에 해당하는 외부몰이 존재하지 않을 때 발생합니다.
 */
public class ShopNotFoundException extends ShopException {

    private static final ShopErrorCode ERROR_CODE = ShopErrorCode.SHOP_NOT_FOUND;

    public ShopNotFoundException() {
        super(ERROR_CODE);
    }

    public ShopNotFoundException(Long shopId) {
        super(ERROR_CODE, String.format("ID가 %d인 외부몰을 찾을 수 없습니다", shopId));
    }
}
