package com.ryuqq.marketplace.adapter.out.persistence.refundoutbox.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.refundoutbox.entity.RefundOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.refundoutbox.mapper.RefundOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.refundoutbox.repository.RefundOutboxJpaRepository;
import com.ryuqq.marketplace.application.refund.port.out.command.RefundOutboxCommandPort;
import com.ryuqq.marketplace.domain.refund.outbox.aggregate.RefundOutbox;
import java.util.List;
import org.springframework.stereotype.Component;

/** 환불 아웃박스 Command Adapter. */
@Component
public class RefundOutboxCommandAdapter implements RefundOutboxCommandPort {

    private final RefundOutboxJpaRepository repository;
    private final RefundOutboxJpaEntityMapper mapper;

    public RefundOutboxCommandAdapter(
            RefundOutboxJpaRepository repository, RefundOutboxJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(RefundOutbox outbox) {
        RefundOutboxJpaEntity saved = repository.save(mapper.toEntity(outbox));
        outbox.refreshVersion(saved.getVersion());
        return saved.getId();
    }

    @Override
    public void persistAll(List<RefundOutbox> outboxes) {
        List<RefundOutboxJpaEntity> entities = outboxes.stream().map(mapper::toEntity).toList();
        List<RefundOutboxJpaEntity> savedEntities = repository.saveAll(entities);
        for (int i = 0; i < outboxes.size(); i++) {
            outboxes.get(i).refreshVersion(savedEntities.get(i).getVersion());
        }
    }
}
