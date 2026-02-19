package com.ryuqq.marketplace.application.productgroup.internal;

import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupCommandManager;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupReadManager;
import com.ryuqq.marketplace.application.productgroup.validator.ProductGroupValidator;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupUpdateData;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ProductGroup per-domain Command Coordinator.
 *
 * <p>ProductGroup 기본 정보의 검증 + 저장을 조율합니다.
 *
 * <p>다른 도메인의 Coordinator와 동일한 패턴: register → Factory + Validator + persist, update → Read +
 * Validator + update + persist.
 *
 * <p>전체 Aggregate 등록/수정은 {@link FullProductGroupRegistrationCoordinator}, {@link
 * FullProductGroupUpdateCoordinator}를 사용합니다.
 */
@Component
public class ProductGroupCommandCoordinator {

    private final ProductGroupValidator productGroupValidator;
    private final ProductGroupReadManager productGroupReadManager;
    private final ProductGroupCommandManager productGroupCommandManager;

    public ProductGroupCommandCoordinator(
            ProductGroupValidator productGroupValidator,
            ProductGroupReadManager productGroupReadManager,
            ProductGroupCommandManager productGroupCommandManager) {
        this.productGroupValidator = productGroupValidator;
        this.productGroupReadManager = productGroupReadManager;
        this.productGroupCommandManager = productGroupCommandManager;
    }

    /**
     * 상품 그룹 등록: 외부 FK 검증 + persist.
     *
     * @param productGroup 등록 대상 ProductGroup
     * @return 생성된 productGroupId
     */
    @Transactional
    public Long register(ProductGroup productGroup) {
        productGroupValidator.validateForRegistration(productGroup);
        return productGroupCommandManager.persist(productGroup);
    }

    /**
     * 상품 그룹 기본 정보 수정: 외부 FK 검증 + 조회 + update + persist.
     *
     * @param updateData 수정 데이터 (도메인 VO)
     */
    @Transactional
    public void update(ProductGroupUpdateData updateData) {
        productGroupValidator.validateForUpdate(updateData);

        ProductGroup productGroup = productGroupReadManager.getById(updateData.productGroupId());
        productGroup.update(updateData);

        productGroupCommandManager.persist(productGroup);
    }
}
