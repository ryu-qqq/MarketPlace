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
 * 네이버 상품 카테고리/옵션 상세 조회 테스트.
 *
 * <p>product_group_id=77288의 네이버 등록 상품(13198454659)의 실제 카테고리와 옵션 구조를 확인합니다.
 */
@Tag("external-integration")
@DisplayName("네이버 상품 카테고리/옵션 상세 조회")
class NaverProductCategoryCheckTest {

    private final ObjectMapper objectMapper =
            new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Test
    @DisplayName("product_group_id=77284 → SOLD_OUT 반영 확인")
    void checkSoldOutStatus() throws Exception {
        HttpClient httpClient = HttpClient.newHttpClient();
        String token = NaverAuthHelper.getAccessToken(httpClient, objectMapper);

        long originProductNo = 13198455428L;

        HttpRequest req =
                HttpRequest.newBuilder()
                        .uri(URI.create(NaverAuthHelper.BASE_URL + "/v2/products/origin-products/" + originProductNo))
                        .header("Authorization", "Bearer " + token)
                        .GET()
                        .build();

        HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        System.out.println("Status: " + resp.statusCode());

        JsonNode root = objectMapper.readTree(resp.body());
        JsonNode originProduct = root.path("originProduct");

        System.out.println("상품명: " + originProduct.path("name").asText());
        System.out.println("statusType: " + originProduct.path("statusType").asText());
        System.out.println("stockQuantity: " + originProduct.path("stockQuantity").asInt());
        System.out.println("salePrice: " + originProduct.path("salePrice").asInt());
    }

    @Test
    @DisplayName("product_group_id=77288 → 네이버 상품 상세 조회 (카테고리, 옵션)")
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
        JsonNode originProduct = root.path("originProduct");

        // 카테고리 정보
        System.out.println("\n===== 카테고리 정보 =====");
        System.out.println("leafCategoryId: " + originProduct.path("leafCategoryId").asText("N/A"));
        System.out.println("상품명: " + originProduct.path("name").asText("N/A"));
        System.out.println("상태: " + originProduct.path("statusType").asText("N/A"));
        System.out.println("판매가: " + originProduct.path("salePrice").asInt());
        System.out.println("재고: " + originProduct.path("stockQuantity").asInt());

        // 옵션 정보
        JsonNode optionInfo =
                originProduct.path("detailAttribute").path("optionInfo");
        System.out.println("\n===== 옵션 정보 =====");

        if (optionInfo.isMissingNode() || optionInfo.isNull()) {
            System.out.println("옵션 없음 (단일상품)");
        } else {
            // 옵션 그룹명
            JsonNode groupNames = optionInfo.path("optionCombinationGroupNames");
            System.out.printf(
                    "optionCombinationGroupNames: [%s, %s, %s, %s]%n",
                    groupNames.path("optionGroupName1").asText(""),
                    groupNames.path("optionGroupName2").asText(""),
                    groupNames.path("optionGroupName3").asText(""),
                    groupNames.path("optionGroupName4").asText(""));

            // optionCombinations
            JsonNode combinations = optionInfo.path("optionCombinations");
            if (combinations.isArray() && combinations.size() > 0) {
                System.out.printf("optionCombinations: %d건%n", combinations.size());
                for (JsonNode c : combinations) {
                    System.out.printf(
                            "  id=%s | %s/%s/%s | SKU=%s | stock=%d | price=%d | usable=%s%n",
                            c.path("id").asText("null"),
                            c.path("optionName1").asText(""),
                            c.path("optionName2").asText(""),
                            c.path("optionName3").asText(""),
                            c.path("sellerManagerCode").asText("null"),
                            c.path("stockQuantity").asInt(),
                            c.path("price").asInt(),
                            c.path("usable").asText(""));
                }
            }

            // optionCustom
            JsonNode optionCustom = optionInfo.path("optionCustom");
            if (optionCustom.isArray() && optionCustom.size() > 0) {
                System.out.printf("optionCustom: %d건%n", optionCustom.size());
                for (JsonNode c : optionCustom) {
                    System.out.printf(
                            "  id=%s | groupName=%s | usable=%s%n",
                            c.path("id").asText("null"),
                            c.path("groupName").asText(""),
                            c.path("usable").asText(""));
                }
            } else {
                System.out.println("optionCustom: 없음");
            }

            // optionSimple
            JsonNode optionSimple = optionInfo.path("optionSimple");
            if (optionSimple.isArray() && optionSimple.size() > 0) {
                System.out.printf("optionSimple: %d건%n", optionSimple.size());
                for (JsonNode c : optionSimple) {
                    System.out.printf(
                            "  id=%s | groupName=%s | name=%s | usable=%s%n",
                            c.path("id").asText("null"),
                            c.path("groupName").asText(""),
                            c.path("name").asText(""),
                            c.path("usable").asText(""));
                }
            }
        }

        // 전체 상세 속성 출력
        System.out.println("\n===== 전체 detailAttribute (요약) =====");
        JsonNode detailAttr = originProduct.path("detailAttribute");
        JsonNode naverSearch = detailAttr.path("naverShoppingSearchInfo");
        System.out.println("categoryId: " + naverSearch.path("categoryId").asText("N/A"));
        System.out.println("brandId: " + naverSearch.path("brandId").asText("N/A"));
        System.out.println("brandName: " + naverSearch.path("brandName").asText("N/A"));
        System.out.println("manufacturerName: " + naverSearch.path("manufacturerName").asText("N/A"));
    }
}
