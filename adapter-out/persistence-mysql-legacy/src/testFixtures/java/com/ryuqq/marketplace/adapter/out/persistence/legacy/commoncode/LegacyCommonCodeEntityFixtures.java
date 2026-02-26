package com.ryuqq.marketplace.adapter.out.persistence.legacy.commoncode;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.commoncode.entity.LegacyCommonCodeEntity;

/**
 * LegacyCommonCodeEntity 테스트 Fixtures.
 *
 * <p>테스트에서 LegacyCommonCodeEntity 관련 객체들을 생성합니다.
 */
public final class LegacyCommonCodeEntityFixtures {

    private LegacyCommonCodeEntityFixtures() {}

    // ===== 기본 상수 =====
    public static final Long DEFAULT_ID = 1L;
    public static final Long DEFAULT_CODE_GROUP_ID = 100L;
    public static final String DEFAULT_CODE_DETAIL = "CREDIT_CARD";
    public static final String DEFAULT_CODE_DETAIL_DISPLAY_NAME = "신용카드";
    public static final Integer DEFAULT_DISPLAY_ORDER = 1;
    public static final String DELETE_YN_NO = "N";
    public static final String DELETE_YN_YES = "Y";

    // ===== Entity Fixtures =====

    /** 활성(미삭제) 상태의 공통 코드 Entity 생성. */
    public static LegacyCommonCodeEntity activeEntity() {
        return LegacyCommonCodeEntity.create(
                DEFAULT_ID,
                DEFAULT_CODE_GROUP_ID,
                DEFAULT_CODE_DETAIL,
                DEFAULT_CODE_DETAIL_DISPLAY_NAME,
                DEFAULT_DISPLAY_ORDER,
                DELETE_YN_NO);
    }

    /** ID를 지정한 활성 상태 공통 코드 Entity 생성. */
    public static LegacyCommonCodeEntity activeEntity(Long id) {
        return LegacyCommonCodeEntity.create(
                id,
                DEFAULT_CODE_GROUP_ID,
                DEFAULT_CODE_DETAIL,
                DEFAULT_CODE_DETAIL_DISPLAY_NAME,
                DEFAULT_DISPLAY_ORDER,
                DELETE_YN_NO);
    }

    /** ID와 코드 그룹 ID를 지정한 활성 상태 공통 코드 Entity 생성. */
    public static LegacyCommonCodeEntity activeEntity(Long id, Long codeGroupId) {
        return LegacyCommonCodeEntity.create(
                id,
                codeGroupId,
                DEFAULT_CODE_DETAIL,
                DEFAULT_CODE_DETAIL_DISPLAY_NAME,
                DEFAULT_DISPLAY_ORDER,
                DELETE_YN_NO);
    }

    /** 커스텀 코드 상세 값을 가진 활성 상태 공통 코드 Entity 생성. */
    public static LegacyCommonCodeEntity activeEntityWithCodeDetail(
            String codeDetail, String displayName) {
        return LegacyCommonCodeEntity.create(
                DEFAULT_ID,
                DEFAULT_CODE_GROUP_ID,
                codeDetail,
                displayName,
                DEFAULT_DISPLAY_ORDER,
                DELETE_YN_NO);
    }

    /** 삭제된(DELETE_YN = 'Y') 공통 코드 Entity 생성. */
    public static LegacyCommonCodeEntity deletedEntity() {
        return LegacyCommonCodeEntity.create(
                2L,
                DEFAULT_CODE_GROUP_ID,
                DEFAULT_CODE_DETAIL,
                DEFAULT_CODE_DETAIL_DISPLAY_NAME,
                DEFAULT_DISPLAY_ORDER,
                DELETE_YN_YES);
    }

    /** ID가 null인 새로 생성될 Entity (DB 저장 전 상태). */
    public static LegacyCommonCodeEntity newEntity() {
        return LegacyCommonCodeEntity.create(
                null,
                DEFAULT_CODE_GROUP_ID,
                DEFAULT_CODE_DETAIL,
                DEFAULT_CODE_DETAIL_DISPLAY_NAME,
                DEFAULT_DISPLAY_ORDER,
                DELETE_YN_NO);
    }

    /** 코드 그룹 ID를 지정한 새 Entity 생성 (ID는 null). */
    public static LegacyCommonCodeEntity newEntityWithGroupId(Long codeGroupId) {
        return LegacyCommonCodeEntity.create(
                null,
                codeGroupId,
                DEFAULT_CODE_DETAIL,
                DEFAULT_CODE_DETAIL_DISPLAY_NAME,
                DEFAULT_DISPLAY_ORDER,
                DELETE_YN_NO);
    }

    /** 코드 그룹 ID와 코드 상세를 지정한 새 Entity 생성 (ID는 null). */
    public static LegacyCommonCodeEntity newEntityWithGroupIdAndDetail(
            Long codeGroupId, String codeDetail, String displayName) {
        return LegacyCommonCodeEntity.create(
                null, codeGroupId, codeDetail, displayName, DEFAULT_DISPLAY_ORDER, DELETE_YN_NO);
    }

    /** 삭제 상태의 새 Entity 생성 (ID는 null). */
    public static LegacyCommonCodeEntity newDeletedEntity() {
        return LegacyCommonCodeEntity.create(
                null,
                DEFAULT_CODE_GROUP_ID,
                DEFAULT_CODE_DETAIL,
                DEFAULT_CODE_DETAIL_DISPLAY_NAME,
                DEFAULT_DISPLAY_ORDER,
                DELETE_YN_YES);
    }

    /** 표시 순서를 지정한 Entity 생성. */
    public static LegacyCommonCodeEntity entityWithDisplayOrder(Integer displayOrder) {
        return LegacyCommonCodeEntity.create(
                DEFAULT_ID,
                DEFAULT_CODE_GROUP_ID,
                DEFAULT_CODE_DETAIL,
                DEFAULT_CODE_DETAIL_DISPLAY_NAME,
                displayOrder,
                DELETE_YN_NO);
    }
}
