package com.ryuqq.marketplace.application.legacyproduct.manager;

import com.ryuqq.marketplace.application.legacyproduct.dto.composite.LegacyProductCompositeResult;
import com.ryuqq.marketplace.application.legacyproduct.port.out.query.LegacyProductCompositionQueryPort;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 세토프 DB 상품(Product) Composition 조회 매니저.
 *
 * <p>Product + Option + Stock 조인을 통한 상품 단위 조회를 담당합니다.
 */
@Component
public class LegacyProductCompositionReadManager {

    private final LegacyProductCompositionQueryPort productCompositionQueryPort;

    public LegacyProductCompositionReadManager(
            LegacyProductCompositionQueryPort productCompositionQueryPort) {
        this.productCompositionQueryPort = productCompositionQueryPort;
    }

    /**
     * 세토프 상품그룹 ID로 상품+옵션+재고 목록 조회.
     *
     * @param productGroupId 세토프 상품그룹 ID
     * @return 상품 정보 목록
     */
    @Transactional(readOnly = true)
    public List<LegacyProductCompositeResult> findProductsByProductGroupId(long productGroupId) {
        return productCompositionQueryPort.findProductsByProductGroupId(productGroupId);
    }
}
