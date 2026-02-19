package com.ryuqq.marketplace.adapter.out.persistence.productgroupinspection.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.productgroupinspection.mapper.ProductGroupInspectionOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupinspection.repository.ProductGroupInspectionOutboxQueryDslRepository;
import com.ryuqq.marketplace.application.productgroupinspection.port.out.query.ProductGroupInspectionOutboxQueryPort;
import com.ryuqq.marketplace.domain.productgroupinspection.aggregate.ProductGroupInspectionOutbox;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * ProductGroupInspectionOutboxQueryAdapter - 상품 그룹 검수 Outbox 조회 어댑터.
 *
 * <p>PER-ADP-001: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 */
@Component
public class ProductGroupInspectionOutboxQueryAdapter
        implements ProductGroupInspectionOutboxQueryPort {

    private final ProductGroupInspectionOutboxQueryDslRepository queryDslRepository;
    private final ProductGroupInspectionOutboxJpaEntityMapper mapper;

    public ProductGroupInspectionOutboxQueryAdapter(
            ProductGroupInspectionOutboxQueryDslRepository queryDslRepository,
            ProductGroupInspectionOutboxJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public List<ProductGroupInspectionOutbox> findPendingOutboxes(Instant beforeTime, int limit) {
        return queryDslRepository.findPendingOutboxes(beforeTime, limit).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<ProductGroupInspectionOutbox> findInProgressTimeoutOutboxes(
            Instant timeoutThreshold, int limit) {
        return queryDslRepository.findInProgressTimeoutOutboxes(timeoutThreshold, limit).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<ProductGroupInspectionOutbox> findById(Long outboxId) {
        return queryDslRepository.findById(outboxId).map(mapper::toDomain);
    }
}
