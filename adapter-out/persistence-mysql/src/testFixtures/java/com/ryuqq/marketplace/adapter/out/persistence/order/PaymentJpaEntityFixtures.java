package com.ryuqq.marketplace.adapter.out.persistence.order;

import com.ryuqq.marketplace.adapter.out.persistence.order.entity.PaymentJpaEntity;
import java.time.Instant;

/** PaymentJpaEntity 테스트 Fixtures. */
public final class PaymentJpaEntityFixtures {

    private PaymentJpaEntityFixtures() {}

    public static final String DEFAULT_ID = "01944b2a-aaaa-7fff-8888-000000000001";
    public static final String DEFAULT_ORDER_ID = "01944b2a-1234-7fff-8888-abcdef012345";
    public static final String DEFAULT_PAYMENT_NUMBER = "PAY-20260218-0001";
    public static final String DEFAULT_PAYMENT_STATUS_COMPLETED = "COMPLETED";
    public static final String DEFAULT_PAYMENT_STATUS_PENDING = "PENDING";
    public static final String DEFAULT_PAYMENT_METHOD = "CARD";
    public static final String DEFAULT_PAYMENT_AGENCY_ID = "PG-TXN-00001";
    public static final int DEFAULT_PAYMENT_AMOUNT = 20000;

    /** COMPLETED 상태의 결제 Entity 생성. */
    public static PaymentJpaEntity completedEntity() {
        Instant now = Instant.now();
        return PaymentJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_ORDER_ID,
                DEFAULT_PAYMENT_NUMBER,
                DEFAULT_PAYMENT_STATUS_COMPLETED,
                DEFAULT_PAYMENT_METHOD,
                DEFAULT_PAYMENT_AGENCY_ID,
                DEFAULT_PAYMENT_AMOUNT,
                now.minusSeconds(60),
                null,
                now.minusSeconds(120),
                now);
    }

    /** COMPLETED 상태의 결제 Entity 생성 (ID 지정). */
    public static PaymentJpaEntity completedEntity(String id, String orderId) {
        Instant now = Instant.now();
        return PaymentJpaEntity.create(
                id,
                orderId,
                DEFAULT_PAYMENT_NUMBER,
                DEFAULT_PAYMENT_STATUS_COMPLETED,
                DEFAULT_PAYMENT_METHOD,
                DEFAULT_PAYMENT_AGENCY_ID,
                DEFAULT_PAYMENT_AMOUNT,
                now.minusSeconds(60),
                null,
                now.minusSeconds(120),
                now);
    }

    /** PENDING 상태의 결제 Entity 생성 (paymentNumber null 포함). */
    public static PaymentJpaEntity pendingEntity(String id, String orderId) {
        Instant now = Instant.now();
        return PaymentJpaEntity.create(
                id,
                orderId,
                null,
                DEFAULT_PAYMENT_STATUS_PENDING,
                null,
                null,
                0,
                null,
                null,
                now,
                now);
    }

    /** 취소된 결제 Entity 생성. */
    public static PaymentJpaEntity canceledEntity(String id, String orderId) {
        Instant now = Instant.now();
        return PaymentJpaEntity.create(
                id,
                orderId,
                DEFAULT_PAYMENT_NUMBER,
                "CANCELED",
                DEFAULT_PAYMENT_METHOD,
                DEFAULT_PAYMENT_AGENCY_ID,
                DEFAULT_PAYMENT_AMOUNT,
                now.minusSeconds(3600),
                now.minusSeconds(60),
                now.minusSeconds(7200),
                now);
    }
}
