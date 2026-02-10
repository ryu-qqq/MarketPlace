package com.ryuqq.marketplace.adapter.out.client.authhub;

import com.ryuqq.marketplace.adapter.out.client.authhub.dto.AuthHubLoginResult;
import com.ryuqq.marketplace.adapter.out.client.authhub.dto.AuthHubRefreshResult;
import com.ryuqq.marketplace.application.auth.dto.response.LoginResult;
import com.ryuqq.marketplace.application.auth.dto.response.MyInfoResult;
import com.ryuqq.marketplace.application.seller.dto.response.SellerIdentityProvisioningResult;
import java.util.List;

/**
 * AuthHub 클라이언트 테스트 Fixtures.
 *
 * <p>AuthHub SDK 관련 테스트 객체들을 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class AuthHubFixtures {

    private AuthHubFixtures() {}

    // ===== 기본 상수 =====
    public static final String DEFAULT_USER_ID = "user-123";
    public static final String DEFAULT_EMAIL = "admin@example.com";
    public static final String DEFAULT_ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.access";
    public static final String DEFAULT_REFRESH_TOKEN =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.refresh";
    public static final String DEFAULT_TOKEN_TYPE = "Bearer";
    public static final Long DEFAULT_EXPIRES_IN = 3600L;
    public static final Long DEFAULT_REFRESH_EXPIRES_IN = 86400L;
    public static final String DEFAULT_TENANT_ID = "tenant-123";
    public static final String DEFAULT_ORGANIZATION_ID = "org-123";

    // ===== Application LoginResult =====

    public static LoginResult successLoginResult() {
        return LoginResult.success(
                DEFAULT_USER_ID,
                DEFAULT_ACCESS_TOKEN,
                DEFAULT_REFRESH_TOKEN,
                DEFAULT_EXPIRES_IN,
                DEFAULT_TOKEN_TYPE);
    }

    public static LoginResult failureLoginResult() {
        return LoginResult.failure("UNAUTHORIZED", "Invalid credentials");
    }

    // ===== Application MyInfoResult =====

    public static MyInfoResult myInfoResult() {
        return new MyInfoResult(
                DEFAULT_USER_ID,
                DEFAULT_EMAIL,
                "관리자",
                DEFAULT_TENANT_ID,
                "테넌트명",
                DEFAULT_ORGANIZATION_ID,
                "조직명",
                List.of(new MyInfoResult.RoleInfo("role-1", "ADMIN")),
                List.of("READ", "WRITE"),
                null,
                null);
    }

    // ===== Internal DTO =====

    public static AuthHubLoginResult authHubLoginResult() {
        return AuthHubLoginResult.success(
                DEFAULT_USER_ID,
                DEFAULT_ACCESS_TOKEN,
                DEFAULT_REFRESH_TOKEN,
                DEFAULT_EXPIRES_IN,
                DEFAULT_TOKEN_TYPE);
    }

    public static AuthHubRefreshResult authHubRefreshResult() {
        return AuthHubRefreshResult.success(
                DEFAULT_ACCESS_TOKEN,
                DEFAULT_REFRESH_TOKEN,
                DEFAULT_EXPIRES_IN,
                DEFAULT_REFRESH_EXPIRES_IN);
    }

    // ===== SellerIdentityProvisioningResult =====

    public static SellerIdentityProvisioningResult successProvisioningResult() {
        return SellerIdentityProvisioningResult.success(DEFAULT_TENANT_ID, DEFAULT_ORGANIZATION_ID);
    }

    public static SellerIdentityProvisioningResult permanentFailureResult() {
        return SellerIdentityProvisioningResult.permanentFailure("BAD_REQUEST", "Invalid request");
    }

    public static SellerIdentityProvisioningResult retryableFailureResult() {
        return SellerIdentityProvisioningResult.retryableFailure("SERVER_ERROR", "Server error");
    }

    // ===== Payload JSON =====

    public static String validOnboardingPayload() {
        return """
               {
                   "tenantName": "Test Tenant",
                   "organizationName": "Test Organization"
               }
               """;
    }

    public static String invalidOnboardingPayload() {
        return "invalid json";
    }
}
