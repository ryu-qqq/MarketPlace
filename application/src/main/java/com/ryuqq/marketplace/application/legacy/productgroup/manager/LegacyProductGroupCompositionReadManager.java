package com.ryuqq.marketplace.application.legacy.productgroup.manager;

import com.ryuqq.marketplace.application.legacy.productgroup.port.out.query.LegacyProductGroupCompositionQueryPort;
import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductGroupCompositeResult;
import com.ryuqq.marketplace.domain.productgroup.exception.ProductGroupNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 세토프 DB 상품그룹 Composition 조회 매니저.
 *
 * <p>세토프 DB의 크로스 테이블 조인을 통한 성능 최적화된 조회를 담당합니다.
 */
@Component
public class LegacyProductGroupCompositionReadManager {

    private final LegacyProductGroupCompositionQueryPort compositionQueryPort;

    public LegacyProductGroupCompositionReadManager(
            LegacyProductGroupCompositionQueryPort compositionQueryPort) {
        this.compositionQueryPort = compositionQueryPort;
    }

    /**
     * 세토프 상품그룹 Composite 상세 조회.
     *
     * @param productGroupId 세토프 상품그룹 ID
     * @return 상품그룹 Composite 결과
     * @throws ProductGroupNotFoundException 상품그룹이 존재하지 않을 때
     */
    @Transactional(readOnly = true)
    public LegacyProductGroupCompositeResult getCompositeById(long productGroupId) {
        return compositionQueryPort
                .findCompositeById(productGroupId)
                .orElseThrow(() -> new ProductGroupNotFoundException(productGroupId));
    }
}
