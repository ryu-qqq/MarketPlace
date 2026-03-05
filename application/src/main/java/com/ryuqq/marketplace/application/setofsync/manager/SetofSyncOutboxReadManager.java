package com.ryuqq.marketplace.application.setofsync.manager;

import com.ryuqq.marketplace.application.setofsync.port.out.query.SetofSyncOutboxQueryPort;
import com.ryuqq.marketplace.domain.setofsync.aggregate.SetofSyncOutbox;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SetofSyncOutboxReadManager {

    private final SetofSyncOutboxQueryPort queryPort;

    public SetofSyncOutboxReadManager(SetofSyncOutboxQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public SetofSyncOutbox getById(Long outboxId) {
        return queryPort.getById(outboxId);
    }

    @Transactional(readOnly = true)
    public List<SetofSyncOutbox> findPendingForRetry(Instant beforeTime, int limit) {
        return queryPort.findPendingForRetry(beforeTime, limit);
    }

    @Transactional(readOnly = true)
    public List<SetofSyncOutbox> findProcessingTimeout(Instant timeoutThreshold, int limit) {
        return queryPort.findProcessingTimeout(timeoutThreshold, limit);
    }
}
