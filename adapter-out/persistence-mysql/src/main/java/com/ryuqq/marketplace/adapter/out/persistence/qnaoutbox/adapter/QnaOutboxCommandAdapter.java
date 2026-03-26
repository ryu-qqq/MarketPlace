package com.ryuqq.marketplace.adapter.out.persistence.qnaoutbox.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.qnaoutbox.entity.QnaOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.qnaoutbox.mapper.QnaOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.qnaoutbox.repository.QnaOutboxJpaRepository;
import com.ryuqq.marketplace.application.qna.port.out.command.QnaOutboxCommandPort;
import com.ryuqq.marketplace.domain.qna.outbox.aggregate.QnaOutbox;
import org.springframework.stereotype.Component;

/** QnaOutbox 저장 어댑터. */
@Component
public class QnaOutboxCommandAdapter implements QnaOutboxCommandPort {

    private final QnaOutboxJpaRepository repository;
    private final QnaOutboxJpaEntityMapper mapper;

    public QnaOutboxCommandAdapter(
            QnaOutboxJpaRepository repository,
            QnaOutboxJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void persist(QnaOutbox outbox) {
        QnaOutboxJpaEntity entity = mapper.toEntity(outbox);
        QnaOutboxJpaEntity saved = repository.save(entity);
        outbox.refreshVersion(saved.getVersion());
    }
}
