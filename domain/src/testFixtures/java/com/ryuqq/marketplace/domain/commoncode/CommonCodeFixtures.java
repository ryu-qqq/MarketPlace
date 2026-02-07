package com.ryuqq.marketplace.domain.commoncode;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.commoncode.aggregate.CommonCode;
import com.ryuqq.marketplace.domain.commoncode.aggregate.CommonCodeUpdateData;
import com.ryuqq.marketplace.domain.commoncode.id.CommonCodeId;
import com.ryuqq.marketplace.domain.commoncodetype.id.CommonCodeTypeId;
import java.time.Instant;

/**
 * CommonCode 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 CommonCode 관련 객체들을 생성합니다.
 */
public final class CommonCodeFixtures {

    private CommonCodeFixtures() {}

    // ===== 상수 =====
    public static final String DEFAULT_CODE = "CREDIT_CARD";
    public static final String DEFAULT_DISPLAY_NAME = "신용카드";
    public static final int DEFAULT_DISPLAY_ORDER = 1;

    // ===== CommonCode Aggregate Fixtures =====
    public static CommonCode newCommonCode() {
        return CommonCode.forNew(
                defaultCommonCodeTypeId(),
                DEFAULT_CODE,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_DISPLAY_ORDER,
                CommonVoFixtures.now());
    }

    public static CommonCode newCommonCode(String code, String displayName) {
        return CommonCode.forNew(
                defaultCommonCodeTypeId(),
                code,
                displayName,
                DEFAULT_DISPLAY_ORDER,
                CommonVoFixtures.now());
    }

    public static CommonCode activeCommonCode() {
        return CommonCode.reconstitute(
                CommonCodeId.of(1L),
                defaultCommonCodeTypeId(),
                DEFAULT_CODE,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_DISPLAY_ORDER,
                true,
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static CommonCode activeCommonCode(Long id) {
        return CommonCode.reconstitute(
                CommonCodeId.of(id),
                defaultCommonCodeTypeId(),
                DEFAULT_CODE,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_DISPLAY_ORDER,
                true,
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static CommonCode inactiveCommonCode() {
        return CommonCode.reconstitute(
                CommonCodeId.of(2L),
                defaultCommonCodeTypeId(),
                DEFAULT_CODE,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_DISPLAY_ORDER,
                false,
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static CommonCode deletedCommonCode() {
        Instant deletedAt = CommonVoFixtures.yesterday();
        return CommonCode.reconstitute(
                CommonCodeId.of(3L),
                defaultCommonCodeTypeId(),
                DEFAULT_CODE,
                DEFAULT_DISPLAY_NAME,
                DEFAULT_DISPLAY_ORDER,
                false,
                deletedAt,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    // ===== CommonCodeTypeId Fixtures =====
    public static CommonCodeTypeId defaultCommonCodeTypeId() {
        return CommonCodeTypeId.of(1L);
    }

    // ===== CommonCodeUpdateData Fixtures =====
    public static CommonCodeUpdateData commonCodeUpdateData() {
        return CommonCodeUpdateData.of("수정된 표시명", 2);
    }

    public static CommonCodeUpdateData commonCodeUpdateData(String displayName, int displayOrder) {
        return CommonCodeUpdateData.of(displayName, displayOrder);
    }
}
