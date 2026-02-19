package com.ryuqq.marketplace.adapter.out.client.naver.auth;

import com.ryuqq.marketplace.adapter.out.client.naver.config.NaverCommerceProperties;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverCommerceTokenResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

/**
 * Naver Commerce OAuth2 토큰 관리자.
 *
 * <p>토큰 발급 및 만료 시간 기반 캐싱을 담당합니다. 남은 유효 시간이 30분 미만이면 자동으로 재발급합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
@ConditionalOnProperty(prefix = "naver-commerce", name = "client-id")
public class NaverCommerceTokenManager {

    private static final long REFRESH_MARGIN_MILLIS = 30 * 60 * 1000L;

    private final RestClient restClient;
    private final NaverCommerceProperties properties;
    private final NaverCommerceSignatureGenerator signatureGenerator;

    private volatile String cachedToken;
    private volatile long tokenExpiresAt;

    public NaverCommerceTokenManager(
            RestClient naverCommerceRestClient,
            NaverCommerceProperties properties,
            NaverCommerceSignatureGenerator signatureGenerator) {
        this.restClient = naverCommerceRestClient;
        this.properties = properties;
        this.signatureGenerator = signatureGenerator;
    }

    /**
     * 유효한 액세스 토큰을 반환합니다.
     *
     * <p>캐시된 토큰이 유효하면 재사용하고, 만료 임박 시 재발급합니다.
     *
     * @return 유효한 Bearer 토큰
     */
    public synchronized String getAccessToken() {
        if (isTokenValid()) {
            return cachedToken;
        }
        return refreshToken();
    }

    private boolean isTokenValid() {
        return cachedToken != null
                && System.currentTimeMillis() < (tokenExpiresAt - REFRESH_MARGIN_MILLIS);
    }

    private String refreshToken() {
        long timestamp = System.currentTimeMillis();
        String clientId = properties.getClientId();
        String clientSecret = properties.getClientSecret();
        String signature = signatureGenerator.generateSignature(clientId, clientSecret, timestamp);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", clientId);
        formData.add("timestamp", String.valueOf(timestamp));
        formData.add("grant_type", "client_credentials");
        formData.add("client_secret_sign", signature);
        formData.add("type", "SELF");

        NaverCommerceTokenResponse response =
                restClient
                        .post()
                        .uri("/v1/oauth2/token")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .body(formData)
                        .retrieve()
                        .body(NaverCommerceTokenResponse.class);

        if (response == null || response.accessToken() == null) {
            throw new IllegalStateException("Failed to obtain Naver Commerce access token");
        }

        this.cachedToken = response.accessToken();
        this.tokenExpiresAt = timestamp + (response.expiresIn() * 1000L);
        return cachedToken;
    }
}
