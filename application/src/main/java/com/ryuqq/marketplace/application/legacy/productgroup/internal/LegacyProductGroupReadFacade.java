package com.ryuqq.marketplace.application.legacy.productgroup.internal;

import com.ryuqq.marketplace.application.legacy.product.manager.LegacyProductCompositionReadManager;
import com.ryuqq.marketplace.application.legacy.productgroup.manager.LegacyProductGroupCompositionReadManager;
import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductCompositeResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductGroupCompositeResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductGroupDetailBundle;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 세토프 DB 상품그룹 Read Facade.
 *
 * <p>Manager만 오케스트레이션하여 조회 결과를 Bundle로 반환합니다. Assembler는 Service에서 사용합니다.
 */
@Component
public class LegacyProductGroupReadFacade {

    private final LegacyProductGroupCompositionReadManager compositionReadManager;
    private final LegacyProductCompositionReadManager productCompositionReadManager;

    public LegacyProductGroupReadFacade(
            LegacyProductGroupCompositionReadManager compositionReadManager,
            LegacyProductCompositionReadManager productCompositionReadManager) {
        this.compositionReadManager = compositionReadManager;
        this.productCompositionReadManager = productCompositionReadManager;
    }

    /**
     * 세토프 상품그룹 상세 조회.
     *
     * <p>ProductGroupCompositionReadManager와 ProductCompositionReadManager의 결과를 번들로 반환합니다.
     *
     * @param productGroupId 세토프 상품그룹 ID
     * @return 상세 조회 번들
     */
    @Transactional(readOnly = true)
    public LegacyProductGroupDetailBundle getDetail(long productGroupId) {
        LegacyProductGroupCompositeResult composite =
                compositionReadManager.getCompositeById(productGroupId);
        List<LegacyProductCompositeResult> products =
                productCompositionReadManager.findProductsByProductGroupId(productGroupId);
        return LegacyProductGroupDetailBundle.of(composite, products);
    }
}
