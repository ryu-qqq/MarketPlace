package com.ryuqq.marketplace.adapter.out.client.naver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverCommerceTokenResponse;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductSearchRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductSearchResponse;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.mindrot.jbcrypt.BCrypt;

/**
 * 네이버 커머스 상품 목록 조회 테스트 러너.
 *
 * <p>Spring 컨텍스트 없이 직접 HTTP 호출로 상품 목록을 조회합니다.
 */
public final class NaverProductSearchRunner {

    private static final String BASE_URL = "https://api.commerce.naver.com/external";
    private static final String CLIENT_ID = "***REDACTED***";
    private static final String CLIENT_SECRET = "***REDACTED***";

    private NaverProductSearchRunner() {}

    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        HttpClient httpClient = HttpClient.newHttpClient();

        // 1. 토큰 발급
        System.out.println("=== 1. 토큰 발급 ===");
        String token = getAccessToken(httpClient, mapper);
        System.out.println("토큰 발급 성공");

        // 2. 상품 목록 조회 (1페이지, 50건)
        System.out.println("\n=== 2. 상품 목록 조회 ===");
        NaverProductSearchRequest request = NaverProductSearchRequest.allProducts(1, 50);
        String requestJson = mapper.writeValueAsString(request);
        System.out.println("Request: " + requestJson);

        HttpRequest searchRequest =
                HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/v1/products/search"))
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json;charset=UTF-8")
                        .header("Authorization", "Bearer " + token)
                        .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                        .build();

        HttpResponse<String> searchResponse =
                httpClient.send(searchRequest, HttpResponse.BodyHandlers.ofString());

        System.out.println("HTTP Status: " + searchResponse.statusCode());

        if (searchResponse.statusCode() == 200) {
            NaverProductSearchResponse response =
                    mapper.readValue(searchResponse.body(), NaverProductSearchResponse.class);

            System.out.println("전체 상품 수: " + response.totalElements());
            System.out.println("전체 페이지 수: " + response.totalPages());
            System.out.println(
                    "조회 건수: " + (response.contents() != null ? response.contents().size() : 0));

            if (response.contents() != null) {
                System.out.println("\n=== 상품 목록 ===");
                for (NaverProductSearchResponse.ProductContent content : response.contents()) {
                    System.out.printf(
                            "  [원상품번호: %d, 그룹상품번호: %d]%n",
                            content.originProductNo(), content.groupProductNo());

                    if (content.channelProducts() != null) {
                        for (NaverProductSearchResponse.ChannelProduct cp :
                                content.channelProducts()) {
                            System.out.printf(
                                    "    - 채널상품번호: %d | 상품명: %s | 상태: %s | 판매가: %d | 재고: %d | 카테고리:"
                                            + " %s%n",
                                    cp.channelProductNo(),
                                    cp.name(),
                                    cp.statusType(),
                                    cp.salePrice() != null ? cp.salePrice() : 0,
                                    cp.stockQuantity() != null ? cp.stockQuantity() : 0,
                                    cp.wholeCategoryName());
                        }
                    }
                }
            }

            // 전체 응답 JSON 출력
            System.out.println("\n=== 전체 응답 JSON ===");
            System.out.println(mapper.writeValueAsString(response));
        } else {
            System.out.println("에러 응답: " + searchResponse.body());
        }
    }

    private static String getAccessToken(HttpClient httpClient, ObjectMapper mapper)
            throws Exception {
        long timestamp = System.currentTimeMillis();
        String signature = generateSignature(CLIENT_ID, CLIENT_SECRET, timestamp);

        String formBody =
                "client_id="
                        + CLIENT_ID
                        + "&timestamp="
                        + timestamp
                        + "&grant_type=client_credentials"
                        + "&client_secret_sign="
                        + java.net.URLEncoder.encode(signature, StandardCharsets.UTF_8)
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

    private static String generateSignature(String clientId, String clientSecret, long timestamp) {
        String message = clientId + "_" + timestamp;
        // clientSecret이 이미 BCrypt salt 원본인 경우 직접 사용
        String hashed = BCrypt.hashpw(message, clientSecret);
        return Base64.getEncoder().encodeToString(hashed.getBytes(StandardCharsets.UTF_8));
    }
}
