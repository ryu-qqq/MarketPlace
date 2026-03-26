package com.ryuqq.marketplace.application.refund.port.out.query;

import com.ryuqq.marketplace.domain.refund.outbox.aggregate.RefundOutbox;
import java.time.Instant;
import java.util.List;

/** 환불 아웃박스 Query Port. */
public interface RefundOutboxQueryPort {

    List<RefundOutbox> findPendingOutboxes(Instant beforeTime, int batchSize);

    List<RefundOutbox> findProcessingTimeoutOutboxes(Instant timeoutBefore, int batchSize);

    RefundOutbox getById(Long outboxId);
}
