package com.ryuqq.marketplace.application.seller.internal;

import com.ryuqq.marketplace.application.seller.dto.bundle.SellerUpdateBundle;
import com.ryuqq.marketplace.application.seller.validator.SellerBusinessInfoValidator;
import com.ryuqq.marketplace.application.seller.validator.SellerContractValidator;
import com.ryuqq.marketplace.application.seller.validator.SellerCsValidator;
import com.ryuqq.marketplace.application.seller.validator.SellerSettlementValidator;
import com.ryuqq.marketplace.application.seller.validator.SellerValidator;
import com.ryuqq.marketplace.domain.seller.aggregate.Seller;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerBusinessInfo;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerContract;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerCs;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerSettlement;
import org.springframework.stereotype.Component;

/**
 * 셀러 수정 Coordinator.
 *
 * <p>검증 → Facade 호출 순서로 셀러 수정을 조율합니다.
 *
 * <p>Seller + BusinessInfo + CS + Contract + Settlement를 한번에 수정합니다.
 */
@Component
public class SellerUpdateCoordinator {

    private final SellerValidator sellerValidator;
    private final SellerBusinessInfoValidator businessInfoValidator;
    private final SellerCsValidator csValidator;
    private final SellerContractValidator contractValidator;
    private final SellerSettlementValidator settlementValidator;
    private final SellerCommandFacade commandFacade;

    public SellerUpdateCoordinator(
            SellerValidator sellerValidator,
            SellerBusinessInfoValidator businessInfoValidator,
            SellerCsValidator csValidator,
            SellerContractValidator contractValidator,
            SellerSettlementValidator settlementValidator,
            SellerCommandFacade commandFacade) {
        this.sellerValidator = sellerValidator;
        this.businessInfoValidator = businessInfoValidator;
        this.csValidator = csValidator;
        this.contractValidator = contractValidator;
        this.settlementValidator = settlementValidator;
        this.commandFacade = commandFacade;
    }

    /**
     * 셀러 수정을 조율합니다.
     *
     * <p>1. 검증 (존재 여부, 중복 검사)
     *
     * <p>2. Facade를 통한 수정 (트랜잭션)
     *
     * @param bundle 셀러 수정 번들
     */
    public void update(SellerUpdateBundle bundle) {
        validateBeforeUpdate(bundle);
        commandFacade.updateSeller(bundle);
    }

    private void validateBeforeUpdate(SellerUpdateBundle bundle) {
        // 1. 존재 여부 검증 및 Domain 객체 획득
        Seller seller = sellerValidator.findExistingOrThrow(bundle.sellerId());
        SellerBusinessInfo businessInfo =
                businessInfoValidator.findExistingOrThrow(bundle.sellerId());
        SellerCs sellerCs = csValidator.findExistingOrThrow(bundle.sellerId());
        SellerContract sellerContract = contractValidator.findExistingOrThrow(bundle.sellerId());
        SellerSettlement sellerSettlement =
                settlementValidator.findExistingOrThrow(bundle.sellerId());

        // Bundle에 검증된 Domain 객체 설정
        bundle.withValidatedEntities(
                seller, businessInfo, sellerCs, sellerContract, sellerSettlement);

        // 2. 중복 검사 (자기 자신 제외)
        sellerValidator.validateSellerNameNotDuplicateExcluding(
                bundle.sellerUpdateData().sellerName().value(), bundle.sellerId());
        businessInfoValidator.validateRegistrationNumberNotDuplicateExcluding(
                bundle.businessInfoUpdateData().registrationNumber().value(), bundle.sellerId());
    }
}
