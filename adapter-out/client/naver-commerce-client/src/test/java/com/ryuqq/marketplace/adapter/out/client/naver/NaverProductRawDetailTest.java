package com.ryuqq.marketplace.adapter.out.client.naver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * 네이버 상품 Raw 응답 전체 출력 테스트.
 *
 * <p>sellerManagementCode 등 레거시 매핑 필드를 찾기 위한 일회성 조회.
 */
@Tag("external-integration")
@DisplayName("네이버 상품 Raw 응답 확인")
class NaverProductRawDetailTest {

    private final ObjectMapper objectMapper =
            new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Test
    @DisplayName("상품 상세 Raw JSON에서 management/seller 관련 필드 찾기")
    void findManagementFields() throws Exception {
        HttpClient httpClient = HttpClient.newHttpClient();
        String token = NaverAuthHelper.getAccessToken(httpClient, objectMapper);

        // 옵션 있는 상품 1건
        long productNo = 13184147202L;

        HttpRequest req =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        NaverAuthHelper.BASE_URL
                                                + "/v2/products/origin-products/"
                                                + productNo))
                        .header("Authorization", "Bearer " + token)
                        .GET()
                        .build();

        HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        JsonNode detail = objectMapper.readTree(resp.body());

        // 전체 JSON 출력 (management, seller, code, id 관련 필드 찾기)
        System.out.println("=== 전체 Raw JSON ===");
        System.out.println(objectMapper.writeValueAsString(detail));
    }
}
