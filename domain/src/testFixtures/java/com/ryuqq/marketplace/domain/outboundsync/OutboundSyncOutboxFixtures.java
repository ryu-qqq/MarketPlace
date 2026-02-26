package com.ryuqq.marketplace.domain.outboundsync;

import com.ryuqq.marketplace.domain.outboundsync.aggregate.OutboundSyncOutbox;
import com.ryuqq.marketplace.domain.outboundsync.id.OutboundSyncOutboxId;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncStatus;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncType;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import java.time.Instant;

/**
 * OutboundSyncOutbox 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 OutboundSyncOutbox 관련 객체들을 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class OutboundSyncOutboxFixtures {

    private OutboundSyncOutboxFixtures() {}

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final Long DEFAULT_PRODUCT_GROUP_ID = 100L;
    public static final Long DEFAULT_SALES_CHANNEL_ID = 10L;
    public static final Long DEFAULT_SELLER_ID = 1L;
    public static final String DEFAULT_PAYLOAD =
            "{\"productGroupId\":100,\"salesChannelId\":10,\"syncType\":\"CREATE\"}";
    public static final int DEFAULT_RETRY_COUNT = 0;
    public static final int DEFAULT_MAX_RETRY = 3;
    public static final long DEFAULT_VERSION = 0L;

    // ===== PENDING 상태 Fixtures =====

    /** ID 없이 PENDING 상태의 새 OutboundSyncOutbox (forNew 패턴). */
    public static OutboundSyncOutbox newPendingOutbox() {
        return OutboundSyncOutbox.forNew(
                ProductGroupId.of(DEFAULT_PRODUCT_GROUP_ID),
                SalesChannelId.of(DEFAULT_SALES_CHANNEL_ID),
                SellerId.of(DEFAULT_SELLER_ID),
                SyncType.CREATE,
                DEFAULT_PAYLOAD,
                Instant.now());
    }

    /** ID 없이 UPDATE 타입의 PENDING 상태 OutboundSyncOutbox. */
    public static OutboundSyncOutbox newPendingUpdateOutbox() {
        return OutboundSyncOutbox.forNew(
                ProductGroupId.of(DEFAULT_PRODUCT_GROUP_ID),
                SalesChannelId.of(DEFAULT_SALES_CHANNEL_ID),
                SellerId.of(DEFAULT_SELLER_ID),
                SyncType.UPDATE,
                DEFAULT_PAYLOAD,
                Instant.now());
    }

    /** ID 없이 DELETE 타입의 PENDING 상태 OutboundSyncOutbox. */
    public static OutboundSyncOutbox newPendingDeleteOutbox() {
        return OutboundSyncOutbox.forNew(
                ProductGroupId.of(DEFAULT_PRODUCT_GROUP_ID),
                SalesChannelId.of(DEFAULT_SALES_CHANNEL_ID),
                SellerId.of(DEFAULT_SELLER_ID),
                SyncType.DELETE,
                DEFAULT_PAYLOAD,
                Instant.now());
    }

    /** DB에서 복원된 PENDING 상태 OutboundSyncOutbox (ID 있음). */
    public static OutboundSyncOutbox pendingOutbox() {
        Instant now = Instant.now();
        return OutboundSyncOutbox.reconstitute(
                OutboundSyncOutboxId.of(DEFAULT_ID),
                ProductGroupId.of(DEFAULT_PRODUCT_GROUP_ID),
                SalesChannelId.of(DEFAULT_SALES_CHANNEL_ID),
                SellerId.of(DEFAULT_SELLER_ID),
                SyncType.CREATE,
                SyncStatus.PENDING,
                DEFAULT_PAYLOAD,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(
                        DEFAULT_PRODUCT_GROUP_ID, DEFAULT_SALES_CHANNEL_ID, SyncType.CREATE, now));
    }

    /** 상품그룹 ID와 판매채널 ID를 지정한 PENDING 상태 OutboundSyncOutbox. */
    public static OutboundSyncOutbox pendingOutbox(Long productGroupId, Long salesChannelId) {
        Instant now = Instant.now();
        return OutboundSyncOutbox.reconstitute(
                OutboundSyncOutboxId.of(DEFAULT_ID),
                ProductGroupId.of(productGroupId),
                SalesChannelId.of(salesChannelId),
                SellerId.of(DEFAULT_SELLER_ID),
                SyncType.CREATE,
                SyncStatus.PENDING,
                DEFAULT_PAYLOAD,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(productGroupId, salesChannelId, SyncType.CREATE, now));
    }

    // ===== PROCESSING 상태 Fixtures =====

    /** DB에서 복원된 PROCESSING 상태 OutboundSyncOutbox (ID 있음). */
    public static OutboundSyncOutbox processingOutbox() {
        Instant now = Instant.now();
        return OutboundSyncOutbox.reconstitute(
                OutboundSyncOutboxId.of(DEFAULT_ID),
                ProductGroupId.of(DEFAULT_PRODUCT_GROUP_ID),
                SalesChannelId.of(DEFAULT_SALES_CHANNEL_ID),
                SellerId.of(DEFAULT_SELLER_ID),
                SyncType.CREATE,
                SyncStatus.PROCESSING,
                DEFAULT_PAYLOAD,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                null,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(
                        DEFAULT_PRODUCT_GROUP_ID, DEFAULT_SALES_CHANNEL_ID, SyncType.CREATE, now));
    }

    /** 타임아웃 테스트용 PROCESSING 상태 OutboundSyncOutbox (과거 updatedAt). */
    public static OutboundSyncOutbox processingTimeoutOutbox(long secondsAgo) {
        Instant now = Instant.now();
        Instant past = now.minusSeconds(secondsAgo);
        return OutboundSyncOutbox.reconstitute(
                OutboundSyncOutboxId.of(DEFAULT_ID),
                ProductGroupId.of(DEFAULT_PRODUCT_GROUP_ID),
                SalesChannelId.of(DEFAULT_SALES_CHANNEL_ID),
                SellerId.of(DEFAULT_SELLER_ID),
                SyncType.CREATE,
                SyncStatus.PROCESSING,
                DEFAULT_PAYLOAD,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                past,
                past,
                null,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(
                        DEFAULT_PRODUCT_GROUP_ID, DEFAULT_SALES_CHANNEL_ID, SyncType.CREATE, past));
    }

    // ===== COMPLETED 상태 Fixtures =====

    /** DB에서 복원된 COMPLETED 상태 OutboundSyncOutbox (ID 있음). */
    public static OutboundSyncOutbox completedOutbox() {
        Instant now = Instant.now();
        return OutboundSyncOutbox.reconstitute(
                OutboundSyncOutboxId.of(DEFAULT_ID),
                ProductGroupId.of(DEFAULT_PRODUCT_GROUP_ID),
                SalesChannelId.of(DEFAULT_SALES_CHANNEL_ID),
                SellerId.of(DEFAULT_SELLER_ID),
                SyncType.CREATE,
                SyncStatus.COMPLETED,
                DEFAULT_PAYLOAD,
                DEFAULT_RETRY_COUNT,
                DEFAULT_MAX_RETRY,
                now,
                now,
                now,
                null,
                DEFAULT_VERSION,
                generateIdempotencyKey(
                        DEFAULT_PRODUCT_GROUP_ID, DEFAULT_SALES_CHANNEL_ID, SyncType.CREATE, now));
    }

    // ===== FAILED 상태 Fixtures =====

    /** DB에서 복원된 FAILED 상태 OutboundSyncOutbox (ID 있음). */
    public static OutboundSyncOutbox failedOutbox() {
        Instant now = Instant.now();
        return OutboundSyncOutbox.reconstitute(
                OutboundSyncOutboxId.of(DEFAULT_ID),
                ProductGroupId.of(DEFAULT_PRODUCT_GROUP_ID),
                SalesChannelId.of(DEFAULT_SALES_CHANNEL_ID),
                SellerId.of(DEFAULT_SELLER_ID),
                SyncType.CREATE,
                SyncStatus.FAILED,
                DEFAULT_PAYLOAD,
                DEFAULT_MAX_RETRY,
                DEFAULT_MAX_RETRY,
                now,
                now,
                now,
                "외부 채널 연동 최대 재시도 초과",
                DEFAULT_VERSION,
                generateIdempotencyKey(
                        DEFAULT_PRODUCT_GROUP_ID, DEFAULT_SALES_CHANNEL_ID, SyncType.CREATE, now));
    }

    // ===== 유틸리티 메서드 =====

    private static String generateIdempotencyKey(
            Long productGroupId, Long salesChannelId, SyncType syncType, Instant createdAt) {
        return "EPSO:"
                + productGroupId
                + ":"
                + salesChannelId
                + ":"
                + syncType.name()
                + ":"
                + createdAt.toEpochMilli();
    }
}
