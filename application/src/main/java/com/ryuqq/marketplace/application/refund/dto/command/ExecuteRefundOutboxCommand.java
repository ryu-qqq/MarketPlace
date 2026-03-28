package com.ryuqq.marketplace.application.refund.dto.command;

/**
 * 환불 Outbox 실행 커맨드.
 *
 * <p>SQS Consumer에서 수신한 환불 Outbox 처리 요청을 담습니다.
 *
 * @param outboxId Outbox ID
 * @param orderItemId 주문상품 ID
 * @param outboxType Outbox 유형
 */
public record ExecuteRefundOutboxCommand(Long outboxId, Long orderItemId, String outboxType) {

    public static ExecuteRefundOutboxCommand of(
            Long outboxId, Long orderItemId, String outboxType) {
        return new ExecuteRefundOutboxCommand(outboxId, orderItemId, outboxType);
    }
}
