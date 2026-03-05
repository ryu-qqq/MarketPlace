package com.ryuqq.marketplace.application.outboundseller.manager;

import com.ryuqq.marketplace.application.outboundseller.port.out.query.OutboundSellerOutboxQueryPort;
import com.ryuqq.marketplace.domain.outboundseller.aggregate.OutboundSellerOutbox;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutboundSellerOutboxReadManager {

    private final OutboundSellerOutboxQueryPort queryPort;

    public OutboundSellerOutboxReadManager(OutboundSellerOutboxQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public OutboundSellerOutbox getById(Long outboxId) {
        return queryPort.getById(outboxId);
    }

    @Transactional(readOnly = true)
    public List<OutboundSellerOutbox> findPendingForRetry(Instant beforeTime, int limit) {
        return queryPort.findPendingForRetry(beforeTime, limit);
    }

    @Transactional(readOnly = true)
    public List<OutboundSellerOutbox> findProcessingTimeout(Instant timeoutThreshold, int limit) {
        return queryPort.findProcessingTimeout(timeoutThreshold, limit);
    }
}
