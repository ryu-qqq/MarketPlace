package com.ryuqq.marketplace.application.settlement.entry.internal;

import com.ryuqq.marketplace.application.settlement.entry.manager.SettlementEntryCommandManager;
import com.ryuqq.marketplace.domain.settlement.entry.aggregate.SettlementEntry;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 정산 원장 퍼시스트 파사드. */
@Component
public class SettlementEntryPersistenceFacade {

    private final SettlementEntryCommandManager entryCommandManager;

    public SettlementEntryPersistenceFacade(SettlementEntryCommandManager entryCommandManager) {
        this.entryCommandManager = entryCommandManager;
    }

    /** Entry 단건 저장. */
    @Transactional
    public void persist(SettlementEntry entry) {
        entryCommandManager.persist(entry);
    }

    /** Entry 일괄 저장. */
    @Transactional
    public void persistAll(List<SettlementEntry> entries) {
        entryCommandManager.persistAll(entries);
    }
}
