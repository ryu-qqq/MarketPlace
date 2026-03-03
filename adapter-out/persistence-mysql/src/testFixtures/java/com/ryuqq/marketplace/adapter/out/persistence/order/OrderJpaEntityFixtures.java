package com.ryuqq.marketplace.adapter.out.persistence.order;

import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderJpaEntity;
import java.time.Instant;

/** OrderJpaEntity 테스트 Fixtures. */
public final class OrderJpaEntityFixtures {

    private OrderJpaEntityFixtures() {}

    public static final String DEFAULT_ID = "01944b2a-1234-7fff-8888-abcdef012345";
    public static final String DEFAULT_ORDER_NUMBER = "ORD-20260302-0001";
    public static final String DEFAULT_STATUS = "ORDERED";
    public static final String DEFAULT_BUYER_NAME = "홍길동";
    public static final String DEFAULT_BUYER_EMAIL = "buyer@example.com";
    public static final String DEFAULT_BUYER_PHONE = "010-1234-5678";
    public static final long DEFAULT_SALES_CHANNEL_ID = 1L;
    public static final long DEFAULT_SHOP_ID = 100L;
    public static final String DEFAULT_EXTERNAL_ORDER_NO = "EXT-ORD-001";

    /** ORDERED 상태의 주문 Entity 생성. */
    public static OrderJpaEntity orderedEntity() {
        Instant now = Instant.now();
        return OrderJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_ORDER_NUMBER,
                DEFAULT_STATUS,
                DEFAULT_BUYER_NAME,
                DEFAULT_BUYER_EMAIL,
                DEFAULT_BUYER_PHONE,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_SHOP_ID,
                DEFAULT_EXTERNAL_ORDER_NO,
                now,
                now,
                now,
                null);
    }

    /** ORDERED 상태의 주문 Entity 생성 (ID 지정). */
    public static OrderJpaEntity orderedEntity(String id) {
        Instant now = Instant.now();
        return OrderJpaEntity.create(
                id,
                DEFAULT_ORDER_NUMBER,
                DEFAULT_STATUS,
                DEFAULT_BUYER_NAME,
                DEFAULT_BUYER_EMAIL,
                DEFAULT_BUYER_PHONE,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_SHOP_ID,
                DEFAULT_EXTERNAL_ORDER_NO,
                now,
                now,
                now,
                null);
    }

    /** 특정 상태의 주문 Entity 생성. */
    public static OrderJpaEntity entityWithStatus(String id, String status) {
        Instant now = Instant.now();
        return OrderJpaEntity.create(
                id,
                DEFAULT_ORDER_NUMBER,
                status,
                DEFAULT_BUYER_NAME,
                DEFAULT_BUYER_EMAIL,
                DEFAULT_BUYER_PHONE,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_SHOP_ID,
                DEFAULT_EXTERNAL_ORDER_NO,
                now,
                now,
                now,
                null);
    }

    /** 삭제된 주문 Entity 생성. */
    public static OrderJpaEntity deletedEntity() {
        Instant now = Instant.now();
        return OrderJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_ORDER_NUMBER,
                DEFAULT_STATUS,
                DEFAULT_BUYER_NAME,
                DEFAULT_BUYER_EMAIL,
                DEFAULT_BUYER_PHONE,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_SHOP_ID,
                DEFAULT_EXTERNAL_ORDER_NO,
                now,
                now,
                now,
                now);
    }
}
