package com.ryuqq.marketplace.domain.selleradmin.exception;

/**
 * 셀러 관리자 가입 신청을 찾을 수 없는 경우 예외.
 *
 * <p>요청한 ID에 해당하는 셀러 관리자 가입 신청이 존재하지 않을 때 발생합니다.
 */
public class SellerAdminNotFoundException extends SellerAdminException {

    private static final SellerAdminErrorCode ERROR_CODE =
            SellerAdminErrorCode.SELLER_ADMIN_APPLICATION_NOT_FOUND;

    public SellerAdminNotFoundException() {
        super(ERROR_CODE);
    }

    public SellerAdminNotFoundException(Long sellerAdminId) {
        super(ERROR_CODE, String.format("ID가 %d인 셀러 관리자 가입 신청을 찾을 수 없습니다", sellerAdminId));
    }

    public SellerAdminNotFoundException(String sellerAdminId) {
        super(ERROR_CODE, String.format("ID가 %s인 셀러 관리자를 찾을 수 없습니다", sellerAdminId));
    }

    /**
     * 커스텀 메시지로 예외 생성.
     *
     * @param message 커스텀 메시지
     * @return SellerAdminNotFoundException
     */
    public static SellerAdminNotFoundException withMessage(String message) {
        return new SellerAdminNotFoundException(ERROR_CODE, message);
    }

    private SellerAdminNotFoundException(SellerAdminErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
