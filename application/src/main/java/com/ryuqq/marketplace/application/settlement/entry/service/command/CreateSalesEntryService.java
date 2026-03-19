package com.ryuqq.marketplace.application.settlement.entry.service.command;

import com.ryuqq.marketplace.application.settlement.entry.dto.command.CreateSalesEntryCommand;
import com.ryuqq.marketplace.application.settlement.entry.factory.SettlementEntryCommandFactory;
import com.ryuqq.marketplace.application.settlement.entry.internal.SettlementEntryPersistenceFacade;
import com.ryuqq.marketplace.application.settlement.entry.port.in.command.CreateSalesEntryUseCase;
import com.ryuqq.marketplace.domain.settlement.entry.aggregate.SettlementEntry;
import org.springframework.stereotype.Service;

/** 판매 Entry 생성 서비스. */
@Service
public class CreateSalesEntryService implements CreateSalesEntryUseCase {

    private final SettlementEntryCommandFactory commandFactory;
    private final SettlementEntryPersistenceFacade persistenceFacade;

    public CreateSalesEntryService(
            SettlementEntryCommandFactory commandFactory,
            SettlementEntryPersistenceFacade persistenceFacade) {
        this.commandFactory = commandFactory;
        this.persistenceFacade = persistenceFacade;
    }

    @Override
    public void execute(CreateSalesEntryCommand command) {
        SettlementEntry entry = commandFactory.createSalesEntry(command);
        persistenceFacade.persist(entry);
    }
}
