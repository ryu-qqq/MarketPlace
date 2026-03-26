package com.ryuqq.marketplace.application.settlement.port.out.command;

import com.ryuqq.marketplace.domain.settlement.aggregate.Settlement;

/** 정산 Command Port. */
public interface SettlementCommandPort {

    void persist(Settlement settlement);
}
