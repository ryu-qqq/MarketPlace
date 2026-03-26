package com.ryuqq.marketplace.application.qna.manager;

import com.ryuqq.marketplace.application.qna.port.out.query.QnaOutboxQueryPort;
import com.ryuqq.marketplace.domain.qna.exception.QnaErrorCode;
import com.ryuqq.marketplace.domain.qna.exception.QnaException;
import com.ryuqq.marketplace.domain.qna.outbox.aggregate.QnaOutbox;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class QnaOutboxReadManager {

    private final QnaOutboxQueryPort queryPort;

    public QnaOutboxReadManager(QnaOutboxQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public QnaOutbox getById(long id) {
        return queryPort.findById(id)
                .orElseThrow(() -> new QnaException(QnaErrorCode.QNA_NOT_FOUND,
                        "QnaOutbox not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<QnaOutbox> findPendingOutboxes(Instant beforeTime, int limit) {
        return queryPort.findPendingOutboxes(beforeTime, limit);
    }

    @Transactional(readOnly = true)
    public List<QnaOutbox> findProcessingTimeoutOutboxes(Instant timeoutBefore, int limit) {
        return queryPort.findProcessingTimeoutOutboxes(timeoutBefore, limit);
    }
}
