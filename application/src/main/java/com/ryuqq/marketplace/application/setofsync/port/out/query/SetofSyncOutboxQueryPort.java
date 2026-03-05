package com.ryuqq.marketplace.application.setofsync.port.out.query;

import com.ryuqq.marketplace.domain.setofsync.aggregate.SetofSyncOutbox;
import java.time.Instant;
import java.util.List;

public interface SetofSyncOutboxQueryPort {
    SetofSyncOutbox getById(Long outboxId);

    List<SetofSyncOutbox> findPendingForRetry(Instant beforeTime, int limit);

    List<SetofSyncOutbox> findProcessingTimeout(Instant timeoutThreshold, int limit);
}
