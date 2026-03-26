package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion;

import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.LegacySellerIdMappingJpaEntity;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicLong;

/**
 * LegacySellerIdMappingJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 LegacySellerIdMappingJpaEntity 관련 객체들을 생성합니다.
 *
 * <p>LegacySellerIdMappingJpaEntity는 정적 팩토리 메서드가 없으므로 리플렉션을 사용합니다.
 *
 * <p>통합 테스트에서 persist 시 ID를 null로 유지해야 합니다 (GenerationType.IDENTITY).
 */
public final class LegacySellerIdMappingJpaEntityFixtures {

    private LegacySellerIdMappingJpaEntityFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final long DEFAULT_LEGACY_SELLER_ID = 5001L;
    public static final long DEFAULT_INTERNAL_SELLER_ID = 1001L;
    public static final String DEFAULT_SELLER_NAME = "테스트셀러";

    // ===== Entity Fixtures =====

    /** 단위 테스트용 Entity 생성 (ID 설정 포함). 주의: JPA persist에는 사용하지 마세요. */
    public static LegacySellerIdMappingJpaEntity entity() {
        return createEntityWithId(
                DEFAULT_ID,
                DEFAULT_LEGACY_SELLER_ID,
                DEFAULT_INTERNAL_SELLER_ID,
                DEFAULT_SELLER_NAME);
    }

    /** 통합 테스트용 신규 Entity 생성 (ID null). JPA persist에 사용합니다. */
    public static LegacySellerIdMappingJpaEntity newEntity() {
        long seq = SEQUENCE.getAndIncrement();
        return createEntityWithoutId(
                DEFAULT_LEGACY_SELLER_ID + seq,
                DEFAULT_INTERNAL_SELLER_ID + seq,
                DEFAULT_SELLER_NAME + seq);
    }

    /** legacySellerId를 지정한 통합 테스트용 Entity (ID null). */
    public static LegacySellerIdMappingJpaEntity newEntityWithLegacySellerId(long legacySellerId) {
        long seq = SEQUENCE.getAndIncrement();
        return createEntityWithoutId(
                legacySellerId, DEFAULT_INTERNAL_SELLER_ID + seq, DEFAULT_SELLER_NAME);
    }

    /** 모든 필드를 지정한 통합 테스트용 Entity (ID null). */
    public static LegacySellerIdMappingJpaEntity entityWithInternalSellerId(
            long legacySellerId, long internalSellerId, String sellerName) {
        return createEntityWithoutId(legacySellerId, internalSellerId, sellerName);
    }

    private static LegacySellerIdMappingJpaEntity createEntityWithId(
            Long id, long legacySellerId, long internalSellerId, String sellerName) {
        try {
            LegacySellerIdMappingJpaEntity entity = createInstance();
            setField(entity, "id", id);
            setField(entity, "legacySellerId", legacySellerId);
            setField(entity, "internalSellerId", internalSellerId);
            setField(entity, "sellerName", sellerName);
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("LegacySellerIdMappingJpaEntity 생성 실패", e);
        }
    }

    private static LegacySellerIdMappingJpaEntity createEntityWithoutId(
            long legacySellerId, long internalSellerId, String sellerName) {
        try {
            LegacySellerIdMappingJpaEntity entity = createInstance();
            setField(entity, "legacySellerId", legacySellerId);
            setField(entity, "internalSellerId", internalSellerId);
            setField(entity, "sellerName", sellerName);
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("LegacySellerIdMappingJpaEntity 생성 실패", e);
        }
    }

    private static LegacySellerIdMappingJpaEntity createInstance() throws Exception {
        var constructor = LegacySellerIdMappingJpaEntity.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

    private static void setField(Object obj, String fieldName, Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }
}
