package com.ryuqq.marketplace.adapter.out.client.naver;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** 네이버 테스트 상품 삭제. */
@Tag("external-integration")
@DisplayName("네이버 테스트 상품 삭제")
class NaverDeleteProductsTest {

    @Test
    @DisplayName("13257110153, 13257110008 삭제")
    void deleteTestProducts() throws Exception {
        HttpClient http = HttpClient.newHttpClient();
        String token = NaverAuthHelper.getAccessToken(http, new ObjectMapper());

        long[] productNos = {13257110153L, 13257110008L};

        for (long no : productNos) {
            HttpRequest req =
                    HttpRequest.newBuilder()
                            .uri(URI.create(NaverAuthHelper.BASE_URL + "/v2/products/origin-products/" + no))
                            .header("Authorization", "Bearer " + token)
                            .DELETE()
                            .build();
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            System.out.printf("삭제 %d: status=%d%n", no, resp.statusCode());
            if (resp.statusCode() != 200) {
                System.out.println("  " + resp.body());
            }
        }
    }
}
