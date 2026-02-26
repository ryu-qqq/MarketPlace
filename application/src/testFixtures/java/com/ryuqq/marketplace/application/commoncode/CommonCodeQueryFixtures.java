package com.ryuqq.marketplace.application.commoncode;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.commoncode.dto.query.CommonCodeSearchParams;

/**
 * CommonCode Query 테스트 Fixtures.
 *
 * <p>CommonCode 관련 Query 객체들을 생성하는 테스트 유틸리티입니다.
 */
public final class CommonCodeQueryFixtures {

    private CommonCodeQueryFixtures() {}

    public static CommonCodeSearchParams searchParams(Long commonCodeTypeId) {
        return CommonCodeSearchParams.of(commonCodeTypeId, null, null, commonSearchParams(0, 20));
    }

    public static CommonCodeSearchParams searchParams(Long commonCodeTypeId, int page, int size) {
        return CommonCodeSearchParams.of(
                commonCodeTypeId, null, null, commonSearchParams(page, size));
    }

    public static CommonCodeSearchParams searchParams(
            Long commonCodeTypeId, Boolean active, String code) {
        return CommonCodeSearchParams.of(commonCodeTypeId, active, code, commonSearchParams(0, 20));
    }

    public static CommonSearchParams commonSearchParams(int page, int size) {
        return CommonSearchParams.of(false, null, null, "DISPLAY_ORDER", "ASC", page, size);
    }
}
