package com.ryuqq.marketplace.application.productgroup.internal;

import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle;
import com.ryuqq.marketplace.application.productgroup.validator.ProductGroupValidator;
import org.springframework.stereotype.Component;

/**
 * 상품 그룹 등록 Coordinator.
 *
 * <p>검증 → Facade 호출 순서로 상품 그룹 등록을 조율합니다.
 */
@Component
public class ProductGroupRegistrationCoordinator {

    private final ProductGroupValidator productGroupValidator;
    private final ProductGroupCommandFacade commandFacade;

    public ProductGroupRegistrationCoordinator(
            ProductGroupValidator productGroupValidator, ProductGroupCommandFacade commandFacade) {
        this.productGroupValidator = productGroupValidator;
        this.commandFacade = commandFacade;
    }

    /**
     * 상품 그룹 등록을 조율합니다.
     *
     * <p>1. 검증 (옵션 구조, 가격 정합성 등)
     *
     * <p>2. Facade를 통한 저장 (트랜잭션)
     *
     * @param bundle 상품 그룹 등록 번들
     * @return 생성된 상품 그룹 ID
     */
    public Long register(ProductGroupRegistrationBundle bundle) {
        // 1. 검증 (외부 FK 존재, 고시정보 필드 일치, 필수 필드 존재)
        productGroupValidator.validateForRegistration(bundle);

        // 2. Facade를 통한 저장 (트랜잭션)
        return commandFacade.registerProductGroup(bundle);
    }
}
