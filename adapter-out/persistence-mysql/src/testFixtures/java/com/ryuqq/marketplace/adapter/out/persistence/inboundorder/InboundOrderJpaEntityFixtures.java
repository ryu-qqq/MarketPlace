package com.ryuqq.marketplace.adapter.out.persistence.inboundorder;

import com.ryuqq.marketplace.adapter.out.persistence.inboundorder.entity.InboundOrderJpaEntity;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * InboundOrderJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 InboundOrderJpaEntity 관련 객체들을 생성합니다.
 */
public final class InboundOrderJpaEntityFixtures {

    private InboundOrderJpaEntityFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    public static final long DEFAULT_SALES_CHANNEL_ID = 100L;
    public static final long DEFAULT_SHOP_ID = 0L;
    public static final long DEFAULT_SELLER_ID = 1L;
    public static final String DEFAULT_EXTERNAL_ORDER_NO = "NAVER-ORD-001";
    public static final String DEFAULT_BUYER_NAME = "홍길동";
    public static final String DEFAULT_BUYER_EMAIL = "buyer@example.com";
    public static final String DEFAULT_BUYER_PHONE = "010-1234-5678";
    public static final String DEFAULT_PAYMENT_METHOD = "CARD";
    public static final int DEFAULT_TOTAL_PAYMENT_AMOUNT = 50000;
    public static final String DEFAULT_INTERNAL_ORDER_ID = "order-uuid-001";

    /** RECEIVED 상태의 신규 수신 Entity 생성 (ID null). */
    public static InboundOrderJpaEntity receivedEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return InboundOrderJpaEntity.create(
                null,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_SHOP_ID,
                DEFAULT_SELLER_ID,
                "NAVER-ORD-" + seq,
                now,
                DEFAULT_BUYER_NAME,
                DEFAULT_BUYER_EMAIL,
                DEFAULT_BUYER_PHONE,
                DEFAULT_PAYMENT_METHOD,
                DEFAULT_TOTAL_PAYMENT_AMOUNT,
                now,
                InboundOrderJpaEntity.Status.RECEIVED,
                null,
                null,
                now,
                now);
    }

    /** ID를 지정한 RECEIVED 상태 Entity 생성. */
    public static InboundOrderJpaEntity receivedEntity(Long id) {
        Instant now = Instant.now();
        return InboundOrderJpaEntity.create(
                id,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_SHOP_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_EXTERNAL_ORDER_NO,
                now,
                DEFAULT_BUYER_NAME,
                DEFAULT_BUYER_EMAIL,
                DEFAULT_BUYER_PHONE,
                DEFAULT_PAYMENT_METHOD,
                DEFAULT_TOTAL_PAYMENT_AMOUNT,
                now,
                InboundOrderJpaEntity.Status.RECEIVED,
                null,
                null,
                now,
                now);
    }

    /** PENDING_MAPPING 상태 Entity 생성. */
    public static InboundOrderJpaEntity pendingMappingEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return InboundOrderJpaEntity.create(
                null,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_SHOP_ID,
                DEFAULT_SELLER_ID,
                "NAVER-ORD-PENDING-" + seq,
                now,
                DEFAULT_BUYER_NAME,
                DEFAULT_BUYER_EMAIL,
                DEFAULT_BUYER_PHONE,
                DEFAULT_PAYMENT_METHOD,
                DEFAULT_TOTAL_PAYMENT_AMOUNT,
                now,
                InboundOrderJpaEntity.Status.PENDING_MAPPING,
                null,
                null,
                now,
                now);
    }

    /** MAPPED 상태 Entity 생성. */
    public static InboundOrderJpaEntity mappedEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return InboundOrderJpaEntity.create(
                null,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_SHOP_ID,
                DEFAULT_SELLER_ID,
                "NAVER-ORD-MAPPED-" + seq,
                now,
                DEFAULT_BUYER_NAME,
                DEFAULT_BUYER_EMAIL,
                DEFAULT_BUYER_PHONE,
                DEFAULT_PAYMENT_METHOD,
                DEFAULT_TOTAL_PAYMENT_AMOUNT,
                now,
                InboundOrderJpaEntity.Status.MAPPED,
                null,
                null,
                now,
                now);
    }

    /** CONVERTED 상태 Entity 생성. */
    public static InboundOrderJpaEntity convertedEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return InboundOrderJpaEntity.create(
                null,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_SHOP_ID,
                DEFAULT_SELLER_ID,
                "NAVER-ORD-CONVERTED-" + seq,
                now,
                DEFAULT_BUYER_NAME,
                DEFAULT_BUYER_EMAIL,
                DEFAULT_BUYER_PHONE,
                DEFAULT_PAYMENT_METHOD,
                DEFAULT_TOTAL_PAYMENT_AMOUNT,
                now,
                InboundOrderJpaEntity.Status.CONVERTED,
                DEFAULT_INTERNAL_ORDER_ID,
                null,
                now,
                now);
    }

    /** 단위 테스트용 기본 Entity. */
    public static InboundOrderJpaEntity entity() {
        Instant now = Instant.now();
        return InboundOrderJpaEntity.create(
                1L,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_SHOP_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_EXTERNAL_ORDER_NO,
                now,
                DEFAULT_BUYER_NAME,
                DEFAULT_BUYER_EMAIL,
                DEFAULT_BUYER_PHONE,
                DEFAULT_PAYMENT_METHOD,
                DEFAULT_TOTAL_PAYMENT_AMOUNT,
                now,
                InboundOrderJpaEntity.Status.RECEIVED,
                null,
                null,
                now,
                now);
    }
}
