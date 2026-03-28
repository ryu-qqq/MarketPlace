package com.ryuqq.marketplace.adapter.out.persistence.refund;

import com.ryuqq.marketplace.adapter.out.persistence.refund.entity.RefundClaimJpaEntity;
import java.time.Instant;

/**
 * RefundClaimJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 RefundClaimJpaEntity 관련 객체들을 생성합니다.
 *
 * <p>참조 모델: ExchangeClaimJpaEntityFixtures
 */
public final class RefundClaimJpaEntityFixtures {

    private RefundClaimJpaEntityFixtures() {}

    // ===== 기본 상수 =====
    public static final String DEFAULT_ID = "01900000-0000-7000-0000-000000000101";
    public static final String DEFAULT_CLAIM_NUMBER = "REF-20260319-0001";
    public static final Long DEFAULT_ORDER_ITEM_ID = 1001L;
    public static final long DEFAULT_SELLER_ID = 10L;
    public static final int DEFAULT_REFUND_QTY = 1;
    public static final String DEFAULT_STATUS_REQUESTED = "REQUESTED";
    public static final String DEFAULT_STATUS_COLLECTING = "COLLECTING";
    public static final String DEFAULT_STATUS_COLLECTED = "COLLECTED";
    public static final String DEFAULT_STATUS_COMPLETED = "COMPLETED";
    public static final String DEFAULT_STATUS_REJECTED = "REJECTED";
    public static final String DEFAULT_REASON_TYPE = "CHANGE_OF_MIND";
    public static final String DEFAULT_REASON_DETAIL = "단순 변심으로 환불 요청합니다";
    public static final String DEFAULT_REQUESTED_BY = "buyer@example.com";
    public static final String DEFAULT_PROCESSED_BY = "admin@marketplace.com";
    public static final int DEFAULT_ORIGINAL_AMOUNT = 29900;
    public static final int DEFAULT_FINAL_AMOUNT = 29900;
    public static final String DEFAULT_REFUND_METHOD = "CARD";

    // ===== REQUESTED 상태 Entity =====

    /** REQUESTED 상태의 환불 클레임 Entity 생성 (기본 ID). */
    public static RefundClaimJpaEntity requestedEntity() {
        Instant now = Instant.now();
        return RefundClaimJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_CLAIM_NUMBER,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_REFUND_QTY,
                DEFAULT_STATUS_REQUESTED,
                DEFAULT_REASON_TYPE,
                DEFAULT_REASON_DETAIL,
                null,
                null,
                null,
                null,
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

    /** REQUESTED 상태 Entity 생성 (ID 지정). */
    public static RefundClaimJpaEntity requestedEntity(String id) {
        Instant now = Instant.now();
        return RefundClaimJpaEntity.create(
                id,
                DEFAULT_CLAIM_NUMBER,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_REFUND_QTY,
                DEFAULT_STATUS_REQUESTED,
                DEFAULT_REASON_TYPE,
                DEFAULT_REASON_DETAIL,
                null,
                null,
                null,
                null,
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

    /** REQUESTED 상태 Entity 생성 (ID + orderItemId + sellerId 지정). */
    public static RefundClaimJpaEntity requestedEntity(String id, Long orderItemId, long sellerId) {
        Instant now = Instant.now();
        return RefundClaimJpaEntity.create(
                id,
                DEFAULT_CLAIM_NUMBER,
                orderItemId,
                sellerId,
                DEFAULT_REFUND_QTY,
                DEFAULT_STATUS_REQUESTED,
                DEFAULT_REASON_TYPE,
                DEFAULT_REASON_DETAIL,
                null,
                null,
                null,
                null,
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

    // ===== COLLECTING 상태 Entity =====

    /** COLLECTING 상태 Entity 생성 (ID 지정). */
    public static RefundClaimJpaEntity collectingEntity(String id) {
        Instant now = Instant.now();
        Instant yesterday = now.minusSeconds(86400);
        return RefundClaimJpaEntity.create(
                id,
                DEFAULT_CLAIM_NUMBER,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_REFUND_QTY,
                DEFAULT_STATUS_COLLECTING,
                DEFAULT_REASON_TYPE,
                DEFAULT_REASON_DETAIL,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                DEFAULT_REQUESTED_BY,
                DEFAULT_PROCESSED_BY,
                yesterday,
                now,
                null,
                yesterday,
                now);
    }

    // ===== COLLECTED 상태 Entity =====

    /** COLLECTED 상태 Entity 생성 (ID 지정). */
    public static RefundClaimJpaEntity collectedEntity(String id) {
        Instant now = Instant.now();
        Instant yesterday = now.minusSeconds(86400);
        return RefundClaimJpaEntity.create(
                id,
                DEFAULT_CLAIM_NUMBER,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_REFUND_QTY,
                DEFAULT_STATUS_COLLECTED,
                DEFAULT_REASON_TYPE,
                DEFAULT_REASON_DETAIL,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                DEFAULT_REQUESTED_BY,
                DEFAULT_PROCESSED_BY,
                yesterday,
                yesterday,
                null,
                yesterday,
                now);
    }

    // ===== COMPLETED 상태 Entity =====

    /** COMPLETED 상태 Entity 생성 (ID 지정). */
    public static RefundClaimJpaEntity completedEntity(String id) {
        Instant now = Instant.now();
        Instant yesterday = now.minusSeconds(86400);
        return RefundClaimJpaEntity.create(
                id,
                DEFAULT_CLAIM_NUMBER,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_REFUND_QTY,
                DEFAULT_STATUS_COMPLETED,
                DEFAULT_REASON_TYPE,
                DEFAULT_REASON_DETAIL,
                DEFAULT_ORIGINAL_AMOUNT,
                DEFAULT_FINAL_AMOUNT,
                0,
                null,
                DEFAULT_REFUND_METHOD,
                yesterday,
                null,
                null,
                null,
                DEFAULT_REQUESTED_BY,
                DEFAULT_PROCESSED_BY,
                yesterday,
                yesterday,
                now,
                yesterday,
                now);
    }

    // ===== REJECTED 상태 Entity =====

    /** REJECTED 상태 Entity 생성 (ID 지정). */
    public static RefundClaimJpaEntity rejectedEntity(String id) {
        Instant now = Instant.now();
        Instant yesterday = now.minusSeconds(86400);
        return RefundClaimJpaEntity.create(
                id,
                DEFAULT_CLAIM_NUMBER,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_REFUND_QTY,
                DEFAULT_STATUS_REJECTED,
                DEFAULT_REASON_TYPE,
                DEFAULT_REASON_DETAIL,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                DEFAULT_REQUESTED_BY,
                DEFAULT_PROCESSED_BY,
                yesterday,
                now,
                null,
                yesterday,
                now);
    }

    // ===== 보류(Hold) 상태 Entity =====

    /** 보류 상태 Entity 생성 (holdReason + holdAt 설정). */
    public static RefundClaimJpaEntity heldEntity(String id, String holdReason) {
        Instant now = Instant.now();
        return RefundClaimJpaEntity.create(
                id,
                DEFAULT_CLAIM_NUMBER,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_REFUND_QTY,
                DEFAULT_STATUS_REQUESTED,
                DEFAULT_REASON_TYPE,
                DEFAULT_REASON_DETAIL,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                holdReason,
                now.minusSeconds(600),
                DEFAULT_REQUESTED_BY,
                null,
                now.minusSeconds(3600),
                null,
                null,
                now,
                now);
    }

    // ===== 범용 생성 메서드 =====

    /** 특정 상태를 가진 Entity 생성 (ID + status 지정). */
    public static RefundClaimJpaEntity entityWithStatus(String id, String status) {
        Instant now = Instant.now();
        return RefundClaimJpaEntity.create(
                id,
                DEFAULT_CLAIM_NUMBER,
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_REFUND_QTY,
                status,
                DEFAULT_REASON_TYPE,
                DEFAULT_REASON_DETAIL,
                null,
                null,
                null,
                null,
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

    /** 소유권 테스트용 Entity 생성 (ID + sellerId 지정). */
    public static RefundClaimJpaEntity entityWithSellerId(String id, long sellerId) {
        Instant now = Instant.now();
        return RefundClaimJpaEntity.create(
                id,
                DEFAULT_CLAIM_NUMBER,
                DEFAULT_ORDER_ITEM_ID,
                sellerId,
                DEFAULT_REFUND_QTY,
                DEFAULT_STATUS_REQUESTED,
                DEFAULT_REASON_TYPE,
                DEFAULT_REASON_DETAIL,
                null,
                null,
                null,
                null,
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
