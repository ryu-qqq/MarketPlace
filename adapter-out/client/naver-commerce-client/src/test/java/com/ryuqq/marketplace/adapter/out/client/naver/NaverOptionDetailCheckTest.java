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
 * 네이버 기존 상품의 옵션 조합(sellerManagerCode) 확인 테스트.
 *
 * <p>환경변수: NAVER_CLIENT_ID, NAVER_CLIENT_SECRET
 */
@Tag("external-integration")
@DisplayName("네이버 기존 상품 옵션 상세 조회")
class NaverOptionDetailCheckTest {

    private final ObjectMapper objectMapper =
            new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Test
    @DisplayName("기존 상품 옵션 조합의 sellerManagerCode 확인")
    void checkOptionSellerManagerCode() throws Exception {
        HttpClient httpClient = HttpClient.newHttpClient();
        String token = NaverAuthHelper.getAccessToken(httpClient, objectMapper);

        // 옵션이 있는 상품 3건 조회 (2 style 상품들)
        long[] productNos = {13184147202L, 13184185075L, 13174797666L};

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
            JsonNode detail = objectMapper.readTree(resp.body());

            String name = detail.path("originProduct").path("name").asText("");
            System.out.printf("%n=== 상품 %d: %s ===%n", productNo, name);

            JsonNode optionInfo =
                    detail.path("originProduct").path("detailAttribute").path("optionInfo");

            if (optionInfo.isMissingNode() || optionInfo.isNull()) {
                System.out.println("  옵션 없음 (단일상품)");
                continue;
            }

            // 옵션 그룹명
            JsonNode groupNames = optionInfo.path("optionCombinationGroupNames");
            System.out.printf(
                    "  옵션그룹: [%s, %s, %s]%n",
                    groupNames.path("optionGroupName1").asText(""),
                    groupNames.path("optionGroupName2").asText(""),
                    groupNames.path("optionGroupName3").asText(""));

            // 옵션 조합 상세
            JsonNode combinations = optionInfo.path("optionCombinations");
            if (combinations.isArray()) {
                System.out.printf("  옵션 조합 %d건:%n", combinations.size());
                for (JsonNode combo : combinations) {
                    System.out.printf(
                            "    id=%s | opt1=%s | opt2=%s | opt3=%s | sellerManagerCode=%s |"
                                    + " stock=%d | price=%d | usable=%s%n",
                            combo.path("id").asText("null"),
                            combo.path("optionName1").asText(""),
                            combo.path("optionName2").asText(""),
                            combo.path("optionName3").asText(""),
                            combo.path("sellerManagerCode").asText("null"),
                            combo.path("stockQuantity").asInt(),
                            combo.path("price").asInt(),
                            combo.path("usable").asText(""));
                }
            }
        }
    }
}
