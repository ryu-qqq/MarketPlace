package com.ryuqq.marketplace.domain.shop.exception;

/**
 * 중복된 외부몰 계정 ID로 등록/수정 시도할 때 예외.
 *
 * <p>이미 존재하는 계정 ID로 신규 등록하거나 수정할 때 발생합니다.
 */
public class ShopAccountIdDuplicateException extends ShopException {

    private static final ShopErrorCode ERROR_CODE = ShopErrorCode.SHOP_ACCOUNT_ID_DUPLICATE;

    public ShopAccountIdDuplicateException() {
        super(ERROR_CODE);
    }

    public ShopAccountIdDuplicateException(String accountId) {
        super(ERROR_CODE, String.format("외부몰 계정 ID '%s'은(는) 이미 존재합니다", accountId));
    }
}
