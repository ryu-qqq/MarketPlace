package com.ryuqq.marketplace.domain.seller.exception;

/**
 * 셀러 CS 정보를 찾을 수 없는 경우 예외.
 *
 * <p>요청한 셀러 ID에 해당하는 CS 정보가 존재하지 않을 때 발생합니다.
 */
public class SellerCsNotFoundException extends SellerException {

    private static final SellerErrorCode ERROR_CODE = SellerErrorCode.CS_NOT_FOUND;

    public SellerCsNotFoundException() {
        super(ERROR_CODE);
    }

    public SellerCsNotFoundException(Long sellerId) {
        super(ERROR_CODE, String.format("셀러 ID %d에 해당하는 CS 정보를 찾을 수 없습니다", sellerId));
    }
}
