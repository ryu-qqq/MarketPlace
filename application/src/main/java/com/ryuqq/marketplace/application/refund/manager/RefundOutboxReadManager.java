package com.ryuqq.marketplace.application.refund.manager;

import com.ryuqq.marketplace.application.refund.port.out.query.RefundOutboxQueryPort;
import com.ryuqq.marketplace.domain.refund.outbox.aggregate.RefundOutbox;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** RefundOutbox Read Manager. */
@Component
public class RefundOutboxReadManager {

    private final RefundOutboxQueryPort queryPort;

    public RefundOutboxReadManager(RefundOutboxQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public List<RefundOutbox> findPendingOutboxes(Instant beforeTime, int batchSize) {
        return queryPort.findPendingOutboxes(beforeTime, batchSize);
    }

    @Transactional(readOnly = true)
    public List<RefundOutbox> findProcessingTimeoutOutboxes(Instant timeoutBefore, int batchSize) {
        return queryPort.findProcessingTimeoutOutboxes(timeoutBefore, batchSize);
    }

    @Transactional(readOnly = true)
    public RefundOutbox getById(Long outboxId) {
        return queryPort.getById(outboxId);
    }
}
