package com.ryuqq.marketplace.application.exchange.manager;

import com.ryuqq.marketplace.application.exchange.port.out.query.ExchangeOutboxQueryPort;
import com.ryuqq.marketplace.domain.exchange.outbox.aggregate.ExchangeOutbox;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** ExchangeOutbox Read Manager. */
@Component
public class ExchangeOutboxReadManager {

    private final ExchangeOutboxQueryPort queryPort;

    public ExchangeOutboxReadManager(ExchangeOutboxQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public List<ExchangeOutbox> findPendingOutboxes(Instant beforeTime, int batchSize) {
        return queryPort.findPendingOutboxes(beforeTime, batchSize);
    }

    @Transactional(readOnly = true)
    public List<ExchangeOutbox> findProcessingTimeoutOutboxes(Instant timeoutBefore, int batchSize) {
        return queryPort.findProcessingTimeoutOutboxes(timeoutBefore, batchSize);
    }

    @Transactional(readOnly = true)
    public ExchangeOutbox getById(Long outboxId) {
        return queryPort.getById(outboxId);
    }
}
