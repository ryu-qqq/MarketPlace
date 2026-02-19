package com.ryuqq.marketplace.application.productgroupinspection.internal.scorer;

import com.ryuqq.marketplace.application.productgroup.port.out.query.ProductGroupQueryPort;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroupinspection.vo.InspectionScoreType;
import org.springframework.stereotype.Component;

/** 캐노니컬 옵션 매핑률을 평가하는 Scorer. */
@Component
public class CanonicalOptionMappingScorer implements InspectionScorer {

    private final ProductGroupQueryPort productGroupQueryPort;

    public CanonicalOptionMappingScorer(ProductGroupQueryPort productGroupQueryPort) {
        this.productGroupQueryPort = productGroupQueryPort;
    }

    @Override
    public InspectionScoreType type() {
        return InspectionScoreType.CANONICAL_OPTION_MAPPING;
    }

    @Override
    public int score(Long productGroupId) {
        return productGroupQueryPort
                .findById(ProductGroupId.of(productGroupId))
                .map(pg -> pg.isFullyMappedToCanonical() ? 100 : 0)
                .orElse(0);
    }
}
