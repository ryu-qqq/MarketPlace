package com.ryuqq.marketplace.application.settlement.entry.port.in.command;

import com.ryuqq.marketplace.application.settlement.entry.dto.command.CompleteSettlementEntryBatchCommand;

/** 정산 원장 일괄 완료(CONFIRMED) 배치 UseCase. */
public interface CompleteSettlementEntryBatchUseCase {

    /** 지정한 Entry 목록을 일괄 CONFIRMED 처리합니다. */
    void execute(CompleteSettlementEntryBatchCommand command);
}
