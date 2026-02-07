package com.ryuqq.marketplace.adapter.out.persistence.commoncodetype;

import com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.entity.CommonCodeTypeJpaEntity;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * CommonCodeTypeJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 CommonCodeTypeJpaEntity 관련 객체들을 생성합니다.
 */
public final class CommonCodeTypeJpaEntityFixtures {

    private CommonCodeTypeJpaEntityFixtures() {}

    /** displayOrder 중복 방지를 위한 카운터. */
    private static final AtomicInteger DISPLAY_ORDER_COUNTER = new AtomicInteger(1);

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final String DEFAULT_CODE = "PAYMENT_METHOD";
    public static final String DEFAULT_NAME = "결제수단";
    public static final String DEFAULT_DESCRIPTION = "결제수단 공통코드 타입";
    public static final int DEFAULT_DISPLAY_ORDER = 1;

    // ===== Entity Fixtures =====

    /** 활성 상태의 공통 코드 타입 Entity 생성. */
    public static CommonCodeTypeJpaEntity activeEntity() {
        Instant now = Instant.now();
        return CommonCodeTypeJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_CODE,
                DEFAULT_NAME,
                DEFAULT_DESCRIPTION,
                DEFAULT_DISPLAY_ORDER,
                true,
                now,
                now,
                null);
    }

    /** ID를 지정한 활성 상태 공통 코드 타입 Entity 생성. */
    public static CommonCodeTypeJpaEntity activeEntity(Long id) {
        Instant now = Instant.now();
        return CommonCodeTypeJpaEntity.create(
                id,
                DEFAULT_CODE,
                DEFAULT_NAME,
                DEFAULT_DESCRIPTION,
                DEFAULT_DISPLAY_ORDER,
                true,
                now,
                now,
                null);
    }

    /** 커스텀 코드를 가진 활성 상태 공통 코드 타입 Entity 생성. */
    public static CommonCodeTypeJpaEntity activeEntityWithCode(String code, String name) {
        Instant now = Instant.now();
        return CommonCodeTypeJpaEntity.create(
                DEFAULT_ID,
                code,
                name,
                DEFAULT_DESCRIPTION,
                DEFAULT_DISPLAY_ORDER,
                true,
                now,
                now,
                null);
    }

    /** 비활성 상태 공통 코드 타입 Entity 생성. */
    public static CommonCodeTypeJpaEntity inactiveEntity() {
        Instant now = Instant.now();
        return CommonCodeTypeJpaEntity.create(
                2L,
                DEFAULT_CODE,
                DEFAULT_NAME,
                DEFAULT_DESCRIPTION,
                DEFAULT_DISPLAY_ORDER,
                false,
                now,
                now,
                null);
    }

    /** 삭제된 상태 공통 코드 타입 Entity 생성. */
    public static CommonCodeTypeJpaEntity deletedEntity() {
        Instant now = Instant.now();
        return CommonCodeTypeJpaEntity.create(
                3L,
                DEFAULT_CODE,
                DEFAULT_NAME,
                DEFAULT_DESCRIPTION,
                DEFAULT_DISPLAY_ORDER,
                false,
                now,
                now,
                now);
    }

    /** 새로 생성될 Entity (ID가 null). */
    public static CommonCodeTypeJpaEntity newEntity() {
        Instant now = Instant.now();
        return CommonCodeTypeJpaEntity.create(
                null,
                DEFAULT_CODE,
                DEFAULT_NAME,
                DEFAULT_DESCRIPTION,
                DEFAULT_DISPLAY_ORDER,
                true,
                now,
                now,
                null);
    }

    /** 설명이 없는 Entity 생성. */
    public static CommonCodeTypeJpaEntity entityWithoutDescription() {
        Instant now = Instant.now();
        return CommonCodeTypeJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_CODE,
                DEFAULT_NAME,
                null,
                DEFAULT_DISPLAY_ORDER,
                true,
                now,
                now,
                null);
    }

    /** 커스텀 코드와 이름을 가진 새 Entity 생성 (ID는 null). displayOrder는 자동 증가. */
    public static CommonCodeTypeJpaEntity newEntityWithCode(String code, String name) {
        Instant now = Instant.now();
        return CommonCodeTypeJpaEntity.create(
                null,
                code,
                name,
                DEFAULT_DESCRIPTION,
                DISPLAY_ORDER_COUNTER.getAndIncrement(),
                true,
                now,
                now,
                null);
    }

    /** 비활성 상태의 새 Entity 생성 (ID는 null). displayOrder는 자동 증가. */
    public static CommonCodeTypeJpaEntity newInactiveEntity() {
        Instant now = Instant.now();
        return CommonCodeTypeJpaEntity.create(
                null,
                "INACTIVE_" + DISPLAY_ORDER_COUNTER.get(),
                "비활성 타입",
                DEFAULT_DESCRIPTION,
                DISPLAY_ORDER_COUNTER.getAndIncrement(),
                false,
                now,
                now,
                null);
    }

    /** 설명이 없는 새 Entity 생성 (ID는 null). */
    public static CommonCodeTypeJpaEntity newEntityWithoutDescription() {
        Instant now = Instant.now();
        return CommonCodeTypeJpaEntity.create(
                null,
                DEFAULT_CODE,
                DEFAULT_NAME,
                null,
                DEFAULT_DISPLAY_ORDER,
                true,
                now,
                now,
                null);
    }
}
