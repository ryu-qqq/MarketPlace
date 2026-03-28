package com.ryuqq.marketplace.application.exchange.dto.command;

/**
 * 교환 Outbox 실행 커맨드.
 *
 * <p>SQS Consumer에서 수신한 교환 Outbox 처리 요청을 담습니다.
 *
 * @param outboxId Outbox ID
 * @param orderItemId 주문상품 ID
 * @param outboxType Outbox 유형
 */
public record ExecuteExchangeOutboxCommand(Long outboxId, Long orderItemId, String outboxType) {

    public static ExecuteExchangeOutboxCommand of(
            Long outboxId, Long orderItemId, String outboxType) {
        return new ExecuteExchangeOutboxCommand(outboxId, orderItemId, outboxType);
    }
}
