package com.ryuqq.marketplace.domain.seller.exception;

/**
 * 중복된 셀러명으로 등록/수정 시도할 때 예외.
 *
 * <p>이미 존재하는 셀러명으로 신규 등록하거나 수정할 때 발생합니다.
 */
public class SellerNameDuplicateException extends SellerException {

    private static final SellerErrorCode ERROR_CODE = SellerErrorCode.SELLER_NAME_DUPLICATE;

    public SellerNameDuplicateException() {
        super(ERROR_CODE);
    }

    public SellerNameDuplicateException(String sellerName) {
        super(ERROR_CODE, String.format("셀러명 '%s'은(는) 이미 존재합니다", sellerName));
    }
}
