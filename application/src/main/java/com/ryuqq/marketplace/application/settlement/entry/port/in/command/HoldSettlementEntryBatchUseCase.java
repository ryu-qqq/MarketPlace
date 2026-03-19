package com.ryuqq.marketplace.application.settlement.entry.port.in.command;

import com.ryuqq.marketplace.application.settlement.entry.dto.command.HoldSettlementEntryBatchCommand;

/** 정산 원장 일괄 보류(HOLD) 배치 UseCase. */
public interface HoldSettlementEntryBatchUseCase {

    /** 지정한 Entry 목록을 일괄 HOLD 처리합니다. */
    void execute(HoldSettlementEntryBatchCommand command);
}
