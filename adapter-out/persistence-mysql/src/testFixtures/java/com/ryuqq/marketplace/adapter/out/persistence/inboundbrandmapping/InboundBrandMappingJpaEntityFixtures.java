package com.ryuqq.marketplace.adapter.out.persistence.inboundbrandmapping;

import com.ryuqq.marketplace.adapter.out.persistence.inboundbrandmapping.entity.InboundBrandMappingJpaEntity;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * InboundBrandMappingJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 InboundBrandMappingJpaEntity 관련 객체들을 생성합니다.
 */
public final class InboundBrandMappingJpaEntityFixtures {

    private InboundBrandMappingJpaEntityFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final Long DEFAULT_INBOUND_SOURCE_ID = 1L;
    public static final String DEFAULT_EXTERNAL_BRAND_CODE = "BR001";
    public static final String DEFAULT_EXTERNAL_BRAND_NAME = "외부 브랜드 A";
    public static final Long DEFAULT_INTERNAL_BRAND_ID = 100L;
    public static final String DEFAULT_STATUS_ACTIVE = "ACTIVE";
    public static final String DEFAULT_STATUS_INACTIVE = "INACTIVE";

    // ===== Entity Fixtures =====

    /** 활성 상태의 InboundBrandMapping Entity 생성 (ID 없음). */
    public static InboundBrandMappingJpaEntity activeEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return InboundBrandMappingJpaEntity.create(
                null,
                DEFAULT_INBOUND_SOURCE_ID,
                DEFAULT_EXTERNAL_BRAND_CODE + seq,
                DEFAULT_EXTERNAL_BRAND_NAME + seq,
                DEFAULT_INTERNAL_BRAND_ID + seq,
                DEFAULT_STATUS_ACTIVE,
                now,
                now);
    }

    /** ID를 지정한 활성 상태 InboundBrandMapping Entity 생성. */
    public static InboundBrandMappingJpaEntity activeEntity(Long id) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return InboundBrandMappingJpaEntity.create(
                id,
                DEFAULT_INBOUND_SOURCE_ID,
                DEFAULT_EXTERNAL_BRAND_CODE + seq,
                DEFAULT_EXTERNAL_BRAND_NAME + seq,
                DEFAULT_INTERNAL_BRAND_ID + seq,
                DEFAULT_STATUS_ACTIVE,
                now,
                now);
    }

    /** 특정 inboundSourceId를 가진 활성 상태 Entity 생성. */
    public static InboundBrandMappingJpaEntity activeEntityWithSourceId(Long inboundSourceId) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return InboundBrandMappingJpaEntity.create(
                null,
                inboundSourceId,
                DEFAULT_EXTERNAL_BRAND_CODE + seq,
                DEFAULT_EXTERNAL_BRAND_NAME + seq,
                DEFAULT_INTERNAL_BRAND_ID + seq,
                DEFAULT_STATUS_ACTIVE,
                now,
                now);
    }

    /** 특정 externalBrandCode를 가진 활성 상태 Entity 생성. */
    public static InboundBrandMappingJpaEntity activeEntityWithCode(
            Long inboundSourceId, String externalBrandCode) {
        Instant now = Instant.now();
        return InboundBrandMappingJpaEntity.create(
                null,
                inboundSourceId,
                externalBrandCode,
                DEFAULT_EXTERNAL_BRAND_NAME,
                DEFAULT_INTERNAL_BRAND_ID,
                DEFAULT_STATUS_ACTIVE,
                now,
                now);
    }

    /** 비활성 상태 InboundBrandMapping Entity 생성. */
    public static InboundBrandMappingJpaEntity inactiveEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return InboundBrandMappingJpaEntity.create(
                null,
                DEFAULT_INBOUND_SOURCE_ID,
                "BR_INACTIVE_" + seq,
                "비활성 브랜드" + seq,
                DEFAULT_INTERNAL_BRAND_ID + seq,
                DEFAULT_STATUS_INACTIVE,
                now,
                now);
    }

    /** 새로 생성될 Entity (ID가 null). */
    public static InboundBrandMappingJpaEntity newEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return InboundBrandMappingJpaEntity.create(
                null,
                DEFAULT_INBOUND_SOURCE_ID,
                DEFAULT_EXTERNAL_BRAND_CODE + seq,
                DEFAULT_EXTERNAL_BRAND_NAME + seq,
                DEFAULT_INTERNAL_BRAND_ID + seq,
                DEFAULT_STATUS_ACTIVE,
                now,
                now);
    }

    /** 완전한 정보를 가진 새 Entity 생성 (ID는 null). */
    public static InboundBrandMappingJpaEntity newEntityWithCompleteInfo(
            Long inboundSourceId,
            String externalBrandCode,
            String externalBrandName,
            Long internalBrandId) {
        Instant now = Instant.now();
        return InboundBrandMappingJpaEntity.create(
                null,
                inboundSourceId,
                externalBrandCode,
                externalBrandName,
                internalBrandId,
                DEFAULT_STATUS_ACTIVE,
                now,
                now);
    }
}
