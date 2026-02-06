package com.ryuqq.marketplace.application.seller.validator;

import com.ryuqq.marketplace.application.seller.manager.SellerContractReadManager;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerContract;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import org.springframework.stereotype.Component;

/**
 * SellerContract Validator.
 *
 * <p>계약 정보 검증을 담당합니다.
 */
@Component
public class SellerContractValidator {

    private final SellerContractReadManager readManager;

    public SellerContractValidator(SellerContractReadManager readManager) {
        this.readManager = readManager;
    }

    /**
     * 셀러 ID로 계약 정보 존재 여부 검증 후 Domain 객체 반환.
     *
     * @param sellerId 셀러 ID
     * @return SellerContract 도메인 객체
     */
    public SellerContract findExistingOrThrow(SellerId sellerId) {
        return readManager.getBySellerId(sellerId);
    }
}
