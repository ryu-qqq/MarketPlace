package com.ryuqq.marketplace.application.setofsync.manager;

import com.ryuqq.marketplace.application.setofsync.port.out.command.SetofSyncOutboxCommandPort;
import com.ryuqq.marketplace.domain.setofsync.aggregate.SetofSyncOutbox;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SetofSyncOutboxCommandManager {

    private final SetofSyncOutboxCommandPort commandPort;

    public SetofSyncOutboxCommandManager(SetofSyncOutboxCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(SetofSyncOutbox outbox) {
        return commandPort.persist(outbox);
    }
}
