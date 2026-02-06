package com.ryuqq.marketplace.domain.seller.exception;

/**
 * 셀러를 찾을 수 없는 경우 예외.
 *
 * <p>요청한 ID에 해당하는 셀러가 존재하지 않을 때 발생합니다.
 */
public class SellerNotFoundException extends SellerException {

    private static final SellerErrorCode ERROR_CODE = SellerErrorCode.SELLER_NOT_FOUND;

    public SellerNotFoundException() {
        super(ERROR_CODE);
    }

    public SellerNotFoundException(Long sellerId) {
        super(ERROR_CODE, String.format("ID가 %d인 셀러를 찾을 수 없습니다", sellerId));
    }

    public SellerNotFoundException(String sellerName) {
        super(ERROR_CODE, String.format("셀러명이  %s인 셀러를 찾을 수 없습니다", sellerName));
    }
}
