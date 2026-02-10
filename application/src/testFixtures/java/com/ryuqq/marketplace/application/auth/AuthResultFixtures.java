package com.ryuqq.marketplace.application.auth;

import com.ryuqq.marketplace.application.auth.dto.response.LoginResult;
import com.ryuqq.marketplace.application.auth.dto.response.MyInfoResult;
import java.util.List;

/**
 * Auth Result 테스트 Fixtures.
 *
 * <p>Auth 관련 Result 객체들을 생성하는 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class AuthResultFixtures {

    private AuthResultFixtures() {}

    // ===== 기본 상수 =====
    public static final String DEFAULT_USER_ID = "user-123";
    public static final String DEFAULT_EMAIL = "admin@example.com";
    public static final String DEFAULT_ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.access";
    public static final String DEFAULT_REFRESH_TOKEN =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.refresh";
    public static final String DEFAULT_TOKEN_TYPE = "Bearer";
    public static final long DEFAULT_EXPIRES_IN = 3600L;

    // ===== LoginResult =====

    public static LoginResult successLoginResult() {
        return LoginResult.success(
                DEFAULT_USER_ID,
                DEFAULT_ACCESS_TOKEN,
                DEFAULT_REFRESH_TOKEN,
                DEFAULT_EXPIRES_IN,
                DEFAULT_TOKEN_TYPE);
    }

    public static LoginResult successLoginResult(
            String userId,
            String accessToken,
            String refreshToken,
            long expiresIn,
            String tokenType) {
        return LoginResult.success(userId, accessToken, refreshToken, expiresIn, tokenType);
    }

    public static LoginResult failureLoginResult() {
        return LoginResult.failure("UNAUTHORIZED", "Invalid credentials");
    }

    public static LoginResult failureLoginResult(String errorCode, String errorMessage) {
        return LoginResult.failure(errorCode, errorMessage);
    }

    // ===== MyInfoResult =====

    public static MyInfoResult myInfoResult() {
        return new MyInfoResult(
                DEFAULT_USER_ID,
                DEFAULT_EMAIL,
                "관리자",
                "tenant-123",
                "테넌트명",
                "org-123",
                "조직명",
                List.of(new MyInfoResult.RoleInfo("role-1", "ADMIN")),
                List.of("READ", "WRITE"),
                1L,
                "010-1234-5678");
    }

    public static MyInfoResult myInfoResult(
            String userId,
            String email,
            String name,
            String tenantId,
            String tenantName,
            String organizationId,
            String organizationName,
            List<MyInfoResult.RoleInfo> roles,
            List<String> permissions) {
        return new MyInfoResult(
                userId,
                email,
                name,
                tenantId,
                tenantName,
                organizationId,
                organizationName,
                roles,
                permissions,
                null,
                null);
    }

    public static MyInfoResult myInfoResultWithoutRoles() {
        return new MyInfoResult(
                DEFAULT_USER_ID,
                DEFAULT_EMAIL,
                "관리자",
                "tenant-123",
                "테넌트명",
                "org-123",
                "조직명",
                null,
                null,
                null,
                null);
    }

    public static MyInfoResult myInfoResultWithEmptyRoles() {
        return new MyInfoResult(
                DEFAULT_USER_ID,
                DEFAULT_EMAIL,
                "관리자",
                "tenant-123",
                "테넌트명",
                "org-123",
                "조직명",
                List.of(),
                List.of(),
                null,
                null);
    }
}
