package com.ryuqq.marketplace.adapter.out.persistence.brandmapping;

import com.ryuqq.marketplace.adapter.out.persistence.brandmapping.entity.BrandMappingJpaEntity;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * BrandMappingJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 BrandMappingJpaEntity 관련 객체들을 생성합니다.
 */
public final class BrandMappingJpaEntityFixtures {

    private BrandMappingJpaEntityFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final Long DEFAULT_PRESET_ID = 1L;
    public static final Long DEFAULT_SALES_CHANNEL_BRAND_ID = 100L;
    public static final Long DEFAULT_INTERNAL_BRAND_ID = 10L;
    public static final String DEFAULT_STATUS = "ACTIVE";

    // ===== Entity Fixtures =====

    /** 활성 상태의 BrandMapping Entity 생성. */
    public static BrandMappingJpaEntity activeEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return BrandMappingJpaEntity.create(
                null,
                DEFAULT_PRESET_ID + seq,
                DEFAULT_SALES_CHANNEL_BRAND_ID + seq,
                DEFAULT_INTERNAL_BRAND_ID + seq,
                "ACTIVE",
                now,
                now);
    }

    /** ID를 지정한 활성 상태 BrandMapping Entity 생성. */
    public static BrandMappingJpaEntity activeEntity(Long id) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return BrandMappingJpaEntity.create(
                id,
                DEFAULT_PRESET_ID + seq,
                DEFAULT_SALES_CHANNEL_BRAND_ID + seq,
                DEFAULT_INTERNAL_BRAND_ID + seq,
                "ACTIVE",
                now,
                now);
    }

    /** 특정 presetId를 가진 활성 상태 BrandMapping Entity 생성. */
    public static BrandMappingJpaEntity activeEntityWithPresetId(Long presetId) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return BrandMappingJpaEntity.create(
                null,
                presetId,
                DEFAULT_SALES_CHANNEL_BRAND_ID + seq,
                DEFAULT_INTERNAL_BRAND_ID + seq,
                "ACTIVE",
                now,
                now);
    }

    /** 비활성 상태 BrandMapping Entity 생성. */
    public static BrandMappingJpaEntity inactiveEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return BrandMappingJpaEntity.create(
                null,
                DEFAULT_PRESET_ID + seq,
                DEFAULT_SALES_CHANNEL_BRAND_ID + seq,
                DEFAULT_INTERNAL_BRAND_ID + seq,
                "INACTIVE",
                now,
                now);
    }

    /** 새로 생성될 Entity (ID가 null). */
    public static BrandMappingJpaEntity newEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return BrandMappingJpaEntity.create(
                null,
                DEFAULT_PRESET_ID + seq,
                DEFAULT_SALES_CHANNEL_BRAND_ID + seq,
                DEFAULT_INTERNAL_BRAND_ID + seq,
                "ACTIVE",
                now,
                now);
    }

    /** PresetId가 null인 Entity 생성. */
    public static BrandMappingJpaEntity entityWithoutPresetId() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return BrandMappingJpaEntity.create(
                null,
                null,
                DEFAULT_SALES_CHANNEL_BRAND_ID + seq,
                DEFAULT_INTERNAL_BRAND_ID + seq,
                "ACTIVE",
                now,
                now);
    }

    /** 완전한 정보를 가진 새 Entity 생성 (ID는 null). */
    public static BrandMappingJpaEntity newEntityWithCompleteInfo(
            Long presetId, Long salesChannelBrandId, Long internalBrandId) {
        Instant now = Instant.now();
        return BrandMappingJpaEntity.create(
                null, presetId, salesChannelBrandId, internalBrandId, "ACTIVE", now, now);
    }
}
