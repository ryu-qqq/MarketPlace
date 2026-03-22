package com.ryuqq.marketplace.adapter.out.client.sqs.qna.adapter;

import com.ryuqq.marketplace.adapter.out.client.sqs.config.SqsClientProperties;
import com.ryuqq.marketplace.application.qna.port.out.client.QnaOutboxPublishClient;
import java.util.Objects;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;

/** QnA Outbox SQS 발행 어댑터. */
@Component
@ConditionalOnProperty(prefix = "sqs.queues", name = "qna-outbox")
public class QnaOutboxPublishAdapter implements QnaOutboxPublishClient {

    private final SqsClient sqsClient;
    private final String queueUrl;

    public QnaOutboxPublishAdapter(SqsClient sqsClient, SqsClientProperties properties) {
        this.sqsClient = sqsClient;
        this.queueUrl =
                Objects.requireNonNull(
                        properties.getQueues().getQnaOutbox(),
                        "sqs.queues.qna-outbox must be configured");
    }

    @Override
    public void publish(String messageBody) {
        try {
            SendMessageRequest request =
                    SendMessageRequest.builder()
                            .queueUrl(queueUrl)
                            .messageBody(messageBody)
                            .build();
            sqsClient.sendMessage(request);
        } catch (SqsException e) {
            throw new RuntimeException("QnA Outbox SQS 메시지 발행 실패: " + e.getMessage(), e);
        }
    }
}
