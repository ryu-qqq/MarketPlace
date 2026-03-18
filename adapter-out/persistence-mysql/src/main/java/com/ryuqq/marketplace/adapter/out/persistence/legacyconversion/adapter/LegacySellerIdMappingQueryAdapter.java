package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.repository.LegacySellerIdMappingQueryDslRepository;
import com.ryuqq.marketplace.application.legacyconversion.port.out.query.LegacySellerIdMappingQueryPort;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * LegacySellerIdMappingQueryAdapter - 셀러 ID 매핑 조회 어댑터.
 *
 * <p>PER-ADP-001: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 */
@Component
public class LegacySellerIdMappingQueryAdapter implements LegacySellerIdMappingQueryPort {

    private final LegacySellerIdMappingQueryDslRepository queryDslRepository;

    public LegacySellerIdMappingQueryAdapter(
            LegacySellerIdMappingQueryDslRepository queryDslRepository) {
        this.queryDslRepository = queryDslRepository;
    }

    @Override
    public Optional<Long> findInternalSellerIdByLegacySellerId(long legacySellerId) {
        return queryDslRepository.findInternalSellerIdByLegacySellerId(legacySellerId);
    }
}
