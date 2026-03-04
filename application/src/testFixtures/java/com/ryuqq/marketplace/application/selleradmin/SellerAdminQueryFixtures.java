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
    public static final String DEFAULT_LOGIN_ID = "seller01";
    public static final String DEFAULT_SELLER_ADMIN_ID = "01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f60";
    public static final String DEFAULT_STATUS = "ACTIVE";
    public static final String DEFAULT_PHONE_NUMBER = "010-1234-5678";

    // ===== VerifySellerAdminQuery Fixtures =====

    public static VerifySellerAdminQuery verifyQuery() {
        return VerifySellerAdminQuery.of(DEFAULT_NAME, DEFAULT_LOGIN_ID);
    }

    public static VerifySellerAdminQuery verifyQuery(String name, String loginId) {
        return VerifySellerAdminQuery.of(name, loginId);
    }

    // ===== VerifySellerAdminResult Fixtures =====

    public static VerifySellerAdminResult foundResult() {
        return VerifySellerAdminResult.of(DEFAULT_STATUS, DEFAULT_SELLER_ADMIN_ID, DEFAULT_PHONE_NUMBER);
    }
}
