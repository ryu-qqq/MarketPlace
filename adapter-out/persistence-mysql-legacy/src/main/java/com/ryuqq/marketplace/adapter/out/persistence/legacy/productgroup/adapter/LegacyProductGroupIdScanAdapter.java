package com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroup.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroup.repository.LegacyProductGroupQueryDslRepository;
import com.ryuqq.marketplace.application.legacyconversion.port.out.query.LegacyProductGroupIdScanPort;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * luxurydb에서 활성 상품그룹 ID를 커서 기반으로 스캔하는 어댑터.
 *
 * <p>PER-ADP-001: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 */
@Component
public class LegacyProductGroupIdScanAdapter implements LegacyProductGroupIdScanPort {

    private final LegacyProductGroupQueryDslRepository queryDslRepository;

    public LegacyProductGroupIdScanAdapter(
            LegacyProductGroupQueryDslRepository queryDslRepository) {
        this.queryDslRepository = queryDslRepository;
    }

    @Override
    public List<Long> findActiveProductGroupIdsAfter(long afterId, int limit) {
        return queryDslRepository.findActiveProductGroupIds(afterId, limit);
    }
}
