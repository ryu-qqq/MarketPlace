package com.ryuqq.marketplace.adapter.out.client.naver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductSearchRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductSearchResponse;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * 네이버 커머스 전체 상품을 CSV로 추출하는 러너.
 *
 * <p>출력: originProductNo,sellerManagementCode,statusType,name
 *
 * <p>실행 전 환경변수 설정 필요: NAVER_CLIENT_ID, NAVER_CLIENT_SECRET
 */
public final class NaverProductExportRunner {

    private static final String OUTPUT_FILE = "naver_products.csv";

    private NaverProductExportRunner() {}

    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        HttpClient httpClient = HttpClient.newHttpClient();

        System.out.println("=== 토큰 발급 ===");
        String token = NaverAuthHelper.getAccessToken(httpClient, mapper);
        System.out.println("토큰 발급 성공");

        int page = 1;
        int size = 500;
        int totalExported = 0;
        int maxPages = 10_000;

        try (PrintWriter writer =
                new PrintWriter(new FileWriter(OUTPUT_FILE, StandardCharsets.UTF_8))) {
            writer.println("origin_product_no,seller_management_code,status_type,name");

            while (page <= maxPages) {
                NaverProductSearchRequest request =
                        NaverProductSearchRequest.allProducts(page, size);
                String requestJson = mapper.writeValueAsString(request);

                HttpRequest searchRequest =
                        HttpRequest.newBuilder()
                                .uri(URI.create(NaverAuthHelper.BASE_URL + "/v1/products/search"))
                                .header("Content-Type", "application/json")
                                .header("Accept", "application/json;charset=UTF-8")
                                .header("Authorization", "Bearer " + token)
                                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                                .build();

                HttpResponse<String> searchResponse =
                        httpClient.send(searchRequest, HttpResponse.BodyHandlers.ofString());

                if (searchResponse.statusCode() != 200) {
                    System.out.println(
                            "에러: " + searchResponse.statusCode() + " " + searchResponse.body());
                    break;
                }

                NaverProductSearchResponse response =
                        mapper.readValue(searchResponse.body(), NaverProductSearchResponse.class);

                if (response.contents() == null || response.contents().isEmpty()) {
                    break;
                }

                for (NaverProductSearchResponse.ProductContent content : response.contents()) {
                    if (content.channelProducts() == null) {
                        continue;
                    }

                    for (NaverProductSearchResponse.ChannelProduct cp : content.channelProducts()) {
                        String escapedName =
                                cp.name() != null
                                        ? "\"" + cp.name().replace("\"", "\"\"") + "\""
                                        : "";
                        writer.printf(
                                "%d,%s,%s,%s%n",
                                content.originProductNo(),
                                cp.sellerManagementCode() != null ? cp.sellerManagementCode() : "",
                                cp.statusType() != null ? cp.statusType() : "",
                                escapedName);
                        totalExported++;
                    }
                }

                System.out.printf(
                        "page %d: %d건 (누적: %d / 전체: %d)%n",
                        page, response.contents().size(), totalExported, response.totalElements());

                if (Boolean.TRUE.equals(response.last())) {
                    break;
                }
                page++;
            }
        }

        System.out.printf("%n=== 완료: %s에 %d건 추출 ===%n", OUTPUT_FILE, totalExported);
    }
}
