package com.ryuqq.marketplace.adapter.out.persistence.commoncode;

import com.ryuqq.marketplace.adapter.out.persistence.commoncode.entity.CommonCodeJpaEntity;
import java.time.Instant;

/**
 * CommonCodeJpaEntity 테스트 Fixtures.
 *
 * <p>테스트에서 CommonCodeJpaEntity 관련 객체들을 생성합니다.
 */
public final class CommonCodeJpaEntityFixtures {

    private CommonCodeJpaEntityFixtures() {}

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final Long DEFAULT_COMMON_CODE_TYPE_ID = 1L;
    public static final String DEFAULT_CODE = "CREDIT_CARD";
    public static final String DEFAULT_DISPLAY_NAME = "신용카드";
    public static final int DEFAULT_DISPLAY_ORDER = 1;

    // ===== Entity Fixtures =====

    /** 활성 상태의 공통 코드 Entity 생성. */
    public static CommonCodeJpaEntity activeEntity() {
        Instant now = Instant.now();
        return CommonCodeJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_COMMON_CODE_TYPE_ID,
                DEFAULT_CODE,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_DISPLAY_ORDER,
                true,
                now,
                now,
                null);
    }

    /** ID를 지정한 활성 상태 공통 코드 Entity 생성. */
    public static CommonCodeJpaEntity activeEntity(Long id) {
        Instant now = Instant.now();
        return CommonCodeJpaEntity.create(
                id,
                DEFAULT_COMMON_CODE_TYPE_ID,
                DEFAULT_CODE,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_DISPLAY_ORDER,
                true,
                now,
                now,
                null);
    }

    /** ID와 코드 타입 ID를 지정한 활성 상태 공통 코드 Entity 생성. */
    public static CommonCodeJpaEntity activeEntity(Long id, Long commonCodeTypeId) {
        Instant now = Instant.now();
        return CommonCodeJpaEntity.create(
                id,
                commonCodeTypeId,
                DEFAULT_CODE,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_DISPLAY_ORDER,
                true,
                now,
                now,
                null);
    }

    /** 커스텀 코드를 가진 활성 상태 공통 코드 Entity 생성. */
    public static CommonCodeJpaEntity activeEntityWithCode(String code, String displayName) {
        Instant now = Instant.now();
        return CommonCodeJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_COMMON_CODE_TYPE_ID,
                code,
                displayName,
                DEFAULT_DISPLAY_ORDER,
                true,
                now,
                now,
                null);
    }

    /** 비활성 상태 공통 코드 Entity 생성. */
    public static CommonCodeJpaEntity inactiveEntity() {
        Instant now = Instant.now();
        return CommonCodeJpaEntity.create(
                2L,
                DEFAULT_COMMON_CODE_TYPE_ID,
                DEFAULT_CODE,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_DISPLAY_ORDER,
                false,
                now,
                now,
                null);
    }

    /** 삭제된 상태 공통 코드 Entity 생성. */
    public static CommonCodeJpaEntity deletedEntity() {
        Instant now = Instant.now();
        return CommonCodeJpaEntity.create(
                3L,
                DEFAULT_COMMON_CODE_TYPE_ID,
                DEFAULT_CODE,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_DISPLAY_ORDER,
                false,
                now,
                now,
                now);
    }

    /** 새로 생성될 Entity (ID가 null). */
    public static CommonCodeJpaEntity newEntity() {
        Instant now = Instant.now();
        return CommonCodeJpaEntity.create(
                null,
                DEFAULT_COMMON_CODE_TYPE_ID,
                DEFAULT_CODE,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_DISPLAY_ORDER,
                true,
                now,
                now,
                null);
    }

    /** 다른 표시 순서를 가진 Entity 생성. */
    public static CommonCodeJpaEntity entityWithDisplayOrder(int displayOrder) {
        Instant now = Instant.now();
        return CommonCodeJpaEntity.create(
                DEFAULT_ID,
                DEFAULT_COMMON_CODE_TYPE_ID,
                DEFAULT_CODE,
                DEFAULT_DISPLAY_NAME,
                displayOrder,
                true,
                now,
                now,
                null);
    }

    /** 커스텀 타입 ID, 코드, 표시명을 가진 새 Entity 생성 (ID는 null). */
    public static CommonCodeJpaEntity newEntityWithTypeIdAndCode(
            Long commonCodeTypeId, String code, String displayName) {
        Instant now = Instant.now();
        return CommonCodeJpaEntity.create(
                null,
                commonCodeTypeId,
                code,
                displayName,
                DEFAULT_DISPLAY_ORDER,
                true,
                now,
                now,
                null);
    }

    /** 타입 ID를 지정한 새 Entity 생성 (ID는 null). */
    public static CommonCodeJpaEntity newEntityWithTypeId(Long commonCodeTypeId) {
        Instant now = Instant.now();
        return CommonCodeJpaEntity.create(
                null,
                commonCodeTypeId,
                DEFAULT_CODE,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_DISPLAY_ORDER,
                true,
                now,
                now,
                null);
    }

    /** 비활성 상태의 새 Entity 생성 (ID는 null). */
    public static CommonCodeJpaEntity newInactiveEntity() {
        Instant now = Instant.now();
        return CommonCodeJpaEntity.create(
                null,
                DEFAULT_COMMON_CODE_TYPE_ID,
                DEFAULT_CODE,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_DISPLAY_ORDER,
                false,
                now,
                now,
                null);
    }

    /** 커스텀 표시 순서를 가진 새 Entity 생성 (ID는 null). */
    public static CommonCodeJpaEntity newEntityWithDisplayOrder(int displayOrder) {
        Instant now = Instant.now();
        return CommonCodeJpaEntity.create(
                null,
                DEFAULT_COMMON_CODE_TYPE_ID,
                DEFAULT_CODE,
                DEFAULT_DISPLAY_NAME,
                displayOrder,
                true,
                now,
                now,
                null);
    }
}
