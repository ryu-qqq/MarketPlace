package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion;

import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.LegacyOrderConversionOutboxJpaEntity;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * LegacyOrderConversionOutboxJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 LegacyOrderConversionOutboxJpaEntity 관련 객체들을 생성합니다.
 */
public final class LegacyOrderConversionOutboxJpaEntityFixtures {

    private LegacyOrderConversionOutboxJpaEntityFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final long DEFAULT_LEGACY_ORDER_ID = 10001L;
    public static final long DEFAULT_LEGACY_PAYMENT_ID = 20001L;
    public static final int DEFAULT_RETRY_COUNT = 0;
    public static final int DEFAULT_MAX_RETRY = 3;
    public static final long DEFAULT_VERSION = 0L;

    // ===== Entity Fixtures =====

    /** PENDING 상태 Entity (단위 테스트용, DEFAULT_ID). */
    public static LegacyOrderConversionOutboxJpaEntity pendingEntity() {
        Instant now = Instant.now();
        return LegacyOrderConversionOutboxJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_LEGACY_ORDER_ID,
                DEFAULT_LEGACY_PAYMENT_ID,
                "PENDING",
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                null,
                null,
                now,
                now,
                DEFAULT_VERSION);
    }

    /** PENDING 상태 신규 Entity (통합 테스트용, ID null). */
    public static LegacyOrderConversionOutboxJpaEntity newPendingEntity() {
        Instant now = Instant.now();
        long seq = SEQUENCE.getAndIncrement();
        return LegacyOrderConversionOutboxJpaEntity.create(
                null,
                DEFAULT_LEGACY_ORDER_ID + seq,
                DEFAULT_LEGACY_PAYMENT_ID + seq,
                "PENDING",
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                null,
                null,
                now,
                now,
                DEFAULT_VERSION);
    }

    /** legacyOrderId를 지정한 PENDING 상태 신규 Entity. */
    public static LegacyOrderConversionOutboxJpaEntity newPendingEntityWithOrderId(
            long legacyOrderId) {
        Instant now = Instant.now();
        return LegacyOrderConversionOutboxJpaEntity.create(
                null,
                legacyOrderId,
                DEFAULT_LEGACY_PAYMENT_ID + SEQUENCE.getAndIncrement(),
                "PENDING",
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                null,
                null,
                now,
                now,
                DEFAULT_VERSION);
    }

    /** PROCESSING 상태 Entity (단위 테스트용, DEFAULT_ID). */
    public static LegacyOrderConversionOutboxJpaEntity processingEntity() {
        Instant now = Instant.now();
        return LegacyOrderConversionOutboxJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_LEGACY_ORDER_ID,
                DEFAULT_LEGACY_PAYMENT_ID,
                "PROCESSING",
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                null,
                null,
                now,
                now,
                DEFAULT_VERSION);
    }

    /** PROCESSING 상태 신규 Entity (통합 테스트용, ID null). */
    public static LegacyOrderConversionOutboxJpaEntity newProcessingEntity() {
        Instant now = Instant.now();
        long seq = SEQUENCE.getAndIncrement();
        return LegacyOrderConversionOutboxJpaEntity.create(
                null,
                DEFAULT_LEGACY_ORDER_ID + seq,
                DEFAULT_LEGACY_PAYMENT_ID + seq,
                "PROCESSING",
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                null,
                null,
                now,
                now,
                DEFAULT_VERSION);
    }

    /** COMPLETED 상태 Entity (단위 테스트용, DEFAULT_ID). */
    public static LegacyOrderConversionOutboxJpaEntity completedEntity() {
        Instant now = Instant.now();
        return LegacyOrderConversionOutboxJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_LEGACY_ORDER_ID,
                DEFAULT_LEGACY_PAYMENT_ID,
                "COMPLETED",
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                null,
                now,
                now,
                now,
                DEFAULT_VERSION);
    }

    /** FAILED 상태 Entity (단위 테스트용, DEFAULT_ID). */
    public static LegacyOrderConversionOutboxJpaEntity failedEntity() {
        Instant now = Instant.now();
        return LegacyOrderConversionOutboxJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_LEGACY_ORDER_ID,
                DEFAULT_LEGACY_PAYMENT_ID,
                "FAILED",
                DEFAULT_MAX_RETRY,
                DEFAULT_MAX_RETRY,
                "최대 재시도 횟수 초과",
                now,
                now,
                now,
                DEFAULT_VERSION);
    }

    /** 재시도 횟수가 있는 PENDING 상태 Entity (통합 테스트용, ID null). */
    public static LegacyOrderConversionOutboxJpaEntity retriedPendingEntity(int retryCount) {
        Instant now = Instant.now();
        long seq = SEQUENCE.getAndIncrement();
        return LegacyOrderConversionOutboxJpaEntity.create(
                null,
                DEFAULT_LEGACY_ORDER_ID + seq,
                DEFAULT_LEGACY_PAYMENT_ID + seq,
                "PENDING",
                retryCount,
                DEFAULT_MAX_RETRY,
                "이전 시도 실패",
                null,
                now,
                now,
                DEFAULT_VERSION);
    }

    /** PROCESSING 타임아웃 테스트용 Entity (과거 updatedAt). */
    public static LegacyOrderConversionOutboxJpaEntity processingTimeoutEntity(long secondsAgo) {
        Instant now = Instant.now();
        Instant updatedAt = now.minusSeconds(secondsAgo);
        long seq = SEQUENCE.getAndIncrement();
        return LegacyOrderConversionOutboxJpaEntity.create(
                null,
                DEFAULT_LEGACY_ORDER_ID + seq,
                DEFAULT_LEGACY_PAYMENT_ID + seq,
                "PROCESSING",
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                null,
                null,
                updatedAt,
                updatedAt,
                DEFAULT_VERSION);
    }

    /** 과거 createdAt을 가진 PENDING Entity (스케줄러 조회 테스트용). */
    public static LegacyOrderConversionOutboxJpaEntity oldPendingEntity(long secondsAgo) {
        Instant now = Instant.now();
        Instant createdAt = now.minusSeconds(secondsAgo);
        long seq = SEQUENCE.getAndIncrement();
        return LegacyOrderConversionOutboxJpaEntity.create(
                null,
                DEFAULT_LEGACY_ORDER_ID + seq,
                DEFAULT_LEGACY_PAYMENT_ID + seq,
                "PENDING",
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                null,
                null,
                createdAt,
                createdAt,
                DEFAULT_VERSION);
    }
}
