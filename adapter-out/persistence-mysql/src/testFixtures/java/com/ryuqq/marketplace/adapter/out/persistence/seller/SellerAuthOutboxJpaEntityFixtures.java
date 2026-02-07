package com.ryuqq.marketplace.adapter.out.persistence.seller;

import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerAuthOutboxJpaEntity;
import java.time.Instant;

/**
 * SellerAuthOutboxJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 SellerAuthOutboxJpaEntity 관련 객체들을 생성합니다.
 */
public final class SellerAuthOutboxJpaEntityFixtures {

    private SellerAuthOutboxJpaEntityFixtures() {}

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final Long DEFAULT_SELLER_ID = 1L;
    public static final String DEFAULT_PAYLOAD =
            "{\"sellerName\":\"테스트셀러\",\"companyName\":\"테스트"
                    + " 주식회사\",\"registrationNumber\":\"123-45-67890\"}";
    public static final int DEFAULT_RETRY_COUNT = 0;
    public static final int DEFAULT_MAX_RETRY = 3;
    public static final long DEFAULT_VERSION = 0L;
    private static final String IDEMPOTENCY_KEY_PREFIX = "SAO";

    private static String generateIdempotencyKey(Long sellerId, Instant createdAt) {
        long sellerIdValue = sellerId != null ? sellerId : 0L;
        return IDEMPOTENCY_KEY_PREFIX + ":" + sellerIdValue + ":" + createdAt.toEpochMilli();
    }

    // ===== Entity Fixtures =====

    /** PENDING 상태의 Outbox Entity 생성. */
    public static SellerAuthOutboxJpaEntity pendingEntity() {
        Instant now = Instant.now();
        return SellerAuthOutboxJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_PAYLOAD,
                SellerAuthOutboxJpaEntity.Status.PENDING,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(DEFAULT_SELLER_ID, now));
    }

    /** ID 없이 PENDING 상태의 새 Entity 생성 (저장용). */
    public static SellerAuthOutboxJpaEntity newPendingEntity() {
        Instant now = Instant.now();
        return SellerAuthOutboxJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_PAYLOAD,
                SellerAuthOutboxJpaEntity.Status.PENDING,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(DEFAULT_SELLER_ID, now));
    }

    /** 셀러 ID를 지정한 PENDING 상태 Entity 생성. ID는 null로 새 엔티티 생성. */
    public static SellerAuthOutboxJpaEntity newPendingEntityWithSellerId(Long sellerId) {
        Instant now = Instant.now();
        return SellerAuthOutboxJpaEntity.create(
                null,
                sellerId,
                DEFAULT_PAYLOAD,
                SellerAuthOutboxJpaEntity.Status.PENDING,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(sellerId, now));
    }

    /** PROCESSING 상태의 Outbox Entity 생성 (저장용, ID null). */
    public static SellerAuthOutboxJpaEntity processingEntity() {
        Instant now = Instant.now();
        return SellerAuthOutboxJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_PAYLOAD,
                SellerAuthOutboxJpaEntity.Status.PROCESSING,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(DEFAULT_SELLER_ID, now));
    }

    /** PROCESSING 상태의 Outbox Entity 생성 (통합 테스트용, ID null). */
    public static SellerAuthOutboxJpaEntity newProcessingEntity() {
        Instant now = Instant.now();
        return SellerAuthOutboxJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_PAYLOAD,
                SellerAuthOutboxJpaEntity.Status.PROCESSING,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(DEFAULT_SELLER_ID, now));
    }

    /** COMPLETED 상태의 Outbox Entity 생성 (단위 테스트용, DEFAULT_ID). */
    public static SellerAuthOutboxJpaEntity completedEntity() {
        Instant now = Instant.now();
        return SellerAuthOutboxJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_PAYLOAD,
                SellerAuthOutboxJpaEntity.Status.COMPLETED,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                now,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(DEFAULT_SELLER_ID, now));
    }

    /** COMPLETED 상태의 Outbox Entity 생성 (통합 테스트용, ID null). */
    public static SellerAuthOutboxJpaEntity newCompletedEntity() {
        Instant now = Instant.now();
        return SellerAuthOutboxJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_PAYLOAD,
                SellerAuthOutboxJpaEntity.Status.COMPLETED,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                now,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(DEFAULT_SELLER_ID, now));
    }

    /** FAILED 상태의 Outbox Entity 생성 (단위 테스트용, DEFAULT_ID). */
    public static SellerAuthOutboxJpaEntity failedEntity() {
        Instant now = Instant.now();
        return SellerAuthOutboxJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_PAYLOAD,
                SellerAuthOutboxJpaEntity.Status.FAILED,
                DEFAULT_MAX_RETRY,
                DEFAULT_MAX_RETRY,
                now,
                now,
                now,
                "연결 실패로 인한 최대 재시도 초과",
                DEFAULT_VERSION,
                generateIdempotencyKey(DEFAULT_SELLER_ID, now));
    }

    /** FAILED 상태의 Outbox Entity 생성 (통합 테스트용, ID null). */
    public static SellerAuthOutboxJpaEntity newFailedEntity() {
        Instant now = Instant.now();
        return SellerAuthOutboxJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_PAYLOAD,
                SellerAuthOutboxJpaEntity.Status.FAILED,
                DEFAULT_MAX_RETRY,
                DEFAULT_MAX_RETRY,
                now,
                now,
                now,
                "연결 실패로 인한 최대 재시도 초과",
                DEFAULT_VERSION,
                generateIdempotencyKey(DEFAULT_SELLER_ID, now));
    }

    /** 재시도 횟수가 있는 PENDING 상태 Entity 생성 (저장용, ID null). */
    public static SellerAuthOutboxJpaEntity retriedPendingEntity(int retryCount) {
        Instant now = Instant.now();
        return SellerAuthOutboxJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_PAYLOAD,
                SellerAuthOutboxJpaEntity.Status.PENDING,
                retryCount,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                "이전 시도 실패",
                DEFAULT_VERSION,
                generateIdempotencyKey(DEFAULT_SELLER_ID, now));
    }

    /** 커스텀 페이로드를 가진 Entity 생성. */
    public static SellerAuthOutboxJpaEntity entityWithPayload(String payload) {
        Instant now = Instant.now();
        return SellerAuthOutboxJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                payload,
                SellerAuthOutboxJpaEntity.Status.PENDING,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(DEFAULT_SELLER_ID, now));
    }

    /** 커스텀 최대 재시도 횟수를 가진 Entity 생성. */
    public static SellerAuthOutboxJpaEntity entityWithMaxRetry(int maxRetry) {
        Instant now = Instant.now();
        return SellerAuthOutboxJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_PAYLOAD,
                SellerAuthOutboxJpaEntity.Status.PENDING,
                DEFAULT_RETRY_COUNT,
                maxRetry,
                now,
                now,
                null,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(DEFAULT_SELLER_ID, now));
    }

    /** PROCESSING 타임아웃 테스트용 Entity (과거 updatedAt). */
    public static SellerAuthOutboxJpaEntity processingTimeoutEntity(long secondsAgo) {
        Instant now = Instant.now();
        Instant updatedAt = now.minusSeconds(secondsAgo);
        return SellerAuthOutboxJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_PAYLOAD,
                SellerAuthOutboxJpaEntity.Status.PROCESSING,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                updatedAt,
                updatedAt,
                null,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(DEFAULT_SELLER_ID, updatedAt));
    }
}
