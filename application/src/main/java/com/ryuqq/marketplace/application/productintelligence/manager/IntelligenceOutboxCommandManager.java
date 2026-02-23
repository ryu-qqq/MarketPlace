package com.ryuqq.marketplace.application.productintelligence.manager;

import com.ryuqq.marketplace.application.productintelligence.port.out.command.IntelligenceOutboxCommandPort;
import com.ryuqq.marketplace.domain.productintelligence.aggregate.IntelligenceOutbox;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Intelligence Pipeline Outbox Command Manager. */
@Component
public class IntelligenceOutboxCommandManager {

    private final IntelligenceOutboxCommandPort commandPort;

    public IntelligenceOutboxCommandManager(IntelligenceOutboxCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(IntelligenceOutbox outbox) {
        return commandPort.persist(outbox);
    }
}
