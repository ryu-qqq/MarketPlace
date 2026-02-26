package com.ryuqq.marketplace.adapter.out.persistence.canonicaloption;

import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.entity.CanonicalOptionGroupJpaEntity;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * CanonicalOptionGroupJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 CanonicalOptionGroupJpaEntity 관련 객체들을 생성합니다.
 */
public final class CanonicalOptionGroupJpaEntityFixtures {

    private CanonicalOptionGroupJpaEntityFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final String DEFAULT_CODE = "COLOR";
    public static final String DEFAULT_NAME_KO = "색상";
    public static final String DEFAULT_NAME_EN = "Color";
    public static final boolean DEFAULT_ACTIVE = true;

    // ===== Entity Fixtures =====

    /** 활성 상태의 옵션 그룹 Entity 생성. */
    public static CanonicalOptionGroupJpaEntity activeEntity() {
        Instant now = Instant.now();
        return CanonicalOptionGroupJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_CODE,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_ACTIVE,
                now,
                now);
    }

    /** ID를 지정한 활성 상태 옵션 그룹 Entity 생성. */
    public static CanonicalOptionGroupJpaEntity activeEntity(Long id) {
        Instant now = Instant.now();
        return CanonicalOptionGroupJpaEntity.create(
                id, DEFAULT_CODE, DEFAULT_NAME_KO, DEFAULT_NAME_EN, DEFAULT_ACTIVE, now, now);
    }

    /** 커스텀 코드를 가진 활성 상태 옵션 그룹 Entity 생성. */
    public static CanonicalOptionGroupJpaEntity activeEntityWithCode(String code) {
        Instant now = Instant.now();
        return CanonicalOptionGroupJpaEntity.create(
                null, code, DEFAULT_NAME_KO, DEFAULT_NAME_EN, DEFAULT_ACTIVE, now, now);
    }

    /** 커스텀 이름을 가진 활성 상태 옵션 그룹 Entity 생성. ID는 null로 새 엔티티 생성. 코드는 자동 생성. */
    public static CanonicalOptionGroupJpaEntity activeEntityWithName(String nameKo, String nameEn) {
        Instant now = Instant.now();
        return CanonicalOptionGroupJpaEntity.create(
                null,
                "CODE_" + SEQUENCE.getAndIncrement(),
                nameKo,
                nameEn,
                DEFAULT_ACTIVE,
                now,
                now);
    }

    /** 비활성 상태 옵션 그룹 Entity 생성. */
    public static CanonicalOptionGroupJpaEntity inactiveEntity() {
        Instant now = Instant.now();
        return CanonicalOptionGroupJpaEntity.create(
                2L, DEFAULT_CODE, DEFAULT_NAME_KO, DEFAULT_NAME_EN, false, now, now);
    }

    /** 새로 생성될 Entity (ID가 null). */
    public static CanonicalOptionGroupJpaEntity newEntity() {
        Instant now = Instant.now();
        return CanonicalOptionGroupJpaEntity.create(
                null, DEFAULT_CODE, DEFAULT_NAME_KO, DEFAULT_NAME_EN, DEFAULT_ACTIVE, now, now);
    }

    /** 영문명이 없는 Entity 생성. */
    public static CanonicalOptionGroupJpaEntity entityWithoutNameEn() {
        Instant now = Instant.now();
        return CanonicalOptionGroupJpaEntity.create(
                3L, DEFAULT_CODE, DEFAULT_NAME_KO, null, DEFAULT_ACTIVE, now, now);
    }

    /** 비활성 상태의 새 Entity 생성 (ID는 null). */
    public static CanonicalOptionGroupJpaEntity newInactiveEntity() {
        Instant now = Instant.now();
        return CanonicalOptionGroupJpaEntity.create(
                null, DEFAULT_CODE, DEFAULT_NAME_KO, DEFAULT_NAME_EN, false, now, now);
    }

    /** 커스텀 코드를 가진 비활성 상태 옵션 그룹 Entity 생성. ID는 null로 새 엔티티 생성. */
    public static CanonicalOptionGroupJpaEntity inactiveEntityWithCode(String code) {
        Instant now = Instant.now();
        return CanonicalOptionGroupJpaEntity.create(
                null, code, DEFAULT_NAME_KO, DEFAULT_NAME_EN, false, now, now);
    }

    /** SIZE 옵션 그룹 Entity 생성. */
    public static CanonicalOptionGroupJpaEntity sizeEntity() {
        Instant now = Instant.now();
        return CanonicalOptionGroupJpaEntity.create(2L, "SIZE", "사이즈", "Size", true, now, now);
    }

    /** MATERIAL 옵션 그룹 Entity 생성. */
    public static CanonicalOptionGroupJpaEntity materialEntity() {
        Instant now = Instant.now();
        return CanonicalOptionGroupJpaEntity.create(
                3L, "MATERIAL", "소재", "Material", true, now, now);
    }
}
