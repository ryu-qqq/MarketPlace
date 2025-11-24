package com.ryuqq.marketplace.domain.externalmall;

/**
 * LF 외부몰 인증 설정
 */
public record LfAuthConfig(
        String accessToken
) implements AuthConfig {

    public LfAuthConfig {
        validateAccessToken(accessToken);
    }

    @Override
    public void validate() {
        validateAccessToken(accessToken);
    }

    private static void validateAccessToken(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalArgumentException("accessToken은 필수입니다");
        }
    }
}
