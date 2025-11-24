package com.ryuqq.marketplace.domain.externalmall;

/**
 * Sellic 외부몰 인증 설정
 */
public record SellicAuthConfig(
        String apiKey,
        String apiSecret
) implements AuthConfig {

    public SellicAuthConfig {
        validate(apiKey, apiSecret);
    }

    @Override
    public void validate() {
        validate(apiKey, apiSecret);
    }

    private static void validate(String apiKey, String apiSecret) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("apiKey는 필수입니다");
        }
        if (apiSecret == null || apiSecret.isBlank()) {
            throw new IllegalArgumentException("apiSecret은 필수입니다");
        }
    }
}
