package com.ryuqq.marketplace.domain.shipment.outbox;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.shipment.ShipmentFixtures;
import com.ryuqq.marketplace.domain.shipment.outbox.aggregate.ShipmentOutbox;
import com.ryuqq.marketplace.domain.shipment.outbox.id.ShipmentOutboxId;
import com.ryuqq.marketplace.domain.shipment.outbox.vo.ShipmentOutboxIdempotencyKey;
import com.ryuqq.marketplace.domain.shipment.outbox.vo.ShipmentOutboxStatus;
import com.ryuqq.marketplace.domain.shipment.outbox.vo.ShipmentOutboxType;
import java.time.Instant;

/**
 * ShipmentOutbox 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 ShipmentOutbox 관련 객체들을 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class ShipmentOutboxFixtures {

    private ShipmentOutboxFixtures() {}

    // ===== 기본 상수 =====
    private static final long DEFAULT_OUTBOX_ID = 1L;
    private static final String DEFAULT_PAYLOAD =
            "{\"shipmentId\":\"01944b2a-1234-7fff-8888-abcdef012345\"}";

    // ===== ID Fixtures =====

    public static ShipmentOutboxId defaultShipmentOutboxId() {
        return ShipmentOutboxId.of(DEFAULT_OUTBOX_ID);
    }

    public static ShipmentOutboxId newShipmentOutboxId() {
        return ShipmentOutboxId.forNew();
    }

    // ===== IdempotencyKey Fixtures =====

    public static ShipmentOutboxIdempotencyKey defaultIdempotencyKey() {
        return ShipmentOutboxIdempotencyKey.generate(
                String.valueOf(ShipmentFixtures.defaultOrderItemId().value()),
                ShipmentOutboxType.SHIP,
                Instant.ofEpochMilli(1700000000000L));
    }

    // ===== forNew Fixtures =====

    public static ShipmentOutbox newShipmentOutbox() {
        return ShipmentOutbox.forNew(
                ShipmentFixtures.defaultOrderItemId(),
                ShipmentOutboxType.SHIP,
                DEFAULT_PAYLOAD,
                CommonVoFixtures.now());
    }

    public static ShipmentOutbox newShipmentOutbox(ShipmentOutboxType outboxType) {
        return ShipmentOutbox.forNew(
                ShipmentFixtures.defaultOrderItemId(),
                outboxType,
                DEFAULT_PAYLOAD,
                CommonVoFixtures.now());
    }

    public static ShipmentOutbox newShipmentOutbox(
            OrderItemId orderItemId, ShipmentOutboxType outboxType) {
        return ShipmentOutbox.forNew(
                orderItemId, outboxType, DEFAULT_PAYLOAD, CommonVoFixtures.now());
    }

    // ===== reconstitute - 상태별 =====

    public static ShipmentOutbox pendingShipmentOutbox() {
        Instant now = CommonVoFixtures.now();
        return ShipmentOutbox.reconstitute(
                defaultShipmentOutboxId(),
                ShipmentFixtures.defaultOrderItemId(),
                ShipmentOutboxType.SHIP,
                ShipmentOutboxStatus.PENDING,
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

    public static ShipmentOutbox processingShipmentOutbox() {
        Instant now = CommonVoFixtures.now();
        return ShipmentOutbox.reconstitute(
                defaultShipmentOutboxId(),
                ShipmentFixtures.defaultOrderItemId(),
                ShipmentOutboxType.SHIP,
                ShipmentOutboxStatus.PROCESSING,
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

    public static ShipmentOutbox completedShipmentOutbox() {
        Instant now = CommonVoFixtures.now();
        return ShipmentOutbox.reconstitute(
                defaultShipmentOutboxId(),
                ShipmentFixtures.defaultOrderItemId(),
                ShipmentOutboxType.SHIP,
                ShipmentOutboxStatus.COMPLETED,
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

    public static ShipmentOutbox failedShipmentOutbox() {
        Instant now = CommonVoFixtures.now();
        return ShipmentOutbox.reconstitute(
                defaultShipmentOutboxId(),
                ShipmentFixtures.defaultOrderItemId(),
                ShipmentOutboxType.SHIP,
                ShipmentOutboxStatus.FAILED,
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

    public static ShipmentOutbox processingShipmentOutboxWithRetry(int retryCount) {
        Instant now = CommonVoFixtures.now();
        return ShipmentOutbox.reconstitute(
                defaultShipmentOutboxId(),
                ShipmentFixtures.defaultOrderItemId(),
                ShipmentOutboxType.SHIP,
                ShipmentOutboxStatus.PROCESSING,
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
