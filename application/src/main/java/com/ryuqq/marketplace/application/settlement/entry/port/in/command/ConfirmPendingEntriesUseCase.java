package com.ryuqq.marketplace.application.settlement.entry.port.in.command;

/** PENDING → CONFIRMED 배치 확정 UseCase. */
public interface ConfirmPendingEntriesUseCase {

    /** 확정 대상 Entry를 일괄 CONFIRMED 처리하고, 처리 건수를 반환합니다. */
    int execute(int batchSize);
}
