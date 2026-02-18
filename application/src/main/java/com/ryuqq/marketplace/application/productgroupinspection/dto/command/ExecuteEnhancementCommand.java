package com.ryuqq.marketplace.application.productgroupinspection.dto.command;

/**
 * Enhancement 실행 커맨드.
 *
 * @param outboxId Outbox ID
 * @param productGroupId 상품 그룹 ID
 */
public record ExecuteEnhancementCommand(Long outboxId, Long productGroupId) {

    public static ExecuteEnhancementCommand of(Long outboxId, Long productGroupId) {
        return new ExecuteEnhancementCommand(outboxId, productGroupId);
    }
}
