package com.ryuqq.marketplace.domain.exchange.outbox;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.exchange.ExchangeFixtures;
import com.ryuqq.marketplace.domain.exchange.outbox.aggregate.ExchangeOutbox;
import com.ryuqq.marketplace.domain.exchange.outbox.id.ExchangeOutboxId;
import com.ryuqq.marketplace.domain.exchange.outbox.vo.ExchangeOutboxIdempotencyKey;
import com.ryuqq.marketplace.domain.exchange.outbox.vo.ExchangeOutboxStatus;
import com.ryuqq.marketplace.domain.exchange.outbox.vo.ExchangeOutboxType;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import java.time.Instant;

/**
 * ExchangeOutbox 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 ExchangeOutbox 관련 객체들을 생성합니다.
 *
 * <p>참조 모델: ShipmentOutboxFixtures
 */
public final class ExchangeOutboxFixtures {

    private ExchangeOutboxFixtures() {}

    // ===== 기본 상수 =====
    private static final long DEFAULT_OUTBOX_ID = 1L;
    private static final String DEFAULT_PAYLOAD =
            "{\"orderItemId\":\"01900000-0000-7000-0000-000000000010\"}";

    // ===== ID Fixtures =====

    public static ExchangeOutboxId defaultExchangeOutboxId() {
        return ExchangeOutboxId.of(DEFAULT_OUTBOX_ID);
    }

    public static ExchangeOutboxId newExchangeOutboxId() {
        return ExchangeOutboxId.forNew();
    }

    // ===== IdempotencyKey Fixtures =====

    public static ExchangeOutboxIdempotencyKey defaultIdempotencyKey() {
        return ExchangeOutboxIdempotencyKey.generate(
                String.valueOf(ExchangeFixtures.defaultOrderItemId().value()),
                ExchangeOutboxType.COLLECT,
                Instant.ofEpochMilli(1700000000000L));
    }

    // ===== forNew Fixtures =====

    public static ExchangeOutbox newExchangeOutbox() {
        return ExchangeOutbox.forNew(
                ExchangeFixtures.defaultOrderItemId(),
                ExchangeOutboxType.COLLECT,
                DEFAULT_PAYLOAD,
                CommonVoFixtures.now());
    }

    public static ExchangeOutbox newExchangeOutbox(ExchangeOutboxType outboxType) {
        return ExchangeOutbox.forNew(
                ExchangeFixtures.defaultOrderItemId(),
                outboxType,
                DEFAULT_PAYLOAD,
                CommonVoFixtures.now());
    }

    public static ExchangeOutbox newExchangeOutbox(
            OrderItemId orderItemId, ExchangeOutboxType outboxType) {
        return ExchangeOutbox.forNew(
                orderItemId, outboxType, DEFAULT_PAYLOAD, CommonVoFixtures.now());
    }

    // ===== reconstitute - 상태별 =====

    public static ExchangeOutbox pendingExchangeOutbox() {
        Instant now = CommonVoFixtures.now();
        return ExchangeOutbox.reconstitute(
                defaultExchangeOutboxId(),
                ExchangeFixtures.defaultOrderItemId(),
                ExchangeOutboxType.COLLECT,
                ExchangeOutboxStatus.PENDING,
                DEFAULT_PAYLOAD,
                0,
                3,
                now,
                now,
                null,
                null,
                0L,
                defaultIdempotencyKey().value());
    }

    public static ExchangeOutbox processingExchangeOutbox() {
        Instant now = CommonVoFixtures.now();
        return ExchangeOutbox.reconstitute(
                defaultExchangeOutboxId(),
                ExchangeFixtures.defaultOrderItemId(),
                ExchangeOutboxType.COLLECT,
                ExchangeOutboxStatus.PROCESSING,
                DEFAULT_PAYLOAD,
                0,
                3,
                now,
                now,
                null,
                null,
                0L,
                defaultIdempotencyKey().value());
    }

    public static ExchangeOutbox completedExchangeOutbox() {
        Instant now = CommonVoFixtures.now();
        return ExchangeOutbox.reconstitute(
                defaultExchangeOutboxId(),
                ExchangeFixtures.defaultOrderItemId(),
                ExchangeOutboxType.COLLECT,
                ExchangeOutboxStatus.COMPLETED,
                DEFAULT_PAYLOAD,
                0,
                3,
                now,
                now,
                now,
                null,
                1L,
                defaultIdempotencyKey().value());
    }

    public static ExchangeOutbox failedExchangeOutbox() {
        Instant now = CommonVoFixtures.now();
        return ExchangeOutbox.reconstitute(
                defaultExchangeOutboxId(),
                ExchangeFixtures.defaultOrderItemId(),
                ExchangeOutboxType.COLLECT,
                ExchangeOutboxStatus.FAILED,
                DEFAULT_PAYLOAD,
                3,
                3,
                now,
                now,
                now,
                "외부 API 호출 실패",
                1L,
                defaultIdempotencyKey().value());
    }

    public static ExchangeOutbox processingExchangeOutboxWithRetry(int retryCount) {
        Instant now = CommonVoFixtures.now();
        return ExchangeOutbox.reconstitute(
                defaultExchangeOutboxId(),
                ExchangeFixtures.defaultOrderItemId(),
                ExchangeOutboxType.COLLECT,
                ExchangeOutboxStatus.PROCESSING,
                DEFAULT_PAYLOAD,
                retryCount,
                3,
                now,
                now,
                null,
                null,
                0L,
                defaultIdempotencyKey().value());
    }
}
