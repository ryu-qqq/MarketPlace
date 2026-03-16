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

/** 네이버 테스트 상품 존재 여부 확인. */
@Tag("external-integration")
@DisplayName("네이버 테스트 상품 존재 확인")
class NaverTestProductCheckTest {

    private final ObjectMapper objectMapper =
            new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Test
    @DisplayName("DEREGISTERED 테스트 상품 7건 조회")
    void checkTestProducts() throws Exception {
        HttpClient httpClient = HttpClient.newHttpClient();
        String token = NaverAuthHelper.getAccessToken(httpClient, objectMapper);

        long[] productNos = {
            13195148472L,
            13195148563L,
            13195148530L,
            13195148600L,
            13195148568L,
            13195148499L,
            13195148603L
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
                System.out.printf("상품 %d: HTTP %d%n", productNo, resp.statusCode());
                continue;
            }

            JsonNode detail = objectMapper.readTree(resp.body());
            JsonNode origin = detail.path("originProduct");
            System.out.printf(
                    "상품 %d: status=%s, name=%s%n",
                    productNo,
                    origin.path("statusType").asText(""),
                    origin.path("name").asText(""));
        }
    }
}
