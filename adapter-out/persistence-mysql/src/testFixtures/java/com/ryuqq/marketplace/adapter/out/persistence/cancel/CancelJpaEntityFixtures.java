package com.ryuqq.marketplace.adapter.out.persistence.cancel;

import com.ryuqq.marketplace.adapter.out.persistence.cancel.entity.CancelJpaEntity;
import java.time.Instant;

/**
 * CancelJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 CancelJpaEntity 관련 객체들을 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class CancelJpaEntityFixtures {

    private CancelJpaEntityFixtures() {}

    // ===== 기본 상수 =====
    public static final String DEFAULT_ID = "01900000-0000-7000-0000-000000000001";
    public static final String DEFAULT_CANCEL_NUMBER = "CAN-20260319-0001";
    public static final String DEFAULT_ORDER_ITEM_ID = "01900000-0000-7000-0000-000000000010";
    public static final long DEFAULT_SELLER_ID = 10L;
    public static final int DEFAULT_CANCEL_QTY = 1;
    public static final String DEFAULT_CANCEL_TYPE_BUYER = "BUYER_CANCEL";
    public static final String DEFAULT_CANCEL_TYPE_SELLER = "SELLER_CANCEL";
    public static final String DEFAULT_STATUS_REQUESTED = "REQUESTED";
    public static final String DEFAULT_STATUS_APPROVED = "APPROVED";
    public static final String DEFAULT_STATUS_REJECTED = "REJECTED";
    public static final String DEFAULT_REASON_TYPE = "OUT_OF_STOCK";
    public static final String DEFAULT_REASON_DETAIL = "재고 소진으로 취소 처리됩니다";
    public static final String DEFAULT_REQUESTED_BY = "buyer@example.com";
    public static final String DEFAULT_PROCESSED_BY = "admin@marketplace.com";

    // ===== Entity Fixtures =====

    /** BUYER_CANCEL / REQUESTED 상태 Entity 생성. */
    public static CancelJpaEntity requestedEntity(
            String cancelId, String orderItemId, long sellerId) {
        Instant now = Instant.now();
        return CancelJpaEntity.create(
                cancelId,
                "CAN-" + cancelId,
                orderItemId,
                sellerId,
                DEFAULT_CANCEL_QTY,
                DEFAULT_CANCEL_TYPE_BUYER,
                DEFAULT_STATUS_REQUESTED,
                DEFAULT_REASON_TYPE,
                DEFAULT_REASON_DETAIL,
                null,
                null,
                null,
                null,
                null,
                DEFAULT_REQUESTED_BY,
                null,
                now.minusSeconds(3600),
                null,
                null,
                now,
                now);
    }

    /** SELLER_CANCEL / APPROVED 상태 Entity 생성. */
    public static CancelJpaEntity approvedEntity(
            String cancelId, String orderItemId, long sellerId) {
        Instant now = Instant.now();
        return CancelJpaEntity.create(
                cancelId,
                "CAN-" + cancelId,
                orderItemId,
                sellerId,
                DEFAULT_CANCEL_QTY,
                DEFAULT_CANCEL_TYPE_SELLER,
                DEFAULT_STATUS_APPROVED,
                DEFAULT_REASON_TYPE,
                DEFAULT_REASON_DETAIL,
                null,
                null,
                null,
                null,
                null,
                DEFAULT_REQUESTED_BY,
                DEFAULT_PROCESSED_BY,
                now.minusSeconds(3600),
                now.minusSeconds(1800),
                null,
                now,
                now);
    }

    /** BUYER_CANCEL / REJECTED 상태 Entity 생성. */
    public static CancelJpaEntity rejectedEntity(
            String cancelId, String orderItemId, long sellerId) {
        Instant now = Instant.now();
        return CancelJpaEntity.create(
                cancelId,
                "CAN-" + cancelId,
                orderItemId,
                sellerId,
                DEFAULT_CANCEL_QTY,
                DEFAULT_CANCEL_TYPE_BUYER,
                DEFAULT_STATUS_REJECTED,
                DEFAULT_REASON_TYPE,
                DEFAULT_REASON_DETAIL,
                null,
                null,
                null,
                null,
                null,
                DEFAULT_REQUESTED_BY,
                DEFAULT_PROCESSED_BY,
                now.minusSeconds(3600),
                now.minusSeconds(1800),
                null,
                now,
                now);
    }

    /** 상태를 직접 지정하는 범용 Entity 생성. */
    public static CancelJpaEntity entityWithStatus(
            String cancelId, String orderItemId, String status) {
        Instant now = Instant.now();
        return CancelJpaEntity.create(
                cancelId,
                "CAN-" + cancelId,
                orderItemId,
                DEFAULT_SELLER_ID,
                DEFAULT_CANCEL_QTY,
                DEFAULT_CANCEL_TYPE_BUYER,
                status,
                DEFAULT_REASON_TYPE,
                DEFAULT_REASON_DETAIL,
                null,
                null,
                null,
                null,
                null,
                DEFAULT_REQUESTED_BY,
                null,
                now.minusSeconds(3600),
                null,
                null,
                now,
                now);
    }

    /** SELLER_CANCEL / APPROVED 상태 + cancelQty 지정 Entity 생성. 부분취소 테스트용. */
    public static CancelJpaEntity approvedEntityWithQty(
            String cancelId,
            String cancelNumber,
            String orderItemId,
            long sellerId,
            int cancelQty,
            Integer refundAmount) {
        Instant now = Instant.now();
        return CancelJpaEntity.create(
                cancelId,
                cancelNumber,
                orderItemId,
                sellerId,
                cancelQty,
                DEFAULT_CANCEL_TYPE_SELLER,
                DEFAULT_STATUS_APPROVED,
                DEFAULT_REASON_TYPE,
                DEFAULT_REASON_DETAIL,
                refundAmount,
                null,
                null,
                null,
                null,
                DEFAULT_REQUESTED_BY,
                DEFAULT_PROCESSED_BY,
                now.minusSeconds(3600),
                now.minusSeconds(1800),
                null,
                now,
                now);
    }

    /** cancelNumber를 지정하는 REQUESTED Entity 생성 (unique 제약 회피용). */
    public static CancelJpaEntity requestedEntityWithNumber(
            String cancelId, String cancelNumber, String orderItemId, long sellerId) {
        Instant now = Instant.now();
        return CancelJpaEntity.create(
                cancelId,
                cancelNumber,
                orderItemId,
                sellerId,
                DEFAULT_CANCEL_QTY,
                DEFAULT_CANCEL_TYPE_BUYER,
                DEFAULT_STATUS_REQUESTED,
                DEFAULT_REASON_TYPE,
                DEFAULT_REASON_DETAIL,
                null,
                null,
                null,
                null,
                null,
                DEFAULT_REQUESTED_BY,
                null,
                now.minusSeconds(3600),
                null,
                null,
                now,
                now);
    }
}
