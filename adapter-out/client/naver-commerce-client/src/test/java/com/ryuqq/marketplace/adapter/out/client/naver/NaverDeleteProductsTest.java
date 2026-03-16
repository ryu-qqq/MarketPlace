package com.ryuqq.marketplace.adapter.out.client.naver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
    @DisplayName("옵션테스트 상품 원상품번호로 삭제")
    void deleteTestProducts() throws Exception {
        HttpClient http = HttpClient.newHttpClient();
        String token = NaverAuthHelper.getAccessToken(http, new ObjectMapper());

        // 이전 옵션 테스트에서 등록된 originProductNo들
        // A: 13198630219, C: 13198630082
        // B, D, E는 테스트에서 이미 삭제됨
        long[] originProductNos = {13198630219L, 13198630082L};

        for (long no : originProductNos) {
            // 먼저 조회해서 존재하는지 확인
            HttpResponse<String> getResp = http.send(
                    HttpRequest.newBuilder()
                            .uri(URI.create(NaverAuthHelper.BASE_URL + "/v2/products/origin-products/" + no))
                            .header("Authorization", "Bearer " + token)
                            .GET().build(),
                    HttpResponse.BodyHandlers.ofString());

            System.out.printf("조회 %d: status=%d%n", no, getResp.statusCode());

            if (getResp.statusCode() == 200) {
                HttpResponse<String> delResp = http.send(
                        HttpRequest.newBuilder()
                                .uri(URI.create(NaverAuthHelper.BASE_URL + "/v2/products/origin-products/" + no))
                                .header("Authorization", "Bearer " + token)
                                .DELETE().build(),
                        HttpResponse.BodyHandlers.ofString());
                System.out.printf("삭제 %d: status=%d%n", no, delResp.statusCode());
                if (delResp.statusCode() != 200) {
                    System.out.println("  " + delResp.body());
                } else {
                    System.out.println("  [PASS] 삭제 완료");
                }
            }
        }
    }
}
