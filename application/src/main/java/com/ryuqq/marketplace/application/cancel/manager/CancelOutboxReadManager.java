package com.ryuqq.marketplace.application.cancel.manager;

import com.ryuqq.marketplace.application.cancel.port.out.query.CancelOutboxQueryPort;
import com.ryuqq.marketplace.domain.cancel.outbox.aggregate.CancelOutbox;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** CancelOutbox Read Manager. */
@Component
public class CancelOutboxReadManager {

    private final CancelOutboxQueryPort queryPort;

    public CancelOutboxReadManager(CancelOutboxQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public List<CancelOutbox> findPendingOutboxes(Instant beforeTime, int batchSize) {
        return queryPort.findPendingOutboxes(beforeTime, batchSize);
    }

    @Transactional(readOnly = true)
    public List<CancelOutbox> findProcessingTimeoutOutboxes(Instant timeoutBefore, int batchSize) {
        return queryPort.findProcessingTimeoutOutboxes(timeoutBefore, batchSize);
    }

    @Transactional(readOnly = true)
    public CancelOutbox getById(Long outboxId) {
        return queryPort.getById(outboxId);
    }
}
