package com.ryuqq.marketplace.application.settlement.entry.port.out.command;

import com.ryuqq.marketplace.domain.settlement.entry.aggregate.SettlementEntry;
import java.util.List;

/** 정산 원장 Command Port. */
public interface SettlementEntryCommandPort {

    void persist(SettlementEntry entry);

    void persistAll(List<SettlementEntry> entries);
}
