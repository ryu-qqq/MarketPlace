package com.ryuqq.marketplace.application.externalproductsync.manager;

import com.ryuqq.marketplace.application.externalproductsync.port.out.command.ExternalProductSyncOutboxCommandPort;
import com.ryuqq.marketplace.domain.externalproductsync.aggregate.ExternalProductSyncOutbox;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 외부 상품 연동 Outbox Command Manager. */
@Component
public class ExternalProductSyncOutboxCommandManager {

    private final ExternalProductSyncOutboxCommandPort commandPort;

    public ExternalProductSyncOutboxCommandManager(
            ExternalProductSyncOutboxCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(ExternalProductSyncOutbox outbox) {
        return commandPort.persist(outbox);
    }

    @Transactional
    public void persistAll(List<ExternalProductSyncOutbox> outboxes) {
        commandPort.persistAll(outboxes);
    }
}
