package com.ryuqq.marketplace.application.refund.port.out.command;

import com.ryuqq.marketplace.domain.refund.outbox.aggregate.RefundOutbox;
import java.util.List;

/** 환불 아웃박스 Command Port. */
public interface RefundOutboxCommandPort {

    Long persist(RefundOutbox outbox);

    void persistAll(List<RefundOutbox> outboxes);
}
