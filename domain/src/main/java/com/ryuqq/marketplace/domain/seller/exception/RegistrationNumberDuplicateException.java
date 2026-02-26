package com.ryuqq.marketplace.domain.seller.exception;

/**
 * 중복된 사업자등록번호로 등록/수정 시도할 때 예외.
 *
 * <p>이미 존재하는 사업자등록번호로 신규 등록하거나 수정할 때 발생합니다.
 */
public class RegistrationNumberDuplicateException extends SellerException {

    private static final SellerErrorCode ERROR_CODE = SellerErrorCode.REGISTRATION_NUMBER_DUPLICATE;

    public RegistrationNumberDuplicateException() {
        super(ERROR_CODE);
    }

    public RegistrationNumberDuplicateException(String registrationNumber) {
        super(ERROR_CODE, String.format("사업자등록번호 '%s'은(는) 이미 등록되어 있습니다", registrationNumber));
    }
}
