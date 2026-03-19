package com.ryuqq.marketplace.application.settlement.entry.service.command;

import com.ryuqq.marketplace.application.settlement.entry.dto.command.CreateReversalEntryCommand;
import com.ryuqq.marketplace.application.settlement.entry.factory.SettlementEntryCommandFactory;
import com.ryuqq.marketplace.application.settlement.entry.internal.SettlementEntryPersistenceFacade;
import com.ryuqq.marketplace.application.settlement.entry.port.in.command.CreateReversalEntryUseCase;
import com.ryuqq.marketplace.domain.settlement.entry.aggregate.SettlementEntry;
import org.springframework.stereotype.Service;

/** 역분개 Entry 생성 서비스. */
@Service
public class CreateReversalEntryService implements CreateReversalEntryUseCase {

    private final SettlementEntryCommandFactory commandFactory;
    private final SettlementEntryPersistenceFacade persistenceFacade;

    public CreateReversalEntryService(
            SettlementEntryCommandFactory commandFactory,
            SettlementEntryPersistenceFacade persistenceFacade) {
        this.commandFactory = commandFactory;
        this.persistenceFacade = persistenceFacade;
    }

    @Override
    public void execute(CreateReversalEntryCommand command) {
        SettlementEntry entry = commandFactory.createReversalEntry(command);
        persistenceFacade.persist(entry);
    }
}
