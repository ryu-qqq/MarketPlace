package com.ryuqq.marketplace.adapter.in.rest.selleradmin;

import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.query.VerifySellerAdminApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.response.VerifySellerAdminApiResponse;
import com.ryuqq.marketplace.application.selleradmin.dto.response.VerifySellerAdminResult;

/**
 * SellerAdminPublic API 테스트 Fixtures.
 *
 * <p>SellerAdminPublic REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class SellerAdminPublicApiFixtures {

    private SellerAdminPublicApiFixtures() {}

    // ===== 상수 =====
    public static final String DEFAULT_NAME = "홍길동";
    public static final String DEFAULT_LOGIN_ID = "seller01";
    public static final String DEFAULT_SELLER_ADMIN_ID = "01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f60";
    public static final String DEFAULT_STATUS = "ACTIVE";
    public static final String DEFAULT_PHONE_NUMBER = "010-1234-5678";

    // ===== VerifySellerAdminApiRequest =====

    public static VerifySellerAdminApiRequest verifyRequest() {
        return new VerifySellerAdminApiRequest(DEFAULT_NAME, DEFAULT_LOGIN_ID);
    }

    // ===== VerifySellerAdminResult (Application) =====

    public static VerifySellerAdminResult foundResult() {
        return VerifySellerAdminResult.of(
                DEFAULT_STATUS, DEFAULT_SELLER_ADMIN_ID, DEFAULT_PHONE_NUMBER);
    }

    // ===== VerifySellerAdminApiResponse (API) =====

    public static VerifySellerAdminApiResponse verifyFoundResponse() {
        return new VerifySellerAdminApiResponse(
                true, DEFAULT_STATUS, DEFAULT_SELLER_ADMIN_ID, DEFAULT_PHONE_NUMBER);
    }
}
