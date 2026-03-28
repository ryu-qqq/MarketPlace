package com.ryuqq.marketplace.adapter.in.sqs.claim.listener;

import com.ryuqq.marketplace.adapter.in.sqs.claim.dto.RefundOutboxSqsMessage;
import com.ryuqq.marketplace.application.refund.dto.command.ExecuteRefundOutboxCommand;
import com.ryuqq.marketplace.application.refund.port.in.command.ExecuteRefundOutboxUseCase;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 환불 Outbox SQS 컨슈머 리스너.
 *
 * <p>SQS 메시지를 수신하여 환불 Outbox 처리를 실행합니다.
 */
@Component
@ConditionalOnProperty(
        prefix = "sqs.consumer.refund-outbox",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false)
public class RefundOutboxListener {

    private static final Logger log = LoggerFactory.getLogger(RefundOutboxListener.class);

    private final ExecuteRefundOutboxUseCase useCase;
    private final MeterRegistry meterRegistry;
    private final Timer durationTimer;
    private final Counter successCounter;
    private final Counter errorCounter;

    public RefundOutboxListener(ExecuteRefundOutboxUseCase useCase, MeterRegistry meterRegistry) {
        this.useCase = useCase;
        this.meterRegistry = meterRegistry;
        this.durationTimer =
                Timer.builder("sqs.consumer.duration")
                        .tag("queue", "refund-outbox")
                        .register(meterRegistry);
        this.successCounter =
                Counter.builder("sqs.consumer.messages")
                        .tag("queue", "refund-outbox")
                        .tag("result", "success")
                        .register(meterRegistry);
        this.errorCounter =
                Counter.builder("sqs.consumer.messages")
                        .tag("queue", "refund-outbox")
                        .tag("result", "error")
                        .register(meterRegistry);
    }

    @SqsListener("${sqs.queues.refund-outbox}")
    public void onMessage(RefundOutboxSqsMessage message) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            useCase.execute(
                    ExecuteRefundOutboxCommand.of(
                            message.outboxId(),
                            Long.parseLong(message.orderItemId()),
                            message.outboxType()));
            sample.stop(durationTimer);
            successCounter.increment();
        } catch (Exception e) {
            sample.stop(durationTimer);
            errorCounter.increment();
            log.error(
                    "Refund Outbox 메시지 처리 실패: outboxId={}, error={}",
                    message.outboxId(),
                    e.getMessage(),
                    e);
            throw e;
        }
    }
}
