package com.ryuqq.marketplace.adapter.out.client.sqs.claim.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.marketplace.adapter.out.client.sqs.config.SqsClientProperties;
import com.ryuqq.marketplace.application.cancel.port.out.client.CancelOutboxMessage;
import com.ryuqq.marketplace.application.cancel.port.out.client.CancelOutboxPublishClient;
import java.util.Objects;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;
import software.amazon.awssdk.services.sqs.model.SqsException;

/** 취소 Outbox SQS 발행 어댑터. */
@Component
@ConditionalOnProperty(prefix = "sqs.queues", name = "cancel-outbox")
public class CancelOutboxPublishAdapter implements CancelOutboxPublishClient {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final String queueUrl;

    public CancelOutboxPublishAdapter(
            SqsClient sqsClient, ObjectMapper objectMapper, SqsClientProperties properties) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
        this.queueUrl =
                Objects.requireNonNull(
                        properties.getQueues().getCancelOutbox(),
                        "sqs.queues.cancel-outbox must be configured");
    }

    @Override
    public String publish(CancelOutboxMessage message) {
        try {
            String messageBody = objectMapper.writeValueAsString(message);
            SendMessageRequest request =
                    SendMessageRequest.builder()
                            .queueUrl(queueUrl)
                            .messageBody(messageBody)
                            .build();
            SendMessageResponse response = sqsClient.sendMessage(request);
            return response.messageId();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(
                    "취소 Outbox SQS 메시지 직렬화 실패: outboxId=" + message.outboxId(), e);
        } catch (SqsException e) {
            throw new RuntimeException("취소 Outbox SQS 메시지 발행 실패: " + e.getMessage(), e);
        }
    }
}
