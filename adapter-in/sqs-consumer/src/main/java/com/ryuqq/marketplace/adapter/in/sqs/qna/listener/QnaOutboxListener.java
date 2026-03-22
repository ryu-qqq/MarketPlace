package com.ryuqq.marketplace.adapter.in.sqs.qna.listener;

import com.ryuqq.marketplace.adapter.in.sqs.qna.dto.QnaOutboxSqsMessage;
import com.ryuqq.marketplace.application.qna.dto.command.ExecuteQnaOutboxCommand;
import com.ryuqq.marketplace.application.qna.port.in.command.ExecuteQnaOutboxUseCase;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * QnA Outbox SQS 컨슈머 리스너.
 *
 * <p>SQS 메시지를 수신하여 QnA 답변 외부 동기화를 실행합니다.
 */
@Component
@ConditionalOnProperty(
        prefix = "sqs.consumer.qna-outbox",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false)
public class QnaOutboxListener {

    private static final Logger log = LoggerFactory.getLogger(QnaOutboxListener.class);

    private final ExecuteQnaOutboxUseCase useCase;
    private final Timer durationTimer;
    private final Counter successCounter;
    private final Counter errorCounter;

    public QnaOutboxListener(ExecuteQnaOutboxUseCase useCase, MeterRegistry meterRegistry) {
        this.useCase = useCase;
        this.durationTimer =
                Timer.builder("sqs.consumer.duration")
                        .tag("queue", "qna-outbox")
                        .register(meterRegistry);
        this.successCounter =
                Counter.builder("sqs.consumer.messages")
                        .tag("queue", "qna-outbox")
                        .tag("result", "success")
                        .register(meterRegistry);
        this.errorCounter =
                Counter.builder("sqs.consumer.messages")
                        .tag("queue", "qna-outbox")
                        .tag("result", "error")
                        .register(meterRegistry);
    }

    @SqsListener("${sqs.queues.qna-outbox}")
    public void onMessage(QnaOutboxSqsMessage message) {
        Timer.Sample sample = Timer.start();
        try {
            useCase.execute(new ExecuteQnaOutboxCommand(
                    message.outboxId(),
                    message.qnaId(),
                    message.salesChannelId(),
                    message.externalQnaId(),
                    message.outboxType()));
            sample.stop(durationTimer);
            successCounter.increment();
        } catch (Exception e) {
            sample.stop(durationTimer);
            errorCounter.increment();
            log.error("QnA Outbox 메시지 처리 실패: outboxId={}, error={}",
                    message.outboxId(), e.getMessage(), e);
            throw e;
        }
    }
}
