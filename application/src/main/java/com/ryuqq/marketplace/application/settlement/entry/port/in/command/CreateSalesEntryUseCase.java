package com.ryuqq.marketplace.application.settlement.entry.port.in.command;

import com.ryuqq.marketplace.application.settlement.entry.dto.command.CreateSalesEntryCommand;

/** 구매확정 → 판매 Entry 생성 UseCase. */
public interface CreateSalesEntryUseCase {

    void execute(CreateSalesEntryCommand command);
}
