package com.ryuqq.marketplace.adapter.out.persistence.exchangeoutbox;

import com.ryuqq.marketplace.adapter.out.persistence.exchangeoutbox.entity.ExchangeOutboxJpaEntity;
import java.time.Instant;

/**
 * ExchangeOutboxJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 ExchangeOutboxJpaEntity 관련 객체들을 생성합니다.
 */
public final class ExchangeOutboxJpaEntityFixtures {

    private ExchangeOutboxJpaEntityFixtures() {}

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final String DEFAULT_ORDER_ITEM_ID = "01900000-0000-7000-0000-000000000010";
    public static final String DEFAULT_OUTBOX_TYPE = "COLLECT";
    public static final String DEFAULT_STATUS_PENDING = "PENDING";
    public static final String DEFAULT_STATUS_PROCESSING = "PROCESSING";
    public static final String DEFAULT_STATUS_COMPLETED = "COMPLETED";
    public static final String DEFAULT_STATUS_FAILED = "FAILED";
    public static final String DEFAULT_PAYLOAD =
            "{\"orderItemId\":\"01900000-0000-7000-0000-000000000010\"}";
    public static final int DEFAULT_RETRY_COUNT = 0;
    public static final int DEFAULT_MAX_RETRY = 3;
    public static final String DEFAULT_IDEMPOTENCY_KEY =
            "EXBO:01900000-0000-7000-0000-000000000010:COLLECT:1700000000000";

    // ===== PENDING 상태 Entity =====

    /** PENDING 상태의 교환 아웃박스 Entity 생성 (기본 ID). */
    public static ExchangeOutboxJpaEntity pendingEntity() {
        Instant now = Instant.now();
        return ExchangeOutboxJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_OUTBOX_TYPE,
                DEFAULT_STATUS_PENDING,
                DEFAULT_PAYLOAD,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now.minusSeconds(60),
                now.minusSeconds(60),
                null,
                null,
                0L,
                DEFAULT_IDEMPOTENCY_KEY);
    }

    /** PENDING 상태 Entity 생성 (ID 지정). */
    public static ExchangeOutboxJpaEntity pendingEntity(Long id) {
        Instant now = Instant.now();
        return ExchangeOutboxJpaEntity.create(
                id,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_OUTBOX_TYPE,
                DEFAULT_STATUS_PENDING,
                DEFAULT_PAYLOAD,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now.minusSeconds(60),
                now.minusSeconds(60),
                null,
                null,
                0L,
                DEFAULT_IDEMPOTENCY_KEY + "_" + id);
    }

    /** 특정 createdAt 시간을 가진 PENDING Entity 생성. */
    public static ExchangeOutboxJpaEntity pendingEntityCreatedAt(Long id, Instant createdAt) {
        return ExchangeOutboxJpaEntity.create(
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
                DEFAULT_IDEMPOTENCY_KEY + "_" + id);
    }

    // ===== PROCESSING 상태 Entity =====

    /** PROCESSING 상태의 교환 아웃박스 Entity 생성 (기본 ID). */
    public static ExchangeOutboxJpaEntity processingEntity() {
        Instant now = Instant.now();
        Instant past = now.minusSeconds(300);
        return ExchangeOutboxJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_OUTBOX_TYPE,
                DEFAULT_STATUS_PROCESSING,
                DEFAULT_PAYLOAD,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                past,
                past,
                null,
                null,
                0L,
                DEFAULT_IDEMPOTENCY_KEY);
    }

    /** 특정 updatedAt 시간을 가진 PROCESSING Entity 생성 (타임아웃 테스트용). */
    public static ExchangeOutboxJpaEntity processingEntityUpdatedAt(Long id, Instant updatedAt) {
        Instant now = Instant.now();
        return ExchangeOutboxJpaEntity.create(
                id,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_OUTBOX_TYPE,
                DEFAULT_STATUS_PROCESSING,
                DEFAULT_PAYLOAD,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now.minusSeconds(600),
                updatedAt,
                null,
                null,
                0L,
                DEFAULT_IDEMPOTENCY_KEY + "_" + id);
    }

    // ===== COMPLETED 상태 Entity =====

    /** COMPLETED 상태의 교환 아웃박스 Entity 생성. */
    public static ExchangeOutboxJpaEntity completedEntity() {
        Instant now = Instant.now();
        Instant past = now.minusSeconds(600);
        return ExchangeOutboxJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_OUTBOX_TYPE,
                DEFAULT_STATUS_COMPLETED,
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

    // ===== FAILED 상태 Entity =====

    /** FAILED 상태의 교환 아웃박스 Entity 생성 (최대 재시도 초과). */
    public static ExchangeOutboxJpaEntity failedEntity() {
        Instant now = Instant.now();
        Instant past = now.minusSeconds(3600);
        return ExchangeOutboxJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_OUTBOX_TYPE,
                DEFAULT_STATUS_FAILED,
                DEFAULT_PAYLOAD,
                DEFAULT_MAX_RETRY,
                DEFAULT_MAX_RETRY,
                past,
                now,
                now,
                "외부 API 호출 실패",
                1L,
                DEFAULT_IDEMPOTENCY_KEY);
    }

    // ===== 재시도 관련 Entity =====

    /** 재시도 횟수가 지정된 PROCESSING Entity 생성. */
    public static ExchangeOutboxJpaEntity processingEntityWithRetry(int retryCount) {
        Instant now = Instant.now();
        Instant past = now.minusSeconds(300);
        return ExchangeOutboxJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_OUTBOX_TYPE,
                DEFAULT_STATUS_PROCESSING,
                DEFAULT_PAYLOAD,
                retryCount,
                DEFAULT_MAX_RETRY,
                past,
                past,
                null,
                null,
                0L,
                DEFAULT_IDEMPOTENCY_KEY);
    }

    // ===== null ID (신규 저장용) Entity =====

    /** null ID를 가진 PENDING Entity 생성 (신규 저장 시뮬레이션). */
    public static ExchangeOutboxJpaEntity newPendingEntity() {
        Instant now = Instant.now();
        return ExchangeOutboxJpaEntity.create(
                null,
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
                DEFAULT_IDEMPOTENCY_KEY + "_new");
    }
}
