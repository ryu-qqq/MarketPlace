package com.ryuqq.marketplace.application.productintelligence.manager;

import com.ryuqq.marketplace.application.productintelligence.port.out.query.IntelligenceOutboxQueryPort;
import com.ryuqq.marketplace.domain.productintelligence.aggregate.IntelligenceOutbox;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Intelligence Pipeline Outbox Read Manager. */
@Component
public class IntelligenceOutboxReadManager {

    private final IntelligenceOutboxQueryPort queryPort;

    public IntelligenceOutboxReadManager(IntelligenceOutboxQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public List<IntelligenceOutbox> findPendingOutboxes(Instant beforeTime, int limit) {
        return queryPort.findPendingOutboxes(beforeTime, limit);
    }

    @Transactional(readOnly = true)
    public List<IntelligenceOutbox> findInProgressTimeoutOutboxes(
            Instant timeoutThreshold, int limit) {
        return queryPort.findInProgressTimeoutOutboxes(timeoutThreshold, limit);
    }

    @Transactional(readOnly = true)
    public Optional<IntelligenceOutbox> findById(Long outboxId) {
        return queryPort.findById(outboxId);
    }
}
