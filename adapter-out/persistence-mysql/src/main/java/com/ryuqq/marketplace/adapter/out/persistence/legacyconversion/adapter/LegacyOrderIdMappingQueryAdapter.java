package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.mapper.LegacyOrderIdMappingJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.repository.LegacyOrderIdMappingQueryDslRepository;
import com.ryuqq.marketplace.application.legacyconversion.port.out.query.LegacyOrderIdMappingQueryPort;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderIdMapping;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * LegacyOrderIdMappingQueryAdapter - 레거시 주문 ID 매핑 조회 어댑터.
 *
 * <p>PER-ADP-001: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 */
@Component
public class LegacyOrderIdMappingQueryAdapter implements LegacyOrderIdMappingQueryPort {

    private final LegacyOrderIdMappingQueryDslRepository queryDslRepository;
    private final LegacyOrderIdMappingJpaEntityMapper mapper;

    public LegacyOrderIdMappingQueryAdapter(
            LegacyOrderIdMappingQueryDslRepository queryDslRepository,
            LegacyOrderIdMappingJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<LegacyOrderIdMapping> findByLegacyOrderId(long legacyOrderId) {
        return queryDslRepository.findByLegacyOrderId(legacyOrderId).map(mapper::toDomain);
    }

    @Override
    public boolean existsByLegacyOrderId(long legacyOrderId) {
        return queryDslRepository.existsByLegacyOrderId(legacyOrderId);
    }

    @Override
    public List<LegacyOrderIdMapping> findByInternalOrderItemIds(List<Long> orderItemIds) {
        return queryDslRepository.findByInternalOrderItemIds(orderItemIds).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
