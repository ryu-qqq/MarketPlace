package com.ryuqq.marketplace.application.productgroupinspection.factory;

import org.springframework.stereotype.Component;

/**
 * SQS inspection message body factory.
 *
 * <p>Pipeline stage 간 SQS 메시지 바디를 일관되게 생성합니다.
 */
@Component
public class InspectionMessageFactory {

    public String createMessageBody(Long outboxId, Long productGroupId) {
        return "{\"outboxId\":" + outboxId + ",\"productGroupId\":" + productGroupId + "}";
    }
}
