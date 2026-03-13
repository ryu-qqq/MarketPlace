package com.ryuqq.marketplace.adapter.out.persistence.outboundsync;

import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.entity.OutboundSyncOutboxJpaEntity;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * OutboundSyncOutboxJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 OutboundSyncOutboxJpaEntity 관련 객체들을 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class OutboundSyncOutboxJpaEntityFixtures {

    private OutboundSyncOutboxJpaEntityFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);
    private static final String IDEMPOTENCY_KEY_PREFIX = "EPSO";

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final Long DEFAULT_PRODUCT_GROUP_ID = 100L;
    public static final Long DEFAULT_SALES_CHANNEL_ID = 10L;
    public static final Long DEFAULT_SHOP_ID = 1L;
    public static final Long DEFAULT_SELLER_ID = 1L;
    public static final String DEFAULT_PAYLOAD =
            "{\"productGroupId\":100,\"salesChannelId\":10,\"syncType\":\"CREATE\"}";
    public static final int DEFAULT_RETRY_COUNT = 0;
    public static final int DEFAULT_MAX_RETRY = 3;
    public static final long DEFAULT_VERSION = 0L;

    private static String generateIdempotencyKey(
            Long productGroupId,
            Long salesChannelId,
            OutboundSyncOutboxJpaEntity.SyncType syncType,
            Instant createdAt) {
        return IDEMPOTENCY_KEY_PREFIX
                + ":"
                + productGroupId
                + ":"
                + salesChannelId
                + ":"
                + syncType.name()
                + ":"
                + createdAt.toEpochMilli();
    }

    // ===== PENDING 상태 Fixtures =====

    /** PENDING 상태 Entity (단위 테스트용, DEFAULT_ID). */
    public static OutboundSyncOutboxJpaEntity pendingEntity() {
        Instant now = Instant.now();
        return OutboundSyncOutboxJpaEntity.of(
                DEFAULT_ID,
                DEFAULT_PRODUCT_GROUP_ID,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_SHOP_ID,
                DEFAULT_SELLER_ID,
                OutboundSyncOutboxJpaEntity.SyncType.CREATE,
                OutboundSyncOutboxJpaEntity.Status.PENDING,
                DEFAULT_PAYLOAD,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(
                        DEFAULT_PRODUCT_GROUP_ID,
                        DEFAULT_SALES_CHANNEL_ID,
                        OutboundSyncOutboxJpaEntity.SyncType.CREATE,
                        now));
    }

    /** ID 없이 PENDING 상태의 새 Entity (저장용). */
    public static OutboundSyncOutboxJpaEntity newPendingEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        long productGroupId = DEFAULT_PRODUCT_GROUP_ID + seq;
        return OutboundSyncOutboxJpaEntity.of(
                null,
                productGroupId,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_SHOP_ID,
                DEFAULT_SELLER_ID,
                OutboundSyncOutboxJpaEntity.SyncType.CREATE,
                OutboundSyncOutboxJpaEntity.Status.PENDING,
                DEFAULT_PAYLOAD,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(
                        productGroupId,
                        DEFAULT_SALES_CHANNEL_ID,
                        OutboundSyncOutboxJpaEntity.SyncType.CREATE,
                        now));
    }

    /** 상품그룹 ID와 판매채널 ID를 지정한 PENDING 상태 Entity (저장용, ID null). */
    public static OutboundSyncOutboxJpaEntity newPendingEntityWith(
            Long productGroupId, Long salesChannelId) {
        Instant now = Instant.now();
        return OutboundSyncOutboxJpaEntity.of(
                null,
                productGroupId,
                salesChannelId,
                DEFAULT_SHOP_ID,
                DEFAULT_SELLER_ID,
                OutboundSyncOutboxJpaEntity.SyncType.CREATE,
                OutboundSyncOutboxJpaEntity.Status.PENDING,
                DEFAULT_PAYLOAD,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(
                        productGroupId,
                        salesChannelId,
                        OutboundSyncOutboxJpaEntity.SyncType.CREATE,
                        now));
    }

    /** UPDATE SyncType의 PENDING 상태 Entity (단위 테스트용, DEFAULT_ID). */
    public static OutboundSyncOutboxJpaEntity pendingUpdateEntity() {
        Instant now = Instant.now();
        return OutboundSyncOutboxJpaEntity.of(
                DEFAULT_ID,
                DEFAULT_PRODUCT_GROUP_ID,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_SHOP_ID,
                DEFAULT_SELLER_ID,
                OutboundSyncOutboxJpaEntity.SyncType.UPDATE,
                OutboundSyncOutboxJpaEntity.Status.PENDING,
                DEFAULT_PAYLOAD,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(
                        DEFAULT_PRODUCT_GROUP_ID,
                        DEFAULT_SALES_CHANNEL_ID,
                        OutboundSyncOutboxJpaEntity.SyncType.UPDATE,
                        now));
    }

    /** DELETE SyncType의 PENDING 상태 Entity (단위 테스트용, DEFAULT_ID). */
    public static OutboundSyncOutboxJpaEntity pendingDeleteEntity() {
        Instant now = Instant.now();
        return OutboundSyncOutboxJpaEntity.of(
                DEFAULT_ID,
                DEFAULT_PRODUCT_GROUP_ID,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_SHOP_ID,
                DEFAULT_SELLER_ID,
                OutboundSyncOutboxJpaEntity.SyncType.DELETE,
                OutboundSyncOutboxJpaEntity.Status.PENDING,
                DEFAULT_PAYLOAD,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(
                        DEFAULT_PRODUCT_GROUP_ID,
                        DEFAULT_SALES_CHANNEL_ID,
                        OutboundSyncOutboxJpaEntity.SyncType.DELETE,
                        now));
    }

    // ===== PROCESSING 상태 Fixtures =====

    /** PROCESSING 상태 Entity (단위 테스트용, DEFAULT_ID). */
    public static OutboundSyncOutboxJpaEntity processingEntity() {
        Instant now = Instant.now();
        return OutboundSyncOutboxJpaEntity.of(
                DEFAULT_ID,
                DEFAULT_PRODUCT_GROUP_ID,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_SHOP_ID,
                DEFAULT_SELLER_ID,
                OutboundSyncOutboxJpaEntity.SyncType.CREATE,
                OutboundSyncOutboxJpaEntity.Status.PROCESSING,
                DEFAULT_PAYLOAD,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(
                        DEFAULT_PRODUCT_GROUP_ID,
                        DEFAULT_SALES_CHANNEL_ID,
                        OutboundSyncOutboxJpaEntity.SyncType.CREATE,
                        now));
    }

    /** PROCESSING 상태의 새 Entity (통합 테스트용, ID null). */
    public static OutboundSyncOutboxJpaEntity newProcessingEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        long productGroupId = DEFAULT_PRODUCT_GROUP_ID + seq;
        return OutboundSyncOutboxJpaEntity.of(
                null,
                productGroupId,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_SHOP_ID,
                DEFAULT_SELLER_ID,
                OutboundSyncOutboxJpaEntity.SyncType.CREATE,
                OutboundSyncOutboxJpaEntity.Status.PROCESSING,
                DEFAULT_PAYLOAD,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(
                        productGroupId,
                        DEFAULT_SALES_CHANNEL_ID,
                        OutboundSyncOutboxJpaEntity.SyncType.CREATE,
                        now));
    }

    /** PROCESSING 타임아웃 테스트용 Entity (과거 updatedAt, ID null). */
    public static OutboundSyncOutboxJpaEntity processingTimeoutEntity(long secondsAgo) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        Instant past = now.minusSeconds(secondsAgo);
        long productGroupId = DEFAULT_PRODUCT_GROUP_ID + seq;
        return OutboundSyncOutboxJpaEntity.of(
                null,
                productGroupId,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_SHOP_ID,
                DEFAULT_SELLER_ID,
                OutboundSyncOutboxJpaEntity.SyncType.CREATE,
                OutboundSyncOutboxJpaEntity.Status.PROCESSING,
                DEFAULT_PAYLOAD,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                past,
                past,
                null,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(
                        productGroupId,
                        DEFAULT_SALES_CHANNEL_ID,
                        OutboundSyncOutboxJpaEntity.SyncType.CREATE,
                        past));
    }

    // ===== COMPLETED 상태 Fixtures =====

    /** COMPLETED 상태 Entity (단위 테스트용, DEFAULT_ID). */
    public static OutboundSyncOutboxJpaEntity completedEntity() {
        Instant now = Instant.now();
        return OutboundSyncOutboxJpaEntity.of(
                DEFAULT_ID,
                DEFAULT_PRODUCT_GROUP_ID,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_SHOP_ID,
                DEFAULT_SELLER_ID,
                OutboundSyncOutboxJpaEntity.SyncType.CREATE,
                OutboundSyncOutboxJpaEntity.Status.COMPLETED,
                DEFAULT_PAYLOAD,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                now,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(
                        DEFAULT_PRODUCT_GROUP_ID,
                        DEFAULT_SALES_CHANNEL_ID,
                        OutboundSyncOutboxJpaEntity.SyncType.CREATE,
                        now));
    }

    /** COMPLETED 상태의 새 Entity (통합 테스트용, ID null). */
    public static OutboundSyncOutboxJpaEntity newCompletedEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        long productGroupId = DEFAULT_PRODUCT_GROUP_ID + seq;
        return OutboundSyncOutboxJpaEntity.of(
                null,
                productGroupId,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_SHOP_ID,
                DEFAULT_SELLER_ID,
                OutboundSyncOutboxJpaEntity.SyncType.CREATE,
                OutboundSyncOutboxJpaEntity.Status.COMPLETED,
                DEFAULT_PAYLOAD,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                now,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(
                        productGroupId,
                        DEFAULT_SALES_CHANNEL_ID,
                        OutboundSyncOutboxJpaEntity.SyncType.CREATE,
                        now));
    }

    // ===== FAILED 상태 Fixtures =====

    /** FAILED 상태 Entity (단위 테스트용, DEFAULT_ID). */
    public static OutboundSyncOutboxJpaEntity failedEntity() {
        Instant now = Instant.now();
        return OutboundSyncOutboxJpaEntity.of(
                DEFAULT_ID,
                DEFAULT_PRODUCT_GROUP_ID,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_SHOP_ID,
                DEFAULT_SELLER_ID,
                OutboundSyncOutboxJpaEntity.SyncType.CREATE,
                OutboundSyncOutboxJpaEntity.Status.FAILED,
                DEFAULT_PAYLOAD,
                DEFAULT_MAX_RETRY,
                DEFAULT_MAX_RETRY,
                now,
                now,
                now,
                "외부 채널 연동 최대 재시도 초과",
                DEFAULT_VERSION,
                generateIdempotencyKey(
                        DEFAULT_PRODUCT_GROUP_ID,
                        DEFAULT_SALES_CHANNEL_ID,
                        OutboundSyncOutboxJpaEntity.SyncType.CREATE,
                        now));
    }

    /** FAILED 상태의 새 Entity (통합 테스트용, ID null). */
    public static OutboundSyncOutboxJpaEntity newFailedEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        long productGroupId = DEFAULT_PRODUCT_GROUP_ID + seq;
        return OutboundSyncOutboxJpaEntity.of(
                null,
                productGroupId,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_SHOP_ID,
                DEFAULT_SELLER_ID,
                OutboundSyncOutboxJpaEntity.SyncType.CREATE,
                OutboundSyncOutboxJpaEntity.Status.FAILED,
                DEFAULT_PAYLOAD,
                DEFAULT_MAX_RETRY,
                DEFAULT_MAX_RETRY,
                now,
                now,
                now,
                "외부 채널 연동 최대 재시도 초과",
                DEFAULT_VERSION,
                generateIdempotencyKey(
                        productGroupId,
                        DEFAULT_SALES_CHANNEL_ID,
                        OutboundSyncOutboxJpaEntity.SyncType.CREATE,
                        now));
    }
}
