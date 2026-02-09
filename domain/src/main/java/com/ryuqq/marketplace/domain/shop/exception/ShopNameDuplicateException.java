package com.ryuqq.marketplace.domain.shop.exception;

/**
 * 중복된 외부몰명으로 등록/수정 시도할 때 예외.
 *
 * <p>이미 존재하는 외부몰명으로 신규 등록하거나 수정할 때 발생합니다.
 */
public class ShopNameDuplicateException extends ShopException {

    private static final ShopErrorCode ERROR_CODE = ShopErrorCode.SHOP_NAME_DUPLICATE;

    public ShopNameDuplicateException() {
        super(ERROR_CODE);
    }

    public ShopNameDuplicateException(String shopName) {
        super(ERROR_CODE, String.format("외부몰명 '%s'은(는) 이미 존재합니다", shopName));
    }
}
