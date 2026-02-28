package com.ryuqq.marketplace.adapter.out.client.sqs.outboundsync.adapter;

import com.ryuqq.marketplace.adapter.out.client.sqs.config.SqsClientProperties;
import com.ryuqq.marketplace.application.outboundsync.port.out.client.OutboundSyncPublishClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;
import software.amazon.awssdk.services.sqs.model.SqsException;

/** OutboundSync SQS 발행 어댑터. */
@Component
@ConditionalOnProperty(prefix = "sqs.queues", name = "outbound-sync")
public class OutboundSyncPublishAdapter implements OutboundSyncPublishClient {

    private final SqsClient sqsClient;
    private final String queueUrl;

    public OutboundSyncPublishAdapter(SqsClient sqsClient, SqsClientProperties properties) {
        this.sqsClient = sqsClient;
        this.queueUrl = properties.getQueues().getOutboundSync();
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
            throw new RuntimeException("OutboundSync SQS 메시지 발행 실패: " + e.getMessage(), e);
        }
    }
}
