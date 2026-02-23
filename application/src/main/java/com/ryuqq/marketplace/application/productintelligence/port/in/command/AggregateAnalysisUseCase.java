package com.ryuqq.marketplace.application.productintelligence.port.in.command;

import com.ryuqq.marketplace.application.productintelligence.dto.command.AggregateAnalysisCommand;

/** 분석 결과 집계 + 최종 판정 UseCase. Aggregation 큐 리스너에서 호출. */
public interface AggregateAnalysisUseCase {

    void execute(AggregateAnalysisCommand command);
}
