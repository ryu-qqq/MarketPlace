package com.ryuqq.marketplace.application.legacy.product.facade;

import com.ryuqq.marketplace.application.legacy.product.manager.LegacyProductCommandManager;
import com.ryuqq.marketplace.application.legacy.product.manager.LegacyProductOptionCommandManager;
import com.ryuqq.marketplace.application.legacy.product.manager.LegacyProductStockCommandManager;
import com.ryuqq.marketplace.domain.legacy.product.aggregate.LegacyProduct;
import com.ryuqq.marketplace.domain.legacy.product.id.LegacyProductId;
import com.ryuqq.marketplace.domain.legacy.product.vo.LegacyProductOption;
import org.springframework.stereotype.Component;

/**
 * 레거시 상품 등록 Facade.
 *
 * <p>상품 + 재고 + 상품-옵션 매핑을 일괄 저장합니다. 상품 저장 후 productId를 재고/옵션매핑에 바인딩합니다.
 */
@Component
public class LegacyProductRegistrationFacade {

    private final LegacyProductCommandManager productCommandManager;
    private final LegacyProductStockCommandManager stockCommandManager;
    private final LegacyProductOptionCommandManager productOptionCommandManager;

    public LegacyProductRegistrationFacade(
            LegacyProductCommandManager productCommandManager,
            LegacyProductStockCommandManager stockCommandManager,
            LegacyProductOptionCommandManager productOptionCommandManager) {
        this.productCommandManager = productCommandManager;
        this.stockCommandManager = stockCommandManager;
        this.productOptionCommandManager = productOptionCommandManager;
    }

    /** 상품 + 재고 + 옵션매핑 일괄 저장 후 productId 반환. */
    public Long register(LegacyProduct product) {
        Long productId = productCommandManager.persist(product);
        LegacyProductId pId = LegacyProductId.of(productId);

        stockCommandManager.persist(pId, product.stockQuantity());

        for (LegacyProductOption option : product.options()) {
            LegacyProductOption bound =
                    LegacyProductOption.forNew(
                            pId,
                            option.optionGroupId(),
                            option.optionDetailId(),
                            option.additionalPrice());
            productOptionCommandManager.persist(bound);
        }

        return productId;
    }
}
