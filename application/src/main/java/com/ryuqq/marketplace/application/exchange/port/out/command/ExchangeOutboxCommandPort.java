package com.ryuqq.marketplace.application.exchange.port.out.command;

import com.ryuqq.marketplace.domain.exchange.outbox.aggregate.ExchangeOutbox;
import java.util.List;

/** 교환 아웃박스 Command Port. */
public interface ExchangeOutboxCommandPort {

    Long persist(ExchangeOutbox outbox);

    void persistAll(List<ExchangeOutbox> outboxes);
}
