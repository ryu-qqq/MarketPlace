package com.ryuqq.marketplace.adapter.in.sqs.intelligence.listener;

import com.ryuqq.marketplace.adapter.in.sqs.intelligence.dto.IntelligenceSqsMessage;
import com.ryuqq.marketplace.application.productintelligence.dto.command.AggregateAnalysisCommand;
import com.ryuqq.marketplace.application.productintelligence.port.in.command.AggregateAnalysisUseCase;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
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
        matchIfMissing = false)
public class AggregationListener {

    private static final Logger log = LoggerFactory.getLogger(AggregationListener.class);
    private static final String QUEUE_TAG = "aggregation";

    private final AggregateAnalysisUseCase aggregateAnalysisUseCase;
    private final MeterRegistry meterRegistry;
    private final Timer durationTimer;
    private final Counter successCounter;
    private final Counter errorCounter;

    public AggregationListener(
            AggregateAnalysisUseCase aggregateAnalysisUseCase, MeterRegistry meterRegistry) {
        this.aggregateAnalysisUseCase = aggregateAnalysisUseCase;
        this.meterRegistry = meterRegistry;
        this.durationTimer =
                Timer.builder("sqs.consumer.duration")
                        .tag("queue", QUEUE_TAG)
                        .publishPercentileHistogram()
                        .register(meterRegistry);
        this.successCounter =
                Counter.builder("sqs.consumer.messages")
                        .tag("queue", QUEUE_TAG)
                        .tag("result", "success")
                        .register(meterRegistry);
        this.errorCounter =
                Counter.builder("sqs.consumer.messages")
                        .tag("queue", QUEUE_TAG)
                        .tag("result", "error")
                        .register(meterRegistry);
    }

    @SqsListener("${sqs.queues.intelligence-aggregation}")
    public void onMessage(IntelligenceSqsMessage message) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            log.debug(
                    "Aggregation 메시지 수신: profileId={}, productGroupId={}",
                    message.profileId(),
                    message.productGroupId());

            aggregateAnalysisUseCase.execute(
                    AggregateAnalysisCommand.of(message.profileId(), message.productGroupId()));

            sample.stop(durationTimer);
            successCounter.increment();

            log.info(
                    "Aggregation 메시지 처리 완료: profileId={}, productGroupId={}",
                    message.profileId(),
                    message.productGroupId());
        } catch (Exception e) {
            sample.stop(durationTimer);
            errorCounter.increment();
            log.error(
                    "Aggregation 메시지 처리 실패: profileId={}, productGroupId={}",
                    message.profileId(),
                    message.productGroupId(),
                    e);
            throw e;
        }
    }
}
