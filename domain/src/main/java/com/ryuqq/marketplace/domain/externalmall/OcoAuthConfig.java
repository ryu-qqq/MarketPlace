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
        validate(clientId, clientSecret, apiKey);
    }

    @Override
    public void validate() {
        validate(clientId, clientSecret, apiKey);
    }

    /**
     * OCO 인증 설정 필드 검증
     */
    private static void validate(String clientId, String clientSecret, String apiKey) {
        if (clientId == null || clientId.isBlank()) {
            throw new IllegalArgumentException("clientId는 필수입니다");
        }
        if (clientSecret == null || clientSecret.isBlank()) {
            throw new IllegalArgumentException("clientSecret은 필수입니다");
        }
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("apiKey는 필수입니다");
        }
    }
}
