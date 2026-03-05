package com.ryuqq.marketplace.application.setofsync.internal;

import com.ryuqq.marketplace.application.setofsync.manager.SetofSyncOutboxCommandManager;
import com.ryuqq.marketplace.application.setofsync.manager.SetofSyncOutboxReadManager;
import com.ryuqq.marketplace.domain.setofsync.aggregate.SetofSyncOutbox;
import java.time.Instant;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SetofSyncCompletionFacade {

    private final SetofSyncOutboxReadManager readManager;
    private final SetofSyncOutboxCommandManager commandManager;

    public SetofSyncCompletionFacade(
            SetofSyncOutboxReadManager readManager, SetofSyncOutboxCommandManager commandManager) {
        this.readManager = readManager;
        this.commandManager = commandManager;
    }

    @Transactional
    public void completeOutbox(Long outboxId, Instant now) {
        SetofSyncOutbox outbox = readManager.getById(outboxId);
        outbox.complete(now);
        commandManager.persist(outbox);
    }
}
