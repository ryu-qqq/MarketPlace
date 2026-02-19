package com.ryuqq.marketplace.application.productgroupinspection.dto.command;

/**
 * Verification 실행 커맨드.
 *
 * @param outboxId Outbox ID
 * @param productGroupId 상품 그룹 ID
 */
public record ExecuteVerificationCommand(Long outboxId, Long productGroupId) {

    public static ExecuteVerificationCommand of(Long outboxId, Long productGroupId) {
        return new ExecuteVerificationCommand(outboxId, productGroupId);
    }
}
