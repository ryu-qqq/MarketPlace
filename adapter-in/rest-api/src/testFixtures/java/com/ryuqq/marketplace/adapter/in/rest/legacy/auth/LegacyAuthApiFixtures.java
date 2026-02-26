package com.ryuqq.marketplace.adapter.in.rest.legacy.auth;

import com.ryuqq.marketplace.adapter.in.rest.legacy.auth.dto.request.LegacyCreateAuthTokenRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.auth.dto.response.LegacyAuthTokenResponse;
import com.ryuqq.marketplace.application.legacyauth.dto.command.LegacyLoginCommand;

/**
 * Legacy Auth API 테스트 Fixtures.
 *
 * <p>Legacy 인증 REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class LegacyAuthApiFixtures {

    private LegacyAuthApiFixtures() {}

    // ===== 상수 =====
    public static final String DEFAULT_USER_ID = "testUser";
    public static final String DEFAULT_PASSWORD = "testPassword";
    public static final String DEFAULT_ROLE_TYPE = "SELLER";
    public static final String DEFAULT_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token";

    // ===== Request Fixtures =====

    public static LegacyCreateAuthTokenRequest request() {
        return new LegacyCreateAuthTokenRequest(
                DEFAULT_USER_ID, DEFAULT_PASSWORD, DEFAULT_ROLE_TYPE);
    }

    public static LegacyCreateAuthTokenRequest requestWith(
            String userId, String password, String roleType) {
        return new LegacyCreateAuthTokenRequest(userId, password, roleType);
    }

    // ===== Command Fixtures =====

    public static LegacyLoginCommand command() {
        return new LegacyLoginCommand(DEFAULT_USER_ID, DEFAULT_PASSWORD);
    }

    // ===== Response Fixtures =====

    public static LegacyAuthTokenResponse authTokenResponse() {
        return new LegacyAuthTokenResponse(DEFAULT_TOKEN);
    }

    public static LegacyAuthTokenResponse authTokenResponse(String token) {
        return new LegacyAuthTokenResponse(token);
    }
}
