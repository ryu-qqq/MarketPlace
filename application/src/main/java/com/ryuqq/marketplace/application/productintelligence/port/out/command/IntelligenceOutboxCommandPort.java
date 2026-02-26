package com.ryuqq.marketplace.application.productintelligence.port.out.command;

import com.ryuqq.marketplace.domain.productintelligence.aggregate.IntelligenceOutbox;

/** Intelligence Pipeline Outbox Command Port. */
public interface IntelligenceOutboxCommandPort {

    Long persist(IntelligenceOutbox outbox);
}
