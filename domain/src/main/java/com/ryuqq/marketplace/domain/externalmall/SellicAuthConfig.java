package com.ryuqq.marketplace.domain.externalmall;

/**
 * Sellic 외부몰 인증 설정
 */
public record SellicAuthConfig(
        String apiKey,
        String apiSecret
) implements AuthConfig {

    public SellicAuthConfig {
        validateApiKey(apiKey);
        validateApiSecret(apiSecret);
    }

    @Override
    public void validate() {
        validateApiKey(apiKey);
        validateApiSecret(apiSecret);
    }

    private static void validateApiKey(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("apiKey는 필수입니다");
        }
    }

    private static void validateApiSecret(String apiSecret) {
        if (apiSecret == null || apiSecret.isBlank()) {
            throw new IllegalArgumentException("apiSecret은 필수입니다");
        }
    }
}
