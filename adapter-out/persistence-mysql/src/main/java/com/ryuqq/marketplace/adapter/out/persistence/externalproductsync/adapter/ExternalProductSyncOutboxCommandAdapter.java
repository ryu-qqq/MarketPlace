package com.ryuqq.marketplace.adapter.out.persistence.externalproductsync.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.externalproductsync.entity.ExternalProductSyncOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.externalproductsync.mapper.ExternalProductSyncOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.externalproductsync.repository.ExternalProductSyncOutboxJpaRepository;
import com.ryuqq.marketplace.application.externalproductsync.port.out.command.ExternalProductSyncOutboxCommandPort;
import com.ryuqq.marketplace.domain.externalproductsync.aggregate.ExternalProductSyncOutbox;
import java.util.List;
import org.springframework.stereotype.Component;

/** 외부 상품 연동 Outbox 커맨드 어댑터. */
@Component
public class ExternalProductSyncOutboxCommandAdapter
        implements ExternalProductSyncOutboxCommandPort {

    private final ExternalProductSyncOutboxJpaRepository repository;
    private final ExternalProductSyncOutboxJpaEntityMapper mapper;

    public ExternalProductSyncOutboxCommandAdapter(
            ExternalProductSyncOutboxJpaRepository repository,
            ExternalProductSyncOutboxJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(ExternalProductSyncOutbox outbox) {
        ExternalProductSyncOutboxJpaEntity entity = mapper.toEntity(outbox);
        ExternalProductSyncOutboxJpaEntity saved = repository.save(entity);
        return saved.getId();
    }

    @Override
    public void persistAll(List<ExternalProductSyncOutbox> outboxes) {
        List<ExternalProductSyncOutboxJpaEntity> entities =
                outboxes.stream().map(mapper::toEntity).toList();
        repository.saveAll(entities);
    }
}
