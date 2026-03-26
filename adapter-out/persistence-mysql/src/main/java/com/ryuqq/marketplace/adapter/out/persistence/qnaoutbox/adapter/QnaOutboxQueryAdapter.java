package com.ryuqq.marketplace.adapter.out.persistence.qnaoutbox.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.qnaoutbox.mapper.QnaOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.qnaoutbox.repository.QnaOutboxQueryDslRepository;
import com.ryuqq.marketplace.application.qna.port.out.query.QnaOutboxQueryPort;
import com.ryuqq.marketplace.domain.qna.outbox.aggregate.QnaOutbox;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** QnaOutbox 조회 어댑터. */
@Component
public class QnaOutboxQueryAdapter implements QnaOutboxQueryPort {

    private final QnaOutboxQueryDslRepository queryDslRepository;
    private final QnaOutboxJpaEntityMapper mapper;

    public QnaOutboxQueryAdapter(
            QnaOutboxQueryDslRepository queryDslRepository,
            QnaOutboxJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<QnaOutbox> findById(long id) {
        return queryDslRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<QnaOutbox> findPendingOutboxes(Instant beforeTime, int limit) {
        return queryDslRepository.findPendingOutboxes(beforeTime, limit).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<QnaOutbox> findProcessingTimeoutOutboxes(Instant timeoutBefore, int limit) {
        return queryDslRepository.findProcessingTimeoutOutboxes(timeoutBefore, limit).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
