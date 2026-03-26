package com.ryuqq.marketplace.application.settlement.entry.service.command;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.settlement.entry.dto.command.ReleaseSettlementEntryBatchCommand;
import com.ryuqq.marketplace.application.settlement.entry.internal.SettlementEntryPersistenceFacade;
import com.ryuqq.marketplace.application.settlement.entry.manager.SettlementEntryReadManager;
import com.ryuqq.marketplace.application.settlement.entry.port.in.command.ReleaseSettlementEntryBatchUseCase;
import com.ryuqq.marketplace.domain.settlement.entry.aggregate.SettlementEntry;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

/** 정산 원장 일괄 보류 해제(PENDING) 배치 서비스. */
@Service
public class ReleaseSettlementEntryBatchService implements ReleaseSettlementEntryBatchUseCase {

    private final SettlementEntryReadManager readManager;
    private final SettlementEntryPersistenceFacade persistenceFacade;
    private final TimeProvider timeProvider;

    public ReleaseSettlementEntryBatchService(
            SettlementEntryReadManager readManager,
            SettlementEntryPersistenceFacade persistenceFacade,
            TimeProvider timeProvider) {
        this.readManager = readManager;
        this.persistenceFacade = persistenceFacade;
        this.timeProvider = timeProvider;
    }

    @Override
    public void execute(ReleaseSettlementEntryBatchCommand command) {
        Instant now = timeProvider.now();
        List<SettlementEntry> entries = readManager.findByIdIn(command.entryIds());
        entries.forEach(entry -> entry.releaseHold(now));
        persistenceFacade.persistAll(entries);
    }
}
