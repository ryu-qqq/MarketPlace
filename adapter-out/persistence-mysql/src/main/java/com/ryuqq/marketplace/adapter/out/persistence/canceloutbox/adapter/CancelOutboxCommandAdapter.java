package com.ryuqq.marketplace.adapter.out.persistence.canceloutbox.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.canceloutbox.entity.CancelOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.canceloutbox.mapper.CancelOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.canceloutbox.repository.CancelOutboxJpaRepository;
import com.ryuqq.marketplace.application.cancel.port.out.command.CancelOutboxCommandPort;
import com.ryuqq.marketplace.domain.cancel.outbox.aggregate.CancelOutbox;
import java.util.List;
import org.springframework.stereotype.Component;

/** 취소 아웃박스 Command Adapter. */
@Component
public class CancelOutboxCommandAdapter implements CancelOutboxCommandPort {

    private final CancelOutboxJpaRepository repository;
    private final CancelOutboxJpaEntityMapper mapper;

    public CancelOutboxCommandAdapter(
            CancelOutboxJpaRepository repository, CancelOutboxJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(CancelOutbox outbox) {
        CancelOutboxJpaEntity saved = repository.save(mapper.toEntity(outbox));
        outbox.refreshVersion(saved.getVersion());
        return saved.getId();
    }

    @Override
    public void persistAll(List<CancelOutbox> outboxes) {
        List<CancelOutboxJpaEntity> entities = outboxes.stream().map(mapper::toEntity).toList();
        List<CancelOutboxJpaEntity> savedEntities = repository.saveAll(entities);
        for (int i = 0; i < outboxes.size(); i++) {
            outboxes.get(i).refreshVersion(savedEntities.get(i).getVersion());
        }
    }
}
