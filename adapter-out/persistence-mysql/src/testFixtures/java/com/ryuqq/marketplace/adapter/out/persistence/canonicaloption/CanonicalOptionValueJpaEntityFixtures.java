package com.ryuqq.marketplace.adapter.out.persistence.canonicaloption;

import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.entity.CanonicalOptionValueJpaEntity;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * CanonicalOptionValueJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 CanonicalOptionValueJpaEntity 관련 객체들을 생성합니다.
 */
public final class CanonicalOptionValueJpaEntityFixtures {

    private CanonicalOptionValueJpaEntityFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final Long DEFAULT_GROUP_ID = 1L;
    public static final String DEFAULT_CODE = "RED";
    public static final String DEFAULT_NAME_KO = "빨강";
    public static final String DEFAULT_NAME_EN = "Red";
    public static final int DEFAULT_SORT_ORDER = 1;

    // ===== Entity Fixtures =====

    /** 기본 옵션 값 Entity 생성. */
    public static CanonicalOptionValueJpaEntity defaultEntity() {
        Instant now = Instant.now();
        return CanonicalOptionValueJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_GROUP_ID,
                DEFAULT_CODE,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_SORT_ORDER,
                now,
                now);
    }

    /** ID를 지정한 옵션 값 Entity 생성. */
    public static CanonicalOptionValueJpaEntity entityWithId(Long id) {
        Instant now = Instant.now();
        return CanonicalOptionValueJpaEntity.create(
                id,
                DEFAULT_GROUP_ID,
                DEFAULT_CODE,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_SORT_ORDER,
                now,
                now);
    }

    /** 그룹 ID를 지정한 옵션 값 Entity 생성. */
    public static CanonicalOptionValueJpaEntity entityWithGroupId(Long groupId) {
        Instant now = Instant.now();
        return CanonicalOptionValueJpaEntity.create(
                null,
                groupId,
                DEFAULT_CODE,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_SORT_ORDER,
                now,
                now);
    }

    /** 커스텀 코드를 가진 옵션 값 Entity 생성. */
    public static CanonicalOptionValueJpaEntity entityWithCode(
            Long groupId, String code, int sortOrder) {
        Instant now = Instant.now();
        return CanonicalOptionValueJpaEntity.create(
                SEQUENCE.getAndIncrement(),
                groupId,
                code,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                sortOrder,
                now,
                now);
    }

    /** 커스텀 코드를 가진 새 옵션 값 Entity 생성 (ID가 null - 통합 테스트용). */
    public static CanonicalOptionValueJpaEntity newEntityWithCode(
            Long groupId, String code, int sortOrder) {
        Instant now = Instant.now();
        return CanonicalOptionValueJpaEntity.create(
                null, groupId, code, DEFAULT_NAME_KO, DEFAULT_NAME_EN, sortOrder, now, now);
    }

    /** 커스텀 이름을 가진 옵션 값 Entity 생성. */
    public static CanonicalOptionValueJpaEntity entityWithName(
            Long groupId, String nameKo, String nameEn, int sortOrder) {
        Instant now = Instant.now();
        return CanonicalOptionValueJpaEntity.create(
                null,
                groupId,
                "CODE_" + SEQUENCE.getAndIncrement(),
                nameKo,
                nameEn,
                sortOrder,
                now,
                now);
    }

    /** 새로 생성될 Entity (ID가 null). */
    public static CanonicalOptionValueJpaEntity newEntity() {
        Instant now = Instant.now();
        return CanonicalOptionValueJpaEntity.create(
                null,
                DEFAULT_GROUP_ID,
                DEFAULT_CODE,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_SORT_ORDER,
                now,
                now);
    }

    /** 영문명이 없는 Entity 생성. */
    public static CanonicalOptionValueJpaEntity entityWithoutNameEn() {
        Instant now = Instant.now();
        return CanonicalOptionValueJpaEntity.create(
                6L,
                DEFAULT_GROUP_ID,
                DEFAULT_CODE,
                DEFAULT_NAME_KO,
                null,
                DEFAULT_SORT_ORDER,
                now,
                now);
    }

    /** COLOR 그룹의 RED 옵션 값 생성. */
    public static CanonicalOptionValueJpaEntity colorRedEntity() {
        Instant now = Instant.now();
        return CanonicalOptionValueJpaEntity.create(1L, 1L, "RED", "빨강", "Red", 1, now, now);
    }

    /** COLOR 그룹의 BLUE 옵션 값 생성. */
    public static CanonicalOptionValueJpaEntity colorBlueEntity() {
        Instant now = Instant.now();
        return CanonicalOptionValueJpaEntity.create(2L, 1L, "BLUE", "파랑", "Blue", 2, now, now);
    }

    /** SIZE 그룹의 SMALL 옵션 값 생성. */
    public static CanonicalOptionValueJpaEntity sizeSmallEntity() {
        Instant now = Instant.now();
        return CanonicalOptionValueJpaEntity.create(3L, 2L, "SMALL", "소형", "Small", 1, now, now);
    }

    /** SIZE 그룹의 MEDIUM 옵션 값 생성. */
    public static CanonicalOptionValueJpaEntity sizeMediumEntity() {
        Instant now = Instant.now();
        return CanonicalOptionValueJpaEntity.create(4L, 2L, "MEDIUM", "중형", "Medium", 2, now, now);
    }

    /** SIZE 그룹의 LARGE 옵션 값 생성. */
    public static CanonicalOptionValueJpaEntity sizeLargeEntity() {
        Instant now = Instant.now();
        return CanonicalOptionValueJpaEntity.create(5L, 2L, "LARGE", "대형", "Large", 3, now, now);
    }
}
