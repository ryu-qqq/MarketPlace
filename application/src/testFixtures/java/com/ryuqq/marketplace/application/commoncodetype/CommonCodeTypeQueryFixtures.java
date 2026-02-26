package com.ryuqq.marketplace.application.commoncodetype;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.commoncodetype.dto.query.CommonCodeTypeSearchParams;

/**
 * CommonCodeType Query DTO 테스트 Fixtures.
 *
 * <p>Application Layer 테스트에서 Query 파라미터 생성에 사용됩니다.
 */
public final class CommonCodeTypeQueryFixtures {

    private CommonCodeTypeQueryFixtures() {}

    // ===== 상수 =====
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 20;

    // ===== CommonCodeTypeSearchParams Fixtures =====

    public static CommonCodeTypeSearchParams searchParams() {
        return new CommonCodeTypeSearchParams(null, null, null, null, defaultCommonSearchParams());
    }

    public static CommonCodeTypeSearchParams searchParams(String searchField, String searchWord) {
        return new CommonCodeTypeSearchParams(
                null, searchField, searchWord, null, defaultCommonSearchParams());
    }

    public static CommonCodeTypeSearchParams searchParams(Boolean active) {
        return new CommonCodeTypeSearchParams(
                active, null, null, null, defaultCommonSearchParams());
    }

    public static CommonCodeTypeSearchParams searchParams(
            Boolean active, String searchField, String searchWord, String type) {
        return new CommonCodeTypeSearchParams(
                active, searchField, searchWord, type, defaultCommonSearchParams());
    }

    public static CommonCodeTypeSearchParams searchParams(int page, int size) {
        return new CommonCodeTypeSearchParams(
                null, null, null, null, commonSearchParams(page, size));
    }

    // ===== CommonSearchParams Fixtures =====

    public static CommonSearchParams defaultCommonSearchParams() {
        return commonSearchParams(DEFAULT_PAGE, DEFAULT_SIZE);
    }

    public static CommonSearchParams commonSearchParams(int page, int size) {
        return new CommonSearchParams(false, null, null, "createdAt", "DESC", page, size);
    }
}
