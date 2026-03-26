package com.ryuqq.marketplace.application.settlement.port.in.command;

/** 정산 보류 UseCase. */
public interface HoldSettlementUseCase {

    void execute(String settlementId, String reason);
}
