package com.ryuqq.marketplace.domain.commoncodetype;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.commoncodetype.aggregate.CommonCodeType;
import com.ryuqq.marketplace.domain.commoncodetype.aggregate.CommonCodeTypeUpdateData;
import com.ryuqq.marketplace.domain.commoncodetype.id.CommonCodeTypeId;
import java.time.Instant;

/**
 * CommonCodeType 도메인 테스트 Fixtures.
 *
 * <p>테스트에서 CommonCodeType 관련 객체들을 생성합니다.
 */
public final class CommonCodeTypeFixtures {

    private CommonCodeTypeFixtures() {}

    // ===== 상수 =====
    public static final String DEFAULT_CODE = "PAYMENT_METHOD";
    public static final String DEFAULT_NAME = "결제수단";
    public static final String DEFAULT_DESCRIPTION = "결제수단 공통코드 타입";
    public static final int DEFAULT_DISPLAY_ORDER = 1;

    // ===== CommonCodeType Aggregate Fixtures =====
    public static CommonCodeType newCommonCodeType() {
        return CommonCodeType.forNew(
                DEFAULT_CODE,
                DEFAULT_NAME,
                DEFAULT_DESCRIPTION,
                DEFAULT_DISPLAY_ORDER,
                CommonVoFixtures.now());
    }

    public static CommonCodeType newCommonCodeType(String code, String name) {
        return CommonCodeType.forNew(
                code, name, DEFAULT_DESCRIPTION, DEFAULT_DISPLAY_ORDER, CommonVoFixtures.now());
    }

    public static CommonCodeType activeCommonCodeType() {
        return CommonCodeType.reconstitute(
                CommonCodeTypeId.of(1L),
                DEFAULT_CODE,
                DEFAULT_NAME,
                DEFAULT_DESCRIPTION,
                DEFAULT_DISPLAY_ORDER,
                true,
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static CommonCodeType activeCommonCodeType(Long id) {
        return CommonCodeType.reconstitute(
                CommonCodeTypeId.of(id),
                DEFAULT_CODE,
                DEFAULT_NAME,
                DEFAULT_DESCRIPTION,
                DEFAULT_DISPLAY_ORDER,
                true,
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static CommonCodeType inactiveCommonCodeType() {
        return CommonCodeType.reconstitute(
                CommonCodeTypeId.of(2L),
                DEFAULT_CODE,
                DEFAULT_NAME,
                DEFAULT_DESCRIPTION,
                DEFAULT_DISPLAY_ORDER,
                false,
                null,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    public static CommonCodeType deletedCommonCodeType() {
        Instant deletedAt = CommonVoFixtures.yesterday();
        return CommonCodeType.reconstitute(
                CommonCodeTypeId.of(3L),
                DEFAULT_CODE,
                DEFAULT_NAME,
                DEFAULT_DESCRIPTION,
                DEFAULT_DISPLAY_ORDER,
                false,
                deletedAt,
                CommonVoFixtures.yesterday(),
                CommonVoFixtures.yesterday());
    }

    // ===== CommonCodeTypeUpdateData Fixtures =====
    public static CommonCodeTypeUpdateData commonCodeTypeUpdateData() {
        return CommonCodeTypeUpdateData.of("수정된 이름", "수정된 설명", 2);
    }

    public static CommonCodeTypeUpdateData commonCodeTypeUpdateData(
            String name, String description, int displayOrder) {
        return CommonCodeTypeUpdateData.of(name, description, displayOrder);
    }
}
