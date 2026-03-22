package com.ryuqq.marketplace.application.legacy.auth;

import com.ryuqq.marketplace.application.legacy.auth.dto.command.LegacyLoginCommand;
import com.ryuqq.marketplace.application.legacy.auth.dto.result.LegacySellerAuthResult;
import com.ryuqq.marketplace.application.legacy.auth.dto.result.LegacyTokenResult;

/**
 * LegacyAuth Application 테스트 Fixtures.
 *
 * <p>레거시 인증 관련 Command, Result 객체들을 생성하는 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class LegacyAuthFixtures {

    private LegacyAuthFixtures() {}

    // ===== 공통 상수 =====

    public static final String DEFAULT_EMAIL = "seller@test.com";
    public static final String DEFAULT_RAW_PASSWORD = "password123";
    public static final String DEFAULT_ENCODED_PASSWORD = "$2a$10$encodedHash";
    public static final String DEFAULT_ROLE_TYPE = "SELLER";
    public static final String MASTER_ROLE_TYPE = "MASTER";
    public static final long DEFAULT_SELLER_ID = 1L;
    public static final String DEFAULT_ACCESS_TOKEN = "access.jwt.token";
    public static final String DEFAULT_REFRESH_TOKEN = "refresh.jwt.token";
    public static final long DEFAULT_EXPIRES_IN_SECONDS = 10800L;

    // ===== LegacyLoginCommand Fixtures =====

    public static LegacyLoginCommand loginCommand() {
        return new LegacyLoginCommand(DEFAULT_EMAIL, DEFAULT_RAW_PASSWORD);
    }

    public static LegacyLoginCommand loginCommand(String identifier, String password) {
        return new LegacyLoginCommand(identifier, password);
    }

    // ===== LegacySellerAuthResult Fixtures =====

    public static LegacySellerAuthResult approvedSellerAuthResult() {
        return new LegacySellerAuthResult(
                DEFAULT_SELLER_ID,
                DEFAULT_EMAIL,
                DEFAULT_ENCODED_PASSWORD,
                DEFAULT_ROLE_TYPE,
                "APPROVED");
    }

    public static LegacySellerAuthResult approvedSellerAuthResult(long sellerId, String email) {
        return new LegacySellerAuthResult(
                sellerId, email, DEFAULT_ENCODED_PASSWORD, DEFAULT_ROLE_TYPE, "APPROVED");
    }

    public static LegacySellerAuthResult pendingSellerAuthResult() {
        return new LegacySellerAuthResult(
                DEFAULT_SELLER_ID,
                DEFAULT_EMAIL,
                DEFAULT_ENCODED_PASSWORD,
                DEFAULT_ROLE_TYPE,
                "PENDING");
    }

    public static LegacySellerAuthResult masterSellerAuthResult() {
        return new LegacySellerAuthResult(
                DEFAULT_SELLER_ID,
                DEFAULT_EMAIL,
                DEFAULT_ENCODED_PASSWORD,
                MASTER_ROLE_TYPE,
                "APPROVED");
    }

    // ===== LegacyTokenResult Fixtures =====

    public static LegacyTokenResult tokenResult() {
        return new LegacyTokenResult(
                DEFAULT_ACCESS_TOKEN,
                DEFAULT_REFRESH_TOKEN,
                DEFAULT_EMAIL,
                DEFAULT_EXPIRES_IN_SECONDS);
    }

    public static LegacyTokenResult tokenResult(String email) {
        return new LegacyTokenResult(
                DEFAULT_ACCESS_TOKEN, DEFAULT_REFRESH_TOKEN, email, DEFAULT_EXPIRES_IN_SECONDS);
    }

    public static LegacyTokenResult tokenResult(
            String accessToken, String refreshToken, String email, long expiresInSeconds) {
        return new LegacyTokenResult(accessToken, refreshToken, email, expiresInSeconds);
    }
}
