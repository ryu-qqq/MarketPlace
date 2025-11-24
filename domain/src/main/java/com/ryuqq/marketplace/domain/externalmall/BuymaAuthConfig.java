package com.ryuqq.marketplace.domain.externalmall;

/**
 * BUYMA 외부몰 인증 설정
 */
public record BuymaAuthConfig(
        String username,
        String password
) implements AuthConfig {

    public BuymaAuthConfig {
        validateUsername(username);
        validatePassword(password);
    }

    @Override
    public void validate() {
        validateUsername(username);
        validatePassword(password);
    }

    private static void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username은 필수입니다");
        }
    }

    private static void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("password는 필수입니다");
        }
    }
}
