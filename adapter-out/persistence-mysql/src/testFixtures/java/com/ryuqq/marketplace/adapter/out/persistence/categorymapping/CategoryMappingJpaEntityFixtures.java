package com.ryuqq.marketplace.adapter.out.persistence.categorymapping;

import com.ryuqq.marketplace.adapter.out.persistence.categorymapping.entity.CategoryMappingJpaEntity;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * CategoryMappingJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 CategoryMappingJpaEntity 관련 객체들을 생성합니다.
 */
public final class CategoryMappingJpaEntityFixtures {

    private CategoryMappingJpaEntityFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final Long DEFAULT_PRESET_ID = 1L;
    public static final Long DEFAULT_SALES_CHANNEL_CATEGORY_ID = 200L;
    public static final Long DEFAULT_INTERNAL_CATEGORY_ID = 20L;
    public static final String DEFAULT_STATUS = "ACTIVE";

    // ===== Entity Fixtures =====

    /** 활성 상태의 CategoryMapping Entity 생성. */
    public static CategoryMappingJpaEntity activeEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return CategoryMappingJpaEntity.create(
                null,
                DEFAULT_PRESET_ID + seq,
                DEFAULT_SALES_CHANNEL_CATEGORY_ID + seq,
                DEFAULT_INTERNAL_CATEGORY_ID + seq,
                "ACTIVE",
                now,
                now);
    }

    /** ID를 지정한 활성 상태 CategoryMapping Entity 생성. */
    public static CategoryMappingJpaEntity activeEntity(Long id) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return CategoryMappingJpaEntity.create(
                id,
                DEFAULT_PRESET_ID + seq,
                DEFAULT_SALES_CHANNEL_CATEGORY_ID + seq,
                DEFAULT_INTERNAL_CATEGORY_ID + seq,
                "ACTIVE",
                now,
                now);
    }

    /** 특정 presetId를 가진 활성 상태 CategoryMapping Entity 생성. */
    public static CategoryMappingJpaEntity activeEntityWithPresetId(Long presetId) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return CategoryMappingJpaEntity.create(
                null,
                presetId,
                DEFAULT_SALES_CHANNEL_CATEGORY_ID + seq,
                DEFAULT_INTERNAL_CATEGORY_ID + seq,
                "ACTIVE",
                now,
                now);
    }

    /** 비활성 상태 CategoryMapping Entity 생성. */
    public static CategoryMappingJpaEntity inactiveEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return CategoryMappingJpaEntity.create(
                null,
                DEFAULT_PRESET_ID + seq,
                DEFAULT_SALES_CHANNEL_CATEGORY_ID + seq,
                DEFAULT_INTERNAL_CATEGORY_ID + seq,
                "INACTIVE",
                now,
                now);
    }

    /** 새로 생성될 Entity (ID가 null). */
    public static CategoryMappingJpaEntity newEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return CategoryMappingJpaEntity.create(
                null,
                DEFAULT_PRESET_ID + seq,
                DEFAULT_SALES_CHANNEL_CATEGORY_ID + seq,
                DEFAULT_INTERNAL_CATEGORY_ID + seq,
                "ACTIVE",
                now,
                now);
    }

    /** PresetId가 null인 Entity 생성. */
    public static CategoryMappingJpaEntity entityWithoutPresetId() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return CategoryMappingJpaEntity.create(
                null,
                null,
                DEFAULT_SALES_CHANNEL_CATEGORY_ID + seq,
                DEFAULT_INTERNAL_CATEGORY_ID + seq,
                "ACTIVE",
                now,
                now);
    }

    /** 완전한 정보를 가진 새 Entity 생성 (ID는 null). */
    public static CategoryMappingJpaEntity newEntityWithCompleteInfo(
            Long presetId, Long salesChannelCategoryId, Long internalCategoryId) {
        Instant now = Instant.now();
        return CategoryMappingJpaEntity.create(
                null, presetId, salesChannelCategoryId, internalCategoryId, "ACTIVE", now, now);
    }
}
