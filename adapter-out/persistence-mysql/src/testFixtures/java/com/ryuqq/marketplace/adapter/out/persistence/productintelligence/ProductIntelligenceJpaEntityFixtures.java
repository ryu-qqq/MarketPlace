package com.ryuqq.marketplace.adapter.out.persistence.productintelligence;

import com.ryuqq.marketplace.adapter.out.persistence.productintelligence.entity.IntelligenceOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productintelligence.entity.ProductProfileJpaEntity;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ProductIntelligence JpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 ProductProfileJpaEntity, IntelligenceOutboxJpaEntity 관련 객체들을 생성합니다.
 */
public final class ProductIntelligenceJpaEntityFixtures {

    private ProductIntelligenceJpaEntityFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final Long DEFAULT_PRODUCT_GROUP_ID = 100L;
    public static final Long DEFAULT_PROFILE_ID = 1L;
    public static final int DEFAULT_PROFILE_VERSION = 1;
    public static final int DEFAULT_EXPECTED_ANALYSIS_COUNT = 3;
    public static final int DEFAULT_COMPLETED_ANALYSIS_COUNT = 0;
    public static final long DEFAULT_VERSION = 0L;
    public static final int DEFAULT_MAX_RETRY = 3;

    // ===== ProductProfileJpaEntity Fixtures =====

    /** PENDING 상태의 ProductProfile Entity 생성 (ID 자동 생성). */
    public static ProductProfileJpaEntity pendingProfileEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return ProductProfileJpaEntity.create(
                null,
                DEFAULT_PRODUCT_GROUP_ID + seq,
                null,
                DEFAULT_PROFILE_VERSION,
                ProductProfileJpaEntity.Status.PENDING,
                DEFAULT_EXPECTED_ANALYSIS_COUNT,
                DEFAULT_COMPLETED_ANALYSIS_COUNT,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                now,
                now,
                null,
                null,
                null,
                null,
                DEFAULT_VERSION);
    }

    /** ID를 지정한 PENDING 상태의 ProductProfile Entity 생성. */
    public static ProductProfileJpaEntity pendingProfileEntity(Long id, Long productGroupId) {
        Instant now = Instant.now();
        return ProductProfileJpaEntity.create(
                id,
                productGroupId,
                null,
                DEFAULT_PROFILE_VERSION,
                ProductProfileJpaEntity.Status.PENDING,
                DEFAULT_EXPECTED_ANALYSIS_COUNT,
                DEFAULT_COMPLETED_ANALYSIS_COUNT,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                now,
                now,
                null,
                null,
                null,
                null,
                DEFAULT_VERSION);
    }

    /** ANALYZING 상태의 ProductProfile Entity 생성. */
    public static ProductProfileJpaEntity analyzingProfileEntity(Long id, Long productGroupId) {
        Instant now = Instant.now();
        return ProductProfileJpaEntity.create(
                id,
                productGroupId,
                null,
                DEFAULT_PROFILE_VERSION,
                ProductProfileJpaEntity.Status.ANALYZING,
                DEFAULT_EXPECTED_ANALYSIS_COUNT,
                1,
                "DESCRIPTION",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                now,
                now,
                null,
                null,
                null,
                null,
                DEFAULT_VERSION);
    }

    /** COMPLETED 상태의 ProductProfile Entity 생성. */
    public static ProductProfileJpaEntity completedProfileEntity(Long id, Long productGroupId) {
        Instant now = Instant.now();
        return ProductProfileJpaEntity.create(
                id,
                productGroupId,
                null,
                DEFAULT_PROFILE_VERSION,
                ProductProfileJpaEntity.Status.COMPLETED,
                DEFAULT_EXPECTED_ANALYSIS_COUNT,
                DEFAULT_EXPECTED_ANALYSIS_COUNT,
                "DESCRIPTION,NOTICE,OPTION",
                null,
                null,
                null,
                ProductProfileJpaEntity.DecisionType.AUTO_APPROVED,
                0.95,
                "[\"분석 완료\"]",
                now,
                null,
                now,
                now,
                now,
                null,
                null,
                "abc123",
                DEFAULT_VERSION);
    }

    /** FAILED 상태의 ProductProfile Entity 생성. */
    public static ProductProfileJpaEntity failedProfileEntity(Long id, Long productGroupId) {
        Instant now = Instant.now();
        return ProductProfileJpaEntity.create(
                id,
                productGroupId,
                null,
                DEFAULT_PROFILE_VERSION,
                ProductProfileJpaEntity.Status.FAILED,
                DEFAULT_EXPECTED_ANALYSIS_COUNT,
                0,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                now,
                now,
                null,
                null,
                "분석 실패",
                null,
                DEFAULT_VERSION);
    }

    /**
     * 만료된(expiredAt이 설정된) ProductProfile Entity 생성.
     *
     * <p>이전 버전의 프로파일로, 새 버전이 생성될 때 만료 처리됩니다.
     */
    public static ProductProfileJpaEntity expiredProfileEntity(Long id, Long productGroupId) {
        Instant now = Instant.now();
        Instant past = now.minusSeconds(3600);
        return ProductProfileJpaEntity.create(
                id,
                productGroupId,
                null,
                DEFAULT_PROFILE_VERSION,
                ProductProfileJpaEntity.Status.COMPLETED,
                DEFAULT_EXPECTED_ANALYSIS_COUNT,
                DEFAULT_EXPECTED_ANALYSIS_COUNT,
                "DESCRIPTION,NOTICE,OPTION",
                null,
                null,
                null,
                ProductProfileJpaEntity.DecisionType.AUTO_APPROVED,
                0.9,
                "[\"이전 분석\"]",
                past,
                null,
                past,
                past,
                past,
                now,
                "만료됨",
                null,
                DEFAULT_VERSION);
    }

    /**
     * ANALYZING 상태에서 멈춘(stuck) ProductProfile Entity 생성.
     *
     * <p>updatedAt이 오래된 상태로 설정되어 stuck 상태 감지 테스트에 사용합니다.
     */
    public static ProductProfileJpaEntity stuckAnalyzingProfileEntity(
            Long id, Long productGroupId) {
        Instant past = Instant.now().minusSeconds(7200);
        return ProductProfileJpaEntity.create(
                id,
                productGroupId,
                null,
                DEFAULT_PROFILE_VERSION,
                ProductProfileJpaEntity.Status.ANALYZING,
                DEFAULT_EXPECTED_ANALYSIS_COUNT,
                DEFAULT_EXPECTED_ANALYSIS_COUNT,
                "DESCRIPTION,NOTICE,OPTION",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                past,
                past,
                null,
                null,
                null,
                null,
                DEFAULT_VERSION);
    }

    /** 특정 버전의 ProductProfile Entity 생성 (다중 버전 테스트용). */
    public static ProductProfileJpaEntity pendingProfileEntityWithVersion(
            Long id, Long productGroupId, int profileVersion) {
        Instant now = Instant.now();
        return ProductProfileJpaEntity.create(
                id,
                productGroupId,
                null,
                profileVersion,
                ProductProfileJpaEntity.Status.PENDING,
                DEFAULT_EXPECTED_ANALYSIS_COUNT,
                DEFAULT_COMPLETED_ANALYSIS_COUNT,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                now,
                now,
                null,
                null,
                null,
                null,
                DEFAULT_VERSION);
    }

    // ===== IntelligenceOutboxJpaEntity Fixtures =====

    /** PENDING 상태의 IntelligenceOutbox Entity 생성 (ID 자동 생성). */
    public static IntelligenceOutboxJpaEntity pendingOutboxEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        long productGroupId = DEFAULT_PRODUCT_GROUP_ID + seq;
        return IntelligenceOutboxJpaEntity.create(
                null,
                productGroupId,
                null,
                IntelligenceOutboxJpaEntity.Status.PENDING,
                0,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                DEFAULT_VERSION,
                "PI:" + productGroupId + ":" + now.toEpochMilli());
    }

    /** ID를 지정한 PENDING 상태의 IntelligenceOutbox Entity 생성. */
    public static IntelligenceOutboxJpaEntity pendingOutboxEntity(
            Long id, Long productGroupId, String idempotencyKey) {
        Instant now = Instant.now();
        return IntelligenceOutboxJpaEntity.create(
                id,
                productGroupId,
                null,
                IntelligenceOutboxJpaEntity.Status.PENDING,
                0,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                DEFAULT_VERSION,
                idempotencyKey);
    }

    /** 오래 전에 생성된 PENDING 상태의 IntelligenceOutbox Entity 생성 (Outbox Relay 조회 테스트용). */
    public static IntelligenceOutboxJpaEntity oldPendingOutboxEntity(
            Long id, Long productGroupId, String idempotencyKey) {
        Instant past = Instant.now().minusSeconds(300);
        return IntelligenceOutboxJpaEntity.create(
                id,
                productGroupId,
                null,
                IntelligenceOutboxJpaEntity.Status.PENDING,
                0,
                DEFAULT_MAX_RETRY,
                past,
                past,
                null,
                null,
                DEFAULT_VERSION,
                idempotencyKey);
    }

    /** SENT 상태의 IntelligenceOutbox Entity 생성. */
    public static IntelligenceOutboxJpaEntity sentOutboxEntity(
            Long id, Long productGroupId, String idempotencyKey) {
        Instant now = Instant.now();
        return IntelligenceOutboxJpaEntity.create(
                id,
                productGroupId,
                DEFAULT_PROFILE_ID,
                IntelligenceOutboxJpaEntity.Status.SENT,
                0,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                DEFAULT_VERSION,
                idempotencyKey);
    }

    /** 타임아웃된 SENT 상태의 IntelligenceOutbox Entity 생성 (타임아웃 복구 테스트용). */
    public static IntelligenceOutboxJpaEntity timeoutSentOutboxEntity(
            Long id, Long productGroupId, String idempotencyKey) {
        Instant past = Instant.now().minusSeconds(3600);
        return IntelligenceOutboxJpaEntity.create(
                id,
                productGroupId,
                DEFAULT_PROFILE_ID,
                IntelligenceOutboxJpaEntity.Status.SENT,
                0,
                DEFAULT_MAX_RETRY,
                past,
                past,
                null,
                null,
                DEFAULT_VERSION,
                idempotencyKey);
    }

    /** COMPLETED 상태의 IntelligenceOutbox Entity 생성. */
    public static IntelligenceOutboxJpaEntity completedOutboxEntity(
            Long id, Long productGroupId, String idempotencyKey) {
        Instant now = Instant.now();
        return IntelligenceOutboxJpaEntity.create(
                id,
                productGroupId,
                DEFAULT_PROFILE_ID,
                IntelligenceOutboxJpaEntity.Status.COMPLETED,
                0,
                DEFAULT_MAX_RETRY,
                now,
                now,
                now,
                null,
                DEFAULT_VERSION,
                idempotencyKey);
    }

    /** FAILED 상태의 IntelligenceOutbox Entity 생성. */
    public static IntelligenceOutboxJpaEntity failedOutboxEntity(
            Long id, Long productGroupId, String idempotencyKey) {
        Instant now = Instant.now();
        return IntelligenceOutboxJpaEntity.create(
                id,
                productGroupId,
                null,
                IntelligenceOutboxJpaEntity.Status.FAILED,
                DEFAULT_MAX_RETRY,
                DEFAULT_MAX_RETRY,
                now,
                now,
                now,
                "최대 재시도 초과",
                DEFAULT_VERSION,
                idempotencyKey);
    }
}
