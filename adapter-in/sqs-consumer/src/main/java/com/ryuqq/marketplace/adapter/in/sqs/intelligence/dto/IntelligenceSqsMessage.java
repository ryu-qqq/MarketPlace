package com.ryuqq.marketplace.adapter.in.sqs.intelligence.dto;

/**
 * Intelligence Pipeline SQS 메시지 DTO.
 *
 * <p>Orchestration / Aggregation 큐에서 공통으로 사용합니다.
 */
public record IntelligenceSqsMessage(Long profileId, Long productGroupId) {

    public static IntelligenceSqsMessage of(Long profileId, Long productGroupId) {
        return new IntelligenceSqsMessage(profileId, productGroupId);
    }
}
