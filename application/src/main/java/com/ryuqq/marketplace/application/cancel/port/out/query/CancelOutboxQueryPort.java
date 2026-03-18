package com.ryuqq.marketplace.application.cancel.port.out.query;

import com.ryuqq.marketplace.domain.cancel.outbox.aggregate.CancelOutbox;
import java.time.Instant;
import java.util.List;

/** 취소 아웃박스 Query Port. */
public interface CancelOutboxQueryPort {

    List<CancelOutbox> findPendingOutboxes(Instant beforeTime, int batchSize);

    List<CancelOutbox> findProcessingTimeoutOutboxes(Instant timeoutBefore, int batchSize);

    CancelOutbox getById(Long outboxId);
}
