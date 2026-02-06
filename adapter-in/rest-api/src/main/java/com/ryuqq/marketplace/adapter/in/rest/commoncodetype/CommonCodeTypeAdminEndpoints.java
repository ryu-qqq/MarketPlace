package com.ryuqq.marketplace.adapter.in.rest.commoncodetype;

/**
 * CommonCodeTypeAdminEndpoints - 공통 코드 타입 Admin API 엔드포인트 상수.
 *
 * <p>API-END-001: Endpoints final class
 *
 * <p>API-END-002: static final 상수
 *
 * <p>API-END-003: Path Variable 상수
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class CommonCodeTypeAdminEndpoints {

    private CommonCodeTypeAdminEndpoints() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /** 공통 코드 타입 기본 경로 */
    public static final String COMMON_CODE_TYPES = "/api/v1/market/common-code-types";

    /** ID Path Variable */
    public static final String ID = "/{commonCodeTypeId}";

    /** ID Path Variable 이름 */
    public static final String PATH_COMMON_CODE_TYPE_ID = "commonCodeTypeId";

    /** 활성화 상태 변경 경로 */
    public static final String ACTIVE_STATUS = "/active-status";
}
