package com.ryuqq.marketplace.adapter.out.persistence.outboundproduct;

import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.entity.OutboundProductJpaEntity;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * OutboundProductJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 OutboundProductJpaEntity 관련 객체들을 생성합니다.
 */
public final class OutboundProductJpaEntityFixtures {

    private OutboundProductJpaEntityFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final Long DEFAULT_PRODUCT_GROUP_ID = 100L;
    public static final Long DEFAULT_SALES_CHANNEL_ID = 10L;
    public static final String DEFAULT_EXTERNAL_PRODUCT_ID = "EXT-PROD-001";
    public static final String STATUS_PENDING = "PENDING_REGISTRATION";
    public static final String STATUS_REGISTERED = "REGISTERED";
    public static final String STATUS_FAILED = "REGISTRATION_FAILED";

    // ===== Entity Fixtures =====

    /** PENDING_REGISTRATION 상태의 신규 Entity (ID null). */
    public static OutboundProductJpaEntity pendingEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return OutboundProductJpaEntity.create(
                null,
                DEFAULT_PRODUCT_GROUP_ID + seq,
                DEFAULT_SALES_CHANNEL_ID,
                null,
                STATUS_PENDING,
                now,
                now);
    }

    /** ID를 지정한 PENDING_REGISTRATION 상태 Entity. */
    public static OutboundProductJpaEntity pendingEntity(Long id) {
        Instant now = Instant.now();
        return OutboundProductJpaEntity.create(
                id,
                DEFAULT_PRODUCT_GROUP_ID,
                DEFAULT_SALES_CHANNEL_ID,
                null,
                STATUS_PENDING,
                now,
                now);
    }

    /** REGISTERED 상태의 신규 Entity (ID null, externalProductId 있음). */
    public static OutboundProductJpaEntity registeredEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return OutboundProductJpaEntity.create(
                null,
                DEFAULT_PRODUCT_GROUP_ID + seq,
                DEFAULT_SALES_CHANNEL_ID,
                "EXT-PROD-" + seq,
                STATUS_REGISTERED,
                now,
                now);
    }

    /** ID를 지정한 REGISTERED 상태 Entity. */
    public static OutboundProductJpaEntity registeredEntity(Long id) {
        Instant now = Instant.now();
        return OutboundProductJpaEntity.create(
                id,
                DEFAULT_PRODUCT_GROUP_ID,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_PRODUCT_ID,
                STATUS_REGISTERED,
                now,
                now);
    }

    /** REGISTRATION_FAILED 상태의 신규 Entity (ID null). */
    public static OutboundProductJpaEntity failedEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return OutboundProductJpaEntity.create(
                null,
                DEFAULT_PRODUCT_GROUP_ID + seq,
                DEFAULT_SALES_CHANNEL_ID,
                null,
                STATUS_FAILED,
                now,
                now);
    }

    /** productGroupId와 salesChannelId를 지정한 PENDING 상태 Entity (ID null). */
    public static OutboundProductJpaEntity pendingEntityWith(
            Long productGroupId, Long salesChannelId) {
        Instant now = Instant.now();
        return OutboundProductJpaEntity.create(
                null, productGroupId, salesChannelId, null, STATUS_PENDING, now, now);
    }

    /** productGroupId와 salesChannelId를 지정한 REGISTERED 상태 Entity (ID null). */
    public static OutboundProductJpaEntity registeredEntityWith(
            Long productGroupId, Long salesChannelId) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return OutboundProductJpaEntity.create(
                null,
                productGroupId,
                salesChannelId,
                "EXT-PROD-" + seq,
                STATUS_REGISTERED,
                now,
                now);
    }

    /** 단위 테스트용 기본 Entity (DEFAULT_ID 사용). */
    public static OutboundProductJpaEntity entity() {
        Instant now = Instant.now();
        return OutboundProductJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_PRODUCT_GROUP_ID,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_PRODUCT_ID,
                STATUS_REGISTERED,
                now,
                now);
    }

    /** ID를 지정한 단위 테스트용 Entity. */
    public static OutboundProductJpaEntity entity(Long id) {
        Instant now = Instant.now();
        return OutboundProductJpaEntity.create(
                id,
                DEFAULT_PRODUCT_GROUP_ID,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_PRODUCT_ID,
                STATUS_REGISTERED,
                now,
                now);
    }
}
