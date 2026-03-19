package com.ryuqq.marketplace.application.settlement.entry.service.command;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.settlement.entry.internal.SettlementEntryPersistenceFacade;
import com.ryuqq.marketplace.application.settlement.entry.manager.SettlementEntryReadManager;
import com.ryuqq.marketplace.application.settlement.entry.port.in.command.ConfirmPendingEntriesUseCase;
import com.ryuqq.marketplace.domain.settlement.entry.aggregate.SettlementEntry;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

/** PENDING → CONFIRMED 배치 확정 서비스. */
@Service
public class ConfirmPendingEntriesService implements ConfirmPendingEntriesUseCase {

    private final SettlementEntryReadManager readManager;
    private final SettlementEntryPersistenceFacade persistenceFacade;
    private final TimeProvider timeProvider;

    public ConfirmPendingEntriesService(
            SettlementEntryReadManager readManager,
            SettlementEntryPersistenceFacade persistenceFacade,
            TimeProvider timeProvider) {
        this.readManager = readManager;
        this.persistenceFacade = persistenceFacade;
        this.timeProvider = timeProvider;
    }

    @Override
    public int execute(int batchSize) {
        Instant now = timeProvider.now();
        List<SettlementEntry> entries = readManager.findConfirmableEntries(now, batchSize);

        if (entries.isEmpty()) {
            return 0;
        }

        entries.forEach(entry -> entry.confirm(now));
        persistenceFacade.persistAll(entries);

        return entries.size();
    }
}
