package com.ryuqq.marketplace.adapter.out.persistence.selleradmin;

import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.entity.SellerAdminEmailOutboxJpaEntity;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * SellerAdminEmailOutboxJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 SellerAdminEmailOutboxJpaEntity 관련 객체들을 생성합니다.
 */
public final class SellerAdminEmailOutboxJpaEntityFixtures {

    private SellerAdminEmailOutboxJpaEntityFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final Long DEFAULT_SELLER_ID = 1L;
    public static final String DEFAULT_PAYLOAD =
            "{\"sellerAdminId\":\"01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f60\",\"loginId\":\"admin@test.com\",\"name\":\"홍길동\"}";
    public static final int DEFAULT_RETRY_COUNT = 0;
    public static final int DEFAULT_MAX_RETRY = 3;
    public static final long DEFAULT_VERSION = 0L;
    private static final String IDEMPOTENCY_KEY_PREFIX = "SAEO";

    private static String generateIdempotencyKey(Long sellerId, Instant createdAt) {
        long sellerIdValue = sellerId != null ? sellerId : 0L;
        long seq = SEQUENCE.getAndIncrement();
        return IDEMPOTENCY_KEY_PREFIX
                + ":"
                + sellerIdValue
                + ":"
                + createdAt.toEpochMilli()
                + ":"
                + seq;
    }

    // ===== Entity Fixtures =====

    /** PENDING 상태의 Outbox Entity 생성. */
    public static SellerAdminEmailOutboxJpaEntity pendingEntity() {
        Instant now = Instant.now();
        return SellerAdminEmailOutboxJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_PAYLOAD,
                SellerAdminEmailOutboxJpaEntity.Status.PENDING,
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
    public static SellerAdminEmailOutboxJpaEntity newPendingEntity() {
        Instant now = Instant.now();
        return SellerAdminEmailOutboxJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_PAYLOAD,
                SellerAdminEmailOutboxJpaEntity.Status.PENDING,
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
    public static SellerAdminEmailOutboxJpaEntity newPendingEntityWithSellerId(Long sellerId) {
        Instant now = Instant.now();
        return SellerAdminEmailOutboxJpaEntity.create(
                null,
                sellerId,
                DEFAULT_PAYLOAD,
                SellerAdminEmailOutboxJpaEntity.Status.PENDING,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(sellerId, now));
    }

    /** PROCESSING 상태의 Outbox Entity 생성. */
    public static SellerAdminEmailOutboxJpaEntity processingEntity() {
        Instant now = Instant.now();
        return SellerAdminEmailOutboxJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_PAYLOAD,
                SellerAdminEmailOutboxJpaEntity.Status.PROCESSING,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(DEFAULT_SELLER_ID, now));
    }

    /** COMPLETED 상태의 Outbox Entity 생성. */
    public static SellerAdminEmailOutboxJpaEntity completedEntity() {
        Instant now = Instant.now();
        return SellerAdminEmailOutboxJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_PAYLOAD,
                SellerAdminEmailOutboxJpaEntity.Status.COMPLETED,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                now,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(DEFAULT_SELLER_ID, now));
    }

    /** FAILED 상태의 Outbox Entity 생성. */
    public static SellerAdminEmailOutboxJpaEntity failedEntity() {
        Instant now = Instant.now();
        return SellerAdminEmailOutboxJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_PAYLOAD,
                SellerAdminEmailOutboxJpaEntity.Status.FAILED,
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
    public static SellerAdminEmailOutboxJpaEntity retriedPendingEntity(int retryCount) {
        Instant now = Instant.now();
        return SellerAdminEmailOutboxJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_PAYLOAD,
                SellerAdminEmailOutboxJpaEntity.Status.PENDING,
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
    public static SellerAdminEmailOutboxJpaEntity entityWithPayload(String payload) {
        Instant now = Instant.now();
        return SellerAdminEmailOutboxJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                payload,
                SellerAdminEmailOutboxJpaEntity.Status.PENDING,
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
    public static SellerAdminEmailOutboxJpaEntity entityWithMaxRetry(int maxRetry) {
        Instant now = Instant.now();
        return SellerAdminEmailOutboxJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_PAYLOAD,
                SellerAdminEmailOutboxJpaEntity.Status.PENDING,
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
    public static SellerAdminEmailOutboxJpaEntity processingTimeoutEntity(long secondsAgo) {
        Instant now = Instant.now();
        Instant updatedAt = now.minusSeconds(secondsAgo);
        return SellerAdminEmailOutboxJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_PAYLOAD,
                SellerAdminEmailOutboxJpaEntity.Status.PROCESSING,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                updatedAt,
                updatedAt,
                null,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(DEFAULT_SELLER_ID, updatedAt));
    }

    /** 특정 생성 시각을 가진 PENDING Entity 생성 (재시도 로직 테스트용). */
    public static SellerAdminEmailOutboxJpaEntity pendingEntityWithCreatedAt(
            Long sellerId, Instant createdAt) {
        return SellerAdminEmailOutboxJpaEntity.create(
                null,
                sellerId,
                DEFAULT_PAYLOAD,
                SellerAdminEmailOutboxJpaEntity.Status.PENDING,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                createdAt,
                createdAt,
                null,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(sellerId, createdAt));
    }

    /** 최대 재시도에 도달한 PENDING Entity 생성 (재시도 불가). */
    public static SellerAdminEmailOutboxJpaEntity pendingEntityMaxRetry() {
        Instant now = Instant.now();
        return SellerAdminEmailOutboxJpaEntity.create(
                null,
                DEFAULT_SELLER_ID,
                DEFAULT_PAYLOAD,
                SellerAdminEmailOutboxJpaEntity.Status.PENDING,
                DEFAULT_MAX_RETRY,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                "최대 재시도 도달",
                DEFAULT_VERSION,
                generateIdempotencyKey(DEFAULT_SELLER_ID, now));
    }
}
