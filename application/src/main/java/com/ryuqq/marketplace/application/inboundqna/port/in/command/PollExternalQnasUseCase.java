package com.ryuqq.marketplace.application.inboundqna.port.in.command;

/** 외부몰 QnA 폴링 UseCase. */
public interface PollExternalQnasUseCase {
    int execute(long salesChannelId, int batchSize);
}
