package com.ryuqq.marketplace.domain.shop.exception;

/** 해당 판매채널에 이미 동일 계정 ID가 존재할 때 예외. */
public class ShopAccountIdDuplicateException extends ShopException {

    private static final ShopErrorCode ERROR_CODE = ShopErrorCode.SHOP_ACCOUNT_DUPLICATE;

    public ShopAccountIdDuplicateException() {
        super(ERROR_CODE);
    }

    public ShopAccountIdDuplicateException(String accountId) {
        super(ERROR_CODE, String.format("계정 ID '%s'은(는) 해당 판매채널에 이미 존재합니다", accountId));
    }
}
