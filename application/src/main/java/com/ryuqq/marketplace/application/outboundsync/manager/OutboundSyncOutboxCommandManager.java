package com.ryuqq.marketplace.application.outboundsync.manager;

import com.ryuqq.marketplace.application.outboundsync.port.out.command.OutboundSyncOutboxCommandPort;
import com.ryuqq.marketplace.domain.outboundsync.aggregate.OutboundSyncOutbox;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 외부 상품 연동 Outbox Command Manager. */
@Component
public class OutboundSyncOutboxCommandManager {

    private final OutboundSyncOutboxCommandPort commandPort;

    public OutboundSyncOutboxCommandManager(OutboundSyncOutboxCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(OutboundSyncOutbox outbox) {
        return commandPort.persist(outbox);
    }

    @Transactional
    public void persistAll(List<OutboundSyncOutbox> outboxes) {
        commandPort.persistAll(outboxes);
    }
}
