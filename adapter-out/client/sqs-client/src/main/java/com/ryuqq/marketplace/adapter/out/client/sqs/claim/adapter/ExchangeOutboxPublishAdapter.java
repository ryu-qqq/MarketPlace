package com.ryuqq.marketplace.adapter.out.client.sqs.claim.adapter;

import com.ryuqq.marketplace.adapter.out.client.sqs.config.SqsClientProperties;
import com.ryuqq.marketplace.application.exchange.port.out.client.ExchangeOutboxPublishClient;
import java.util.Objects;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;
import software.amazon.awssdk.services.sqs.model.SqsException;

/** 교환 Outbox SQS 발행 어댑터. */
@Component
@ConditionalOnProperty(prefix = "sqs.queues", name = "exchange-outbox")
public class ExchangeOutboxPublishAdapter implements ExchangeOutboxPublishClient {

    private final SqsClient sqsClient;
    private final String queueUrl;

    public ExchangeOutboxPublishAdapter(SqsClient sqsClient, SqsClientProperties properties) {
        this.sqsClient = sqsClient;
        this.queueUrl =
                Objects.requireNonNull(
                        properties.getQueues().getExchangeOutbox(),
                        "sqs.queues.exchange-outbox must be configured");
    }

    @Override
    public String publish(String messageBody) {
        try {
            SendMessageRequest request =
                    SendMessageRequest.builder()
                            .queueUrl(queueUrl)
                            .messageBody(messageBody)
                            .build();
            SendMessageResponse response = sqsClient.sendMessage(request);
            return response.messageId();
        } catch (SqsException e) {
            throw new RuntimeException("교환 Outbox SQS 메시지 발행 실패: " + e.getMessage(), e);
        }
    }
}
