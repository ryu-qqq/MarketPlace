package com.ryuqq.marketplace.adapter.out.persistence.selleradmin;

import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.entity.SellerAdminAuthOutboxJpaEntity;
import java.time.Instant;

/**
 * SellerAdminAuthOutboxJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 SellerAdminAuthOutboxJpaEntity 관련 객체들을 생성합니다.
 */
public final class SellerAdminAuthOutboxJpaEntityFixtures {

    private SellerAdminAuthOutboxJpaEntityFixtures() {}

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final String DEFAULT_SELLER_ADMIN_ID = "01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f60";
    public static final String DEFAULT_PAYLOAD =
            "{\"sellerAdminId\":\"01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f60\",\"loginId\":\"admin@test.com\",\"name\":\"홍길동\"}";
    public static final int DEFAULT_RETRY_COUNT = 0;
    public static final int DEFAULT_MAX_RETRY = 3;
    public static final long DEFAULT_VERSION = 0L;
    private static final String IDEMPOTENCY_KEY_PREFIX = "SAAO";

    private static String generateIdempotencyKey(String sellerAdminId, Instant createdAt) {
        String idValue = sellerAdminId != null ? sellerAdminId : "unknown";
        return IDEMPOTENCY_KEY_PREFIX + ":" + idValue + ":" + createdAt.toEpochMilli();
    }

    // ===== Entity Fixtures =====

    /** PENDING 상태의 Outbox Entity 생성. */
    public static SellerAdminAuthOutboxJpaEntity pendingEntity() {
        Instant now = Instant.now();
        return SellerAdminAuthOutboxJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_SELLER_ADMIN_ID,
                DEFAULT_PAYLOAD,
                SellerAdminAuthOutboxJpaEntity.Status.PENDING,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(DEFAULT_SELLER_ADMIN_ID, now));
    }

    /** ID 없이 PENDING 상태의 새 Entity 생성 (저장용). */
    public static SellerAdminAuthOutboxJpaEntity newPendingEntity() {
        Instant now = Instant.now();
        return SellerAdminAuthOutboxJpaEntity.create(
                null,
                DEFAULT_SELLER_ADMIN_ID,
                DEFAULT_PAYLOAD,
                SellerAdminAuthOutboxJpaEntity.Status.PENDING,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(DEFAULT_SELLER_ADMIN_ID, now));
    }

    /** SellerAdminId를 지정한 PENDING 상태 Entity 생성. ID는 null로 새 엔티티 생성. */
    public static SellerAdminAuthOutboxJpaEntity newPendingEntityWithSellerAdminId(
            String sellerAdminId) {
        Instant now = Instant.now();
        return SellerAdminAuthOutboxJpaEntity.create(
                null,
                sellerAdminId,
                DEFAULT_PAYLOAD,
                SellerAdminAuthOutboxJpaEntity.Status.PENDING,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(sellerAdminId, now));
    }

    /** PROCESSING 상태의 Outbox Entity 생성. */
    public static SellerAdminAuthOutboxJpaEntity processingEntity() {
        Instant now = Instant.now();
        return SellerAdminAuthOutboxJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_SELLER_ADMIN_ID,
                DEFAULT_PAYLOAD,
                SellerAdminAuthOutboxJpaEntity.Status.PROCESSING,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(DEFAULT_SELLER_ADMIN_ID, now));
    }

    /** COMPLETED 상태의 Outbox Entity 생성. */
    public static SellerAdminAuthOutboxJpaEntity completedEntity() {
        Instant now = Instant.now();
        return SellerAdminAuthOutboxJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_SELLER_ADMIN_ID,
                DEFAULT_PAYLOAD,
                SellerAdminAuthOutboxJpaEntity.Status.COMPLETED,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                now,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(DEFAULT_SELLER_ADMIN_ID, now));
    }

    /** FAILED 상태의 Outbox Entity 생성. */
    public static SellerAdminAuthOutboxJpaEntity failedEntity() {
        Instant now = Instant.now();
        return SellerAdminAuthOutboxJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_SELLER_ADMIN_ID,
                DEFAULT_PAYLOAD,
                SellerAdminAuthOutboxJpaEntity.Status.FAILED,
                DEFAULT_MAX_RETRY,
                DEFAULT_MAX_RETRY,
                now,
                now,
                now,
                "인증 서버 연동 실패로 최대 재시도 초과",
                DEFAULT_VERSION,
                generateIdempotencyKey(DEFAULT_SELLER_ADMIN_ID, now));
    }

    /** 재시도 횟수가 있는 PENDING 상태 Entity 생성 (저장용, ID null). */
    public static SellerAdminAuthOutboxJpaEntity retriedPendingEntity(int retryCount) {
        Instant now = Instant.now();
        return SellerAdminAuthOutboxJpaEntity.create(
                null,
                DEFAULT_SELLER_ADMIN_ID,
                DEFAULT_PAYLOAD,
                SellerAdminAuthOutboxJpaEntity.Status.PENDING,
                retryCount,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                "이전 시도 실패",
                DEFAULT_VERSION,
                generateIdempotencyKey(DEFAULT_SELLER_ADMIN_ID, now));
    }

    /** PROCESSING 타임아웃 테스트용 Entity (과거 updatedAt). */
    public static SellerAdminAuthOutboxJpaEntity processingTimeoutEntity(long secondsAgo) {
        Instant now = Instant.now();
        Instant updatedAt = now.minusSeconds(secondsAgo);
        return SellerAdminAuthOutboxJpaEntity.create(
                null,
                DEFAULT_SELLER_ADMIN_ID,
                DEFAULT_PAYLOAD,
                SellerAdminAuthOutboxJpaEntity.Status.PROCESSING,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                updatedAt,
                updatedAt,
                null,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(DEFAULT_SELLER_ADMIN_ID, updatedAt));
    }

    /** 특정 생성 시각을 가진 PENDING Entity 생성 (재시도 로직 테스트용). */
    public static SellerAdminAuthOutboxJpaEntity pendingEntityWithCreatedAt(
            String sellerAdminId, Instant createdAt) {
        return SellerAdminAuthOutboxJpaEntity.create(
                null,
                sellerAdminId,
                DEFAULT_PAYLOAD,
                SellerAdminAuthOutboxJpaEntity.Status.PENDING,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                createdAt,
                createdAt,
                null,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(sellerAdminId, createdAt));
    }

    /** 최대 재시도에 도달한 PENDING Entity 생성 (재시도 불가). */
    public static SellerAdminAuthOutboxJpaEntity pendingEntityMaxRetry() {
        Instant now = Instant.now();
        return SellerAdminAuthOutboxJpaEntity.create(
                null,
                DEFAULT_SELLER_ADMIN_ID,
                DEFAULT_PAYLOAD,
                SellerAdminAuthOutboxJpaEntity.Status.PENDING,
                DEFAULT_MAX_RETRY,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                "최대 재시도 도달",
                DEFAULT_VERSION,
                generateIdempotencyKey(DEFAULT_SELLER_ADMIN_ID, now));
    }
}
