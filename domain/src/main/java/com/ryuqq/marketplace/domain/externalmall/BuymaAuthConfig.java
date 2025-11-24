package com.ryuqq.marketplace.domain.externalmall;

/**
 * BUYMA 외부몰 인증 설정
 */
public record BuymaAuthConfig(
        String username,
        String password
) implements AuthConfig {

    public BuymaAuthConfig {
        validate(username, password);
    }

    @Override
    public void validate() {
        validate(username, password);
    }

    private static void validate(String username, String password) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username은 필수입니다");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("password는 필수입니다");
        }
    }
}
