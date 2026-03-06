package com.ryuqq.marketplace.application.outboundseller.port.out.command;

import com.ryuqq.marketplace.domain.outboundseller.aggregate.OutboundSellerOutbox;

public interface OutboundSellerOutboxCommandPort {
    Long persist(OutboundSellerOutbox outbox);
}
