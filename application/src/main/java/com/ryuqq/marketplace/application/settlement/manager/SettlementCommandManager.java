package com.ryuqq.marketplace.application.settlement.manager;

import com.ryuqq.marketplace.application.settlement.port.out.command.SettlementCommandPort;
import com.ryuqq.marketplace.domain.settlement.aggregate.Settlement;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 정산 Write Manager. */
@Component
public class SettlementCommandManager {

    private final SettlementCommandPort commandPort;

    public SettlementCommandManager(SettlementCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public void persist(Settlement settlement) {
        commandPort.persist(settlement);
    }
}
