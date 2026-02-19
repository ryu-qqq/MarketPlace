package com.ryuqq.marketplace.adapter.out.persistence.productgroupinspection;

import com.ryuqq.marketplace.adapter.out.persistence.productgroupinspection.entity.ProductGroupInspectionOutboxJpaEntity;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ProductGroupInspectionOutboxJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 ProductGroupInspectionOutboxJpaEntity 관련 객체들을 생성합니다.
 */
public final class ProductGroupInspectionOutboxJpaEntityFixtures {

    private ProductGroupInspectionOutboxJpaEntityFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    // ===== 기본 상수 =====
    public static final Long DEFAULT_PRODUCT_GROUP_ID = 1000L;
    public static final int DEFAULT_MAX_RETRY = 3;
    public static final String DEFAULT_INSPECTION_RESULT_JSON =
            "{\"score\":85,\"categories\":[\"title\",\"image\"]}";
    public static final Integer DEFAULT_TOTAL_SCORE = 85;
    public static final Boolean DEFAULT_PASSED = true;
    public static final String DEFAULT_ERROR_MESSAGE = "검수 처리 중 오류가 발생했습니다";

    // ===== 내부 유틸 =====

    private static String uniqueIdempotencyKey() {
        return "PGI:"
                + DEFAULT_PRODUCT_GROUP_ID
                + ":"
                + Instant.now().toEpochMilli()
                + ":"
                + SEQUENCE.getAndIncrement();
    }

    // ===== PENDING Entity Fixtures =====

    /**
     * 신규 PENDING Entity 생성 (ID null).
     *
     * <p>DB 저장 전 상태를 표현합니다.
     */
    public static ProductGroupInspectionOutboxJpaEntity newPendingEntity() {
        Instant now = Instant.now();
        return ProductGroupInspectionOutboxJpaEntity.create(
                null,
                DEFAULT_PRODUCT_GROUP_ID,
                ProductGroupInspectionOutboxJpaEntity.Status.PENDING,
                null,
                null,
                null,
                0,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                0L,
                uniqueIdempotencyKey());
    }

    /**
     * ID를 지정한 PENDING Entity 생성.
     *
     * <p>DB 저장 후 상태를 표현합니다.
     */
    public static ProductGroupInspectionOutboxJpaEntity pendingEntity(Long id) {
        Instant now = Instant.now();
        return ProductGroupInspectionOutboxJpaEntity.create(
                id,
                DEFAULT_PRODUCT_GROUP_ID,
                ProductGroupInspectionOutboxJpaEntity.Status.PENDING,
                null,
                null,
                null,
                0,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                0L,
                uniqueIdempotencyKey());
    }

    /**
     * retryCount를 지정한 신규 PENDING Entity 생성 (ID null).
     *
     * <p>재시도 횟수 조건 테스트에 사용합니다.
     */
    public static ProductGroupInspectionOutboxJpaEntity newPendingEntityWithRetry(int retryCount) {
        Instant now = Instant.now();
        return ProductGroupInspectionOutboxJpaEntity.create(
                null,
                DEFAULT_PRODUCT_GROUP_ID,
                ProductGroupInspectionOutboxJpaEntity.Status.PENDING,
                null,
                null,
                null,
                retryCount,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                0L,
                uniqueIdempotencyKey());
    }

    // ===== PROCESSING Entity Fixtures =====

    /**
     * 신규 PROCESSING Entity 생성 (ID null).
     *
     * <p>처리 중 상태를 표현합니다.
     */
    public static ProductGroupInspectionOutboxJpaEntity newProcessingEntity() {
        Instant now = Instant.now();
        return ProductGroupInspectionOutboxJpaEntity.create(
                null,
                DEFAULT_PRODUCT_GROUP_ID,
                ProductGroupInspectionOutboxJpaEntity.Status.PROCESSING,
                null,
                null,
                null,
                0,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                0L,
                uniqueIdempotencyKey());
    }

    /**
     * updatedAt을 지정한 신규 PROCESSING Entity 생성 (ID null).
     *
     * <p>타임아웃 조건 테스트에 사용합니다.
     */
    public static ProductGroupInspectionOutboxJpaEntity newProcessingEntityWithOldUpdatedAt(
            Instant oldUpdatedAt) {
        Instant now = Instant.now();
        return ProductGroupInspectionOutboxJpaEntity.create(
                null,
                DEFAULT_PRODUCT_GROUP_ID,
                ProductGroupInspectionOutboxJpaEntity.Status.PROCESSING,
                null,
                null,
                null,
                0,
                DEFAULT_MAX_RETRY,
                now,
                oldUpdatedAt,
                null,
                null,
                0L,
                uniqueIdempotencyKey());
    }

    // ===== SENT (In-Progress) Entity Fixtures =====

    /**
     * 신규 SENT Entity 생성 (ID null).
     *
     * <p>SQS 전송 완료 상태를 표현합니다.
     */
    public static ProductGroupInspectionOutboxJpaEntity newSentEntity() {
        Instant now = Instant.now();
        return ProductGroupInspectionOutboxJpaEntity.create(
                null,
                DEFAULT_PRODUCT_GROUP_ID,
                ProductGroupInspectionOutboxJpaEntity.Status.SENT,
                null,
                null,
                null,
                0,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                0L,
                uniqueIdempotencyKey());
    }

    /**
     * updatedAt을 지정한 신규 SENT Entity 생성 (ID null).
     *
     * <p>진행 중 타임아웃 조건 테스트에 사용합니다.
     */
    public static ProductGroupInspectionOutboxJpaEntity newSentEntityWithOldUpdatedAt(
            Instant oldUpdatedAt) {
        Instant now = Instant.now();
        return ProductGroupInspectionOutboxJpaEntity.create(
                null,
                DEFAULT_PRODUCT_GROUP_ID,
                ProductGroupInspectionOutboxJpaEntity.Status.SENT,
                null,
                null,
                null,
                0,
                DEFAULT_MAX_RETRY,
                now,
                oldUpdatedAt,
                null,
                null,
                0L,
                uniqueIdempotencyKey());
    }

    // ===== COMPLETED Entity Fixtures =====

    /**
     * 신규 COMPLETED Entity 생성 (ID null).
     *
     * <p>검수 완료(통과) 상태를 표현합니다.
     */
    public static ProductGroupInspectionOutboxJpaEntity newCompletedEntity() {
        Instant now = Instant.now();
        return ProductGroupInspectionOutboxJpaEntity.create(
                null,
                DEFAULT_PRODUCT_GROUP_ID,
                ProductGroupInspectionOutboxJpaEntity.Status.COMPLETED,
                DEFAULT_INSPECTION_RESULT_JSON,
                DEFAULT_TOTAL_SCORE,
                DEFAULT_PASSED,
                0,
                DEFAULT_MAX_RETRY,
                now,
                now,
                now,
                null,
                0L,
                uniqueIdempotencyKey());
    }

    /**
     * ID를 지정한 COMPLETED Entity 생성.
     *
     * <p>DB 저장 후 완료 상태를 표현합니다.
     */
    public static ProductGroupInspectionOutboxJpaEntity completedEntity(Long id) {
        Instant now = Instant.now();
        return ProductGroupInspectionOutboxJpaEntity.create(
                id,
                DEFAULT_PRODUCT_GROUP_ID,
                ProductGroupInspectionOutboxJpaEntity.Status.COMPLETED,
                DEFAULT_INSPECTION_RESULT_JSON,
                DEFAULT_TOTAL_SCORE,
                DEFAULT_PASSED,
                0,
                DEFAULT_MAX_RETRY,
                now,
                now,
                now,
                null,
                0L,
                uniqueIdempotencyKey());
    }

    // ===== FAILED Entity Fixtures =====

    /**
     * 신규 FAILED Entity 생성 (ID null).
     *
     * <p>검수 실패 상태를 표현합니다.
     */
    public static ProductGroupInspectionOutboxJpaEntity newFailedEntity() {
        Instant now = Instant.now();
        return ProductGroupInspectionOutboxJpaEntity.create(
                null,
                DEFAULT_PRODUCT_GROUP_ID,
                ProductGroupInspectionOutboxJpaEntity.Status.FAILED,
                null,
                null,
                null,
                DEFAULT_MAX_RETRY,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                DEFAULT_ERROR_MESSAGE,
                0L,
                uniqueIdempotencyKey());
    }

    /**
     * ID를 지정한 FAILED Entity 생성.
     *
     * <p>DB 저장 후 실패 상태를 표현합니다.
     */
    public static ProductGroupInspectionOutboxJpaEntity failedEntity(Long id) {
        Instant now = Instant.now();
        return ProductGroupInspectionOutboxJpaEntity.create(
                id,
                DEFAULT_PRODUCT_GROUP_ID,
                ProductGroupInspectionOutboxJpaEntity.Status.FAILED,
                null,
                null,
                null,
                DEFAULT_MAX_RETRY,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                DEFAULT_ERROR_MESSAGE,
                0L,
                uniqueIdempotencyKey());
    }
}
