package com.ryuqq.marketplace.adapter.out.persistence.imageupload;

import com.ryuqq.marketplace.adapter.out.persistence.imageupload.entity.ImageUploadOutboxJpaEntity;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageUploadOutboxStatus;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ImageUploadOutboxJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 ImageUploadOutboxJpaEntity 관련 객체들을 생성합니다.
 */
public final class ImageUploadOutboxJpaEntityFixtures {

    private ImageUploadOutboxJpaEntityFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final Long DEFAULT_SOURCE_ID = 100L;
    public static final ImageSourceType DEFAULT_SOURCE_TYPE = ImageSourceType.PRODUCT_GROUP_IMAGE;
    public static final String DEFAULT_ORIGIN_URL = "https://example.com/images/product.jpg";
    public static final int DEFAULT_RETRY_COUNT = 0;
    public static final int DEFAULT_MAX_RETRY = 3;
    public static final long DEFAULT_VERSION = 0L;
    private static final String IDEMPOTENCY_KEY_PREFIX = "IUO";

    private static String generateIdempotencyKey(
            ImageSourceType sourceType, Long sourceId, Instant createdAt) {
        long seq = SEQUENCE.getAndIncrement();
        return IDEMPOTENCY_KEY_PREFIX
                + ":"
                + sourceType.name()
                + ":"
                + sourceId
                + ":"
                + createdAt.toEpochMilli()
                + ":"
                + seq;
    }

    // ===== Entity Fixtures (단위 테스트용 - ID 있음) =====

    /** PENDING 상태의 Outbox Entity 생성 (단위 테스트용, DEFAULT_ID). */
    public static ImageUploadOutboxJpaEntity pendingEntity() {
        Instant now = Instant.now();
        return ImageUploadOutboxJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_SOURCE_ID,
                DEFAULT_SOURCE_TYPE,
                DEFAULT_ORIGIN_URL,
                ImageUploadOutboxStatus.PENDING,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(DEFAULT_SOURCE_TYPE, DEFAULT_SOURCE_ID, now));
    }

    /** PROCESSING 상태의 Outbox Entity 생성 (단위 테스트용, DEFAULT_ID). */
    public static ImageUploadOutboxJpaEntity processingEntity() {
        Instant now = Instant.now();
        return ImageUploadOutboxJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_SOURCE_ID,
                DEFAULT_SOURCE_TYPE,
                DEFAULT_ORIGIN_URL,
                ImageUploadOutboxStatus.PROCESSING,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(DEFAULT_SOURCE_TYPE, DEFAULT_SOURCE_ID, now));
    }

    /** COMPLETED 상태의 Outbox Entity 생성 (단위 테스트용, DEFAULT_ID). */
    public static ImageUploadOutboxJpaEntity completedEntity() {
        Instant now = Instant.now();
        return ImageUploadOutboxJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_SOURCE_ID,
                DEFAULT_SOURCE_TYPE,
                DEFAULT_ORIGIN_URL,
                ImageUploadOutboxStatus.COMPLETED,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                now,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(DEFAULT_SOURCE_TYPE, DEFAULT_SOURCE_ID, now));
    }

    /** FAILED 상태의 Outbox Entity 생성 (단위 테스트용, DEFAULT_ID). */
    public static ImageUploadOutboxJpaEntity failedEntity() {
        Instant now = Instant.now();
        return ImageUploadOutboxJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_SOURCE_ID,
                DEFAULT_SOURCE_TYPE,
                DEFAULT_ORIGIN_URL,
                ImageUploadOutboxStatus.FAILED,
                DEFAULT_MAX_RETRY,
                DEFAULT_MAX_RETRY,
                now,
                now,
                now,
                "연결 실패로 인한 최대 재시도 초과",
                DEFAULT_VERSION,
                generateIdempotencyKey(DEFAULT_SOURCE_TYPE, DEFAULT_SOURCE_ID, now));
    }

    // ===== Entity Fixtures (통합 테스트용 - ID null) =====

    /** ID 없이 PENDING 상태의 새 Entity 생성 (통합 테스트용). */
    public static ImageUploadOutboxJpaEntity newPendingEntity() {
        Instant now = Instant.now();
        return ImageUploadOutboxJpaEntity.create(
                null,
                DEFAULT_SOURCE_ID,
                DEFAULT_SOURCE_TYPE,
                DEFAULT_ORIGIN_URL,
                ImageUploadOutboxStatus.PENDING,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(DEFAULT_SOURCE_TYPE, DEFAULT_SOURCE_ID, now));
    }

    /** sourceId를 지정한 PENDING 상태 Entity 생성 (통합 테스트용, ID null). */
    public static ImageUploadOutboxJpaEntity newPendingEntityWithSourceId(Long sourceId) {
        Instant now = Instant.now();
        return ImageUploadOutboxJpaEntity.create(
                null,
                sourceId,
                DEFAULT_SOURCE_TYPE,
                DEFAULT_ORIGIN_URL,
                ImageUploadOutboxStatus.PENDING,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(DEFAULT_SOURCE_TYPE, sourceId, now));
    }

    /** sourceType을 지정한 PENDING 상태 Entity 생성 (통합 테스트용, ID null). */
    public static ImageUploadOutboxJpaEntity newPendingEntityWithSourceType(
            ImageSourceType sourceType) {
        Instant now = Instant.now();
        return ImageUploadOutboxJpaEntity.create(
                null,
                DEFAULT_SOURCE_ID,
                sourceType,
                DEFAULT_ORIGIN_URL,
                ImageUploadOutboxStatus.PENDING,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(sourceType, DEFAULT_SOURCE_ID, now));
    }

    /** sourceId와 sourceType을 지정한 PENDING 상태 Entity 생성 (통합 테스트용, ID null). */
    public static ImageUploadOutboxJpaEntity newPendingEntityWithSourceIdAndType(
            Long sourceId, ImageSourceType sourceType) {
        Instant now = Instant.now();
        return ImageUploadOutboxJpaEntity.create(
                null,
                sourceId,
                sourceType,
                DEFAULT_ORIGIN_URL,
                ImageUploadOutboxStatus.PENDING,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(sourceType, sourceId, now));
    }

    /** PROCESSING 상태의 Outbox Entity 생성 (통합 테스트용, ID null, updatedAt이 최신). */
    public static ImageUploadOutboxJpaEntity newProcessingEntity() {
        Instant now = Instant.now();
        return ImageUploadOutboxJpaEntity.create(
                null,
                DEFAULT_SOURCE_ID,
                DEFAULT_SOURCE_TYPE,
                DEFAULT_ORIGIN_URL,
                ImageUploadOutboxStatus.PROCESSING,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(DEFAULT_SOURCE_TYPE, DEFAULT_SOURCE_ID, now));
    }

    /** COMPLETED 상태의 Outbox Entity 생성 (통합 테스트용, ID null). */
    public static ImageUploadOutboxJpaEntity newCompletedEntity() {
        Instant now = Instant.now();
        return ImageUploadOutboxJpaEntity.create(
                null,
                DEFAULT_SOURCE_ID,
                DEFAULT_SOURCE_TYPE,
                DEFAULT_ORIGIN_URL,
                ImageUploadOutboxStatus.COMPLETED,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                now,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(DEFAULT_SOURCE_TYPE, DEFAULT_SOURCE_ID, now));
    }

    /** FAILED 상태의 Outbox Entity 생성 (통합 테스트용, ID null). */
    public static ImageUploadOutboxJpaEntity newFailedEntity() {
        Instant now = Instant.now();
        return ImageUploadOutboxJpaEntity.create(
                null,
                DEFAULT_SOURCE_ID,
                DEFAULT_SOURCE_TYPE,
                DEFAULT_ORIGIN_URL,
                ImageUploadOutboxStatus.FAILED,
                DEFAULT_MAX_RETRY,
                DEFAULT_MAX_RETRY,
                now,
                now,
                now,
                "연결 실패로 인한 최대 재시도 초과",
                DEFAULT_VERSION,
                generateIdempotencyKey(DEFAULT_SOURCE_TYPE, DEFAULT_SOURCE_ID, now));
    }

    // ===== 특수 시나리오 Fixtures =====

    /**
     * 재시도 횟수가 소진된 PENDING 상태 Entity 생성 (통합 테스트용, ID null).
     *
     * <p>retryCount == maxRetry 이므로 findPendingOutboxesForRetry 조회 대상에서 제외됩니다.
     */
    public static ImageUploadOutboxJpaEntity retriedPendingEntity(int retryCount) {
        Instant now = Instant.now();
        return ImageUploadOutboxJpaEntity.create(
                null,
                DEFAULT_SOURCE_ID,
                DEFAULT_SOURCE_TYPE,
                DEFAULT_ORIGIN_URL,
                ImageUploadOutboxStatus.PENDING,
                retryCount,
                retryCount,
                now,
                now,
                null,
                "이전 시도 실패",
                DEFAULT_VERSION,
                generateIdempotencyKey(DEFAULT_SOURCE_TYPE, DEFAULT_SOURCE_ID, now));
    }

    /**
     * PROCESSING 타임아웃 테스트용 Entity (과거 updatedAt, ID null).
     *
     * <p>updatedAt이 secondsAgo 초 이전으로 설정되어 findProcessingTimeoutOutboxes 조회 대상이 됩니다.
     */
    public static ImageUploadOutboxJpaEntity processingTimeoutEntity(long secondsAgo) {
        Instant now = Instant.now();
        Instant pastTime = now.minusSeconds(secondsAgo);
        return ImageUploadOutboxJpaEntity.create(
                null,
                DEFAULT_SOURCE_ID,
                DEFAULT_SOURCE_TYPE,
                DEFAULT_ORIGIN_URL,
                ImageUploadOutboxStatus.PROCESSING,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                pastTime,
                pastTime,
                null,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(DEFAULT_SOURCE_TYPE, DEFAULT_SOURCE_ID, pastTime));
    }
}
