package com.ryuqq.marketplace.adapter.out.persistence.channeloptionmapping;

import com.ryuqq.marketplace.adapter.out.persistence.channeloptionmapping.entity.ChannelOptionMappingJpaEntity;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ChannelOptionMappingJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 ChannelOptionMappingJpaEntity 관련 객체들을 생성합니다.
 */
public final class ChannelOptionMappingJpaEntityFixtures {

    private ChannelOptionMappingJpaEntityFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final Long DEFAULT_SALES_CHANNEL_ID = 1L;
    public static final Long DEFAULT_CANONICAL_OPTION_VALUE_ID = 100L;
    public static final String DEFAULT_EXTERNAL_OPTION_CODE = "EXT-OPTION-001";

    // ===== Entity Fixtures =====

    /** 기본 ChannelOptionMapping Entity 생성 (ID 없음). */
    public static ChannelOptionMappingJpaEntity newEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return ChannelOptionMappingJpaEntity.create(
                null,
                DEFAULT_SALES_CHANNEL_ID + seq,
                DEFAULT_CANONICAL_OPTION_VALUE_ID + seq,
                DEFAULT_EXTERNAL_OPTION_CODE + "-" + seq,
                now,
                now);
    }

    /** ID를 지정한 ChannelOptionMapping Entity 생성. */
    public static ChannelOptionMappingJpaEntity entity(Long id) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return ChannelOptionMappingJpaEntity.create(
                id,
                DEFAULT_SALES_CHANNEL_ID + seq,
                DEFAULT_CANONICAL_OPTION_VALUE_ID + seq,
                DEFAULT_EXTERNAL_OPTION_CODE + "-" + seq,
                now,
                now);
    }

    /** 특정 salesChannelId를 가진 Entity 생성. */
    public static ChannelOptionMappingJpaEntity entityWithSalesChannelId(Long salesChannelId) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return ChannelOptionMappingJpaEntity.create(
                null,
                salesChannelId,
                DEFAULT_CANONICAL_OPTION_VALUE_ID + seq,
                DEFAULT_EXTERNAL_OPTION_CODE + "-" + seq,
                now,
                now);
    }

    /** 특정 salesChannelId와 canonicalOptionValueId를 가진 Entity 생성. */
    public static ChannelOptionMappingJpaEntity entityWith(
            Long salesChannelId, Long canonicalOptionValueId) {
        Instant now = Instant.now();
        return ChannelOptionMappingJpaEntity.create(
                null,
                salesChannelId,
                canonicalOptionValueId,
                DEFAULT_EXTERNAL_OPTION_CODE,
                now,
                now);
    }

    /** 완전한 정보를 가진 새 Entity 생성 (ID는 null). */
    public static ChannelOptionMappingJpaEntity newEntityWithCompleteInfo(
            Long salesChannelId, Long canonicalOptionValueId, String externalOptionCode) {
        Instant now = Instant.now();
        return ChannelOptionMappingJpaEntity.create(
                null, salesChannelId, canonicalOptionValueId, externalOptionCode, now, now);
    }

    /** 완전한 정보를 가진 Entity 생성 (ID 포함). */
    public static ChannelOptionMappingJpaEntity entityWithCompleteInfo(
            Long id, Long salesChannelId, Long canonicalOptionValueId, String externalOptionCode) {
        Instant now = Instant.now();
        return ChannelOptionMappingJpaEntity.create(
                id, salesChannelId, canonicalOptionValueId, externalOptionCode, now, now);
    }
}
