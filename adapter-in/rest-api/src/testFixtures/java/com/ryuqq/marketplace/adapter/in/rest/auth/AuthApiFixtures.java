package com.ryuqq.marketplace.adapter.in.rest.auth;

import com.ryuqq.marketplace.adapter.in.rest.auth.dto.command.LoginApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.auth.dto.response.LoginApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.auth.dto.response.MyInfoApiResponse;
import com.ryuqq.marketplace.application.auth.dto.response.LoginResult;
import com.ryuqq.marketplace.application.auth.dto.response.MyInfoResult;
import java.util.List;

/**
 * Auth API 테스트 Fixtures.
 *
 * <p>Auth REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class AuthApiFixtures {

    private AuthApiFixtures() {}

    // ===== 상수 =====
    public static final String DEFAULT_IDENTIFIER = "admin@example.com";
    public static final String DEFAULT_PASSWORD = "password123!";
    public static final String DEFAULT_USER_ID = "user-123";
    public static final String DEFAULT_ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiJ9.access";
    public static final String DEFAULT_REFRESH_TOKEN = "eyJhbGciOiJIUzI1NiJ9.refresh";
    public static final String DEFAULT_TOKEN_TYPE = "Bearer";
    public static final long DEFAULT_EXPIRES_IN = 3600L;

    public static final String DEFAULT_EMAIL = "admin@example.com";
    public static final String DEFAULT_NAME = "관리자";
    public static final String DEFAULT_TENANT_ID = "tenant-123";
    public static final String DEFAULT_TENANT_NAME = "테넌트명";
    public static final String DEFAULT_ORG_ID = "org-123";
    public static final String DEFAULT_ORG_NAME = "조직명";

    // ===== LoginApiRequest =====

    public static LoginApiRequest loginRequest() {
        return new LoginApiRequest(DEFAULT_IDENTIFIER, DEFAULT_PASSWORD);
    }

    public static LoginApiRequest loginRequest(String identifier, String password) {
        return new LoginApiRequest(identifier, password);
    }

    // ===== LoginResult (Application) =====

    public static LoginResult successLoginResult() {
        return LoginResult.success(
                DEFAULT_USER_ID,
                DEFAULT_ACCESS_TOKEN,
                DEFAULT_REFRESH_TOKEN,
                DEFAULT_EXPIRES_IN,
                DEFAULT_TOKEN_TYPE);
    }

    public static LoginResult failureLoginResult() {
        return LoginResult.failure("AUTH-001", "Invalid credentials");
    }

    // ===== MyInfoResult (Application) =====

    public static MyInfoResult myInfoResult() {
        return new MyInfoResult(
                DEFAULT_USER_ID,
                DEFAULT_EMAIL,
                DEFAULT_NAME,
                DEFAULT_TENANT_ID,
                DEFAULT_TENANT_NAME,
                DEFAULT_ORG_ID,
                DEFAULT_ORG_NAME,
                defaultRoles(),
                List.of("READ", "WRITE", "DELETE"),
                "sa-001",
                1L,
                "010-1234-5678");
    }

    public static MyInfoResult myInfoResultWithNullRoles() {
        return new MyInfoResult(
                DEFAULT_USER_ID,
                DEFAULT_EMAIL,
                DEFAULT_NAME,
                DEFAULT_TENANT_ID,
                DEFAULT_TENANT_NAME,
                DEFAULT_ORG_ID,
                DEFAULT_ORG_NAME,
                null,
                List.of("READ"),
                null,
                null,
                null);
    }

    public static MyInfoResult myInfoResultWithEmptyRoles() {
        return new MyInfoResult(
                DEFAULT_USER_ID,
                DEFAULT_EMAIL,
                DEFAULT_NAME,
                DEFAULT_TENANT_ID,
                DEFAULT_TENANT_NAME,
                DEFAULT_ORG_ID,
                DEFAULT_ORG_NAME,
                List.of(),
                List.of(),
                null,
                null,
                null);
    }

    public static List<MyInfoResult.RoleInfo> defaultRoles() {
        return List.of(
                new MyInfoResult.RoleInfo("role-1", "ADMIN"),
                new MyInfoResult.RoleInfo("role-2", "MANAGER"));
    }

    // ===== LoginApiResponse =====

    public static LoginApiResponse loginApiResponse() {
        return new LoginApiResponse(
                DEFAULT_ACCESS_TOKEN, DEFAULT_REFRESH_TOKEN,
                DEFAULT_TOKEN_TYPE, DEFAULT_EXPIRES_IN);
    }

    // ===== MyInfoApiResponse =====

    public static MyInfoApiResponse myInfoApiResponse() {
        return MyInfoApiResponse.from(myInfoResult());
    }
}
