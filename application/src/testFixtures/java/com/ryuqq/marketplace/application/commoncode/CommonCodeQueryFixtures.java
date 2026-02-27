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

    public static CommonCodeSearchParams searchParams(String commonCodeTypeCode) {
        return CommonCodeSearchParams.of(commonCodeTypeCode, null, commonSearchParams(0, 20));
    }

    public static CommonCodeSearchParams searchParams(
            String commonCodeTypeCode, int page, int size) {
        return CommonCodeSearchParams.of(commonCodeTypeCode, null, commonSearchParams(page, size));
    }

    public static CommonCodeSearchParams searchParams(String commonCodeTypeCode, Boolean active) {
        return CommonCodeSearchParams.of(commonCodeTypeCode, active, commonSearchParams(0, 20));
    }

    public static CommonSearchParams commonSearchParams(int page, int size) {
        return CommonSearchParams.of(false, null, null, "DISPLAY_ORDER", "ASC", page, size);
    }
}
