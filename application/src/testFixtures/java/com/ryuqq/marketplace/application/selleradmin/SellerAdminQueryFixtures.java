package com.ryuqq.marketplace.application.selleradmin;

import com.ryuqq.marketplace.application.selleradmin.dto.query.VerifySellerAdminQuery;
import com.ryuqq.marketplace.application.selleradmin.dto.response.VerifySellerAdminResult;

/**
 * SellerAdmin Application Query 테스트 Fixtures.
 *
 * <p>SellerAdmin 관련 Query 파라미터 및 Result 객체들을 생성하는 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class SellerAdminQueryFixtures {

    private SellerAdminQueryFixtures() {}

    // ===== 공통 상수 =====
    public static final String DEFAULT_NAME = "홍길동";
    public static final String DEFAULT_PHONE_NUMBER = "010-1234-5678";

    // ===== VerifySellerAdminQuery Fixtures =====

    public static VerifySellerAdminQuery verifyQuery() {
        return VerifySellerAdminQuery.of(DEFAULT_NAME, DEFAULT_PHONE_NUMBER);
    }

    public static VerifySellerAdminQuery verifyQuery(String name, String phoneNumber) {
        return VerifySellerAdminQuery.of(name, phoneNumber);
    }

    // ===== VerifySellerAdminResult Fixtures =====

    public static VerifySellerAdminResult foundResult(String status) {
        return VerifySellerAdminResult.found(status);
    }

    public static VerifySellerAdminResult activeFoundResult() {
        return VerifySellerAdminResult.found("ACTIVE");
    }

    public static VerifySellerAdminResult pendingFoundResult() {
        return VerifySellerAdminResult.found("PENDING_APPROVAL");
    }

    public static VerifySellerAdminResult notFoundResult() {
        return VerifySellerAdminResult.notFound();
    }
}
