package com.ryuqq.marketplace.domain.selleradmin.exception;

/** 미승인 셀러 관리자 접근 시 예외. */
public class SellerAdminNotApprovedException extends SellerAdminException {

    private static final SellerAdminErrorCode ERROR_CODE =
            SellerAdminErrorCode.SELLER_ADMIN_NOT_APPROVED;

    public SellerAdminNotApprovedException() {
        super(ERROR_CODE);
    }

    public SellerAdminNotApprovedException(String email) {
        super(ERROR_CODE, String.format("미승인 셀러 관리자: email=%s", email));
    }
}
