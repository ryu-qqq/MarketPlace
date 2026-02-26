package com.ryuqq.marketplace.application.sellerapplication.validator;

import com.ryuqq.marketplace.application.sellerapplication.manager.SellerApplicationReadManager;
import com.ryuqq.marketplace.domain.sellerapplication.aggregate.SellerApplication;
import com.ryuqq.marketplace.domain.sellerapplication.exception.SellerApplicationErrorCode;
import com.ryuqq.marketplace.domain.sellerapplication.exception.SellerApplicationException;
import com.ryuqq.marketplace.domain.sellerapplication.exception.SellerApplicationNotFoundException;
import com.ryuqq.marketplace.domain.sellerapplication.id.SellerApplicationId;
import org.springframework.stereotype.Component;

/**
 * SellerApplicationValidator - 셀러 입점 신청 Validator.
 *
 * <p>APP-VAL-001: 검증 성공 시 Domain 객체를 반환합니다.
 *
 * <p>APP-VAL-002: 도메인 전용 예외를 발생시킵니다.
 *
 * @author ryu-qqq
 */
@Component
public class SellerApplicationValidator {

    private final SellerApplicationReadManager readManager;

    public SellerApplicationValidator(SellerApplicationReadManager readManager) {
        this.readManager = readManager;
    }

    /**
     * 입점 신청 존재 여부 검증 후 Domain 객체 반환.
     *
     * @param id 입점 신청 ID
     * @return SellerApplication 도메인 객체
     * @throws SellerApplicationNotFoundException 존재하지 않는 경우
     */
    public SellerApplication findExistingOrThrow(SellerApplicationId id) {
        return readManager.getById(id);
    }

    /**
     * 동일 사업자등록번호로 대기 중인 신청이 존재하지 않는지 검증합니다.
     *
     * @param registrationNumber 사업자등록번호
     */
    public void validateNoPendingApplication(String registrationNumber) {
        if (readManager.existsPendingByRegistrationNumber(registrationNumber)) {
            throw new SellerApplicationException(
                    SellerApplicationErrorCode.SELLER_APPLICATION_PENDING_EXISTS,
                    String.format("사업자등록번호 %s로 이미 대기 중인 신청이 존재합니다", registrationNumber));
        }
    }
}
