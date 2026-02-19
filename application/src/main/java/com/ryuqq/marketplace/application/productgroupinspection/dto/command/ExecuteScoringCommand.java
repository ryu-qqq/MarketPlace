package com.ryuqq.marketplace.application.productgroupinspection.dto.command;

/**
 * Scoring 실행 커맨드.
 *
 * @param outboxId Outbox ID
 * @param productGroupId 상품 그룹 ID
 */
public record ExecuteScoringCommand(Long outboxId, Long productGroupId) {

    public static ExecuteScoringCommand of(Long outboxId, Long productGroupId) {
        return new ExecuteScoringCommand(outboxId, productGroupId);
    }
}
