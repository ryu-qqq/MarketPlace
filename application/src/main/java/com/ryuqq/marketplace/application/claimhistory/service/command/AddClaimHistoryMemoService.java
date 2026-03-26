package com.ryuqq.marketplace.application.claimhistory.service.command;

import com.ryuqq.marketplace.application.claimhistory.dto.command.AddClaimHistoryMemoCommand;
import com.ryuqq.marketplace.application.claimhistory.factory.ClaimHistoryFactory;
import com.ryuqq.marketplace.application.claimhistory.manager.ClaimHistoryCommandManager;
import com.ryuqq.marketplace.application.claimhistory.port.in.command.AddClaimHistoryMemoUseCase;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import org.springframework.stereotype.Service;

/** 수기 메모 등록 서비스. */
@Service
public class AddClaimHistoryMemoService implements AddClaimHistoryMemoUseCase {

    private final ClaimHistoryFactory historyFactory;
    private final ClaimHistoryCommandManager historyCommandManager;

    public AddClaimHistoryMemoService(
            ClaimHistoryFactory historyFactory, ClaimHistoryCommandManager historyCommandManager) {
        this.historyFactory = historyFactory;
        this.historyCommandManager = historyCommandManager;
    }

    @Override
    public String execute(AddClaimHistoryMemoCommand command) {
        ClaimHistory history =
                historyFactory.createManualMemo(
                        command.claimType(),
                        command.claimId(),
                        command.orderItemId(),
                        command.message(),
                        command.actorId(),
                        command.actorName());
        historyCommandManager.persist(history);
        return history.idValue();
    }
}
