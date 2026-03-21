package com.ryuqq.marketplace.adapter.out.client.sellic;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ryuqq.marketplace.adapter.out.client.sellic.dto.SellicApiResponse;
import com.ryuqq.marketplace.adapter.out.client.sellic.dto.SellicProductRegistrationRequest;
import com.ryuqq.marketplace.adapter.out.client.sellic.dto.SellicProductUpdateRequest;
import com.ryuqq.marketplace.adapter.out.client.sellic.mapper.SellicCommerceProductMapper;
import com.ryuqq.marketplace.application.product.dto.response.ProductOptionMappingResult;
import com.ryuqq.marketplace.application.product.dto.response.ProductResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeQueryResult;
import com.ryuqq.marketplace.application.productgroup.dto.response.ProductGroupSyncData;
import com.ryuqq.marketplace.application.productgroup.dto.response.SellerOptionGroupResult;
import com.ryuqq.marketplace.application.productgroup.dto.response.SellerOptionValueResult;
import com.ryuqq.marketplace.application.productgroupimage.dto.response.ProductGroupImageResult;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * 셀릭 API 실제 상품 등록/수정/삭제 통합 테스트.
 *
 * <p>실행: {@code ./gradlew :adapter-out:client:sellic-commerce-client:test --tests
 * "*SellicRealProductIntegrationTest*" -Dtest.tags=external-integration}
 */
@Tag("external-integration")
@DisplayName("셀릭 실제 상품 등록/수정/삭제 통합 테스트")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SellicRealProductIntegrationTest {

    private static final String BASE_URL = "http://api.sellic.co.kr";
    private static final String CUSTOMER_ID = "1012";
    private static final String API_KEY = "REDACTED_API_KEY";

    private final SellicCommerceProductMapper mapper = new SellicCommerceProductMapper();
    private final ObjectMapper objectMapper =
            new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    private final HttpClient httpClient =
            HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();

    /** 등록 후 product_id를 저장하여 수정/삭제에서 사용. */
    private static String registeredProductId;

    @Test
    @Order(1)
    @DisplayName("1. 상품 등록 → 셀릭 API 호출")
    void registerProduct() throws Exception {
        ProductGroupSyncData syncData = buildTestSyncData();

        SellicProductRegistrationRequest request =
                mapper.toRegistrationRequest(syncData, CUSTOMER_ID, API_KEY);

        String requestJson = objectMapper.writeValueAsString(request);
        System.out.println("=== 셀릭 상품 등록 요청 JSON ===");
        System.out.println(requestJson);

        SellicApiResponse response = callSellicApi("/openapi/set_product", requestJson);

        System.out.println("=== 셀릭 상품 등록 응답 ===");
        System.out.println("result: " + response.result());
        System.out.println("message: " + response.message());
        System.out.println("product_id: " + response.productId());

        assertThat(response.isSuccess()).as("상품 등록 성공 여부").isTrue();
        assertThat(response.productId()).as("상품 ID 반환").isNotNull();

        registeredProductId = response.productId();
        System.out.println("[PASS] 상품 등록 성공! productId=" + registeredProductId);
    }

    @Test
    @Order(2)
    @DisplayName("2. 상품 수정 → 셀릭 API 호출")
    void updateProduct() throws Exception {
        if (registeredProductId == null) {
            System.out.println("[SKIP] 등록된 상품이 없어 수정 테스트 건너뜀");
            return;
        }

        ProductGroupSyncData syncData = buildTestSyncDataForUpdate();

        SellicProductUpdateRequest request =
                mapper.toUpdateRequest(syncData, registeredProductId, CUSTOMER_ID, API_KEY);

        String requestJson = objectMapper.writeValueAsString(request);
        System.out.println("=== 셀릭 상품 수정 요청 JSON ===");
        System.out.println(requestJson);

        SellicApiResponse response = callSellicApi("/openapi/edit_product", requestJson);

        System.out.println("=== 셀릭 상품 수정 응답 ===");
        System.out.println("result: " + response.result());
        System.out.println("message: " + response.message());

        assertThat(response.isSuccess()).as("상품 수정 성공 여부").isTrue();
        System.out.println("[PASS] 상품 수정 성공! productId=" + registeredProductId);
    }

    @Test
    @Order(3)
    @DisplayName("3. 상품 삭제(판매종료) → 셀릭 API 호출")
    void deleteProduct() throws Exception {
        if (registeredProductId == null) {
            System.out.println("[SKIP] 등록된 상품이 없어 삭제 테스트 건너뜀");
            return;
        }

        SellicProductUpdateRequest request =
                mapper.toDeleteRequest(registeredProductId, CUSTOMER_ID, API_KEY);

        String requestJson = objectMapper.writeValueAsString(request);
        System.out.println("=== 셀릭 상품 삭제(판매종료) 요청 JSON ===");
        System.out.println(requestJson);

        SellicApiResponse response = callSellicApi("/openapi/edit_product", requestJson);

        System.out.println("=== 셀릭 상품 삭제 응답 ===");
        System.out.println("result: " + response.result());
        System.out.println("message: " + response.message());

        assertThat(response.isSuccess()).as("상품 삭제 성공 여부").isTrue();
        System.out.println("[PASS] 상품 삭제(판매종료) 성공! productId=" + registeredProductId);
    }

    // ===== Helper =====

    private SellicApiResponse callSellicApi(String path, String requestJson) throws Exception {
        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + path))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                        .build();

        HttpResponse<String> httpResponse =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("HTTP Status: " + httpResponse.statusCode());
        System.out.println("Response Body: " + httpResponse.body());

        String body = httpResponse.body();
        if (body == null || !body.trim().startsWith("{")) {
            return new SellicApiResponse("error", "HTTP " + httpResponse.statusCode() + ": " + body, null);
        }
        return objectMapper.readValue(body, SellicApiResponse.class);
    }

    /** 등록용 테스트 SyncData (옵션 있는 상품). */
    private ProductGroupSyncData buildTestSyncData() {
        Instant now = Instant.now();

        ProductGroupDetailCompositeQueryResult queryResult =
                new ProductGroupDetailCompositeQueryResult(
                        99999L, 25L, "테스트 셀러", 421L, "TESTBRAND",
                        52L, "테스트 카테고리", "패션의류 > 여성의류 > 티셔츠", "52",
                        "[테스트] 셀릭 API 연동 테스트 상품", "COMBINATION", "ACTIVE",
                        now, now, null, null);

        List<ProductGroupImageResult> images = List.of(
                new ProductGroupImageResult(
                        1L,
                        "https://stage-cdn.set-of.com/public/2026/03/019ce630-e584-7c2b-8b1e-1e38864f7f4e.jpg",
                        "https://stage-cdn.set-of.com/public/2026/03/019ce630-e584-7c2b-8b1e-1e38864f7f4e.jpg",
                        "THUMBNAIL", 0, List.of()));

        List<SellerOptionGroupResult> optionGroups = List.of(
                new SellerOptionGroupResult(1L, "사이즈", null, "SELECT", 0,
                        List.of(
                                new SellerOptionValueResult(10L, 1L, "S", null, 0),
                                new SellerOptionValueResult(11L, 1L, "M", null, 1),
                                new SellerOptionValueResult(12L, 1L, "L", null, 2))),
                new SellerOptionGroupResult(2L, "색상", null, "SELECT", 1,
                        List.of(
                                new SellerOptionValueResult(20L, 2L, "블랙", null, 0),
                                new SellerOptionValueResult(21L, 2L, "화이트", null, 1))));

        List<ProductResult> products = List.of(
                new ProductResult(1001L, 99999L, "SKU-001", 50000, 39000, 39000, 22, 10,
                        "ACTIVE", 0,
                        List.of(
                                new ProductOptionMappingResult(1L, 1001L, 10L, "사이즈", "S"),
                                new ProductOptionMappingResult(2L, 1001L, 20L, "색상", "블랙")),
                        now, now),
                new ProductResult(1002L, 99999L, "SKU-002", 50000, 39000, 39000, 22, 15,
                        "ACTIVE", 1,
                        List.of(
                                new ProductOptionMappingResult(3L, 1002L, 11L, "사이즈", "M"),
                                new ProductOptionMappingResult(4L, 1002L, 20L, "색상", "블랙")),
                        now, now),
                new ProductResult(1003L, 99999L, "SKU-003", 50000, 39000, 39000, 22, 20,
                        "ACTIVE", 2,
                        List.of(
                                new ProductOptionMappingResult(5L, 1003L, 12L, "사이즈", "L"),
                                new ProductOptionMappingResult(6L, 1003L, 21L, "색상", "화이트")),
                        now, now));

        return new ProductGroupSyncData(
                queryResult, images, optionGroups, "ACTIVE", false, products,
                Optional.of("<p>셀릭 API 연동 테스트 상품입니다.</p>"),
                Optional.empty(), Optional.empty(), Optional.empty(), Map.of());
    }

    /** 수정용 테스트 SyncData (상품명, 가격 변경). */
    private ProductGroupSyncData buildTestSyncDataForUpdate() {
        Instant now = Instant.now();

        ProductGroupDetailCompositeQueryResult queryResult =
                new ProductGroupDetailCompositeQueryResult(
                        99999L, 25L, "테스트 셀러", 421L, "TESTBRAND",
                        52L, "테스트 카테고리", "패션의류 > 여성의류 > 티셔츠", "52",
                        "[테스트] 셀릭 API 연동 테스트 상품 (수정됨)", "COMBINATION", "ACTIVE",
                        now, now, null, null);

        List<ProductGroupImageResult> images = List.of(
                new ProductGroupImageResult(
                        1L,
                        "https://stage-cdn.set-of.com/public/2026/03/019ce630-e584-7c2b-8b1e-1e38864f7f4e.jpg",
                        "https://stage-cdn.set-of.com/public/2026/03/019ce630-e584-7c2b-8b1e-1e38864f7f4e.jpg",
                        "THUMBNAIL", 0, List.of()));

        List<SellerOptionGroupResult> optionGroups = List.of(
                new SellerOptionGroupResult(1L, "사이즈", null, "SELECT", 0,
                        List.of(
                                new SellerOptionValueResult(10L, 1L, "S", null, 0),
                                new SellerOptionValueResult(11L, 1L, "M", null, 1),
                                new SellerOptionValueResult(12L, 1L, "L", null, 2))),
                new SellerOptionGroupResult(2L, "색상", null, "SELECT", 1,
                        List.of(
                                new SellerOptionValueResult(20L, 2L, "블랙", null, 0),
                                new SellerOptionValueResult(21L, 2L, "화이트", null, 1))));

        List<ProductResult> products = List.of(
                new ProductResult(1001L, 99999L, "SKU-001", 55000, 42000, 42000, 24, 5,
                        "ACTIVE", 0,
                        List.of(
                                new ProductOptionMappingResult(1L, 1001L, 10L, "사이즈", "S"),
                                new ProductOptionMappingResult(2L, 1001L, 20L, "색상", "블랙")),
                        now, now),
                new ProductResult(1002L, 99999L, "SKU-002", 55000, 42000, 42000, 24, 8,
                        "ACTIVE", 1,
                        List.of(
                                new ProductOptionMappingResult(3L, 1002L, 11L, "사이즈", "M"),
                                new ProductOptionMappingResult(4L, 1002L, 20L, "색상", "블랙")),
                        now, now),
                new ProductResult(1003L, 99999L, "SKU-003", 55000, 42000, 42000, 24, 12,
                        "ACTIVE", 2,
                        List.of(
                                new ProductOptionMappingResult(5L, 1003L, 12L, "사이즈", "L"),
                                new ProductOptionMappingResult(6L, 1003L, 21L, "색상", "화이트")),
                        now, now));

        return new ProductGroupSyncData(
                queryResult, images, optionGroups, "ACTIVE", false, products,
                Optional.of("<p>셀릭 API 연동 테스트 상품 (수정됨).</p>"),
                Optional.empty(), Optional.empty(), Optional.empty(), Map.of());
    }
}
