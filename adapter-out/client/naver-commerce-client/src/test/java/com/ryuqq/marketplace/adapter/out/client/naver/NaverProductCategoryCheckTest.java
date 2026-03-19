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

@Tag("external-integration")
@DisplayName("네이버 상품 상세 조회")
class NaverProductCategoryCheckTest {

    private final ObjectMapper objectMapper =
            new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Test
    @DisplayName("77288 → 네이버 상품 옵션/상태 전체 조회")
    void checkProductDetail() throws Exception {
        HttpClient httpClient = HttpClient.newHttpClient();
        String token = NaverAuthHelper.getAccessToken(httpClient, objectMapper);

        long originProductNo = 13198454659L;

        HttpRequest req =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        NaverAuthHelper.BASE_URL
                                                + "/v2/products/origin-products/"
                                                + originProductNo))
                        .header("Authorization", "Bearer " + token)
                        .GET()
                        .build();

        HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        System.out.println("Status: " + resp.statusCode());

        if (resp.statusCode() != 200) {
            System.out.println("ERROR: " + resp.body());
            return;
        }

        JsonNode root = objectMapper.readTree(resp.body());
        JsonNode op = root.path("originProduct");

        System.out.println("\n===== 기본 정보 =====");
        System.out.println("상품명: " + op.path("name").asText());
        System.out.println("statusType: " + op.path("statusType").asText());
        System.out.println("salePrice: " + op.path("salePrice").asInt());
        System.out.println("stockQuantity: " + op.path("stockQuantity").asInt());
        System.out.println("leafCategoryId: " + op.path("leafCategoryId").asText());

        JsonNode optionInfo = op.path("detailAttribute").path("optionInfo");
        System.out.println("\n===== 옵션 정보 =====");

        if (optionInfo.isMissingNode() || optionInfo.isNull()) {
            System.out.println("옵션 없음");
            return;
        }

        // optionCombinations
        JsonNode combinations = optionInfo.path("optionCombinations");
        if (combinations.isArray() && combinations.size() > 0) {
            System.out.println("optionCombinations: " + combinations.size() + "건");
            for (JsonNode c : combinations) {
                System.out.printf(
                        "  id=%s | %s/%s | SKU=%s | stock=%d | price=%d%n",
                        c.path("id").asText(),
                        c.path("optionName1").asText(),
                        c.path("optionName2").asText(),
                        c.path("sellerManagerCode").asText(),
                        c.path("stockQuantity").asInt(),
                        c.path("price").asInt());
            }
        } else {
            System.out.println("optionCombinations: 없음");
        }

        // optionCustom
        JsonNode optionCustom = optionInfo.path("optionCustom");
        if (optionCustom.isArray() && optionCustom.size() > 0) {
            System.out.println("optionCustom: " + optionCustom.size() + "건");
            for (JsonNode c : optionCustom) {
                System.out.printf(
                        "  id=%s | groupName=%s | usable=%s%n",
                        c.path("id").asText(),
                        c.path("groupName").asText(),
                        c.path("usable").asText());
            }
        } else {
            System.out.println("optionCustom: 없음");
        }

        // optionSimple
        JsonNode optionSimple = optionInfo.path("optionSimple");
        if (optionSimple.isArray() && optionSimple.size() > 0) {
            System.out.println("optionSimple: " + optionSimple.size() + "건");
        }

        // groupNames
        JsonNode groupNames = optionInfo.path("optionCombinationGroupNames");
        if (!groupNames.isMissingNode()) {
            System.out.printf(
                    "optionCombinationGroupNames: [%s, %s]%n",
                    groupNames.path("optionGroupName1").asText(""),
                    groupNames.path("optionGroupName2").asText(""));
        }
    }
}
