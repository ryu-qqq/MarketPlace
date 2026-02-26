package com.ryuqq.marketplace.application.productintelligence.port.in.command;

import com.ryuqq.marketplace.application.productintelligence.dto.command.ExecuteDescriptionAnalysisCommand;

/** Description 분석 실행 UseCase. SQS 리스너에서 호출. */
public interface ExecuteDescriptionAnalysisUseCase {

    void execute(ExecuteDescriptionAnalysisCommand command);
}
