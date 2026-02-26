package com.ryuqq.marketplace.application.seller.manager;

import com.ryuqq.marketplace.application.seller.port.out.command.SellerAuthOutboxCommandPort;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerAuthOutbox;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** SellerAuthOutbox Command Manager. */
@Component
public class SellerAuthOutboxCommandManager {

    private final SellerAuthOutboxCommandPort commandPort;

    public SellerAuthOutboxCommandManager(SellerAuthOutboxCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(SellerAuthOutbox outbox) {
        return commandPort.persist(outbox);
    }
}
