package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.mapper.LegacyOrderConversionOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.repository.LegacyOrderConversionOutboxQueryDslRepository;
import com.ryuqq.marketplace.application.legacyconversion.port.out.query.LegacyOrderConversionOutboxQueryPort;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderConversionOutbox;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * LegacyOrderConversionOutboxQueryAdapter - 레거시 주문 변환 Outbox 조회 어댑터.
 *
 * <p>PER-ADP-001: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 */
@Component
public class LegacyOrderConversionOutboxQueryAdapter
        implements LegacyOrderConversionOutboxQueryPort {

    private final LegacyOrderConversionOutboxQueryDslRepository queryDslRepository;
    private final LegacyOrderConversionOutboxJpaEntityMapper mapper;

    public LegacyOrderConversionOutboxQueryAdapter(
            LegacyOrderConversionOutboxQueryDslRepository queryDslRepository,
            LegacyOrderConversionOutboxJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public List<LegacyOrderConversionOutbox> findPendingOutboxes(Instant beforeTime, int limit) {
        return queryDslRepository.findPendingOutboxes(beforeTime, limit).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<LegacyOrderConversionOutbox> findProcessingTimeoutOutboxes(
            Instant timeoutThreshold, int limit) {
        return queryDslRepository.findProcessingTimeoutOutboxes(timeoutThreshold, limit).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByLegacyOrderId(long legacyOrderId) {
        return queryDslRepository.existsByLegacyOrderId(legacyOrderId);
    }
}
