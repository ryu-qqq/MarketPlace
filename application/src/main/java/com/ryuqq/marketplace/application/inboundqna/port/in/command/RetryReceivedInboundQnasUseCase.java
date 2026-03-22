package com.ryuqq.marketplace.application.inboundqna.port.in.command;

/** RECEIVED 상태 InboundQna 일괄 재변환 UseCase. */
public interface RetryReceivedInboundQnasUseCase {
    int execute(int batchSize);
}
