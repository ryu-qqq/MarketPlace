package com.ryuqq.marketplace.application.outboundproduct.port.in.command;

/** 연동 재처리 UseCase. */
public interface RetryOutboundSyncUseCase {
    /**
     * FAILED 상태의 Outbox를 PENDING 상태로 재처리 요청.
     *
     * @param outboxId 재처리 대상 Outbox ID
     */
    void execute(long outboxId);
}
