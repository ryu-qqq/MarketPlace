package com.ryuqq.marketplace.application.settlement.entry.port.in.command;

import com.ryuqq.marketplace.application.settlement.entry.dto.command.CreateReversalEntryCommand;

/** 클레임 완료 → 역분개 Entry 생성 UseCase. */
public interface CreateReversalEntryUseCase {

    void execute(CreateReversalEntryCommand command);
}
