package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion;

import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.LegacyOrderIdMappingJpaEntity;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * LegacyOrderIdMappingJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 LegacyOrderIdMappingJpaEntity 관련 객체들을 생성합니다.
 */
public final class LegacyOrderIdMappingJpaEntityFixtures {

    private LegacyOrderIdMappingJpaEntityFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final long DEFAULT_LEGACY_ORDER_ID = 10001L;
    public static final long DEFAULT_LEGACY_PAYMENT_ID = 20001L;
    public static final String DEFAULT_INTERNAL_ORDER_ID = "order-uuid-00001";
    public static final long DEFAULT_SALES_CHANNEL_ID = 1L;
    public static final String DEFAULT_CHANNEL_NAME = "NAVER";

    // ===== Entity Fixtures =====

    /** 기본값으로 Entity 생성 (단위 테스트용, DEFAULT_ID). */
    public static LegacyOrderIdMappingJpaEntity entity() {
        return LegacyOrderIdMappingJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_LEGACY_ORDER_ID,
                DEFAULT_LEGACY_PAYMENT_ID,
                DEFAULT_INTERNAL_ORDER_ID,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_CHANNEL_NAME,
                Instant.now());
    }

    /** ID를 지정한 Entity 생성 (단위 테스트용). */
    public static LegacyOrderIdMappingJpaEntity entity(Long id) {
        return LegacyOrderIdMappingJpaEntity.create(
                id,
                DEFAULT_LEGACY_ORDER_ID,
                DEFAULT_LEGACY_PAYMENT_ID,
                DEFAULT_INTERNAL_ORDER_ID,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_CHANNEL_NAME,
                Instant.now());
    }

    /** 신규 Entity (통합 테스트용, ID null). */
    public static LegacyOrderIdMappingJpaEntity newEntity() {
        long seq = SEQUENCE.getAndIncrement();
        return LegacyOrderIdMappingJpaEntity.create(
                null,
                DEFAULT_LEGACY_ORDER_ID + seq,
                DEFAULT_LEGACY_PAYMENT_ID + seq,
                DEFAULT_INTERNAL_ORDER_ID + seq,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_CHANNEL_NAME,
                Instant.now());
    }

    /** legacyOrderId를 지정한 신규 Entity. */
    public static LegacyOrderIdMappingJpaEntity newEntityWithLegacyOrderId(long legacyOrderId) {
        long seq = SEQUENCE.getAndIncrement();
        return LegacyOrderIdMappingJpaEntity.create(
                null,
                legacyOrderId,
                DEFAULT_LEGACY_PAYMENT_ID + seq,
                DEFAULT_INTERNAL_ORDER_ID + seq,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_CHANNEL_NAME,
                Instant.now());
    }

    /** 특정 internalOrderId를 가진 신규 Entity. */
    public static LegacyOrderIdMappingJpaEntity newEntityWithInternalOrderId(
            String internalOrderId) {
        long seq = SEQUENCE.getAndIncrement();
        return LegacyOrderIdMappingJpaEntity.create(
                null,
                DEFAULT_LEGACY_ORDER_ID + seq,
                DEFAULT_LEGACY_PAYMENT_ID + seq,
                internalOrderId,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_CHANNEL_NAME,
                Instant.now());
    }
}
