package com.ryuqq.marketplace.application.settlement.entry.manager;

import com.ryuqq.marketplace.application.settlement.entry.port.out.command.SettlementEntryCommandPort;
import com.ryuqq.marketplace.domain.settlement.entry.aggregate.SettlementEntry;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 정산 원장 Write Manager. */
@Component
public class SettlementEntryCommandManager {

    private final SettlementEntryCommandPort commandPort;

    public SettlementEntryCommandManager(SettlementEntryCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public void persist(SettlementEntry entry) {
        commandPort.persist(entry);
    }

    @Transactional
    public void persistAll(List<SettlementEntry> entries) {
        commandPort.persistAll(entries);
    }
}
