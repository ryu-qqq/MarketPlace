package com.ryuqq.marketplace.application.productgroupinspection.port.in.command;

import com.ryuqq.marketplace.application.productgroupinspection.dto.command.ExecuteScoringCommand;

/** Scoring 단계 실행 UseCase. SQS ScoringConsumer에서 호출. */
public interface ExecuteScoringUseCase {

    void execute(ExecuteScoringCommand command);
}
