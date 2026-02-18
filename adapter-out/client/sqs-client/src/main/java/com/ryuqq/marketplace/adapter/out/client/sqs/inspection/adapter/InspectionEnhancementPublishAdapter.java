package com.ryuqq.marketplace.adapter.out.client.sqs.inspection.adapter;

import com.ryuqq.marketplace.adapter.out.client.sqs.config.SqsClientProperties;
import com.ryuqq.marketplace.application.productgroupinspection.port.out.client.InspectionEnhancementPublishClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;
import software.amazon.awssdk.services.sqs.model.SqsException;

@Component
@ConditionalOnProperty(prefix = "sqs.queues", name = "inspection-enhancement")
public class InspectionEnhancementPublishAdapter implements InspectionEnhancementPublishClient {

    private static final Logger log =
            LoggerFactory.getLogger(InspectionEnhancementPublishAdapter.class);

    private final SqsClient sqsClient;
    private final String queueUrl;

    public InspectionEnhancementPublishAdapter(
            SqsClient sqsClient, SqsClientProperties properties) {
        this.sqsClient = sqsClient;
        this.queueUrl = properties.getQueues().getInspectionEnhancement();
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
            log.info("Enhancement SQS 메시지 발행 성공: messageId={}", response.messageId());
            return response.messageId();

        } catch (SqsException e) {
            log.error("Enhancement SQS 메시지 발행 실패: error={}", e.getMessage());
            throw new RuntimeException("Enhancement SQS 메시지 발행 실패: " + e.getMessage(), e);
        }
    }
}
