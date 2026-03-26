package com.ryuqq.marketplace.adapter.in.sqs.shipment.listener;

import com.ryuqq.marketplace.adapter.in.sqs.shipment.dto.ShipmentOutboxSqsMessage;
import com.ryuqq.marketplace.application.shipment.dto.command.ExecuteShipmentOutboxCommand;
import com.ryuqq.marketplace.application.shipment.port.in.command.ExecuteShipmentOutboxUseCase;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 배송 Outbox SQS 컨슈머 리스너.
 *
 * <p>SQS 메시지를 수신하여 배송 Outbox 처리를 실행합니다.
 */
@Component
@ConditionalOnProperty(
        prefix = "sqs.consumer.shipment-outbox",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false)
public class ShipmentOutboxListener {

    private static final Logger log = LoggerFactory.getLogger(ShipmentOutboxListener.class);

    private final ExecuteShipmentOutboxUseCase useCase;
    private final MeterRegistry meterRegistry;
    private final Timer durationTimer;
    private final Counter successCounter;
    private final Counter errorCounter;

    public ShipmentOutboxListener(
            ExecuteShipmentOutboxUseCase useCase, MeterRegistry meterRegistry) {
        this.useCase = useCase;
        this.meterRegistry = meterRegistry;
        this.durationTimer =
                Timer.builder("sqs.consumer.duration")
                        .tag("queue", "shipment-outbox")
                        .register(meterRegistry);
        this.successCounter =
                Counter.builder("sqs.consumer.messages")
                        .tag("queue", "shipment-outbox")
                        .tag("result", "success")
                        .register(meterRegistry);
        this.errorCounter =
                Counter.builder("sqs.consumer.messages")
                        .tag("queue", "shipment-outbox")
                        .tag("result", "error")
                        .register(meterRegistry);
    }

    @SqsListener("${sqs.queues.shipment-outbox}")
    public void onMessage(ShipmentOutboxSqsMessage message) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            useCase.execute(
                    ExecuteShipmentOutboxCommand.of(
                            message.outboxId(), message.orderItemId(), message.outboxType()));
            sample.stop(durationTimer);
            successCounter.increment();
        } catch (Exception e) {
            sample.stop(durationTimer);
            errorCounter.increment();
            log.error(
                    "Shipment Outbox 메시지 처리 실패: outboxId={}, error={}",
                    message.outboxId(),
                    e.getMessage(),
                    e);
            throw e;
        }
    }
}
