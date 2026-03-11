package com.ryuqq.marketplace.adapter.out.client.naver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverCommerceTokenResponse;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.mindrot.jbcrypt.BCrypt;

/**
 * 네이버 커머스 API 인증 공통 유틸리티.
 *
 * <p>환경변수 NAVER_CLIENT_ID, NAVER_CLIENT_SECRET에서 자격증명을 로드합니다.
 */
public final class NaverAuthHelper {

    static final String BASE_URL = "https://api.commerce.naver.com/external";

    private NaverAuthHelper() {}

    static String requireClientId() {
        String clientId = System.getenv("NAVER_CLIENT_ID");
        if (clientId == null || clientId.isBlank()) {
            throw new IllegalStateException("환경변수 NAVER_CLIENT_ID가 설정되지 않았습니다.");
        }
        return clientId;
    }

    static String requireClientSecret() {
        String clientSecret = System.getenv("NAVER_CLIENT_SECRET");
        if (clientSecret == null || clientSecret.isBlank()) {
            throw new IllegalStateException("환경변수 NAVER_CLIENT_SECRET이 설정되지 않았습니다.");
        }
        return clientSecret;
    }

    static String getAccessToken(HttpClient httpClient, ObjectMapper mapper) throws Exception {
        String clientId = requireClientId();
        String clientSecret = requireClientSecret();

        long timestamp = System.currentTimeMillis();
        String signature = generateSignature(clientId, clientSecret, timestamp);

        String formBody =
                "client_id="
                        + clientId
                        + "&timestamp="
                        + timestamp
                        + "&grant_type=client_credentials"
                        + "&client_secret_sign="
                        + URLEncoder.encode(signature, StandardCharsets.UTF_8)
                        + "&type=SELF";

        HttpRequest tokenRequest =
                HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/v1/oauth2/token"))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString(formBody))
                        .build();

        HttpResponse<String> tokenResponse =
                httpClient.send(tokenRequest, HttpResponse.BodyHandlers.ofString());

        if (tokenResponse.statusCode() != 200) {
            throw new RuntimeException(
                    "토큰 발급 실패: " + tokenResponse.statusCode() + " " + tokenResponse.body());
        }

        NaverCommerceTokenResponse response =
                mapper.readValue(tokenResponse.body(), NaverCommerceTokenResponse.class);
        return response.accessToken();
    }

    static String generateSignature(String clientId, String clientSecret, long timestamp) {
        String message = clientId + "_" + timestamp;
        String hashed = BCrypt.hashpw(message, clientSecret);
        return Base64.getEncoder().encodeToString(hashed.getBytes(StandardCharsets.UTF_8));
    }
}
