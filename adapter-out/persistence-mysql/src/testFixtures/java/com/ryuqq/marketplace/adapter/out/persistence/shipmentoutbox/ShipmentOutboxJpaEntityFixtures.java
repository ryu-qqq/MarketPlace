package com.ryuqq.marketplace.adapter.out.persistence.shipmentoutbox;

import com.ryuqq.marketplace.adapter.out.persistence.shipmentoutbox.entity.ShipmentOutboxJpaEntity;
import java.time.Instant;

/**
 * ShipmentOutboxJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 ShipmentOutboxJpaEntity 관련 객체들을 생성합니다.
 */
public final class ShipmentOutboxJpaEntityFixtures {

    private ShipmentOutboxJpaEntityFixtures() {}

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final String DEFAULT_ORDER_ITEM_ID = "01940001-0000-7000-8000-000000000001";
    public static final String DEFAULT_PAYLOAD =
            "{\"shipmentId\":\"01944b2a-1234-7fff-8888-abcdef012345\"}";
    public static final int DEFAULT_RETRY_COUNT = 0;
    public static final int DEFAULT_MAX_RETRY = 3;
    public static final String DEFAULT_IDEMPOTENCY_KEY =
            "SHPO:01940001-0000-7000-8000-000000000001:SHIP:1700000000000";
    public static final String DEFAULT_ERROR_MESSAGE = "외부 API 호출 실패";

    // ===== Entity Fixtures =====

    /** PENDING 상태 신규 아웃박스 Entity 생성 (기본). */
    public static ShipmentOutboxJpaEntity pendingEntity() {
        Instant now = Instant.now();
        return ShipmentOutboxJpaEntity.of(
                DEFAULT_ID,
                DEFAULT_ORDER_ITEM_ID,
                ShipmentOutboxJpaEntity.OutboxType.SHIP,
                ShipmentOutboxJpaEntity.Status.PENDING,
                DEFAULT_PAYLOAD,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                0L,
                DEFAULT_IDEMPOTENCY_KEY);
    }

    /** PENDING 상태 아웃박스 Entity 생성 (ID 지정). */
    public static ShipmentOutboxJpaEntity pendingEntity(Long id) {
        Instant now = Instant.now();
        return ShipmentOutboxJpaEntity.of(
                id,
                DEFAULT_ORDER_ITEM_ID,
                ShipmentOutboxJpaEntity.OutboxType.SHIP,
                ShipmentOutboxJpaEntity.Status.PENDING,
                DEFAULT_PAYLOAD,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                0L,
                "SHPO:" + DEFAULT_ORDER_ITEM_ID + ":SHIP:" + id);
    }

    /** PROCESSING 상태 아웃박스 Entity 생성. */
    public static ShipmentOutboxJpaEntity processingEntity() {
        Instant now = Instant.now();
        return ShipmentOutboxJpaEntity.of(
                DEFAULT_ID,
                DEFAULT_ORDER_ITEM_ID,
                ShipmentOutboxJpaEntity.OutboxType.SHIP,
                ShipmentOutboxJpaEntity.Status.PROCESSING,
                DEFAULT_PAYLOAD,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now.minusSeconds(60),
                now,
                null,
                null,
                0L,
                DEFAULT_IDEMPOTENCY_KEY);
    }

    /** COMPLETED 상태 아웃박스 Entity 생성. */
    public static ShipmentOutboxJpaEntity completedEntity() {
        Instant now = Instant.now();
        Instant past = now.minusSeconds(60);
        return ShipmentOutboxJpaEntity.of(
                DEFAULT_ID,
                DEFAULT_ORDER_ITEM_ID,
                ShipmentOutboxJpaEntity.OutboxType.SHIP,
                ShipmentOutboxJpaEntity.Status.COMPLETED,
                DEFAULT_PAYLOAD,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                past,
                now,
                now,
                null,
                1L,
                DEFAULT_IDEMPOTENCY_KEY);
    }

    /** FAILED 상태 아웃박스 Entity 생성. */
    public static ShipmentOutboxJpaEntity failedEntity() {
        Instant now = Instant.now();
        Instant past = now.minusSeconds(180);
        return ShipmentOutboxJpaEntity.of(
                DEFAULT_ID,
                DEFAULT_ORDER_ITEM_ID,
                ShipmentOutboxJpaEntity.OutboxType.SHIP,
                ShipmentOutboxJpaEntity.Status.FAILED,
                DEFAULT_PAYLOAD,
                DEFAULT_MAX_RETRY,
                DEFAULT_MAX_RETRY,
                past,
                now,
                now,
                DEFAULT_ERROR_MESSAGE,
                1L,
                DEFAULT_IDEMPOTENCY_KEY);
    }

    /** CONFIRM 타입 PENDING 아웃박스 Entity 생성. */
    public static ShipmentOutboxJpaEntity pendingConfirmEntity() {
        Instant now = Instant.now();
        return ShipmentOutboxJpaEntity.of(
                DEFAULT_ID,
                DEFAULT_ORDER_ITEM_ID,
                ShipmentOutboxJpaEntity.OutboxType.CONFIRM,
                ShipmentOutboxJpaEntity.Status.PENDING,
                DEFAULT_PAYLOAD,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                0L,
                "SHPO:" + DEFAULT_ORDER_ITEM_ID + ":CONFIRM:" + now.toEpochMilli());
    }

    /** DELIVER 타입 PENDING 아웃박스 Entity 생성. */
    public static ShipmentOutboxJpaEntity pendingDeliverEntity() {
        Instant now = Instant.now();
        return ShipmentOutboxJpaEntity.of(
                DEFAULT_ID,
                DEFAULT_ORDER_ITEM_ID,
                ShipmentOutboxJpaEntity.OutboxType.DELIVER,
                ShipmentOutboxJpaEntity.Status.PENDING,
                DEFAULT_PAYLOAD,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                0L,
                "SHPO:" + DEFAULT_ORDER_ITEM_ID + ":DELIVER:" + now.toEpochMilli());
    }

    /** 특정 OutboxType을 가진 PENDING Entity 생성. */
    public static ShipmentOutboxJpaEntity pendingEntityWithType(
            Long id, ShipmentOutboxJpaEntity.OutboxType outboxType) {
        Instant now = Instant.now();
        return ShipmentOutboxJpaEntity.of(
                id,
                DEFAULT_ORDER_ITEM_ID,
                outboxType,
                ShipmentOutboxJpaEntity.Status.PENDING,
                DEFAULT_PAYLOAD,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                0L,
                "SHPO:"
                        + DEFAULT_ORDER_ITEM_ID
                        + ":"
                        + outboxType.name()
                        + ":"
                        + now.toEpochMilli());
    }

    /** 특정 orderItemId를 가진 PENDING Entity 생성. */
    public static ShipmentOutboxJpaEntity pendingEntityWithOrderItemId(
            Long id, String orderItemId) {
        Instant now = Instant.now();
        return ShipmentOutboxJpaEntity.of(
                id,
                orderItemId,
                ShipmentOutboxJpaEntity.OutboxType.SHIP,
                ShipmentOutboxJpaEntity.Status.PENDING,
                DEFAULT_PAYLOAD,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                0L,
                "SHPO:" + orderItemId + ":SHIP:" + now.toEpochMilli());
    }

    /** 재시도 횟수가 있는 PENDING Entity 생성. */
    public static ShipmentOutboxJpaEntity pendingEntityWithRetry(int retryCount) {
        Instant now = Instant.now();
        return ShipmentOutboxJpaEntity.of(
                DEFAULT_ID,
                DEFAULT_ORDER_ITEM_ID,
                ShipmentOutboxJpaEntity.OutboxType.SHIP,
                ShipmentOutboxJpaEntity.Status.PENDING,
                DEFAULT_PAYLOAD,
                retryCount,
                DEFAULT_MAX_RETRY,
                now.minusSeconds(retryCount * 60L),
                now,
                null,
                DEFAULT_ERROR_MESSAGE,
                0L,
                DEFAULT_IDEMPOTENCY_KEY);
    }
}
