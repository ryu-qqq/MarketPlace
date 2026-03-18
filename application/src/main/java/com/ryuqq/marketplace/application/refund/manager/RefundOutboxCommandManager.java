package com.ryuqq.marketplace.application.refund.manager;

import com.ryuqq.marketplace.application.refund.port.out.command.RefundOutboxCommandPort;
import com.ryuqq.marketplace.domain.refund.outbox.aggregate.RefundOutbox;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** RefundOutbox Write Manager. */
@Component
public class RefundOutboxCommandManager {

    private final RefundOutboxCommandPort commandPort;

    public RefundOutboxCommandManager(RefundOutboxCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(RefundOutbox outbox) {
        return commandPort.persist(outbox);
    }

    @Transactional
    public void persistAll(List<RefundOutbox> outboxes) {
        commandPort.persistAll(outboxes);
    }
}
