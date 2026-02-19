package com.ryuqq.marketplace.adapter.in.sqs.inspection.dto;

/**
 * SQS 검수 파이프라인 메시지 DTO.
 *
 * <p>Scoring / Enhancement / Verification 큐에서 공통으로 사용합니다.
 */
public record InspectionSqsMessage(Long outboxId, Long productGroupId) {

    public static InspectionSqsMessage of(Long outboxId, Long productGroupId) {
        return new InspectionSqsMessage(outboxId, productGroupId);
    }
}
