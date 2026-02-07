package com.ryuqq.marketplace.application.seller.validator;

import com.ryuqq.marketplace.application.seller.manager.SellerBusinessInfoReadManager;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerBusinessInfo;
import com.ryuqq.marketplace.domain.seller.exception.RegistrationNumberDuplicateException;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import org.springframework.stereotype.Component;

/**
 * SellerBusinessInfo Validator.
 *
 * <p>APP-VAL-001: 검증 성공 시 Domain 객체를 반환합니다.
 *
 * <p>APP-VAL-002: 도메인 전용 예외를 발생시킵니다.
 */
@Component
public class SellerBusinessInfoValidator {

    private final SellerBusinessInfoReadManager readManager;

    public SellerBusinessInfoValidator(SellerBusinessInfoReadManager readManager) {
        this.readManager = readManager;
    }

    /**
     * 사업자 정보 존재 여부 검증 후 Domain 객체 반환.
     *
     * @param sellerId 셀러 ID
     * @return SellerBusinessInfo 도메인 객체
     */
    public SellerBusinessInfo findExistingOrThrow(SellerId sellerId) {
        return readManager.getBySellerId(sellerId);
    }

    /**
     * 사업자등록번호 중복 여부 검증. (등록 시 사용)
     *
     * @param registrationNumber 사업자등록번호
     * @throws RegistrationNumberDuplicateException 이미 존재하는 경우
     */
    public void validateRegistrationNumberNotDuplicate(String registrationNumber) {
        if (readManager.existsByRegistrationNumber(registrationNumber)) {
            throw new RegistrationNumberDuplicateException(registrationNumber);
        }
    }

    /**
     * 사업자등록번호 중복 여부 검증 (자기 자신 제외). (수정 시 사용)
     *
     * @param registrationNumber 사업자등록번호
     * @param excludeId 제외할 셀러 ID
     * @throws RegistrationNumberDuplicateException 이미 존재하는 경우
     */
    public void validateRegistrationNumberNotDuplicateExcluding(
            String registrationNumber, SellerId excludeId) {
        if (readManager.existsByRegistrationNumberExcluding(registrationNumber, excludeId)) {
            throw new RegistrationNumberDuplicateException(registrationNumber);
        }
    }
}
