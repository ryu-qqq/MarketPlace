package com.ryuqq.marketplace.application.outboundsync.port.in.command;

import com.ryuqq.marketplace.application.outboundsync.dto.command.ExecuteOutboundSyncCommand;

/** SQS 메시지 수신 후 외부 채널 연동을 실행하는 UseCase. */
public interface ExecuteOutboundSyncUseCase {

    void execute(ExecuteOutboundSyncCommand command);
}
