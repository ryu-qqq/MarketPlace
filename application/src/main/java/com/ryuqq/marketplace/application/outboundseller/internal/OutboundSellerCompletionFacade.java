package com.ryuqq.marketplace.application.outboundseller.internal;

import com.ryuqq.marketplace.application.outboundseller.manager.OutboundSellerOutboxCommandManager;
import com.ryuqq.marketplace.application.outboundseller.manager.OutboundSellerOutboxReadManager;
import com.ryuqq.marketplace.domain.outboundseller.aggregate.OutboundSellerOutbox;
import java.time.Instant;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutboundSellerCompletionFacade {

    private final OutboundSellerOutboxReadManager readManager;
    private final OutboundSellerOutboxCommandManager commandManager;

    public OutboundSellerCompletionFacade(
            OutboundSellerOutboxReadManager readManager,
            OutboundSellerOutboxCommandManager commandManager) {
        this.readManager = readManager;
        this.commandManager = commandManager;
    }

    @Transactional
    public void completeOutbox(Long outboxId, Instant now) {
        OutboundSellerOutbox outbox = readManager.getById(outboxId);
        outbox.complete(now);
        commandManager.persist(outbox);
    }
}
