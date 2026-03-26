package com.ryuqq.marketplace.application.settlement.entry.port.in.command;

import com.ryuqq.marketplace.application.settlement.entry.dto.command.ReleaseSettlementEntryBatchCommand;

/** 정산 원장 일괄 보류 해제(PENDING) 배치 UseCase. */
public interface ReleaseSettlementEntryBatchUseCase {

    /** 지정한 Entry 목록을 일괄 HOLD → PENDING 처리합니다. */
    void execute(ReleaseSettlementEntryBatchCommand command);
}
