package com.ryuqq.marketplace.application.exchange.manager;

import com.ryuqq.marketplace.application.exchange.port.out.command.ExchangeOutboxCommandPort;
import com.ryuqq.marketplace.domain.exchange.outbox.aggregate.ExchangeOutbox;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** ExchangeOutbox Write Manager. */
@Component
public class ExchangeOutboxCommandManager {

    private final ExchangeOutboxCommandPort commandPort;

    public ExchangeOutboxCommandManager(ExchangeOutboxCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(ExchangeOutbox outbox) {
        return commandPort.persist(outbox);
    }

    @Transactional
    public void persistAll(List<ExchangeOutbox> outboxes) {
        commandPort.persistAll(outboxes);
    }
}
