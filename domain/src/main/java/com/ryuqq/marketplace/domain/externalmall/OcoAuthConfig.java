package com.ryuqq.marketplace.domain.externalmall;

/**
 * OCO 외부몰 인증 설정
 */
public record OcoAuthConfig(
        String clientId,
        String clientSecret,
        String apiKey
) implements AuthConfig {

    /**
     * OCO 인증 설정 생성자 (검증 포함)
     */
    public OcoAuthConfig {
        validateClientId(clientId);
        validateClientSecret(clientSecret);
        validateApiKey(apiKey);
    }

    @Override
    public void validate() {
        validateClientId(clientId);
        validateClientSecret(clientSecret);
        validateApiKey(apiKey);
    }

    private static void validateClientId(String clientId) {
        if (clientId == null || clientId.isBlank()) {
            throw new IllegalArgumentException("clientId는 필수입니다");
        }
    }

    private static void validateClientSecret(String clientSecret) {
        if (clientSecret == null || clientSecret.isBlank()) {
            throw new IllegalArgumentException("clientSecret은 필수입니다");
        }
    }

    private static void validateApiKey(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("apiKey는 필수입니다");
        }
    }
}
