package com.ryuqq.marketplace.adapter.out.persistence.productgroupinspection.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.productgroupinspection.entity.ProductGroupInspectionOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupinspection.mapper.ProductGroupInspectionOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupinspection.repository.ProductGroupInspectionOutboxJpaRepository;
import com.ryuqq.marketplace.application.productgroupinspection.port.out.command.ProductGroupInspectionOutboxCommandPort;
import com.ryuqq.marketplace.domain.productgroupinspection.aggregate.ProductGroupInspectionOutbox;
import org.springframework.stereotype.Component;

/**
 * ProductGroupInspectionOutboxCommandAdapter - 상품 그룹 검수 Outbox 명령 어댑터.
 *
 * <p>ProductGroupInspectionOutboxCommandPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 */
@Component
public class ProductGroupInspectionOutboxCommandAdapter
        implements ProductGroupInspectionOutboxCommandPort {

    private final ProductGroupInspectionOutboxJpaRepository repository;
    private final ProductGroupInspectionOutboxJpaEntityMapper mapper;

    public ProductGroupInspectionOutboxCommandAdapter(
            ProductGroupInspectionOutboxJpaRepository repository,
            ProductGroupInspectionOutboxJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(ProductGroupInspectionOutbox outbox) {
        ProductGroupInspectionOutboxJpaEntity entity = mapper.toEntity(outbox);
        ProductGroupInspectionOutboxJpaEntity saved = repository.save(entity);
        return saved.getId();
    }
}
