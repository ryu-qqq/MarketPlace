package com.ryuqq.marketplace.application.settlement.port.in.command;

import com.ryuqq.marketplace.application.settlement.dto.command.AggregateSettlementCommand;

/** CONFIRMED Entry → Settlement 집계 UseCase. */
public interface AggregateSettlementUseCase {

    void execute(AggregateSettlementCommand command);
}
