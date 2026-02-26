package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.mapper.LegacyProductIdMappingJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.repository.LegacyProductIdMappingQueryDslRepository;
import com.ryuqq.marketplace.application.legacyconversion.port.out.query.LegacyProductIdMappingQueryPort;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyProductIdMapping;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * LegacyProductIdMappingQueryAdapter - ID 매핑 조회 어댑터.
 *
 * <p>PER-ADP-001: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 */
@Component
public class LegacyProductIdMappingQueryAdapter implements LegacyProductIdMappingQueryPort {

    private final LegacyProductIdMappingQueryDslRepository queryDslRepository;
    private final LegacyProductIdMappingJpaEntityMapper mapper;

    public LegacyProductIdMappingQueryAdapter(
            LegacyProductIdMappingQueryDslRepository queryDslRepository,
            LegacyProductIdMappingJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<LegacyProductIdMapping> findByLegacyProductId(long legacyProductId) {
        return queryDslRepository.findByLegacyProductId(legacyProductId).map(mapper::toDomain);
    }

    @Override
    public Optional<LegacyProductIdMapping> findByInternalProductId(long internalProductId) {
        return queryDslRepository.findByInternalProductId(internalProductId).map(mapper::toDomain);
    }

    @Override
    public List<LegacyProductIdMapping> findByLegacyProductGroupId(long legacyProductGroupId) {
        return queryDslRepository.findByLegacyProductGroupId(legacyProductGroupId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
