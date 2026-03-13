package com.ryuqq.marketplace.adapter.out.client.naver;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.client.naver.adapter.NaverCommerceProductClientAdapter;
import com.ryuqq.marketplace.adapter.out.client.naver.auth.NaverCommerceTokenManager;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductSearchRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductSearchResponse;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

/**
 * 네이버 커머스 API 실제 호출 통합 테스트.
 *
 * <p>DTO 기반으로 상품 등록 → 조회 → 수정 → 판매중지 전체 흐름을 검증합니다.
 *
 * <p>실행 시 환경변수 필요: NAVER_COMMERCE_CLIENT_ID, NAVER_COMMERCE_CLIENT_SECRET
 */
@SpringBootTest(classes = NaverCommerceTestApplication.class)
@ActiveProfiles("naver-test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class NaverCommerceProductIntegrationTest {

    /** 네이버 카테고리 ID (완구 > 피규어/인형 > 캐릭터인형). */
    private static final String TEST_CATEGORY_ID = "50002322";

    /** 테스트에서 등록한 상품 ID를 공유. */
    private static String registeredProductId;

    @Autowired
    private NaverCommerceProductClientAdapter productClientAdapter;

    @Autowired
    private RestClient naverCommerceRestClient;

    @Autowired
    private NaverCommerceTokenManager tokenManager;

    @Test
    @Order(1)
    @DisplayName("토큰 발급 - 정상적으로 액세스 토큰을 받아온다")
    void tokenIssuance() {
        String token = tokenManager.getAccessToken();
        assertThat(token).isNotBlank();
        System.out.println("[PASS] 토큰 발급 성공");
    }

    @Test
    @Order(2)
    @DisplayName("상품 등록 - 기존 테스트 상품 구조를 복제하여 신규 등록한다")
    void registerProduct() {
        String token = tokenManager.getAccessToken();
        String productName = "MarketPlace-테스트-" + System.currentTimeMillis() % 100000;

        // 기존 "test" 상품(12648262923)의 구조를 가져와서 복제 등록
        @SuppressWarnings("unchecked")
        Map<String, Object> sourceDetail = naverCommerceRestClient
                .get()
                .uri("/v2/products/origin-products/{id}", "12648262923")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(Map.class);

        @SuppressWarnings("unchecked")
        Map<String, Object> sourceOrigin = new java.util.HashMap<>(
                (Map<String, Object>) sourceDetail.get("originProduct"));

        // 변경할 필드만 덮어쓰기
        sourceOrigin.put("name", productName);
        sourceOrigin.put("salePrice", 5000);
        sourceOrigin.put("stockQuantity", 10);
        sourceOrigin.put("detailContent",
                "<p>MarketPlace 통합 테스트 상품입니다. 등록 후 즉시 판매중지 예정.</p>");
        // 등록 시 불필요한 읽기전용 필드 제거
        sourceOrigin.remove("originProductNo");
        sourceOrigin.remove("channelProducts");
        sourceOrigin.remove("createdDate");
        sourceOrigin.remove("modifiedDate");
        sourceOrigin.remove("barcode");

        @SuppressWarnings("unchecked")
        Map<String, Object> sourceSmartstore = sourceDetail.get("smartstoreChannelProduct") != null
                ? new java.util.HashMap<>((Map<String, Object>) sourceDetail.get("smartstoreChannelProduct"))
                : new java.util.HashMap<>();
        sourceSmartstore.put("channelProductName", productName);
        sourceSmartstore.remove("channelProductNo");

        Map<String, Object> registerBody = Map.of(
                "originProduct", sourceOrigin,
                "smartstoreChannelProduct", sourceSmartstore);

        // POST 등록
        @SuppressWarnings("unchecked")
        Map<String, Object> response = naverCommerceRestClient
                .post()
                .uri("/v2/products")
                .header("Authorization", "Bearer " + token)
                .body(registerBody)
                .retrieve()
                .body(Map.class);

        assertThat(response).isNotNull();
        assertThat(response.get("originProductNo")).isNotNull();

        registeredProductId = String.valueOf(response.get("originProductNo"));
        System.out.println("[PASS] 상품 등록 성공: originProductNo=" + registeredProductId
                + ", name=" + productName);
    }

    @Test
    @Order(3)
    @DisplayName("상품 조회 - 등록한 상품을 GET API로 조회하여 데이터를 확인한다")
    void verifyRegisteredProduct() {
        assertThat(registeredProductId).as("등록된 상품 ID가 없습니다 (등록 테스트 실패?)").isNotNull();

        String token = tokenManager.getAccessToken();

        @SuppressWarnings("unchecked")
        Map<String, Object> detail = naverCommerceRestClient
                .get()
                .uri("/v2/products/origin-products/{id}", registeredProductId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(Map.class);

        @SuppressWarnings("unchecked")
        Map<String, Object> origin = (Map<String, Object>) detail.get("originProduct");

        assertThat(origin.get("statusType")).isEqualTo("SALE");
        assertThat(((Number) origin.get("salePrice")).intValue()).isEqualTo(5000);
        assertThat(((Number) origin.get("stockQuantity")).intValue()).isEqualTo(10);

        System.out.println("[PASS] 상품 조회 확인: name=" + origin.get("name")
                + ", status=" + origin.get("statusType")
                + ", price=" + origin.get("salePrice")
                + ", stock=" + origin.get("stockQuantity"));
    }

    @Test
    @Order(4)
    @DisplayName("상품 수정 - DTO 기반으로 가격/재고/상품명을 변경한다")
    void updateRegisteredProduct() {
        assertThat(registeredProductId).as("등록된 상품 ID가 없습니다").isNotNull();

        String token = tokenManager.getAccessToken();

        // 기존 데이터 조회
        @SuppressWarnings("unchecked")
        Map<String, Object> detail = naverCommerceRestClient
                .get()
                .uri("/v2/products/origin-products/{id}", registeredProductId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(Map.class);

        @SuppressWarnings("unchecked")
        Map<String, Object> currentOrigin = (Map<String, Object>) detail.get("originProduct");
        @SuppressWarnings("unchecked")
        Map<String, Object> currentImages = (Map<String, Object>) currentOrigin.get("images");
        @SuppressWarnings("unchecked")
        Map<String, Object> currentDetailAttr =
                (Map<String, Object>) currentOrigin.get("detailAttribute");
        String categoryId = String.valueOf(currentOrigin.get("leafCategoryId"));

        // 수정할 값
        String updatedName = "MarketPlace-수정됨-" + System.currentTimeMillis() % 100000;

        // 기존 구조 재활용 + 변경분만 덮어쓰기
        @SuppressWarnings("unchecked")
        Map<String, Object> originUpdate = new java.util.HashMap<>(currentOrigin);
        originUpdate.put("name", updatedName);
        originUpdate.put("salePrice", 3000);
        originUpdate.put("stockQuantity", 50);
        originUpdate.put("detailContent", "<p>수정된 테스트 상품 상세입니다.</p>");

        @SuppressWarnings("unchecked")
        Map<String, Object> currentSmartstore = detail.get("smartstoreChannelProduct") != null
                ? new java.util.HashMap<>((Map<String, Object>) detail.get("smartstoreChannelProduct"))
                : new java.util.HashMap<>();
        currentSmartstore.put("channelProductName", updatedName);

        Map<String, Object> updateBody = Map.of(
                "originProduct", originUpdate,
                "smartstoreChannelProduct", currentSmartstore);

        // PUT 수정
        naverCommerceRestClient
                .put()
                .uri("/v2/products/origin-products/{id}", registeredProductId)
                .header("Authorization", "Bearer " + token)
                .body(updateBody)
                .retrieve()
                .toBodilessEntity();

        // 수정 확인
        @SuppressWarnings("unchecked")
        Map<String, Object> afterDetail = naverCommerceRestClient
                .get()
                .uri("/v2/products/origin-products/{id}", registeredProductId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(Map.class);

        @SuppressWarnings("unchecked")
        Map<String, Object> afterOrigin = (Map<String, Object>) afterDetail.get("originProduct");

        assertThat(String.valueOf(afterOrigin.get("name"))).isEqualTo(updatedName);
        assertThat(((Number) afterOrigin.get("salePrice")).intValue()).isEqualTo(3000);
        assertThat(((Number) afterOrigin.get("stockQuantity")).intValue()).isEqualTo(50);

        System.out.println("[PASS] 상품 수정 확인: name=" + afterOrigin.get("name")
                + ", price=" + afterOrigin.get("salePrice")
                + ", stock=" + afterOrigin.get("stockQuantity"));
    }

    @Test
    @Order(5)
    @DisplayName("판매중지 - 등록한 테스트 상품을 판매중지 상태로 변경한다")
    void suspendRegisteredProduct() {
        assertThat(registeredProductId).as("등록된 상품 ID가 없습니다").isNotNull();

        String token = tokenManager.getAccessToken();

        // 기존 데이터 조회
        @SuppressWarnings("unchecked")
        Map<String, Object> detail = naverCommerceRestClient
                .get()
                .uri("/v2/products/origin-products/{id}", registeredProductId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(Map.class);

        @SuppressWarnings("unchecked")
        Map<String, Object> originUpdate = new java.util.HashMap<>(
                (Map<String, Object>) detail.get("originProduct"));
        originUpdate.put("statusType", "SUSPENSION");

        @SuppressWarnings("unchecked")
        Map<String, Object> smartstoreUpdate = detail.get("smartstoreChannelProduct") != null
                ? new java.util.HashMap<>((Map<String, Object>) detail.get("smartstoreChannelProduct"))
                : new java.util.HashMap<>();
        smartstoreUpdate.put("channelProductDisplayStatusType", "SUSPENSION");

        Map<String, Object> updateBody = Map.of(
                "originProduct", originUpdate,
                "smartstoreChannelProduct", smartstoreUpdate);

        // PUT 판매중지
        naverCommerceRestClient
                .put()
                .uri("/v2/products/origin-products/{id}", registeredProductId)
                .header("Authorization", "Bearer " + token)
                .body(updateBody)
                .retrieve()
                .toBodilessEntity();

        // 판매중지 확인
        @SuppressWarnings("unchecked")
        Map<String, Object> afterDetail = naverCommerceRestClient
                .get()
                .uri("/v2/products/origin-products/{id}", registeredProductId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(Map.class);

        @SuppressWarnings("unchecked")
        Map<String, Object> afterOrigin = (Map<String, Object>) afterDetail.get("originProduct");

        assertThat(String.valueOf(afterOrigin.get("statusType"))).isEqualTo("SUSPENSION");

        System.out.println("[PASS] 판매중지 확인: originProductNo=" + registeredProductId
                + ", status=" + afterOrigin.get("statusType"));
    }

    @Test
    @Order(6)
    @DisplayName("전체 상품 목록 조회 - 총 상품수를 확인한다")
    void searchAllProductsFirstPage() {
        NaverProductSearchRequest request = NaverProductSearchRequest.allProducts(1, 10);
        NaverProductSearchResponse response = productClientAdapter.searchProducts(request);

        assertThat(response).isNotNull();
        assertThat(response.totalElements()).isGreaterThan(0);
        System.out.println("[PASS] 전체 상품 조회: totalElements=" + response.totalElements());
    }
}
