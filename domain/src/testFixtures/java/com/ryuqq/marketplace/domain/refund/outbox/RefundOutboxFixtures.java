package com.ryuqq.marketplace.domain.refund.outbox;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.refund.RefundFixtures;
import com.ryuqq.marketplace.domain.refund.outbox.aggregate.RefundOutbox;
import com.ryuqq.marketplace.domain.refund.outbox.id.RefundOutboxId;
import com.ryuqq.marketplace.domain.refund.outbox.vo.RefundOutboxIdempotencyKey;
import com.ryuqq.marketplace.domain.refund.outbox.vo.RefundOutboxStatus;
import com.ryuqq.marketplace.domain.refund.outbox.vo.RefundOutboxType;
import java.time.Instant;

/**
 * RefundOutbox 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 RefundOutbox 관련 객체들을 생성합니다.
 *
 * <p>참조 모델: ShipmentOutboxFixtures, ExchangeOutboxFixtures
 */
public final class RefundOutboxFixtures {

    private RefundOutboxFixtures() {}

    // ===== 기본 상수 =====
    private static final long DEFAULT_OUTBOX_ID = 1L;
    private static final String DEFAULT_PAYLOAD =
            "{\"orderItemId\":\"01940001-0000-7000-8000-000000000001\"}";

    // ===== ID Fixtures =====

    public static RefundOutboxId defaultRefundOutboxId() {
        return RefundOutboxId.of(DEFAULT_OUTBOX_ID);
    }

    public static RefundOutboxId newRefundOutboxId() {
        return RefundOutboxId.forNew();
    }

    // ===== IdempotencyKey Fixtures =====

    public static RefundOutboxIdempotencyKey defaultIdempotencyKey() {
        return RefundOutboxIdempotencyKey.generate(
                String.valueOf(RefundFixtures.defaultOrderItemId().value()),
                RefundOutboxType.REQUEST,
                Instant.ofEpochMilli(1700000000000L));
    }

    // ===== forNew Fixtures =====

    public static RefundOutbox newRefundOutbox() {
        return RefundOutbox.forNew(
                RefundFixtures.defaultOrderItemId(),
                RefundOutboxType.REQUEST,
                DEFAULT_PAYLOAD,
                CommonVoFixtures.now());
    }

    public static RefundOutbox newRefundOutbox(RefundOutboxType outboxType) {
        return RefundOutbox.forNew(
                RefundFixtures.defaultOrderItemId(),
                outboxType,
                DEFAULT_PAYLOAD,
                CommonVoFixtures.now());
    }

    public static RefundOutbox newRefundOutbox(
            OrderItemId orderItemId, RefundOutboxType outboxType) {
        return RefundOutbox.forNew(
                orderItemId, outboxType, DEFAULT_PAYLOAD, CommonVoFixtures.now());
    }

    // ===== reconstitute - 상태별 =====

    public static RefundOutbox pendingRefundOutbox() {
        Instant now = CommonVoFixtures.now();
        return RefundOutbox.reconstitute(
                defaultRefundOutboxId(),
                RefundFixtures.defaultOrderItemId(),
                RefundOutboxType.REQUEST,
                RefundOutboxStatus.PENDING,
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

    public static RefundOutbox processingRefundOutbox() {
        Instant now = CommonVoFixtures.now();
        return RefundOutbox.reconstitute(
                defaultRefundOutboxId(),
                RefundFixtures.defaultOrderItemId(),
                RefundOutboxType.REQUEST,
                RefundOutboxStatus.PROCESSING,
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

    public static RefundOutbox completedRefundOutbox() {
        Instant now = CommonVoFixtures.now();
        return RefundOutbox.reconstitute(
                defaultRefundOutboxId(),
                RefundFixtures.defaultOrderItemId(),
                RefundOutboxType.REQUEST,
                RefundOutboxStatus.COMPLETED,
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

    public static RefundOutbox failedRefundOutbox() {
        Instant now = CommonVoFixtures.now();
        return RefundOutbox.reconstitute(
                defaultRefundOutboxId(),
                RefundFixtures.defaultOrderItemId(),
                RefundOutboxType.REQUEST,
                RefundOutboxStatus.FAILED,
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

    public static RefundOutbox processingRefundOutboxWithRetry(int retryCount) {
        Instant now = CommonVoFixtures.now();
        return RefundOutbox.reconstitute(
                defaultRefundOutboxId(),
                RefundFixtures.defaultOrderItemId(),
                RefundOutboxType.REQUEST,
                RefundOutboxStatus.PROCESSING,
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
