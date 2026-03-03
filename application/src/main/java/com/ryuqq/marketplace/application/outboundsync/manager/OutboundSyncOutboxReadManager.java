package com.ryuqq.marketplace.application.outboundsync.manager;

import com.ryuqq.marketplace.application.outboundsync.port.out.query.OutboundSyncOutboxQueryPort;
import com.ryuqq.marketplace.domain.outboundsync.aggregate.OutboundSyncOutbox;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncType;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** OutboundSync Outbox 조회 Manager. */
@Component
public class OutboundSyncOutboxReadManager {

    private final OutboundSyncOutboxQueryPort queryPort;

    public OutboundSyncOutboxReadManager(OutboundSyncOutboxQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public List<OutboundSyncOutbox> findPendingByProductGroupId(ProductGroupId productGroupId) {
        return queryPort.findPendingByProductGroupId(productGroupId);
    }

    @Transactional(readOnly = true)
    public List<OutboundSyncOutbox> findPendingOutboxes(Instant beforeTime, int batchSize) {
        return queryPort.findPendingOutboxes(beforeTime, batchSize);
    }

    @Transactional(readOnly = true)
    public List<OutboundSyncOutbox> findProcessingTimeoutOutboxes(
            Instant timeoutBefore, int batchSize) {
        return queryPort.findProcessingTimeoutOutboxes(timeoutBefore, batchSize);
    }

    @Transactional(readOnly = true)
    public OutboundSyncOutbox getById(Long outboxId) {
        return queryPort.getById(outboxId);
    }

    @Transactional(readOnly = true)
    public List<OutboundSyncOutbox> findActiveByProductGroupIdAndSyncType(
            ProductGroupId productGroupId, SyncType syncType) {
        return queryPort.findActiveByProductGroupIdAndSyncType(productGroupId, syncType);
    }
}
