package com.ryuqq.marketplace.application.setofsync.port.out.command;

import com.ryuqq.marketplace.domain.setofsync.aggregate.SetofSyncOutbox;

public interface SetofSyncOutboxCommandPort {
    Long persist(SetofSyncOutbox outbox);
}
