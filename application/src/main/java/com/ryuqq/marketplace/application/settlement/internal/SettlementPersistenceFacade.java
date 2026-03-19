package com.ryuqq.marketplace.application.settlement.internal;

import com.ryuqq.marketplace.application.settlement.entry.manager.SettlementEntryCommandManager;
import com.ryuqq.marketplace.application.settlement.manager.SettlementCommandManager;
import com.ryuqq.marketplace.domain.settlement.aggregate.Settlement;
import com.ryuqq.marketplace.domain.settlement.entry.aggregate.SettlementEntry;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 정산 퍼시스트 파사드.
 *
 * <p>Settlement + SettlementEntry 상태 변경을 같은 트랜잭션에서 일괄 처리합니다.
 */
@Component
public class SettlementPersistenceFacade {

    private final SettlementCommandManager settlementCommandManager;
    private final SettlementEntryCommandManager entryCommandManager;

    public SettlementPersistenceFacade(
            SettlementCommandManager settlementCommandManager,
            SettlementEntryCommandManager entryCommandManager) {
        this.settlementCommandManager = settlementCommandManager;
        this.entryCommandManager = entryCommandManager;
    }

    /** Settlement 단건 저장. */
    @Transactional
    public void persist(Settlement settlement) {
        settlementCommandManager.persist(settlement);
    }

    /** Settlement 생성 + Entry SETTLED 상태 일괄 저장. */
    @Transactional
    public void persistWithSettledEntries(Settlement settlement, List<SettlementEntry> entries) {
        settlementCommandManager.persist(settlement);
        entryCommandManager.persistAll(entries);
    }
}
