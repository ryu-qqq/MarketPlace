package com.ryuqq.marketplace.application.cancel.manager;

import com.ryuqq.marketplace.application.cancel.port.out.command.CancelOutboxCommandPort;
import com.ryuqq.marketplace.domain.cancel.outbox.aggregate.CancelOutbox;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** CancelOutbox Write Manager. */
@Component
public class CancelOutboxCommandManager {

    private final CancelOutboxCommandPort commandPort;

    public CancelOutboxCommandManager(CancelOutboxCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(CancelOutbox outbox) {
        return commandPort.persist(outbox);
    }

    @Transactional
    public void persistAll(List<CancelOutbox> outboxes) {
        commandPort.persistAll(outboxes);
    }
}
