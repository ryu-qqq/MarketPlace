package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion;

import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.LegacyProductIdMappingJpaEntity;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * LegacyProductIdMappingJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 LegacyProductIdMappingJpaEntity 관련 객체들을 생성합니다.
 */
public final class LegacyProductIdMappingJpaEntityFixtures {

    private LegacyProductIdMappingJpaEntityFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final Long DEFAULT_LEGACY_PRODUCT_ID = 200L;
    public static final Long DEFAULT_INTERNAL_PRODUCT_ID = 300L;
    public static final Long DEFAULT_LEGACY_PRODUCT_GROUP_ID = 100L;
    public static final Long DEFAULT_INTERNAL_PRODUCT_GROUP_ID = 400L;

    // ===== Entity Fixtures =====

    /** 기본값으로 Entity 생성 (단위 테스트용, DEFAULT_ID). */
    public static LegacyProductIdMappingJpaEntity entity() {
        return LegacyProductIdMappingJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_LEGACY_PRODUCT_ID,
                DEFAULT_INTERNAL_PRODUCT_ID,
                DEFAULT_LEGACY_PRODUCT_GROUP_ID,
                DEFAULT_INTERNAL_PRODUCT_GROUP_ID,
                Instant.now());
    }

    /** ID를 지정한 Entity 생성 (단위 테스트용). */
    public static LegacyProductIdMappingJpaEntity entity(Long id) {
        return LegacyProductIdMappingJpaEntity.create(
                id,
                DEFAULT_LEGACY_PRODUCT_ID,
                DEFAULT_INTERNAL_PRODUCT_ID,
                DEFAULT_LEGACY_PRODUCT_GROUP_ID,
                DEFAULT_INTERNAL_PRODUCT_GROUP_ID,
                Instant.now());
    }

    /** 신규 Entity (통합 테스트용, ID null). */
    public static LegacyProductIdMappingJpaEntity newEntity() {
        long seq = SEQUENCE.getAndIncrement();
        return LegacyProductIdMappingJpaEntity.create(
                null,
                DEFAULT_LEGACY_PRODUCT_ID + seq,
                DEFAULT_INTERNAL_PRODUCT_ID + seq,
                DEFAULT_LEGACY_PRODUCT_GROUP_ID,
                DEFAULT_INTERNAL_PRODUCT_GROUP_ID,
                Instant.now());
    }

    /** legacyProductId를 지정한 신규 Entity. */
    public static LegacyProductIdMappingJpaEntity newEntityWithLegacyProductId(
            long legacyProductId) {
        return LegacyProductIdMappingJpaEntity.create(
                null,
                legacyProductId,
                DEFAULT_INTERNAL_PRODUCT_ID + SEQUENCE.getAndIncrement(),
                DEFAULT_LEGACY_PRODUCT_GROUP_ID,
                DEFAULT_INTERNAL_PRODUCT_GROUP_ID,
                Instant.now());
    }

    /** internalProductId를 지정한 신규 Entity. */
    public static LegacyProductIdMappingJpaEntity newEntityWithInternalProductId(
            long internalProductId) {
        return LegacyProductIdMappingJpaEntity.create(
                null,
                DEFAULT_LEGACY_PRODUCT_ID + SEQUENCE.getAndIncrement(),
                internalProductId,
                DEFAULT_LEGACY_PRODUCT_GROUP_ID,
                DEFAULT_INTERNAL_PRODUCT_GROUP_ID,
                Instant.now());
    }

    /** legacyProductGroupId를 지정한 신규 Entity. */
    public static LegacyProductIdMappingJpaEntity newEntityWithGroupId(
            long legacyProductId,
            long internalProductId,
            long legacyProductGroupId,
            long internalProductGroupId) {
        return LegacyProductIdMappingJpaEntity.create(
                null,
                legacyProductId,
                internalProductId,
                legacyProductGroupId,
                internalProductGroupId,
                Instant.now());
    }
}
