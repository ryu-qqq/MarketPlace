package com.ryuqq.marketplace.adapter.out.persistence.canceloutbox;

import com.ryuqq.marketplace.adapter.out.persistence.canceloutbox.entity.CancelOutboxJpaEntity;
import java.time.Instant;

/**
 * CancelOutboxJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 CancelOutboxJpaEntity 관련 객체들을 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class CancelOutboxJpaEntityFixtures {

    private CancelOutboxJpaEntityFixtures() {}

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final String DEFAULT_ORDER_ITEM_ID = "01900000-0000-7000-0000-000000000010";
    public static final String DEFAULT_OUTBOX_TYPE = "APPROVE";
    public static final String DEFAULT_STATUS_PENDING = "PENDING";
    public static final String DEFAULT_STATUS_PROCESSING = "PROCESSING";
    public static final String DEFAULT_STATUS_COMPLETED = "COMPLETED";
    public static final String DEFAULT_STATUS_FAILED = "FAILED";
    public static final String DEFAULT_PAYLOAD = "{\"status\":\"APPROVED\"}";
    public static final int DEFAULT_RETRY_COUNT = 0;
    public static final int DEFAULT_MAX_RETRY = 3;
    public static final String DEFAULT_IDEMPOTENCY_KEY =
            "COBO:" + DEFAULT_ORDER_ITEM_ID + ":APPROVE:1700000000000";

    // ===== Entity Fixtures =====

    /** PENDING 상태 Entity 생성 (신규). */
    public static CancelOutboxJpaEntity pendingEntity() {
        Instant now = Instant.now();
        return CancelOutboxJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_OUTBOX_TYPE,
                DEFAULT_STATUS_PENDING,
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

    /** PENDING 상태 Entity 생성 (ID 지정). */
    public static CancelOutboxJpaEntity pendingEntity(Long id, String orderItemId) {
        Instant now = Instant.now();
        String idempotencyKey = "COBO:" + orderItemId + ":APPROVE:" + now.toEpochMilli();
        return CancelOutboxJpaEntity.create(
                id,
                orderItemId,
                DEFAULT_OUTBOX_TYPE,
                DEFAULT_STATUS_PENDING,
                DEFAULT_PAYLOAD,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                0L,
                idempotencyKey);
    }

    /** PENDING 상태 Entity 생성 (createdAt 지정, 타임아웃 시나리오용). */
    public static CancelOutboxJpaEntity pendingEntityCreatedAt(Long id, Instant createdAt) {
        return CancelOutboxJpaEntity.create(
                id,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_OUTBOX_TYPE,
                DEFAULT_STATUS_PENDING,
                DEFAULT_PAYLOAD,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                createdAt,
                createdAt,
                null,
                null,
                0L,
                DEFAULT_IDEMPOTENCY_KEY + id);
    }

    /** PROCESSING 상태 Entity 생성. */
    public static CancelOutboxJpaEntity processingEntity() {
        Instant createdAt = Instant.now().minusSeconds(60);
        Instant updatedAt = Instant.now();
        return CancelOutboxJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_OUTBOX_TYPE,
                DEFAULT_STATUS_PROCESSING,
                DEFAULT_PAYLOAD,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                createdAt,
                updatedAt,
                null,
                null,
                0L,
                DEFAULT_IDEMPOTENCY_KEY);
    }

    /** PROCESSING 상태 Entity 생성 (updatedAt 지정, 타임아웃 시나리오용). */
    public static CancelOutboxJpaEntity processingEntityUpdatedAt(Long id, Instant updatedAt) {
        Instant createdAt = updatedAt.minusSeconds(300);
        return CancelOutboxJpaEntity.create(
                id,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_OUTBOX_TYPE,
                DEFAULT_STATUS_PROCESSING,
                DEFAULT_PAYLOAD,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                createdAt,
                updatedAt,
                null,
                null,
                0L,
                DEFAULT_IDEMPOTENCY_KEY + id);
    }

    /** COMPLETED 상태 Entity 생성. */
    public static CancelOutboxJpaEntity completedEntity() {
        Instant createdAt = Instant.now().minusSeconds(3600);
        Instant processedAt = Instant.now();
        return CancelOutboxJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_OUTBOX_TYPE,
                DEFAULT_STATUS_COMPLETED,
                DEFAULT_PAYLOAD,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                createdAt,
                processedAt,
                processedAt,
                null,
                1L,
                DEFAULT_IDEMPOTENCY_KEY);
    }

    /** FAILED 상태 Entity 생성 (최대 재시도 초과). */
    public static CancelOutboxJpaEntity failedEntity() {
        Instant createdAt = Instant.now().minusSeconds(3600);
        Instant updatedAt = Instant.now();
        return CancelOutboxJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_OUTBOX_TYPE,
                DEFAULT_STATUS_FAILED,
                DEFAULT_PAYLOAD,
                DEFAULT_MAX_RETRY,
                DEFAULT_MAX_RETRY,
                createdAt,
                updatedAt,
                updatedAt,
                "외부 API 오류",
                2L,
                DEFAULT_IDEMPOTENCY_KEY);
    }

    /** outboxType을 지정하는 PENDING Entity 생성. */
    public static CancelOutboxJpaEntity pendingEntityWithType(String outboxType) {
        Instant now = Instant.now();
        String idempotencyKey =
                "COBO:" + DEFAULT_ORDER_ITEM_ID + ":" + outboxType + ":" + now.toEpochMilli();
        return CancelOutboxJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_ORDER_ITEM_ID,
                outboxType,
                DEFAULT_STATUS_PENDING,
                DEFAULT_PAYLOAD,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                0L,
                idempotencyKey);
    }

    /** 재시도 카운트를 지정하는 PENDING Entity 생성. */
    public static CancelOutboxJpaEntity pendingEntityWithRetry(int retryCount) {
        Instant now = Instant.now();
        return CancelOutboxJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_OUTBOX_TYPE,
                DEFAULT_STATUS_PENDING,
                DEFAULT_PAYLOAD,
                retryCount,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                0L,
                DEFAULT_IDEMPOTENCY_KEY);
    }
}
