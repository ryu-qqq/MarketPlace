package com.ryuqq.marketplace.application.auth;

import com.ryuqq.marketplace.application.auth.dto.command.LoginCommand;
import com.ryuqq.marketplace.application.auth.dto.command.LogoutCommand;

/**
 * Auth Command 테스트 Fixtures.
 *
 * <p>Auth 관련 Command 객체들을 생성하는 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class AuthCommandFixtures {

    private AuthCommandFixtures() {}

    // ===== 기본 상수 =====
    public static final String DEFAULT_IDENTIFIER = "admin@example.com";
    public static final String DEFAULT_PASSWORD = "password123!";
    public static final String DEFAULT_USER_ID = "user-123";

    // ===== LoginCommand =====

    public static LoginCommand loginCommand() {
        return new LoginCommand(DEFAULT_IDENTIFIER, DEFAULT_PASSWORD);
    }

    public static LoginCommand loginCommand(String identifier, String password) {
        return new LoginCommand(identifier, password);
    }

    // ===== LogoutCommand =====

    public static LogoutCommand logoutCommand() {
        return new LogoutCommand(DEFAULT_USER_ID);
    }

    public static LogoutCommand logoutCommand(String userId) {
        return new LogoutCommand(userId);
    }
}
