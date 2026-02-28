package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.mapper.LegacyConversionOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.repository.LegacyConversionOutboxQueryDslRepository;
import com.ryuqq.marketplace.application.legacyconversion.port.out.query.LegacyConversionOutboxQueryPort;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyConversionOutbox;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

/**
 * LegacyConversionOutboxQueryAdapter - 레거시 변환 Outbox 조회 어댑터.
 *
 * <p>PER-ADP-001: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 */
@Component
public class LegacyConversionOutboxQueryAdapter implements LegacyConversionOutboxQueryPort {

    private final LegacyConversionOutboxQueryDslRepository queryDslRepository;
    private final LegacyConversionOutboxJpaEntityMapper mapper;

    public LegacyConversionOutboxQueryAdapter(
            LegacyConversionOutboxQueryDslRepository queryDslRepository,
            LegacyConversionOutboxJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public List<LegacyConversionOutbox> findPendingOutboxes(Instant beforeTime, int limit) {
        return queryDslRepository.findPendingOutboxes(beforeTime, limit).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<LegacyConversionOutbox> findProcessingTimeoutOutboxes(
            Instant timeoutThreshold, int limit) {
        return queryDslRepository.findProcessingTimeoutOutboxes(timeoutThreshold, limit).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsPendingByLegacyProductGroupId(long legacyProductGroupId) {
        return queryDslRepository
                .findPendingByLegacyProductGroupId(legacyProductGroupId)
                .isPresent();
    }

    @Override
    public Set<Long> findExistingLegacyProductGroupIds(Collection<Long> legacyProductGroupIds) {
        return queryDslRepository.findExistingLegacyProductGroupIds(legacyProductGroupIds);
    }

    @Override
    public long countDistinctLegacyProductGroupIds() {
        return queryDslRepository.countDistinctLegacyProductGroupIds();
    }
}
