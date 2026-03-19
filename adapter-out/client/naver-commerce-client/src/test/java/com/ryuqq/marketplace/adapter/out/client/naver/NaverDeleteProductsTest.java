package com.ryuqq.marketplace.adapter.out.client.naver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("external-integration")
@DisplayName("네이버 테스트 상품 삭제")
class NaverDeleteProductsTest {

    @Test
    @DisplayName("중복 등록된 상품 검색 후 삭제")
    void deleteDuplicates() throws Exception {
        HttpClient http = HttpClient.newHttpClient();
        ObjectMapper om = new ObjectMapper();
        String token = NaverAuthHelper.getAccessToken(http, om);

        // 검색해서 77288 관련 상품 전부 확인
        String searchBody =
                "{\"searchKeywordType\":\"TITLE\",\"searchKeyword\":\"디즈니 엘사 겨울왕국 부츠\"}";
        HttpResponse<String> searchResp =
                http.send(
                        HttpRequest.newBuilder()
                                .uri(URI.create(NaverAuthHelper.BASE_URL + "/v1/products/search"))
                                .header("Authorization", "Bearer " + token)
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString(searchBody))
                                .build(),
                        HttpResponse.BodyHandlers.ofString());

        JsonNode contents = om.readTree(searchResp.body()).path("contents");
        System.out.println("검색 결과: " + contents.size() + "건");

        long keepOriginNo = 13198454659L; // 원래 상품 - 유지
        for (JsonNode item : contents) {
            long originNo = item.path("originProductNo").asLong();
            System.out.printf("  originNo=%d%n", originNo);
            if (originNo != keepOriginNo && originNo > 0) {
                HttpResponse<String> delResp =
                        http.send(
                                HttpRequest.newBuilder()
                                        .uri(
                                                URI.create(
                                                        NaverAuthHelper.BASE_URL
                                                                + "/v2/products/origin-products/"
                                                                + originNo))
                                        .header("Authorization", "Bearer " + token)
                                        .DELETE()
                                        .build(),
                                HttpResponse.BodyHandlers.ofString());
                System.out.printf("  → 삭제 %d: status=%d%n", originNo, delResp.statusCode());
            } else if (originNo == keepOriginNo) {
                System.out.println("  → 유지 (원래 상품)");
            }
        }
    }
}
