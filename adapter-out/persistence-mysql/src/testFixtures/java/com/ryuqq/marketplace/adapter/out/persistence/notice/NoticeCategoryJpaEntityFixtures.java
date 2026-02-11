package com.ryuqq.marketplace.adapter.out.persistence.notice;

import com.ryuqq.marketplace.adapter.out.persistence.notice.entity.NoticeCategoryJpaEntity;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * NoticeCategoryJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 NoticeCategoryJpaEntity 관련 객체들을 생성합니다.
 */
public final class NoticeCategoryJpaEntityFixtures {

    private NoticeCategoryJpaEntityFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final String DEFAULT_CODE = "CLOTHING";
    public static final String DEFAULT_NAME_KO = "의류";
    public static final String DEFAULT_NAME_EN = "Clothing";
    public static final String DEFAULT_TARGET_CATEGORY_GROUP = "CLOTHING";

    // ===== Entity Fixtures =====

    /** 활성 상태의 NoticeCategory Entity 생성. */
    public static NoticeCategoryJpaEntity activeEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return NoticeCategoryJpaEntity.create(
                seq,
                DEFAULT_CODE + "_" + seq,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_TARGET_CATEGORY_GROUP,
                true,
                now,
                now);
    }

    /** ID를 지정한 활성 상태 NoticeCategory Entity 생성. */
    public static NoticeCategoryJpaEntity activeEntity(Long id) {
        Instant now = Instant.now();
        return NoticeCategoryJpaEntity.create(
                id,
                DEFAULT_CODE,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_TARGET_CATEGORY_GROUP,
                true,
                now,
                now);
    }

    /** 커스텀 코드를 가진 활성 상태 NoticeCategory Entity 생성. */
    public static NoticeCategoryJpaEntity activeEntityWithCode(String code, String targetGroup) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return NoticeCategoryJpaEntity.create(
                seq,
                code,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                targetGroup,
                true,
                now,
                now);
    }

    /** 비활성 상태 NoticeCategory Entity 생성. */
    public static NoticeCategoryJpaEntity inactiveEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return NoticeCategoryJpaEntity.create(
                seq,
                DEFAULT_CODE + "_" + seq,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_TARGET_CATEGORY_GROUP,
                false,
                now,
                now);
    }

    /** 새로 생성될 Entity (ID가 null). */
    public static NoticeCategoryJpaEntity newEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return NoticeCategoryJpaEntity.create(
                null,
                DEFAULT_CODE + "_" + seq,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_TARGET_CATEGORY_GROUP,
                true,
                now,
                now);
    }

    /** 영어 이름이 없는 Entity 생성. */
    public static NoticeCategoryJpaEntity entityWithoutEnglishName() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return NoticeCategoryJpaEntity.create(
                seq,
                DEFAULT_CODE + "_" + seq,
                DEFAULT_NAME_KO,
                null,
                DEFAULT_TARGET_CATEGORY_GROUP,
                true,
                now,
                now);
    }

    /** 전자제품 카테고리 Entity 생성. */
    public static NoticeCategoryJpaEntity electronicsEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return NoticeCategoryJpaEntity.create(
                seq,
                "DIGITAL",
                "디지털/가전",
                "Digital",
                "DIGITAL",
                true,
                now,
                now);
    }

    /** 가구 카테고리 Entity 생성. */
    public static NoticeCategoryJpaEntity furnitureEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return NoticeCategoryJpaEntity.create(
                seq,
                "FURNITURE",
                "가구",
                "Furniture",
                "FURNITURE",
                true,
                now,
                now);
    }

    /** 비활성 상태의 새 Entity 생성 (ID는 null). */
    public static NoticeCategoryJpaEntity newInactiveEntity() {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return NoticeCategoryJpaEntity.create(
                null,
                DEFAULT_CODE + "_" + seq,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_TARGET_CATEGORY_GROUP,
                false,
                now,
                now);
    }

    /** 커스텀 이름을 가진 활성 상태 Entity 생성. */
    public static NoticeCategoryJpaEntity activeEntityWithName(
            String nameKo, String nameEn) {
        long seq = SEQUENCE.getAndIncrement();
        Instant now = Instant.now();
        return NoticeCategoryJpaEntity.create(
                seq,
                DEFAULT_CODE + "_" + seq,
                nameKo,
                nameEn,
                DEFAULT_TARGET_CATEGORY_GROUP,
                true,
                now,
                now);
    }
}
