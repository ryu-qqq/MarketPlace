package com.ryuqq.marketplace.application.cancel.port.out.command;

import com.ryuqq.marketplace.domain.cancel.outbox.aggregate.CancelOutbox;
import java.util.List;

/** 취소 아웃박스 Command Port. */
public interface CancelOutboxCommandPort {

    Long persist(CancelOutbox outbox);

    void persistAll(List<CancelOutbox> outboxes);
}
