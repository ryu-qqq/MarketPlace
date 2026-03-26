package com.ryuqq.marketplace.adapter.out.client.naver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * 레거시 luxurydb EXTERNAL_ORDER_PK_ID 패턴으로 네이버 주문 조회 가능 여부 검증.
 *
 * <p>패턴별 테스트:
 *
 * <ul>
 *   <li>16_16 패턴: orderId_productOrderId → productOrderId로 상세 조회
 *   <li>16자리 단독: orderId → orderId로 productOrderId 목록 조회 후 상세 조회
 *   <li>15자리 (구형): 셀릭 초기 주문번호 → 조회 가능 여부 확인
 * </ul>
 */
@Tag("external-integration")
@DisplayName("레거시 SELLIC 주문번호로 네이버 주문 조회 검증")
class NaverOrderLegacyMappingTest {

    private final ObjectMapper objectMapper =
            new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Test
    @DisplayName("16_16 패턴: productOrderId로 직접 상세 조회")
    void queryByProductOrderId_16_16_pattern() throws Exception {
        HttpClient httpClient = HttpClient.newHttpClient();
        String token = NaverAuthHelper.getAccessToken(httpClient, objectMapper);

        // luxurydb EXTERNAL_ORDER_PK_ID 16_16 패턴 샘플
        String legacyPkId = "2026031868532771_2026031850087351";
        String orderId = legacyPkId.split("_")[0];
        String productOrderId = legacyPkId.split("_")[1];

        System.out.println("=== 16_16 패턴 테스트 ===");
        System.out.println("legacyPkId: " + legacyPkId);
        System.out.println("orderId: " + orderId);
        System.out.println("productOrderId: " + productOrderId);

        // 1) productOrderId로 상세 조회
        System.out.println("\n--- productOrderId로 상세 조회 ---");
        queryProductOrderDetail(httpClient, token, List.of(productOrderId));

        // 2) orderId로 productOrderId 목록 조회
        System.out.println("\n--- orderId로 productOrderId 목록 조회 ---");
        getProductOrderIdsByOrderId(httpClient, token, orderId);
    }

    @Test
    @DisplayName("16자리 단독 패턴: orderId로 productOrderId 조회 후 상세 조회")
    void queryByOrderId_16digit_pattern() throws Exception {
        HttpClient httpClient = HttpClient.newHttpClient();
        String token = NaverAuthHelper.getAccessToken(httpClient, objectMapper);

        // luxurydb EXTERNAL_ORDER_PK_ID 16자리 단독 패턴 샘플
        String orderId = "2026011610290721";

        System.out.println("=== 16자리 단독 패턴 테스트 ===");
        System.out.println("orderId: " + orderId);

        // orderId → productOrderId 목록
        System.out.println("\n--- orderId로 productOrderId 목록 조회 ---");
        JsonNode idsResponse = getProductOrderIdsByOrderId(httpClient, token, orderId);

        // productOrderId가 있으면 상세 조회
        if (idsResponse != null && idsResponse.has("data")) {
            JsonNode data = idsResponse.get("data");
            if (data.isArray() && data.size() > 0) {
                List<String> productOrderIds = new java.util.ArrayList<>();
                for (JsonNode id : data) {
                    productOrderIds.add(id.asText());
                }
                System.out.println("\n--- productOrderId 상세 조회 ---");
                queryProductOrderDetail(httpClient, token, productOrderIds);
            }
        }
    }

    @Test
    @DisplayName("15자리 구형 패턴: 셀릭 초기 주문번호 조회 시도")
    void queryByOldSellicPattern() throws Exception {
        HttpClient httpClient = HttpClient.newHttpClient();
        String token = NaverAuthHelper.getAccessToken(httpClient, objectMapper);

        // luxurydb EXTERNAL_ORDER_PK_ID 15자리 샘플
        String oldPkId = "202405273328066";

        System.out.println("=== 15자리 구형 패턴 테스트 ===");
        System.out.println("oldPkId: " + oldPkId);

        // orderId로 시도
        System.out.println("\n--- orderId로 productOrderId 목록 조회 시도 ---");
        getProductOrderIdsByOrderId(httpClient, token, oldPkId);
    }

    private JsonNode getProductOrderIdsByOrderId(
            HttpClient httpClient, String token, String orderId) throws Exception {
        HttpRequest req =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        NaverAuthHelper.BASE_URL
                                                + "/v1/pay-order/seller/orders/"
                                                + orderId
                                                + "/product-order-ids"))
                        .header("Authorization", "Bearer " + token)
                        .GET()
                        .build();

        HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        System.out.println("status: " + resp.statusCode());
        JsonNode json = objectMapper.readTree(resp.body());
        System.out.println(objectMapper.writeValueAsString(json));
        return json;
    }

    private void queryProductOrderDetail(
            HttpClient httpClient, String token, List<String> productOrderIds) throws Exception {
        String body =
                objectMapper.writeValueAsString(
                        java.util.Map.of("productOrderIds", productOrderIds));

        HttpRequest req =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        NaverAuthHelper.BASE_URL
                                                + "/v1/pay-order/seller/product-orders/query"))
                        .header("Authorization", "Bearer " + token)
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(body))
                        .build();

        HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        System.out.println("status: " + resp.statusCode());
        JsonNode json = objectMapper.readTree(resp.body());
        System.out.println(objectMapper.writeValueAsString(json));
    }
}
