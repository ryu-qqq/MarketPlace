package com.ryuqq.marketplace.adapter.out.persistence.inboundcategorymapping;

import com.ryuqq.marketplace.adapter.out.persistence.inboundcategorymapping.entity.InboundCategoryMappingJpaEntity;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * InboundCategoryMappingJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 InboundCategoryMappingJpaEntity 관련 객체들을 생성합니다.
 */
public final class InboundCategoryMappingJpaEntityFixtures {

    private InboundCategoryMappingJpaEntityFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final Long DEFAULT_INBOUND_SOURCE_ID = 1L;
    public static final String DEFAULT_EXTERNAL_CATEGORY_CODE = "CAT001";
    public static final String DEFAULT_EXTERNAL_CATEGORY_NAME = "외부 카테고리 A";
    public static final Long DEFAULT_INTERNAL_CATEGORY_ID = 100L;
    public static final String DEFAULT_STATUS_ACTIVE = "ACTIVE";
    public static final String DEFAULT_STATUS_INACTIVE = "INACTIVE";

    // ===== Entity Fixtures =====

    /** 활성 상태의 InboundCategoryMapping Entity 생성 (ID 없음). */
    public static InboundCategoryMappingJpaEntity activeEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return InboundCategoryMappingJpaEntity.create(
                null,
                DEFAULT_INBOUND_SOURCE_ID,
                DEFAULT_EXTERNAL_CATEGORY_CODE + seq,
                DEFAULT_EXTERNAL_CATEGORY_NAME + seq,
                DEFAULT_INTERNAL_CATEGORY_ID + seq,
                DEFAULT_STATUS_ACTIVE,
                now,
                now);
    }

    /** ID를 지정한 활성 상태 InboundCategoryMapping Entity 생성. */
    public static InboundCategoryMappingJpaEntity activeEntity(Long id) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return InboundCategoryMappingJpaEntity.create(
                id,
                DEFAULT_INBOUND_SOURCE_ID,
                DEFAULT_EXTERNAL_CATEGORY_CODE + seq,
                DEFAULT_EXTERNAL_CATEGORY_NAME + seq,
                DEFAULT_INTERNAL_CATEGORY_ID + seq,
                DEFAULT_STATUS_ACTIVE,
                now,
                now);
    }

    /** 특정 inboundSourceId를 가진 활성 상태 Entity 생성. */
    public static InboundCategoryMappingJpaEntity activeEntityWithSourceId(Long inboundSourceId) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return InboundCategoryMappingJpaEntity.create(
                null,
                inboundSourceId,
                DEFAULT_EXTERNAL_CATEGORY_CODE + seq,
                DEFAULT_EXTERNAL_CATEGORY_NAME + seq,
                DEFAULT_INTERNAL_CATEGORY_ID + seq,
                DEFAULT_STATUS_ACTIVE,
                now,
                now);
    }

    /** 특정 externalCategoryCode를 가진 활성 상태 Entity 생성. */
    public static InboundCategoryMappingJpaEntity activeEntityWithCode(
            Long inboundSourceId, String externalCategoryCode) {
        Instant now = Instant.now();
        return InboundCategoryMappingJpaEntity.create(
                null,
                inboundSourceId,
                externalCategoryCode,
                DEFAULT_EXTERNAL_CATEGORY_NAME,
                DEFAULT_INTERNAL_CATEGORY_ID,
                DEFAULT_STATUS_ACTIVE,
                now,
                now);
    }

    /** 비활성 상태 InboundCategoryMapping Entity 생성. */
    public static InboundCategoryMappingJpaEntity inactiveEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return InboundCategoryMappingJpaEntity.create(
                null,
                DEFAULT_INBOUND_SOURCE_ID,
                "CAT_INACTIVE_" + seq,
                "비활성 카테고리" + seq,
                DEFAULT_INTERNAL_CATEGORY_ID + seq,
                DEFAULT_STATUS_INACTIVE,
                now,
                now);
    }

    /** 새로 생성될 Entity (ID가 null). */
    public static InboundCategoryMappingJpaEntity newEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return InboundCategoryMappingJpaEntity.create(
                null,
                DEFAULT_INBOUND_SOURCE_ID,
                DEFAULT_EXTERNAL_CATEGORY_CODE + seq,
                DEFAULT_EXTERNAL_CATEGORY_NAME + seq,
                DEFAULT_INTERNAL_CATEGORY_ID + seq,
                DEFAULT_STATUS_ACTIVE,
                now,
                now);
    }

    /** 완전한 정보를 가진 새 Entity 생성 (ID는 null). */
    public static InboundCategoryMappingJpaEntity newEntityWithCompleteInfo(
            Long inboundSourceId,
            String externalCategoryCode,
            String externalCategoryName,
            Long internalCategoryId) {
        Instant now = Instant.now();
        return InboundCategoryMappingJpaEntity.create(
                null,
                inboundSourceId,
                externalCategoryCode,
                externalCategoryName,
                internalCategoryId,
                DEFAULT_STATUS_ACTIVE,
                now,
                now);
    }
}
