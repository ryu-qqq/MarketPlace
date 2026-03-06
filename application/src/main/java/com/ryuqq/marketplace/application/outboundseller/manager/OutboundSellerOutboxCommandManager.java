package com.ryuqq.marketplace.application.outboundseller.manager;

import com.ryuqq.marketplace.application.outboundseller.port.out.command.OutboundSellerOutboxCommandPort;
import com.ryuqq.marketplace.domain.outboundseller.aggregate.OutboundSellerOutbox;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutboundSellerOutboxCommandManager {

    private final OutboundSellerOutboxCommandPort commandPort;

    public OutboundSellerOutboxCommandManager(OutboundSellerOutboxCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(OutboundSellerOutbox outbox) {
        return commandPort.persist(outbox);
    }
}
