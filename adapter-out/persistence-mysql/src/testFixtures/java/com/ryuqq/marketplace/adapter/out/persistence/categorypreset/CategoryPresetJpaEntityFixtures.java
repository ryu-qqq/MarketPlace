package com.ryuqq.marketplace.adapter.out.persistence.categorypreset;

import com.ryuqq.marketplace.adapter.out.persistence.categorypreset.entity.CategoryPresetJpaEntity;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * CategoryPresetJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 CategoryPresetJpaEntity 관련 객체들을 생성합니다.
 */
public final class CategoryPresetJpaEntityFixtures {

    private CategoryPresetJpaEntityFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final Long DEFAULT_SHOP_ID = 100L;
    public static final Long DEFAULT_SALES_CHANNEL_CATEGORY_ID = 200L;
    public static final String DEFAULT_PRESET_NAME = "테스트 카테고리 프리셋";
    public static final String DEFAULT_STATUS = "ACTIVE";

    // ===== Entity Fixtures =====

    /** 활성 상태의 CategoryPreset Entity 생성. */
    public static CategoryPresetJpaEntity activeEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return CategoryPresetJpaEntity.create(
                null,
                DEFAULT_SHOP_ID,
                DEFAULT_SALES_CHANNEL_CATEGORY_ID,
                "테스트 카테고리 프리셋 " + seq,
                "ACTIVE",
                now,
                now);
    }

    /** ID를 지정한 활성 상태 CategoryPreset Entity 생성. */
    public static CategoryPresetJpaEntity activeEntity(Long id) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return CategoryPresetJpaEntity.create(
                id,
                DEFAULT_SHOP_ID,
                DEFAULT_SALES_CHANNEL_CATEGORY_ID,
                "테스트 카테고리 프리셋 " + seq,
                "ACTIVE",
                now,
                now);
    }

    /** 커스텀 프리셋명을 가진 활성 상태 CategoryPreset Entity 생성. ID는 null로 새 엔티티 생성. */
    public static CategoryPresetJpaEntity activeEntityWithName(String presetName) {
        Instant now = Instant.now();
        return CategoryPresetJpaEntity.create(
                null,
                DEFAULT_SHOP_ID,
                DEFAULT_SALES_CHANNEL_CATEGORY_ID,
                presetName,
                "ACTIVE",
                now,
                now);
    }

    /** 비활성 상태 CategoryPreset Entity 생성. */
    public static CategoryPresetJpaEntity inactiveEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return CategoryPresetJpaEntity.create(
                null,
                DEFAULT_SHOP_ID,
                DEFAULT_SALES_CHANNEL_CATEGORY_ID,
                "테스트 카테고리 프리셋 " + seq,
                "INACTIVE",
                now,
                now);
    }

    /** 새로 생성될 Entity (ID가 null). */
    public static CategoryPresetJpaEntity newEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return CategoryPresetJpaEntity.create(
                null,
                DEFAULT_SHOP_ID,
                DEFAULT_SALES_CHANNEL_CATEGORY_ID,
                "테스트 카테고리 프리셋 " + seq,
                "ACTIVE",
                now,
                now);
    }

    /** 비활성 상태의 새 Entity 생성 (ID는 null). */
    public static CategoryPresetJpaEntity newInactiveEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return CategoryPresetJpaEntity.create(
                null,
                DEFAULT_SHOP_ID,
                DEFAULT_SALES_CHANNEL_CATEGORY_ID,
                "테스트 카테고리 프리셋 " + seq,
                "INACTIVE",
                now,
                now);
    }

    /** 커스텀 ShopId를 가진 활성 상태 CategoryPreset Entity 생성. */
    public static CategoryPresetJpaEntity activeEntityWithShopId(Long shopId) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return CategoryPresetJpaEntity.create(
                null,
                shopId,
                DEFAULT_SALES_CHANNEL_CATEGORY_ID,
                "테스트 카테고리 프리셋 " + seq,
                "ACTIVE",
                now,
                now);
    }

    /** 커스텀 SalesChannelCategoryId를 가진 활성 상태 CategoryPreset Entity 생성. */
    public static CategoryPresetJpaEntity activeEntityWithSalesChannelCategoryId(
            Long salesChannelCategoryId) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return CategoryPresetJpaEntity.create(
                null,
                DEFAULT_SHOP_ID,
                salesChannelCategoryId,
                "테스트 카테고리 프리셋 " + seq,
                "ACTIVE",
                now,
                now);
    }

    /** 비활성 상태의 커스텀 프리셋명 CategoryPreset Entity 생성. ID는 null로 새 엔티티 생성. */
    public static CategoryPresetJpaEntity inactiveEntityWithName(String presetName) {
        Instant now = Instant.now();
        return CategoryPresetJpaEntity.create(
                null, DEFAULT_SHOP_ID, DEFAULT_SALES_CHANNEL_CATEGORY_ID, presetName, "INACTIVE", now, now);
    }
}
