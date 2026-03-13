package com.ryuqq.marketplace.adapter.out.client.setof;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofDescriptionRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofImagesRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofNoticeRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupBasicInfoUpdateRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupRegistrationRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupRegistrationRequest.DescriptionRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupRegistrationRequest.ImageRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupRegistrationRequest.NoticeEntryRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupRegistrationRequest.NoticeRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupRegistrationRequest.ProductRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupRegistrationResponse;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupUpdateRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductPriceUpdateRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductStockUpdateRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductsUpdateRequest;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

/**
 * 세토프 커머스 API 실제 호출 통합 테스트.
 *
 * <p>DTO 기반으로 상품 등록 → 조회 → 부분 수정(BASIC_INFO, PRICE, STOCK, OPTION/PRODUCTS, IMAGE,
 * DESCRIPTION, NOTICE) → 전체 수정 → 판매중지 전체 흐름을 검증합니다.
 *
 * <p>실행 시 환경변수 필요: SETOF_COMMERCE_BASE_URL, SETOF_COMMERCE_SERVICE_TOKEN
 */
@SpringBootTest(classes = SetofCommerceTestApplication.class)
@ActiveProfiles("setof-test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SetofCommerceProductIntegrationTest {

    /** 세토프 셀러 ID (trexi = 25). */
    private static final long TEST_SELLER_ID = 25L;

    /** 세토프 브랜드 ID. */
    private static final long TEST_BRAND_ID = 19L;

    /** 세토프 카테고리 ID. */
    private static final long TEST_CATEGORY_ID = 1L;

    /** 테스트에서 등록한 상품 그룹 ID를 공유. */
    private static Long registeredProductGroupId;

    /** 등록 시 생성된 개별 상품 ID (가격/재고 수정에 필요). */
    private static Long registeredProductId;

    @Autowired
    private RestClient setofCommerceRestClient;

    // ──────────────────────────────────────────────────────────────
    // 1. 상품 등록
    // ──────────────────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("상품 등록 - 최소 데이터로 상품 그룹을 등록한다")
    void registerProductGroup() {
        String productName = "MarketPlace-테스트-" + System.currentTimeMillis() % 100000;

        SetofProductGroupRegistrationRequest request = new SetofProductGroupRegistrationRequest(
                null,
                TEST_SELLER_ID,
                TEST_BRAND_ID,
                TEST_CATEGORY_ID,
                null,
                null,
                productName,
                "NONE",
                50000,
                40000,
                List.of(
                        new ImageRequest(
                                "THUMBNAIL",
                                "https://stage-cdn.set-of.com/public/2026/03/019cd52e-d294-7f72-89ec-210f68a66e5b.jpg",
                                0)),
                List.of(),
                List.of(
                        new ProductRequest(
                                "TEST-SKU-001",
                                50000,
                                40000,
                                10,
                                1,
                                List.of())),
                new DescriptionRequest(
                        "<p>MarketPlace 통합 테스트 상품입니다.</p>",
                        List.of()),
                new NoticeRequest(
                        List.of(
                                new NoticeEntryRequest(null, "제조국", "상세 페이지 참고"),
                                new NoticeEntryRequest(null, "색상", "상세 페이지 참고"))));

        SetofProductGroupRegistrationResponse response = setofCommerceRestClient
                .post()
                .uri("/api/v2/admin/product-groups")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(SetofProductGroupRegistrationResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.productGroupId()).isNotNull();

        registeredProductGroupId = response.productGroupId();
        System.out.println("[PASS] 상품 등록 성공: productGroupId=" + registeredProductGroupId
                + ", name=" + productName);
    }

    // ──────────────────────────────────────────────────────────────
    // 2. 상품 조회 + 개별 상품 ID 추출
    // ──────────────────────────────────────────────────────────────

    @Test
    @Order(2)
    @DisplayName("상품 조회 - 등록한 상품 그룹을 GET API로 조회하고 개별 상품 ID를 추출한다")
    @SuppressWarnings("unchecked")
    void verifyRegisteredProductGroup() {
        assertThat(registeredProductGroupId).as("등록된 상품 ID가 없습니다 (등록 테스트 실패?)").isNotNull();

        Map<String, Object> response = setofCommerceRestClient
                .get()
                .uri("/api/v2/admin/product-groups/{id}", registeredProductGroupId)
                .retrieve()
                .body(Map.class);

        assertThat(response).isNotNull();

        // 개별 상품 ID 추출 (가격/재고 수정 테스트에 필요)
        List<Map<String, Object>> products = (List<Map<String, Object>>) response.get("products");
        if (products != null && !products.isEmpty()) {
            registeredProductId = ((Number) products.get(0).get("productId")).longValue();
            System.out.println("[PASS] 상품 조회 성공: productGroupId=" + registeredProductGroupId
                    + ", productId=" + registeredProductId);
        } else {
            System.out.println("[PASS] 상품 조회 성공 (개별 상품 ID 미추출): " + response);
        }
    }

    // ──────────────────────────────────────────────────────────────
    // 3. 부분 수정 - BASIC_INFO (상품명/브랜드/카테고리)
    // ──────────────────────────────────────────────────────────────

    @Test
    @Order(3)
    @DisplayName("부분수정 - BASIC_INFO: 상품명/브랜드/카테고리를 PATCH로 변경한다")
    void updateBasicInfo() {
        assertThat(registeredProductGroupId).as("등록된 상품 ID가 없습니다").isNotNull();

        String updatedName = "MarketPlace-기본정보수정-" + System.currentTimeMillis() % 100000;

        SetofProductGroupBasicInfoUpdateRequest request = new SetofProductGroupBasicInfoUpdateRequest(
                updatedName,
                TEST_BRAND_ID,
                TEST_CATEGORY_ID,
                null,
                null);

        setofCommerceRestClient
                .patch()
                .uri("/api/v2/admin/product-groups/{id}/basic-info", registeredProductGroupId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();

        System.out.println("[PASS] BASIC_INFO 수정 성공: productGroupId=" + registeredProductGroupId
                + ", name=" + updatedName);
    }

    // ──────────────────────────────────────────────────────────────
    // 4. 부분 수정 - PRICE (개별 상품 가격)
    // ──────────────────────────────────────────────────────────────

    @Test
    @Order(4)
    @DisplayName("부분수정 - PRICE: 개별 상품의 정가/판매가를 PATCH로 변경한다")
    void updateProductPrice() {
        assertThat(registeredProductId).as("개별 상품 ID가 없습니다 (조회 테스트에서 추출 실패?)").isNotNull();

        SetofProductPriceUpdateRequest request = new SetofProductPriceUpdateRequest(55000, 42000);

        setofCommerceRestClient
                .patch()
                .uri("/api/v2/admin/products/{productId}/price", registeredProductId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();

        System.out.println("[PASS] PRICE 수정 성공: productId=" + registeredProductId
                + ", regularPrice=55000, currentPrice=42000");
    }

    // ──────────────────────────────────────────────────────────────
    // 5. 부분 수정 - STOCK (개별 상품 재고)
    // ──────────────────────────────────────────────────────────────

    @Test
    @Order(5)
    @DisplayName("부분수정 - STOCK: 개별 상품의 재고수량을 PATCH로 변경한다")
    void updateProductStock() {
        assertThat(registeredProductId).as("개별 상품 ID가 없습니다").isNotNull();

        SetofProductStockUpdateRequest request = new SetofProductStockUpdateRequest(99);

        setofCommerceRestClient
                .patch()
                .uri("/api/v2/admin/products/{productId}/stock", registeredProductId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();

        System.out.println("[PASS] STOCK 수정 성공: productId=" + registeredProductId
                + ", stockQuantity=99");
    }

    // ──────────────────────────────────────────────────────────────
    // 6. 부분 수정 - OPTION/PRODUCTS (옵션그룹 + 상품 일괄)
    // ──────────────────────────────────────────────────────────────

    @Test
    @Order(6)
    @DisplayName("부분수정 - OPTION: 옵션그룹/상품을 일괄 PATCH로 변경한다")
    void updateProductsAndOptions() {
        assertThat(registeredProductGroupId).as("등록된 상품 ID가 없습니다").isNotNull();

        SetofProductsUpdateRequest request = new SetofProductsUpdateRequest(
                List.of(
                        new SetofProductsUpdateRequest.OptionGroupRequest(
                                null,
                                "사이즈",
                                0,
                                List.of(
                                        new SetofProductsUpdateRequest.OptionValueRequest(
                                                null, "FREE", 0)))),
                List.of(
                        new SetofProductsUpdateRequest.ProductRequest(
                                registeredProductId,
                                "TEST-SKU-001",
                                55000,
                                42000,
                                99,
                                1,
                                List.of(
                                        new SetofProductsUpdateRequest.SelectedOptionRequest(
                                                "사이즈", "FREE")))));

        setofCommerceRestClient
                .patch()
                .uri("/api/v2/admin/products/product-groups/{productGroupId}", registeredProductGroupId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();

        System.out.println("[PASS] OPTION/PRODUCTS 수정 성공: productGroupId=" + registeredProductGroupId);
    }

    // ──────────────────────────────────────────────────────────────
    // 7. 부분 수정 - IMAGE (이미지 교체)
    // ──────────────────────────────────────────────────────────────

    @Test
    @Order(7)
    @DisplayName("부분수정 - IMAGE: 상품 그룹의 이미지를 PUT으로 교체한다")
    void updateImages() {
        assertThat(registeredProductGroupId).as("등록된 상품 ID가 없습니다").isNotNull();

        SetofImagesRequest request = new SetofImagesRequest(
                List.of(
                        new SetofImagesRequest.ImageRequest(
                                "THUMBNAIL",
                                "https://stage-cdn.set-of.com/public/2026/03/019cd52e-d294-7f72-89ec-210f68a66e5b.jpg",
                                0),
                        new SetofImagesRequest.ImageRequest(
                                "DETAIL",
                                "https://stage-cdn.set-of.com/public/2026/03/019cd52e-d294-7f72-89ec-210f68a66e5b.jpg",
                                1)));

        setofCommerceRestClient
                .put()
                .uri("/api/v2/admin/product-groups/{id}/images", registeredProductGroupId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();

        System.out.println("[PASS] IMAGE 수정 성공: productGroupId=" + registeredProductGroupId);
    }

    // ──────────────────────────────────────────────────────────────
    // 8. 부분 수정 - DESCRIPTION (상세설명)
    // ──────────────────────────────────────────────────────────────

    @Test
    @Order(8)
    @DisplayName("부분수정 - DESCRIPTION: 상품 그룹의 상세설명을 PUT으로 교체한다")
    void updateDescription() {
        assertThat(registeredProductGroupId).as("등록된 상품 ID가 없습니다").isNotNull();

        SetofDescriptionRequest request = new SetofDescriptionRequest(
                "<p>MarketPlace 통합 테스트 - 상세설명이 수정되었습니다.</p>",
                List.of(
                        new SetofDescriptionRequest.DescriptionImageRequest(
                                "https://stage-cdn.set-of.com/public/2026/03/019cd52e-d294-7f72-89ec-210f68a66e5b.jpg",
                                0)));

        setofCommerceRestClient
                .put()
                .uri("/api/v2/admin/product-groups/{id}/description", registeredProductGroupId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();

        System.out.println("[PASS] DESCRIPTION 수정 성공: productGroupId=" + registeredProductGroupId);
    }

    // ──────────────────────────────────────────────────────────────
    // 9. 부분 수정 - NOTICE (고시정보)
    // ──────────────────────────────────────────────────────────────

    @Test
    @Order(9)
    @DisplayName("부분수정 - NOTICE: 상품 그룹의 고시정보를 PUT으로 교체한다")
    void updateNotice() {
        assertThat(registeredProductGroupId).as("등록된 상품 ID가 없습니다").isNotNull();

        SetofNoticeRequest request = new SetofNoticeRequest(
                List.of(
                        new SetofNoticeRequest.NoticeEntryRequest(null, "제조국", "대한민국"),
                        new SetofNoticeRequest.NoticeEntryRequest(null, "색상", "블랙"),
                        new SetofNoticeRequest.NoticeEntryRequest(null, "소재", "면 100%")));

        setofCommerceRestClient
                .put()
                .uri("/api/v2/admin/product-groups/{id}/notice", registeredProductGroupId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();

        System.out.println("[PASS] NOTICE 수정 성공: productGroupId=" + registeredProductGroupId);
    }

    // ──────────────────────────────────────────────────────────────
    // 10. 전체 수정 (Full Update)
    // ──────────────────────────────────────────────────────────────

    @Test
    @Order(10)
    @DisplayName("전체수정 - 가격/상품명을 포함한 전체 PUT 수정을 수행한다")
    void updateProductGroupFull() {
        assertThat(registeredProductGroupId).as("등록된 상품 ID가 없습니다").isNotNull();

        String updatedName = "MarketPlace-전체수정-" + System.currentTimeMillis() % 100000;

        SetofProductGroupUpdateRequest updateRequest = new SetofProductGroupUpdateRequest(
                updatedName,
                TEST_BRAND_ID,
                TEST_CATEGORY_ID,
                null,
                null,
                "NONE",
                60000,
                45000,
                null,
                null,
                List.of(
                        new SetofProductGroupUpdateRequest.ProductRequest(
                                registeredProductId,
                                "TEST-SKU-001",
                                60000,
                                45000,
                                20,
                                1,
                                List.of())),
                null,
                null);

        setofCommerceRestClient
                .put()
                .uri("/api/v2/admin/product-groups/{id}", registeredProductGroupId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateRequest)
                .retrieve()
                .toBodilessEntity();

        System.out.println("[PASS] 전체 수정 성공: productGroupId=" + registeredProductGroupId
                + ", name=" + updatedName);
    }

    // ──────────────────────────────────────────────────────────────
    // 11. 판매중지
    // ──────────────────────────────────────────────────────────────

    @Test
    @Order(11)
    @DisplayName("판매중지 - 등록한 상품 그룹을 판매중지 상태로 변경한다")
    void suspendProductGroup() {
        assertThat(registeredProductGroupId).as("등록된 상품 ID가 없습니다").isNotNull();

        SetofProductGroupUpdateRequest deleteRequest = new SetofProductGroupUpdateRequest(
                null, null, null, null, null, null,
                0, 0, null, null, null, null, null);

        setofCommerceRestClient
                .put()
                .uri("/api/v2/admin/product-groups/{id}", registeredProductGroupId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(deleteRequest)
                .retrieve()
                .toBodilessEntity();

        System.out.println("[PASS] 판매중지 성공: productGroupId=" + registeredProductGroupId);
    }
}
