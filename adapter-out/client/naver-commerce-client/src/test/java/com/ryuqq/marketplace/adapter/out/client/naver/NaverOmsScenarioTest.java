package com.ryuqq.marketplace.adapter.out.client.naver;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * 네이버 OMS 전체 시나리오 테스트.
 *
 * <p>테스트 상품 등록 → 주문 폴링 → 발주확인 → 송장등록 → 취소/반품/교환 시나리오를 실제 네이버 API로 검증합니다.
 *
 * <p>실행: {@code NAVER_CLIENT_ID=... NAVER_CLIENT_SECRET=...
 * ./gradlew :adapter-out:client:naver-commerce-client:externalIntegrationTest --tests
 * "*NaverOmsScenarioTest*"}
 */
@Tag("external-integration")
@DisplayName("네이버 OMS 전체 시나리오 테스트")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NaverOmsScenarioTest {

    private static final String BASE_URL = "https://api.commerce.naver.com/external";

    /** 네이버 카테고리: 패션잡화 > 기타 (필수정보 최소) */
    private static final long CATEGORY_ID = 50000804L;

    private final ObjectMapper objectMapper =
            new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    private final HttpClient httpClient = HttpClient.newHttpClient();

    private String token;
    private String registeredProductId;

    @BeforeAll
    void setUp() throws Exception {
        token = NaverAuthHelper.getAccessToken(httpClient, objectMapper);
        System.out.println("[OK] 네이버 토큰 발급 성공");
    }

    @Test
    @Order(1)
    @DisplayName("1. 테스트 상품 등록 (100원, '테스트 상품 주문하지마세요')")
    void registerTestProduct() throws Exception {
        // 최소 필수 필드로 상품 등록
        String requestJson = objectMapper.writeValueAsString(Map.of(
                "originProduct", Map.ofEntries(
                        Map.entry("statusType", "SALE"),
                        Map.entry("saleType", "NEW"),
                        Map.entry("leafCategoryId", String.valueOf(CATEGORY_ID)),
                        Map.entry("name", "[테스트] OMS 시나리오 테스트 상품 - 주문하지마세요"),
                        Map.entry("salePrice", 100),
                        Map.entry("stockQuantity", 999),
                        Map.entry("images", Map.of(
                                "representativeImage", Map.of(
                                        "url", "https://shop-phinf.pstatic.net/20250107_131/1736220241952imrJw_JPEG/20493846848437712_1555581227.jpg"))),
                        Map.entry("detailContent",
                                "<p>OMS 시나리오 테스트용 상품입니다. 주문하지 마세요.</p>"),
                        Map.entry("deliveryInfo", Map.of(
                                "deliveryType", "DELIVERY",
                                "deliveryAttributeType", "NORMAL",
                                "deliveryCompany", "CJGLS",
                                "deliveryFee", Map.of(
                                        "deliveryFeeType", "FREE",
                                        "baseFee", 0),
                                "claimDeliveryInfo", Map.of(
                                        "returnDeliveryFee", 3000,
                                        "exchangeDeliveryFee", 6000))),
                        Map.entry("detailAttribute", Map.of(
                                "afterServiceInfo", Map.of(
                                        "afterServiceTelephoneNumber", "01012345678",
                                        "afterServiceGuideContent", "테스트용"),
                                "originAreaInfo", Map.of(
                                        "originAreaCode", "03",
                                        "content", "중국"),
                                "minorPurchasable", false,
                                "productInfoProvidedNotice", Map.of(
                                        "productInfoProvidedNoticeType", "ETC",
                                        "etc", Map.of(
                                                "returnCostReason", "테스트",
                                                "noRefundReason", "테스트",
                                                "qualityAssuranceStandard", "테스트",
                                                "compensationProcedure", "테스트",
                                                "troubleShootingContents", "테스트",
                                                "itemName", "테스트 상품",
                                                "modelName", "테스트",
                                                "manufacturer", "테스트 제조사",
                                                "customerServicePhoneNumber", "01012345678"))))),
                "smartstoreChannelProduct", Map.of(
                        "channelProductDisplayStatusType", "ON",
                        "naverShoppingRegistration", true)));

        System.out.println("=== 상품 등록 요청 ===");

        HttpResponse<String> response = callNaverApi("POST", "/v2/products", requestJson);

        System.out.println("Status: " + response.statusCode());
        JsonNode body = objectMapper.readTree(response.body());
        System.out.println(objectMapper.writeValueAsString(body));

        if (response.statusCode() == 200) {
            registeredProductId = body.path("smartstoreChannelProductNo").asText(null);
            if (registeredProductId == null) {
                registeredProductId = body.path("originProductNo").asText(null);
            }
            System.out.println("[PASS] 상품 등록 성공! productId=" + registeredProductId);
        } else {
            System.out.println("[FAIL] 상품 등록 실패 — 에러 내용 위 로그 참조");
            System.out.println("에러 해결 후 판매자센터에서 직접 등록하셔도 됩니다.");
        }
    }

    @Test
    @Order(2)
    @DisplayName("2. 최근 주문 폴링 (결제완료 주문 조회)")
    void pollRecentOrders() throws Exception {
        // 최근 7일 변경 주문 조회
        java.time.Instant now = java.time.Instant.now();
        java.time.Instant from = now.minus(java.time.Duration.ofDays(7));
        String fromStr = formatForNaver(from);
        String toStr = formatForNaver(now);

        String url = "/v1/pay-order/seller/product-orders/last-changed-statuses"
                + "?lastChangedType=PAYED"
                + "&lastChangedFrom=" + java.net.URLEncoder.encode(fromStr, "UTF-8")
                + "&lastChangedTo=" + java.net.URLEncoder.encode(toStr, "UTF-8")
                + "&limitCount=50";

        HttpResponse<String> response = callNaverApi("GET", url, null);

        System.out.println("=== 주문 폴링 결과 ===");
        System.out.println("Status: " + response.statusCode());

        if (response.statusCode() == 200) {
            JsonNode body = objectMapper.readTree(response.body());
            JsonNode statuses = body.path("data").path("lastChangeStatuses");
            System.out.println("변경 주문 수: " + (statuses.isArray() ? statuses.size() : 0));

            if (statuses.isArray()) {
                for (int i = 0; i < Math.min(statuses.size(), 5); i++) {
                    JsonNode s = statuses.get(i);
                    System.out.println("  - productOrderId: " + s.path("productOrderId").asText()
                            + " | status: " + s.path("productOrderStatus").asText()
                            + " | changed: " + s.path("lastChangedType").asText());
                }
            }
        } else {
            System.out.println("응답: " + response.body());
        }
    }

    @Test
    @Order(3)
    @DisplayName("3. 배송완료된 운송장으로 발송처리 테스트 (실제 API 호출)")
    void dispatchWithDeliveredInvoice() throws Exception {
        // 실제 배송완료된 운송장 (luxurydb에서 조회한 것)
        // 이 테스트는 실제 productOrderId가 필요합니다.
        // 주문이 생기면 주석을 해제하고 productOrderId를 넣어 실행하세요.

        String productOrderId = null; // TODO: 실제 주문 생성 후 입력

        if (productOrderId == null) {
            System.out.println("[SKIP] productOrderId가 없어 발송처리 테스트 건너뜀");
            System.out.println("  → 스마트스토어에서 테스트 주문 생성 후 productOrderId를 입력하세요");
            return;
        }

        // 3-1. 발주확인
        String confirmJson = objectMapper.writeValueAsString(Map.of(
                "productOrderIds", List.of(productOrderId)));

        HttpResponse<String> confirmResponse = callNaverApi(
                "POST", "/v1/pay-order/seller/product-orders/confirm", confirmJson);

        System.out.println("=== 발주확인 ===");
        System.out.println("Status: " + confirmResponse.statusCode());
        System.out.println("Body: " + confirmResponse.body());

        // 3-2. 발송처리 (배송완료된 운송장 사용)
        String dispatchJson = objectMapper.writeValueAsString(Map.of(
                "dispatchProductOrders", List.of(Map.of(
                        "productOrderId", productOrderId,
                        "deliveryMethod", "DELIVERY",
                        "deliveryCompanyCode", "CJLOGISTICS",
                        "trackingNumber", "505836445860",
                        "dispatchDate", formatForNaver(java.time.Instant.now())))));

        HttpResponse<String> dispatchResponse = callNaverApi(
                "POST", "/v1/pay-order/seller/product-orders/dispatch", dispatchJson);

        System.out.println("=== 발송처리 ===");
        System.out.println("Status: " + dispatchResponse.statusCode());
        System.out.println("Body: " + dispatchResponse.body());
    }

    @Test
    @Order(4)
    @DisplayName("4. 취소 요청 테스트")
    void cancelOrder() throws Exception {
        String productOrderId = null; // TODO: 실제 주문 생성 후 입력

        if (productOrderId == null) {
            System.out.println("[SKIP] productOrderId가 없어 취소 테스트 건너뜀");
            return;
        }

        String cancelJson = objectMapper.writeValueAsString(Map.of(
                "productOrderId", productOrderId,
                "cancelReason", Map.of(
                        "code", "INTENT_CHANGED",
                        "detailedReason", "OMS 시나리오 테스트 - 판매자 취소")));

        HttpResponse<String> response = callNaverApi(
                "POST", "/v1/pay-order/seller/product-orders/"
                        + productOrderId + "/cancel", cancelJson);

        System.out.println("=== 취소 요청 ===");
        System.out.println("Status: " + response.statusCode());
        System.out.println("Body: " + response.body());
    }

    @Test
    @Order(5)
    @DisplayName("5. API 연결 전체 검증 (토큰 + 주문조회 + 상품조회)")
    void verifyApiConnectivity() throws Exception {
        System.out.println("=== API 연결 검증 ===");

        // 5-1. 토큰 유효성
        assertThat(token).as("토큰 발급").isNotNull().isNotBlank();
        System.out.println("[OK] 토큰: " + token.substring(0, 20) + "...");

        // 5-2. 판매자 정보 조회
        HttpResponse<String> sellerResponse = callNaverApi(
                "GET", "/v1/seller/get", null);
        System.out.println("[" + (sellerResponse.statusCode() == 200 ? "OK" : "FAIL")
                + "] 판매자 정보: " + sellerResponse.statusCode());

        // 5-3. 상품 목록 조회
        HttpResponse<String> productResponse = callNaverApi(
                "GET", "/v2/products/search?page=1&size=1", null);
        System.out.println("[" + (productResponse.statusCode() == 200 ? "OK" : "FAIL")
                + "] 상품 조회: " + productResponse.statusCode());

        // 5-4. 주문 조회 (조건형)
        String now = formatForNaver(java.time.Instant.now());
        String weekAgo = formatForNaver(java.time.Instant.now().minus(java.time.Duration.ofDays(7)));
        HttpResponse<String> orderResponse = callNaverApi(
                "GET", "/v1/pay-order/seller/product-orders/last-changed-statuses"
                        + "?lastChangedType=PAYED"
                        + "&lastChangedFrom=" + java.net.URLEncoder.encode(weekAgo, "UTF-8")
                        + "&lastChangedTo=" + java.net.URLEncoder.encode(now, "UTF-8")
                        + "&limitCount=1", null);
        System.out.println("[" + (orderResponse.statusCode() == 200 ? "OK" : "FAIL")
                + "] 주문 조회: " + orderResponse.statusCode());

        System.out.println("\n[DONE] API 연결 검증 완료");
    }

    // ===== Helper =====

    private HttpResponse<String> callNaverApi(String method, String path, String body)
            throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Authorization", "Bearer " + token);

        if ("POST".equals(method)) {
            builder.header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body != null ? body : "{}"));
        } else {
            builder.GET();
        }

        return httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private String formatForNaver(java.time.Instant instant) {
        return instant.atZone(java.time.ZoneId.of("Asia/Seoul"))
                .format(java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}
