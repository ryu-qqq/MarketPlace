package com.ryuqq.marketplace.adapter.out.persistence.brand;

import com.ryuqq.marketplace.adapter.out.persistence.brand.entity.BrandJpaEntity;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * BrandJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 BrandJpaEntity 관련 객체들을 생성합니다.
 */
public final class BrandJpaEntityFixtures {

    private BrandJpaEntityFixtures() {}

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final String DEFAULT_CODE = "BRAND001";
    public static final String DEFAULT_NAME_KO = "테스트 브랜드";
    public static final String DEFAULT_NAME_EN = "Test Brand";
    public static final String DEFAULT_SHORT_NAME = "테스트";
    public static final String DEFAULT_STATUS = "ACTIVE";
    public static final String DEFAULT_LOGO_URL = "https://example.com/logo.png";

    // ===== Entity Fixtures =====

    /** 활성 상태의 브랜드 Entity 생성. */
    public static BrandJpaEntity activeEntity() {
        Instant now = Instant.now();
        return BrandJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_CODE,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_SHORT_NAME,
                DEFAULT_STATUS,
                DEFAULT_LOGO_URL,
                now,
                now,
                null);
    }

    /** ID를 지정한 활성 상태 브랜드 Entity 생성. */
    public static BrandJpaEntity activeEntity(Long id) {
        Instant now = Instant.now();
        return BrandJpaEntity.create(
                id,
                DEFAULT_CODE,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_SHORT_NAME,
                DEFAULT_STATUS,
                DEFAULT_LOGO_URL,
                now,
                now,
                null);
    }

    /** 커스텀 코드를 가진 활성 상태 브랜드 Entity 생성. */
    public static BrandJpaEntity activeEntityWithCode(String code) {
        Instant now = Instant.now();
        return BrandJpaEntity.create(
                null,
                code,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_SHORT_NAME,
                DEFAULT_STATUS,
                DEFAULT_LOGO_URL,
                now,
                now,
                null);
    }

    /** 커스텀 이름을 가진 활성 상태 브랜드 Entity 생성. ID는 null로 새 엔티티 생성. 코드는 자동 생성. */
    public static BrandJpaEntity activeEntityWithName(
            String nameKo, String nameEn, String shortName) {
        Instant now = Instant.now();
        return BrandJpaEntity.create(
                null,
                "BRAND_NAME_" + SEQUENCE.getAndIncrement(),
                nameKo,
                nameEn,
                shortName,
                DEFAULT_STATUS,
                DEFAULT_LOGO_URL,
                now,
                now,
                null);
    }

    /** 비활성 상태 브랜드 Entity 생성. */
    public static BrandJpaEntity inactiveEntity() {
        Instant now = Instant.now();
        return BrandJpaEntity.create(
                null,
                DEFAULT_CODE,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_SHORT_NAME,
                "INACTIVE",
                DEFAULT_LOGO_URL,
                now,
                now,
                null);
    }

    /** 삭제된 상태 브랜드 Entity 생성. */
    public static BrandJpaEntity deletedEntity() {
        Instant now = Instant.now();
        return BrandJpaEntity.create(
                null,
                DEFAULT_CODE,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_SHORT_NAME,
                DEFAULT_STATUS,
                DEFAULT_LOGO_URL,
                now,
                now,
                now);
    }

    /** 새로 생성될 Entity (ID가 null). */
    public static BrandJpaEntity newEntity() {
        Instant now = Instant.now();
        return BrandJpaEntity.create(
                null,
                DEFAULT_CODE,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_SHORT_NAME,
                DEFAULT_STATUS,
                DEFAULT_LOGO_URL,
                now,
                now,
                null);
    }

    /** 로고 URL이 없는 Entity 생성. */
    public static BrandJpaEntity entityWithoutLogoUrl() {
        Instant now = Instant.now();
        return BrandJpaEntity.create(
                null,
                DEFAULT_CODE,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_SHORT_NAME,
                DEFAULT_STATUS,
                null,
                now,
                now,
                null);
    }

    /** 영문명이 없는 Entity 생성. */
    public static BrandJpaEntity entityWithoutNameEn() {
        Instant now = Instant.now();
        return BrandJpaEntity.create(
                null,
                DEFAULT_CODE,
                DEFAULT_NAME_KO,
                null,
                DEFAULT_SHORT_NAME,
                DEFAULT_STATUS,
                DEFAULT_LOGO_URL,
                now,
                now,
                null);
    }

    /** 약칭이 없는 Entity 생성. */
    public static BrandJpaEntity entityWithoutShortName() {
        Instant now = Instant.now();
        return BrandJpaEntity.create(
                null,
                DEFAULT_CODE,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                null,
                DEFAULT_STATUS,
                DEFAULT_LOGO_URL,
                now,
                now,
                null);
    }

    /** 로고 URL이 없는 새 Entity 생성 (ID는 null). */
    public static BrandJpaEntity newEntityWithoutLogoUrl() {
        Instant now = Instant.now();
        return BrandJpaEntity.create(
                null,
                DEFAULT_CODE,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_SHORT_NAME,
                DEFAULT_STATUS,
                null,
                now,
                now,
                null);
    }

    /** 비활성 상태의 새 Entity 생성 (ID는 null). */
    public static BrandJpaEntity newInactiveEntity() {
        Instant now = Instant.now();
        return BrandJpaEntity.create(
                null,
                DEFAULT_CODE,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_SHORT_NAME,
                "INACTIVE",
                DEFAULT_LOGO_URL,
                now,
                now,
                null);
    }

    /** 삭제된 상태의 새 Entity 생성 (ID는 null). */
    public static BrandJpaEntity newDeletedEntity() {
        Instant now = Instant.now();
        return BrandJpaEntity.create(
                null,
                DEFAULT_CODE,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_SHORT_NAME,
                DEFAULT_STATUS,
                DEFAULT_LOGO_URL,
                now,
                now,
                now);
    }

    /** 커스텀 코드를 가진 비활성 상태 브랜드 Entity 생성. ID는 null로 새 엔티티 생성. */
    public static BrandJpaEntity inactiveEntityWithCode(String code) {
        Instant now = Instant.now();
        return BrandJpaEntity.create(
                null,
                code,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_SHORT_NAME,
                "INACTIVE",
                DEFAULT_LOGO_URL,
                now,
                now,
                null);
    }

    /** 커스텀 코드를 가진 삭제 상태 브랜드 Entity 생성. ID는 null로 새 엔티티 생성. */
    public static BrandJpaEntity deletedEntityWithCode(String code) {
        Instant now = Instant.now();
        return BrandJpaEntity.create(
                null,
                code,
                DEFAULT_NAME_KO,
                DEFAULT_NAME_EN,
                DEFAULT_SHORT_NAME,
                DEFAULT_STATUS,
                DEFAULT_LOGO_URL,
                now,
                now,
                now);
    }
}
