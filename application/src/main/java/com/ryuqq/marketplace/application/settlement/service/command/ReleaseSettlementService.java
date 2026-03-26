package com.ryuqq.marketplace.application.settlement.service.command;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.settlement.internal.SettlementPersistenceFacade;
import com.ryuqq.marketplace.application.settlement.manager.SettlementReadManager;
import com.ryuqq.marketplace.application.settlement.port.in.command.ReleaseSettlementUseCase;
import com.ryuqq.marketplace.domain.settlement.aggregate.Settlement;
import com.ryuqq.marketplace.domain.settlement.id.SettlementId;
import org.springframework.stereotype.Service;

/** 보류 해제 서비스. */
@Service
public class ReleaseSettlementService implements ReleaseSettlementUseCase {

    private final SettlementReadManager readManager;
    private final SettlementPersistenceFacade persistenceFacade;
    private final TimeProvider timeProvider;

    public ReleaseSettlementService(
            SettlementReadManager readManager,
            SettlementPersistenceFacade persistenceFacade,
            TimeProvider timeProvider) {
        this.readManager = readManager;
        this.persistenceFacade = persistenceFacade;
        this.timeProvider = timeProvider;
    }

    @Override
    public void execute(String settlementId) {
        Settlement settlement = readManager.getById(SettlementId.of(settlementId));
        settlement.releaseHold(timeProvider.now());
        persistenceFacade.persist(settlement);
    }
}
