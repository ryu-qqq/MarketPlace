package com.ryuqq.marketplace.application.seller.validator;

import com.ryuqq.marketplace.application.seller.manager.SellerSettlementReadManager;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerSettlement;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import org.springframework.stereotype.Component;

/**
 * SellerSettlement Validator.
 *
 * <p>정산 정보 검증을 담당합니다.
 */
@Component
public class SellerSettlementValidator {

    private final SellerSettlementReadManager readManager;

    public SellerSettlementValidator(SellerSettlementReadManager readManager) {
        this.readManager = readManager;
    }

    /**
     * 셀러 ID로 정산 정보 존재 여부 검증 후 Domain 객체 반환.
     *
     * @param sellerId 셀러 ID
     * @return SellerSettlement 도메인 객체
     */
    public SellerSettlement findExistingOrThrow(SellerId sellerId) {
        return readManager.getBySellerId(sellerId);
    }
}
