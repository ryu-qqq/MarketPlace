package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion;

import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.LegacyConversionOutboxJpaEntity;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * LegacyConversionOutboxJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 LegacyConversionOutboxJpaEntity 관련 객체들을 생성합니다.
 */
public final class LegacyConversionOutboxJpaEntityFixtures {

    private LegacyConversionOutboxJpaEntityFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final Long DEFAULT_LEGACY_PRODUCT_GROUP_ID = 100L;
    public static final int DEFAULT_RETRY_COUNT = 0;
    public static final int DEFAULT_MAX_RETRY = 3;
    public static final long DEFAULT_VERSION = 0L;

    // ===== Entity Fixtures =====

    /** PENDING 상태 Entity (단위 테스트용, DEFAULT_ID). */
    public static LegacyConversionOutboxJpaEntity pendingEntity() {
        Instant now = Instant.now();
        return LegacyConversionOutboxJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_LEGACY_PRODUCT_GROUP_ID,
                LegacyConversionOutboxJpaEntity.Status.PENDING,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                DEFAULT_VERSION);
    }

    /** PENDING 상태 신규 Entity (통합 테스트용, ID null). */
    public static LegacyConversionOutboxJpaEntity newPendingEntity() {
        Instant now = Instant.now();
        return LegacyConversionOutboxJpaEntity.create(
                null,
                DEFAULT_LEGACY_PRODUCT_GROUP_ID + SEQUENCE.getAndIncrement(),
                LegacyConversionOutboxJpaEntity.Status.PENDING,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                DEFAULT_VERSION);
    }

    /** legacyProductGroupId를 지정한 PENDING 상태 신규 Entity. */
    public static LegacyConversionOutboxJpaEntity newPendingEntityWithGroupId(
            long legacyProductGroupId) {
        Instant now = Instant.now();
        return LegacyConversionOutboxJpaEntity.create(
                null,
                legacyProductGroupId,
                LegacyConversionOutboxJpaEntity.Status.PENDING,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                DEFAULT_VERSION);
    }

    /** PROCESSING 상태 Entity (단위 테스트용, DEFAULT_ID). */
    public static LegacyConversionOutboxJpaEntity processingEntity() {
        Instant now = Instant.now();
        return LegacyConversionOutboxJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_LEGACY_PRODUCT_GROUP_ID,
                LegacyConversionOutboxJpaEntity.Status.PROCESSING,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                DEFAULT_VERSION);
    }

    /** PROCESSING 상태 신규 Entity (통합 테스트용, ID null). */
    public static LegacyConversionOutboxJpaEntity newProcessingEntity() {
        Instant now = Instant.now();
        return LegacyConversionOutboxJpaEntity.create(
                null,
                DEFAULT_LEGACY_PRODUCT_GROUP_ID + SEQUENCE.getAndIncrement(),
                LegacyConversionOutboxJpaEntity.Status.PROCESSING,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                DEFAULT_VERSION);
    }

    /** COMPLETED 상태 Entity (단위 테스트용, DEFAULT_ID). */
    public static LegacyConversionOutboxJpaEntity completedEntity() {
        Instant now = Instant.now();
        return LegacyConversionOutboxJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_LEGACY_PRODUCT_GROUP_ID,
                LegacyConversionOutboxJpaEntity.Status.COMPLETED,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                now,
                null,
                DEFAULT_VERSION);
    }

    /** COMPLETED 상태 신규 Entity (통합 테스트용, ID null). */
    public static LegacyConversionOutboxJpaEntity newCompletedEntity() {
        Instant now = Instant.now();
        return LegacyConversionOutboxJpaEntity.create(
                null,
                DEFAULT_LEGACY_PRODUCT_GROUP_ID + SEQUENCE.getAndIncrement(),
                LegacyConversionOutboxJpaEntity.Status.COMPLETED,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                now,
                null,
                DEFAULT_VERSION);
    }

    /** FAILED 상태 Entity (단위 테스트용, DEFAULT_ID). */
    public static LegacyConversionOutboxJpaEntity failedEntity() {
        Instant now = Instant.now();
        return LegacyConversionOutboxJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_LEGACY_PRODUCT_GROUP_ID,
                LegacyConversionOutboxJpaEntity.Status.FAILED,
                DEFAULT_MAX_RETRY,
                DEFAULT_MAX_RETRY,
                now,
                now,
                now,
                "최대 재시도 횟수 초과",
                DEFAULT_VERSION);
    }

    /** FAILED 상태 신규 Entity (통합 테스트용, ID null). */
    public static LegacyConversionOutboxJpaEntity newFailedEntity() {
        Instant now = Instant.now();
        return LegacyConversionOutboxJpaEntity.create(
                null,
                DEFAULT_LEGACY_PRODUCT_GROUP_ID + SEQUENCE.getAndIncrement(),
                LegacyConversionOutboxJpaEntity.Status.FAILED,
                DEFAULT_MAX_RETRY,
                DEFAULT_MAX_RETRY,
                now,
                now,
                now,
                "최대 재시도 횟수 초과",
                DEFAULT_VERSION);
    }

    /** 재시도 횟수가 있는 PENDING 상태 Entity (통합 테스트용, ID null). */
    public static LegacyConversionOutboxJpaEntity retriedPendingEntity(int retryCount) {
        Instant now = Instant.now();
        return LegacyConversionOutboxJpaEntity.create(
                null,
                DEFAULT_LEGACY_PRODUCT_GROUP_ID + SEQUENCE.getAndIncrement(),
                LegacyConversionOutboxJpaEntity.Status.PENDING,
                retryCount,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                "이전 시도 실패",
                DEFAULT_VERSION);
    }

    /** PROCESSING 타임아웃 테스트용 Entity (과거 updatedAt). */
    public static LegacyConversionOutboxJpaEntity processingTimeoutEntity(long secondsAgo) {
        Instant now = Instant.now();
        Instant updatedAt = now.minusSeconds(secondsAgo);
        return LegacyConversionOutboxJpaEntity.create(
                null,
                DEFAULT_LEGACY_PRODUCT_GROUP_ID + SEQUENCE.getAndIncrement(),
                LegacyConversionOutboxJpaEntity.Status.PROCESSING,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                updatedAt,
                updatedAt,
                null,
                null,
                DEFAULT_VERSION);
    }

    /** 과거 createdAt을 가진 PENDING Entity (스케줄러 조회 테스트용). */
    public static LegacyConversionOutboxJpaEntity oldPendingEntity(long secondsAgo) {
        Instant now = Instant.now();
        Instant createdAt = now.minusSeconds(secondsAgo);
        return LegacyConversionOutboxJpaEntity.create(
                null,
                DEFAULT_LEGACY_PRODUCT_GROUP_ID + SEQUENCE.getAndIncrement(),
                LegacyConversionOutboxJpaEntity.Status.PENDING,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                createdAt,
                createdAt,
                null,
                null,
                DEFAULT_VERSION);
    }
}
