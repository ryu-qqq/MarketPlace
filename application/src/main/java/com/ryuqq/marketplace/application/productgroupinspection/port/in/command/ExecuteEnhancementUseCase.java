package com.ryuqq.marketplace.application.productgroupinspection.port.in.command;

import com.ryuqq.marketplace.application.productgroupinspection.dto.command.ExecuteEnhancementCommand;

/** Enhancement 단계 실행 UseCase. SQS EnhancementConsumer에서 호출. */
public interface ExecuteEnhancementUseCase {

    void execute(ExecuteEnhancementCommand command);
}
