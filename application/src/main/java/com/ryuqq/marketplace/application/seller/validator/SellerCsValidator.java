package com.ryuqq.marketplace.application.seller.validator;

import com.ryuqq.marketplace.application.seller.manager.SellerCsReadManager;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerCs;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import org.springframework.stereotype.Component;

@Component
public class SellerCsValidator {
    private final SellerCsReadManager csReadManager;

    public SellerCsValidator(SellerCsReadManager csReadManager) {
        this.csReadManager = csReadManager;
    }

    /**
     * 셀러 ID로 CS 정보 존재 여부 검증 후 Domain 객체 반환.
     *
     * @param sellerId 셀러 ID
     * @return SellerCs 도메인 객체
     */
    public SellerCs findExistingOrThrow(SellerId sellerId) {
        return csReadManager.getBySellerId(sellerId);
    }
}
