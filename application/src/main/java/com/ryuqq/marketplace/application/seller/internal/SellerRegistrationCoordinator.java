package com.ryuqq.marketplace.application.seller.internal;

import com.ryuqq.marketplace.application.seller.dto.bundle.SellerRegistrationBundle;
import com.ryuqq.marketplace.application.seller.validator.SellerBusinessInfoValidator;
import com.ryuqq.marketplace.application.seller.validator.SellerValidator;
import org.springframework.stereotype.Component;

/**
 * 셀러 등록 Coordinator.
 *
 * <p>검증 → Facade 호출 순서로 셀러 등록을 조율합니다.
 */
@Component
public class SellerRegistrationCoordinator {

    private final SellerValidator sellerValidator;
    private final SellerBusinessInfoValidator businessInfoValidator;
    private final SellerCommandFacade commandFacade;

    public SellerRegistrationCoordinator(
            SellerValidator sellerValidator,
            SellerBusinessInfoValidator businessInfoValidator,
            SellerCommandFacade commandFacade) {
        this.sellerValidator = sellerValidator;
        this.businessInfoValidator = businessInfoValidator;
        this.commandFacade = commandFacade;
    }

    /**
     * 셀러 등록을 조율합니다.
     *
     * <p>1. 검증 (셀러명 중복, 사업자등록번호 중복)
     *
     * <p>2. Facade를 통한 저장 (트랜잭션)
     *
     * @param bundle 셀러 등록 번들
     * @return 생성된 셀러 ID
     */
    public Long register(SellerRegistrationBundle bundle) {
        validateBeforeRegister(bundle);
        return commandFacade.registerSeller(bundle);
    }

    private void validateBeforeRegister(SellerRegistrationBundle bundle) {
        sellerValidator.validateSellerNameNotDuplicate(bundle.sellerNameValue());
        businessInfoValidator.validateRegistrationNumberNotDuplicate(
                bundle.registrationNumberValue());
    }
}
