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
 * 네이버 상품 수정 후 sellerManagementCode 유지 여부 확인.
 *
 * <p>우리 시스템에서 UPDATE한 상품 vs 레거시에서 등록한 상품 비교.
 */
@Tag("external-integration")
@DisplayName("네이버 sellerManagementCode 유지 확인")
class NaverManagementCodeCheckTest {

    private final ObjectMapper objectMapper =
            new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Test
    @DisplayName("UPDATE된 상품과 원본 상품의 sellerManagementCode 비교")
    void compareManagementCodes() throws Exception {
        HttpClient httpClient = HttpClient.newHttpClient();
        String token = NaverAuthHelper.getAccessToken(httpClient, objectMapper);

        // 1. 우리가 UPDATE한 상품 (DEREGISTERED)
        // 2. 레거시에서만 등록한 상품 (SALE)
        // 3. 우리가 신규 등록한 테스트 상품 (SUSPENSION)
        long[] productNos = {
            13195148530L, // 우리가 UPDATE + DELETE한 상품
            13184147202L, // 레거시 등록 상품 (sellerManagementCode=517448 확인됨)
            13174797666L, // 레거시 등록 상품 (단일옵션)
        };

        for (long productNo : productNos) {
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

            if (resp.statusCode() != 200) {
                System.out.printf("상품 %d: HTTP %d (삭제됨?)%n", productNo, resp.statusCode());
                continue;
            }

            JsonNode detail = objectMapper.readTree(resp.body());
            JsonNode origin = detail.path("originProduct");

            String name = origin.path("name").asText("");
            String status = origin.path("statusType").asText("");
            JsonNode sellerCodeInfo = origin.path("detailAttribute").path("sellerCodeInfo");
            String managementCode = sellerCodeInfo.path("sellerManagementCode").asText("(없음)");

            System.out.printf(
                    "%n=== 상품 %d ===%n  이름: %s%n  상태: %s%n  sellerManagementCode: %s%n",
                    productNo, name, status, managementCode);
        }
    }
}
