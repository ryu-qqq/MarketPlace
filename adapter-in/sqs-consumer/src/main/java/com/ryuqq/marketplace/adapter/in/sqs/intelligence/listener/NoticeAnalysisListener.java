package com.ryuqq.marketplace.adapter.in.sqs.intelligence.listener;

import com.ryuqq.marketplace.adapter.in.sqs.intelligence.dto.IntelligenceSqsMessage;
import com.ryuqq.marketplace.application.productintelligence.dto.command.ExecuteNoticeAnalysisCommand;
import com.ryuqq.marketplace.application.productintelligence.port.in.command.ExecuteNoticeAnalysisUseCase;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Notice 분석 SQS Listener.
 *
 * <p>Orchestrator가 발행한 intelligence-notice-analysis 큐의 메시지를 수신하여 누락된 고시정보 필드를 보강하는 분석을 실행합니다.
 *
 * <p>메시지 처리 성공 시 자동으로 acknowledge (삭제)되며, 실패 시 visibility timeout 후 재처리됩니다. maxReceiveCount 초과 시
 * DLQ로 이동합니다.
 */
@Component
@ConditionalOnProperty(
        prefix = "sqs.consumer.intelligence-notice-analysis",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false)
public class NoticeAnalysisListener {

    private static final Logger log = LoggerFactory.getLogger(NoticeAnalysisListener.class);
    private static final String QUEUE_TAG = "notice-analysis";

    private final ExecuteNoticeAnalysisUseCase useCase;
    private final MeterRegistry meterRegistry;
    private final Timer durationTimer;
    private final Counter successCounter;
    private final Counter errorCounter;

    public NoticeAnalysisListener(
            ExecuteNoticeAnalysisUseCase useCase, MeterRegistry meterRegistry) {
        this.useCase = useCase;
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

    @SqsListener("${sqs.queues.intelligence-notice-analysis}")
    public void onMessage(IntelligenceSqsMessage message) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            log.debug(
                    "Notice 분석 메시지 수신: profileId={}, productGroupId={}",
                    message.profileId(),
                    message.productGroupId());

            useCase.execute(
                    ExecuteNoticeAnalysisCommand.of(
                            message.profileId(), message.productGroupId()));

            sample.stop(durationTimer);
            successCounter.increment();

            log.info(
                    "Notice 분석 메시지 처리 완료: profileId={}, productGroupId={}",
                    message.profileId(),
                    message.productGroupId());
        } catch (Exception e) {
            sample.stop(durationTimer);
            errorCounter.increment();
            log.error(
                    "Notice 분석 메시지 처리 실패: profileId={}, productGroupId={}",
                    message.profileId(),
                    message.productGroupId(),
                    e);
            throw e;
        }
    }
}
