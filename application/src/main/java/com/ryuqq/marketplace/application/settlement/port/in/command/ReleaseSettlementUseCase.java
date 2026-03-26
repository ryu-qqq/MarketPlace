package com.ryuqq.marketplace.application.settlement.port.in.command;

/** 보류 해제 UseCase. */
public interface ReleaseSettlementUseCase {

    void execute(String settlementId);
}
