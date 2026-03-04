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
    public static final String DEFAULT_PHONE_NUMBER = "01012345678";
    public static final String DEFAULT_STATUS_ACTIVE = "ACTIVE";
    public static final String DEFAULT_STATUS_PENDING = "PENDING_APPROVAL";

    // ===== VerifySellerAdminApiRequest =====

    public static VerifySellerAdminApiRequest verifyRequest() {
        return new VerifySellerAdminApiRequest(DEFAULT_NAME, DEFAULT_PHONE_NUMBER);
    }

    public static VerifySellerAdminApiRequest verifyRequest(String name, String phoneNumber) {
        return new VerifySellerAdminApiRequest(name, phoneNumber);
    }

    // ===== VerifySellerAdminResult (Application) =====

    public static VerifySellerAdminResult foundResult() {
        return VerifySellerAdminResult.found(DEFAULT_STATUS_ACTIVE);
    }

    public static VerifySellerAdminResult foundResult(String status) {
        return VerifySellerAdminResult.found(status);
    }

    public static VerifySellerAdminResult notFoundResult() {
        return VerifySellerAdminResult.notFound();
    }

    // ===== VerifySellerAdminApiResponse (API) =====

    public static VerifySellerAdminApiResponse verifyFoundResponse() {
        return new VerifySellerAdminApiResponse(true, DEFAULT_STATUS_ACTIVE);
    }

    public static VerifySellerAdminApiResponse verifyFoundResponse(String status) {
        return new VerifySellerAdminApiResponse(true, status);
    }

    public static VerifySellerAdminApiResponse verifyNotFoundResponse() {
        return new VerifySellerAdminApiResponse(false, null);
    }
}
