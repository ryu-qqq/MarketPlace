package com.ryuqq.marketplace.application.settlement.port.in.command;

import com.ryuqq.marketplace.application.settlement.dto.command.AggregateSettlementCommand;

/** CONFIRMED Entry → Settlement 집계 UseCase. */
public interface AggregateSettlementUseCase {

    void execute(AggregateSettlementCommand command);

    /** CONFIRMED Entry가 존재하는 모든 셀러에 대해 주간 집계를 수행한다. */
    void executeAll();
}
