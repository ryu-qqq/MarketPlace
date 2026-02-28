package com.ryuqq.marketplace.adapter.in.sqs.outboundsync.listener;

import com.ryuqq.marketplace.adapter.in.sqs.outboundsync.dto.OutboundSyncSqsMessage;
import com.ryuqq.marketplace.application.outboundsync.dto.command.ExecuteOutboundSyncCommand;
import com.ryuqq.marketplace.application.outboundsync.port.in.command.ExecuteOutboundSyncUseCase;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncType;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * OutboundSync SQS 컨슈머 리스너.
 *
 * <p>SQS 메시지를 수신하여 외부 채널 연동을 실행합니다.
 */
@Component
@ConditionalOnProperty(
        prefix = "sqs.consumer.outbound-sync",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false)
public class OutboundSyncListener {

    private static final Logger log = LoggerFactory.getLogger(OutboundSyncListener.class);

    private final ExecuteOutboundSyncUseCase useCase;
    private final MeterRegistry meterRegistry;
    private final Timer durationTimer;
    private final Counter successCounter;
    private final Counter errorCounter;

    public OutboundSyncListener(ExecuteOutboundSyncUseCase useCase, MeterRegistry meterRegistry) {
        this.useCase = useCase;
        this.meterRegistry = meterRegistry;
        this.durationTimer =
                Timer.builder("sqs.consumer.duration")
                        .tag("queue", "outbound-sync")
                        .register(meterRegistry);
        this.successCounter =
                Counter.builder("sqs.consumer.messages")
                        .tag("queue", "outbound-sync")
                        .tag("result", "success")
                        .register(meterRegistry);
        this.errorCounter =
                Counter.builder("sqs.consumer.messages")
                        .tag("queue", "outbound-sync")
                        .tag("result", "error")
                        .register(meterRegistry);
    }

    @SqsListener("${sqs.queues.outbound-sync}")
    public void onMessage(OutboundSyncSqsMessage message) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            useCase.execute(
                    ExecuteOutboundSyncCommand.of(
                            message.outboxId(),
                            message.productGroupId(),
                            message.salesChannelId(),
                            SyncType.valueOf(message.syncType())));
            sample.stop(durationTimer);
            successCounter.increment();
        } catch (Exception e) {
            sample.stop(durationTimer);
            errorCounter.increment();
            log.error(
                    "OutboundSync 메시지 처리 실패: outboxId={}, error={}",
                    message.outboxId(),
                    e.getMessage(),
                    e);
            throw e;
        }
    }
}
