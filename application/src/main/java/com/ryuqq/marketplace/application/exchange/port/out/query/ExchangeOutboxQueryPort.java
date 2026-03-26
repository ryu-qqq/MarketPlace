package com.ryuqq.marketplace.application.exchange.port.out.query;

import com.ryuqq.marketplace.domain.exchange.outbox.aggregate.ExchangeOutbox;
import java.time.Instant;
import java.util.List;

/** 교환 아웃박스 Query Port. */
public interface ExchangeOutboxQueryPort {

    List<ExchangeOutbox> findPendingOutboxes(Instant beforeTime, int batchSize);

    List<ExchangeOutbox> findProcessingTimeoutOutboxes(Instant timeoutBefore, int batchSize);

    ExchangeOutbox getById(Long outboxId);
}
