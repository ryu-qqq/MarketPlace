package com.ryuqq.marketplace.application.outboundseller.port.out.query;

import com.ryuqq.marketplace.domain.outboundseller.aggregate.OutboundSellerOutbox;
import java.time.Instant;
import java.util.List;

public interface OutboundSellerOutboxQueryPort {
    OutboundSellerOutbox getById(Long outboxId);

    List<OutboundSellerOutbox> findPendingForRetry(Instant beforeTime, int limit);

    List<OutboundSellerOutbox> findProcessingTimeout(Instant timeoutThreshold, int limit);
}
