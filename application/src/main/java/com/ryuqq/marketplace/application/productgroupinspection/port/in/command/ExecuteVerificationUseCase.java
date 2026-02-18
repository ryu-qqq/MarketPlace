package com.ryuqq.marketplace.application.productgroupinspection.port.in.command;

import com.ryuqq.marketplace.application.productgroupinspection.dto.command.ExecuteVerificationCommand;

/** Verification 단계 실행 UseCase. SQS VerificationConsumer에서 호출. */
public interface ExecuteVerificationUseCase {

    void execute(ExecuteVerificationCommand command);
}
