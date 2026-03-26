package com.ryuqq.marketplace.domain.selleradmin.exception;

/** 비밀번호 불일치 예외. */
public class SellerAdminInvalidPasswordException extends SellerAdminException {

    private static final SellerAdminErrorCode ERROR_CODE =
            SellerAdminErrorCode.SELLER_ADMIN_INVALID_PASSWORD;

    public SellerAdminInvalidPasswordException() {
        super(ERROR_CODE);
    }
}
