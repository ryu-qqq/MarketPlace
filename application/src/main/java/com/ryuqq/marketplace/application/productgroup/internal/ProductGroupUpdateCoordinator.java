package com.ryuqq.marketplace.application.productgroup.internal;

import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupUpdateBundle;
import com.ryuqq.marketplace.application.productgroup.validator.ProductGroupValidator;
import org.springframework.stereotype.Component;

/**
 * ProductGroup 수정 Coordinator.
 *
 * <p>수정 전 검증 → Facade 호출 흐름을 관리합니다.
 */
@Component
public class ProductGroupUpdateCoordinator {

    private final ProductGroupValidator validator;
    private final ProductGroupCommandFacade commandFacade;

    public ProductGroupUpdateCoordinator(
            ProductGroupValidator validator, ProductGroupCommandFacade commandFacade) {
        this.validator = validator;
        this.commandFacade = commandFacade;
    }

    /**
     * 상품 그룹 전체 수정을 조율합니다.
     *
     * @param bundle 수정 Bundle
     */
    public void update(ProductGroupUpdateBundle bundle) {
        // 1. 검증
        validator.validateForUpdate(bundle);

        // 2. Facade를 통한 트랜잭션 처리
        commandFacade.updateProductGroup(bundle);
    }
}
