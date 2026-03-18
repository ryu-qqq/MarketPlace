package com.ryuqq.marketplace.application.cancel.dto.command;

/**
 * 취소 Outbox 실행 커맨드.
 *
 * <p>SQS Consumer에서 수신한 취소 Outbox 처리 요청을 담습니다.
 *
 * @param outboxId Outbox ID
 * @param orderItemId 주문상품 ID
 * @param outboxType Outbox 유형
 */
public record ExecuteCancelOutboxCommand(Long outboxId, String orderItemId, String outboxType) {

    public static ExecuteCancelOutboxCommand of(
            Long outboxId, String orderItemId, String outboxType) {
        return new ExecuteCancelOutboxCommand(outboxId, orderItemId, outboxType);
    }
}
