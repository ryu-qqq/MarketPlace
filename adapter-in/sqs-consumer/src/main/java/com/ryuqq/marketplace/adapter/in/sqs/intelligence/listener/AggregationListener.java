package com.ryuqq.marketplace.adapter.in.sqs.intelligence.listener;

import com.ryuqq.marketplace.adapter.in.sqs.intelligence.dto.IntelligenceSqsMessage;
import com.ryuqq.marketplace.application.productintelligence.dto.command.AggregateAnalysisCommand;
import com.ryuqq.marketplace.application.productintelligence.port.in.command.AggregateAnalysisUseCase;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Aggregation 단계 SQS Listener.
 *
 * <p>마지막 Analyzer가 완료된 후 Aggregation 큐의 메시지를 수신하여 3개 분석 결과를 종합하고 최종 판정을 내립니다.
 *
 * <p>메시지 처리 성공 시 자동으로 acknowledge (삭제)되며, 실패 시 visibility timeout 후 재처리됩니다. maxReceiveCount 초과 시
 * DLQ로 이동합니다.
 */
@Component
@ConditionalOnProperty(
        prefix = "sqs.consumer.intelligence-aggregation",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
public class AggregationListener {

    private static final Logger log = LoggerFactory.getLogger(AggregationListener.class);

    private final AggregateAnalysisUseCase aggregateAnalysisUseCase;

    public AggregationListener(AggregateAnalysisUseCase aggregateAnalysisUseCase) {
        this.aggregateAnalysisUseCase = aggregateAnalysisUseCase;
    }

    @SqsListener("${sqs.queues.intelligence-aggregation}")
    public void onMessage(IntelligenceSqsMessage message) {
        log.debug(
                "Aggregation 메시지 수신: profileId={}, productGroupId={}",
                message.profileId(),
                message.productGroupId());

        aggregateAnalysisUseCase.execute(
                AggregateAnalysisCommand.of(message.profileId(), message.productGroupId()));

        log.info(
                "Aggregation 메시지 처리 완료: profileId={}, productGroupId={}",
                message.profileId(),
                message.productGroupId());
    }
}
