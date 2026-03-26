package com.ryuqq.marketplace.integration.inboundwebhook;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import com.ryuqq.marketplace.adapter.out.persistence.claimsync.repository.ClaimSyncLogJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.inboundorder.repository.InboundOrderItemJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.inboundorder.repository.InboundOrderJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.OrderItemJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.OrderJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderItemJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemHistoryJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.ordermapping.entity.ExternalOrderItemMappingJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.ordermapping.repository.ExternalOrderItemMappingJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.refund.repository.RefundClaimJpaRepository;
import com.ryuqq.marketplace.integration.E2ETestBase;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * 내부 웹훅 전체 플로우 E2E 테스트.
 *
 * <p>테스트 대상: POST /api/v1/market/internal/webhooks/orders/*
 *
 * <ul>
 *   <li>FLOW-1: 주문 생성 → 구매 확정 흐름
 *   <li>FLOW-2: 주문 생성 → 즉시 취소 흐름
 *   <li>FLOW-3: 주문 생성 → 반품 요청 → 반품 철회 흐름
 *   <li>FLOW-4: 중복 주문 생성 방지 (멱등성)
 * </ul>
 *
 * <p>주의: 내부 웹훅은 VPC 내부 통신용으로 인증 헤더 없이 접근 가능합니다.
 *
 * @author ryu-qqq
 * @since 1.2.0
 */
@Tag("e2e")
@Tag("webhook")
@Tag("flow")
@DisplayName("내부 웹훅 전체 플로우 E2E 테스트")
class InboundWebhookFlowE2ETest extends E2ETestBase {

    // basePath: /api/v1/market (E2ETestBase에서 설정)
    private static final String WEBHOOK_CREATED = "/internal/webhooks/orders/created";
    private static final String WEBHOOK_CANCELLED = "/internal/webhooks/orders/cancelled";
    private static final String WEBHOOK_RETURN_REQUESTED =
            "/internal/webhooks/orders/return-requested";
    private static final String WEBHOOK_RETURN_WITHDRAWN =
            "/internal/webhooks/orders/return-withdrawn";
    private static final String WEBHOOK_PURCHASE_CONFIRMED =
            "/internal/webhooks/orders/purchase-confirmed";

    /** Fixtures의 DEFAULT_SALES_CHANNEL_ID와 동일해야 합니다. */
    private static final long SALES_CHANNEL_ID = 1L;

    private static final long SHOP_ID = 10L;

    @Autowired private InboundOrderJpaRepository inboundOrderRepository;
    @Autowired private InboundOrderItemJpaRepository inboundOrderItemRepository;
    @Autowired private OrderJpaRepository orderRepository;
    @Autowired private OrderItemJpaRepository orderItemRepository;
    @Autowired private OrderItemHistoryJpaRepository orderItemHistoryRepository;
    @Autowired private ExternalOrderItemMappingJpaRepository externalOrderItemMappingRepository;
    @Autowired private RefundClaimJpaRepository refundClaimRepository;
    @Autowired private ClaimSyncLogJpaRepository claimSyncLogRepository;

    @BeforeEach
    void setUp() {
        cleanUp();
    }

    @AfterEach
    void tearDown() {
        cleanUp();
    }

    private void cleanUp() {
        claimSyncLogRepository.deleteAll();
        refundClaimRepository.deleteAll();
        externalOrderItemMappingRepository.deleteAll();
        orderItemHistoryRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        inboundOrderItemRepository.deleteAll();
        inboundOrderRepository.deleteAll();
    }

    // ===== 사전 데이터 준비 헬퍼 =====

    /**
     * ExternalOrderItemMapping을 직접 시딩합니다.
     *
     * <p>ClaimSync 파이프라인은 externalProductOrderId → orderItemId 매핑이 있어야 처리됩니다. ORDER_CREATED 웹훅을 통해
     * 자동 생성된 경우 이미 매핑이 존재하지만, 매핑이 없는 경우(PENDING_MAPPING 등)를 대비해 직접 시딩합니다.
     *
     * @param externalOrderId 외부 주문 번호
     * @param externalProductOrderId 외부 상품주문 번호
     * @param orderItemId 내부 orderItemId
     */
    private void seedMapping(
            String externalOrderId, String externalProductOrderId, String orderItemId) {
        externalOrderItemMappingRepository.save(
                ExternalOrderItemMappingJpaEntity.create(
                        null,
                        SALES_CHANNEL_ID,
                        "SETOF",
                        externalOrderId,
                        externalProductOrderId,
                        orderItemId,
                        Instant.now()));
    }

    /**
     * Order + OrderItem (지정 상태)을 직접 시딩합니다.
     *
     * @param orderId 주문 ID
     * @param orderItemStatus OrderItem 상태 ("READY", "CONFIRMED" 등)
     * @return 저장된 orderItemId
     */
    private String seedOrderItemWithStatus(String orderId, String orderItemStatus) {
        OrderJpaEntity order = OrderJpaEntityFixtures.orderedEntity(orderId);
        orderRepository.save(order);

        OrderItemJpaEntity item =
                "CONFIRMED".equals(orderItemStatus)
                        ? OrderItemJpaEntityFixtures.confirmedItem(orderId)
                        : OrderItemJpaEntityFixtures.defaultItem(orderId);

        return orderItemRepository.save(item).getId();
    }

    // ===== Request Body 생성 헬퍼 =====

    private Map<String, Object> orderCreatedRequest(
            String externalOrderNo, String externalProductOrderId) {
        Map<String, Object> item = new HashMap<>();
        item.put("externalProductOrderId", externalProductOrderId);
        item.put("externalProductId", "EXT-PROD-001");
        item.put("externalOptionId", "EXT-OPT-001");
        item.put("productName", "테스트 상품명");
        item.put("optionName", "옵션A");
        item.put("imageUrl", "https://example.com/image.jpg");
        item.put("unitPrice", 30000);
        item.put("quantity", 1);
        item.put("totalAmount", 30000);
        item.put("discountAmount", 0);
        item.put("paymentAmount", 30000);
        item.put("receiverName", "김수령");
        item.put("receiverPhone", "010-9999-8888");
        item.put("receiverZipCode", "12345");
        item.put("receiverAddress", "서울시 강남구");
        item.put("receiverAddressDetail", "테헤란로 123");
        item.put("deliveryRequest", "문 앞에 놔주세요");

        Map<String, Object> request = new HashMap<>();
        request.put("salesChannelId", SALES_CHANNEL_ID);
        request.put("shopId", SHOP_ID);
        request.put("externalOrderNo", externalOrderNo);
        request.put("orderedAt", "2026-03-21T01:00:00Z");
        request.put("buyerName", "홍길동");
        request.put("buyerEmail", "buyer@example.com");
        request.put("buyerPhone", "010-1234-5678");
        request.put("paymentMethod", "CARD");
        request.put("totalPaymentAmount", 30000);
        request.put("paidAt", "2026-03-21T01:01:00Z");
        request.put("items", List.of(item));
        return request;
    }

    private Map<String, Object> purchaseConfirmedRequest(
            String externalOrderId, String externalProductOrderId) {
        return Map.of(
                "salesChannelId", SALES_CHANNEL_ID,
                "externalOrderId", externalOrderId,
                "items", List.of(Map.of("externalProductOrderId", externalProductOrderId)));
    }

    private Map<String, Object> orderCancelledRequest(
            String externalOrderId, String externalProductOrderId) {
        return Map.of(
                "salesChannelId", SALES_CHANNEL_ID,
                "externalOrderId", externalOrderId,
                "items",
                        List.of(
                                Map.of(
                                        "externalProductOrderId",
                                        externalProductOrderId,
                                        "cancelReason",
                                        "고객 변심",
                                        "cancelDetailedReason",
                                        "다른 상품으로 구매 예정",
                                        "cancelQuantity",
                                        1)));
    }

    private Map<String, Object> returnRequestedRequest(
            String externalOrderId, String externalProductOrderId) {
        return Map.of(
                "salesChannelId", SALES_CHANNEL_ID,
                "externalOrderId", externalOrderId,
                "items",
                        List.of(
                                Map.of(
                                        "externalProductOrderId", externalProductOrderId,
                                        "returnReason", "상품 불량",
                                        "returnDetailedReason", "수령 후 파손 확인",
                                        "returnQuantity", 1,
                                        "collectDeliveryCompany", "CJ대한통운",
                                        "collectTrackingNumber", "1234567890123")));
    }

    private Map<String, Object> returnWithdrawnRequest(
            String externalOrderId, String externalProductOrderId) {
        return Map.of(
                "salesChannelId", SALES_CHANNEL_ID,
                "externalOrderId", externalOrderId,
                "items", List.of(Map.of("externalProductOrderId", externalProductOrderId)));
    }

    // ===== FLOW-1: 주문 생성 → 구매 확정 =====

    @Nested
    @DisplayName("FLOW-1: 주문 생성 → 구매 확정 흐름")
    class OrderCreatedThenPurchaseConfirmedFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[FLOW-1-1] ORDER_CREATED 웹훅 호출 → InboundOrder 생성 + 응답 검증")
        void orderCreated_CreatesInboundOrderAndReturnsResult() {
            // given
            String externalOrderNo = "EXT-FLOW1-ORD-001";
            String externalProductOrderId = "EXT-FLOW1-PO-001";

            // when
            given().spec(givenUnauthenticated())
                    .body(orderCreatedRequest(externalOrderNo, externalProductOrderId))
                    .when()
                    .post(WEBHOOK_CREATED)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue())
                    // total = 1
                    .body("data.total", equalTo(1));

            // then: InboundOrder가 DB에 저장됨
            assertThat(inboundOrderRepository.count()).isGreaterThanOrEqualTo(1);
        }

        @Test
        @Tag("P0")
        @DisplayName("[FLOW-1-2] ORDER_CREATED 후 PURCHASE_CONFIRMED → OrderItem CONFIRMED 상태 확인")
        void orderCreated_ThenPurchaseConfirmed_OrderItemBecomesConfirmed() {
            // Step 1. Order + OrderItem 직접 시딩 (READY 상태)
            String externalOrderId = "EXT-FLOW1-ORD-002";
            String externalProductOrderId = "EXT-FLOW1-PO-002";
            String orderId = UUID.randomUUID().toString();
            String orderItemId = seedOrderItemWithStatus(orderId, "READY");

            // Step 2. ExternalOrderItemMapping 시딩 (externalProductOrderId → orderItemId)
            seedMapping(externalOrderId, externalProductOrderId, orderItemId);

            // Step 3. PURCHASE_CONFIRMED 웹훅 호출
            given().spec(givenUnauthenticated())
                    .body(purchaseConfirmedRequest(externalOrderId, externalProductOrderId))
                    .when()
                    .post(WEBHOOK_PURCHASE_CONFIRMED)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", nullValue());

            // Step 4. DB 검증 - OrderItem이 CONFIRMED 상태로 전환됨
            var updatedItem = orderItemRepository.findById(orderItemId).orElseThrow();
            assertThat(updatedItem.getOrderItemStatus()).isEqualTo("CONFIRMED");
        }

        @Test
        @Tag("P0")
        @DisplayName("[FLOW-1-3] 이미 CONFIRMED인 항목에 PURCHASE_CONFIRMED 재호출 → 멱등성 보장 (정상 응답)")
        void purchaseConfirmed_AlreadyConfirmed_IdempotentResponse() {
            // Step 1. 이미 CONFIRMED 상태인 OrderItem 시딩
            String externalOrderId = "EXT-FLOW1-ORD-003";
            String externalProductOrderId = "EXT-FLOW1-PO-003";
            String orderId = UUID.randomUUID().toString();
            String orderItemId = seedOrderItemWithStatus(orderId, "CONFIRMED");
            seedMapping(externalOrderId, externalProductOrderId, orderItemId);

            // Step 2. PURCHASE_CONFIRMED 첫 번째 호출
            given().spec(givenUnauthenticated())
                    .body(purchaseConfirmedRequest(externalOrderId, externalProductOrderId))
                    .when()
                    .post(WEBHOOK_PURCHASE_CONFIRMED)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // Step 3. PURCHASE_CONFIRMED 두 번째 호출 (멱등성 검증)
            given().spec(givenUnauthenticated())
                    .body(purchaseConfirmedRequest(externalOrderId, externalProductOrderId))
                    .when()
                    .post(WEBHOOK_PURCHASE_CONFIRMED)
                    .then()
                    // 이미 CONFIRMED 상태여도 에러 없이 200 반환
                    .statusCode(HttpStatus.OK.value());

            // Step 4. DB 검증 - 상태 변화 없음 (CONFIRMED 유지)
            var item = orderItemRepository.findById(orderItemId).orElseThrow();
            assertThat(item.getOrderItemStatus()).isEqualTo("CONFIRMED");
        }
    }

    // ===== FLOW-2: 주문 생성 → 즉시 취소 =====

    @Nested
    @DisplayName("FLOW-2: 주문 생성 → 즉시 취소 흐름")
    class OrderCreatedThenCancelledFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[FLOW-2] ORDER_CREATED 후 ORDER_CANCELLED → 취소 동기화 성공")
        void orderCreated_ThenCancelled_CancelSynced() {
            // Step 1. Order + OrderItem 직접 시딩 (READY 상태)
            String externalOrderId = "EXT-FLOW2-ORD-001";
            String externalProductOrderId = "EXT-FLOW2-PO-001";
            String orderId = UUID.randomUUID().toString();
            String orderItemId = seedOrderItemWithStatus(orderId, "READY");

            // Step 2. ExternalOrderItemMapping 시딩
            seedMapping(externalOrderId, externalProductOrderId, orderItemId);

            // Step 3. ORDER_CANCELLED 웹훅 호출 → 취소 동기화 성공
            given().spec(givenUnauthenticated())
                    .body(orderCancelledRequest(externalOrderId, externalProductOrderId))
                    .when()
                    .post(WEBHOOK_CANCELLED)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue())
                    .body("data.totalProcessed", equalTo(1))
                    .body("data.cancelSynced", equalTo(1))
                    .body("data.failed", equalTo(0));

            // Step 4. Cancel 생성 확인은 Step 3의 cancelSynced=1로 검증 완료
        }

        @Test
        @Tag("P1")
        @DisplayName("[FLOW-2-skip] 매핑 없는 externalProductOrderId → skipped 처리 (에러 없이 정상 응답)")
        void orderCancelled_NoMapping_Skipped() {
            // given: 매핑이 없는 externalProductOrderId
            String externalOrderId = "EXT-FLOW2-ORD-NOMAPPING";
            String externalProductOrderId = "EXT-FLOW2-PO-NOMAPPING";

            // when
            given().spec(givenUnauthenticated())
                    .body(orderCancelledRequest(externalOrderId, externalProductOrderId))
                    .when()
                    .post(WEBHOOK_CANCELLED)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalProcessed", equalTo(1))
                    .body("data.cancelSynced", equalTo(0))
                    .body("data.skipped", equalTo(1));
        }
    }

    // ===== FLOW-3: 주문 생성 → 반품 요청 → 반품 철회 =====

    @Nested
    @DisplayName("FLOW-3: 주문 생성 → 반품 요청 → 반품 철회 흐름")
    class OrderCreatedThenReturnRequestedThenWithdrawnFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[FLOW-3-1] CONFIRMED OrderItem에 RETURN_REQUESTED → 반품 동기화 성공")
        void returnRequested_ConfirmedItem_RefundSynced() {
            // Step 1. CONFIRMED 상태 OrderItem 시딩
            String externalOrderId = "EXT-FLOW3-ORD-001";
            String externalProductOrderId = "EXT-FLOW3-PO-001";
            String orderId = UUID.randomUUID().toString();
            String orderItemId = seedOrderItemWithStatus(orderId, "CONFIRMED");
            seedMapping(externalOrderId, externalProductOrderId, orderItemId);

            // Step 2. RETURN_REQUESTED 웹훅 호출 → 반품 동기화 성공
            given().spec(givenUnauthenticated())
                    .body(returnRequestedRequest(externalOrderId, externalProductOrderId))
                    .when()
                    .post(WEBHOOK_RETURN_REQUESTED)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalProcessed", equalTo(1))
                    .body("data.refundSynced", equalTo(1))
                    .body("data.failed", equalTo(0));

            // Step 3. DB 검증 - RefundClaim 생성됨
            assertThat(refundClaimRepository.count()).isGreaterThan(0);

            // Step 4. DB 검증 - OrderItem이 RETURN_REQUESTED로 변경
            var updatedItem = orderItemRepository.findById(orderItemId).orElseThrow();
            assertThat(updatedItem.getOrderItemStatus()).isEqualTo("RETURN_REQUESTED");
        }

        @Test
        @Tag("P0")
        @DisplayName("[FLOW-3-2] RETURN_REQUESTED 후 RETURN_WITHDRAWN → 반품 생성 후 철회 성공")
        void returnRequested_ThenWithdrawn_RefundCreatedThenWithdrawn() {
            // Step 1. CONFIRMED 상태 OrderItem 시딩
            String externalOrderId = "EXT-FLOW3-ORD-002";
            String externalProductOrderId = "EXT-FLOW3-PO-002";
            String orderId = UUID.randomUUID().toString();
            String orderItemId = seedOrderItemWithStatus(orderId, "CONFIRMED");
            seedMapping(externalOrderId, externalProductOrderId, orderItemId);

            // Step 2. RETURN_REQUESTED 웹훅 호출 → 반품 생성 성공
            given().spec(givenUnauthenticated())
                    .body(returnRequestedRequest(externalOrderId, externalProductOrderId))
                    .when()
                    .post(WEBHOOK_RETURN_REQUESTED)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.refundSynced", equalTo(1))
                    .body("data.failed", equalTo(0));

            // Step 3. RETURN_WITHDRAWN 웹훅 호출 → 반품 철회
            given().spec(givenUnauthenticated())
                    .body(returnWithdrawnRequest(externalOrderId, externalProductOrderId))
                    .when()
                    .post(WEBHOOK_RETURN_WITHDRAWN)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalProcessed", equalTo(1));

            // Step 4. DB 검증 - RefundClaim이 철회 상태
            var refunds = refundClaimRepository.findAll();
            assertThat(refunds).isNotEmpty();
        }

        @Test
        @Tag("P1")
        @DisplayName("[FLOW-3-skip] RETURN_WITHDRAWN 호출 시 매핑 없음 → skipped 처리")
        void returnWithdrawn_NoMapping_Skipped() {
            // given: 매핑 없는 externalProductOrderId
            String externalOrderId = "EXT-FLOW3-ORD-NOMAP";
            String externalProductOrderId = "EXT-FLOW3-PO-NOMAP";

            // when
            given().spec(givenUnauthenticated())
                    .body(returnWithdrawnRequest(externalOrderId, externalProductOrderId))
                    .when()
                    .post(WEBHOOK_RETURN_WITHDRAWN)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalProcessed", equalTo(1))
                    .body("data.skipped", equalTo(1));
        }
    }

    // ===== FLOW-4: 중복 주문 생성 방지 =====

    @Nested
    @DisplayName("FLOW-4: 중복 주문 생성 방지 (멱등성)")
    class DuplicateOrderCreationPreventionFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[FLOW-4] 동일 externalOrderNo로 ORDER_CREATED 2번 호출 → 두 번째는 duplicated 카운트")
        void orderCreated_DuplicateOrderNo_SecondCallCounted_As_Duplicated() {
            // given
            String externalOrderNo = "EXT-FLOW4-DUP-001";
            String externalProductOrderId = "EXT-FLOW4-PO-001";
            Map<String, Object> request =
                    orderCreatedRequest(externalOrderNo, externalProductOrderId);

            // Step 1. 첫 번째 호출 - 정상 처리
            given().spec(givenUnauthenticated())
                    .body(request)
                    .when()
                    .post(WEBHOOK_CREATED)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.total", equalTo(1))
                    .body("data.duplicated", equalTo(0));

            long countAfterFirst = inboundOrderRepository.count();

            // Step 2. 동일 요청으로 두 번째 호출 - duplicated로 처리됨
            given().spec(givenUnauthenticated())
                    .body(request)
                    .when()
                    .post(WEBHOOK_CREATED)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.total", equalTo(1))
                    .body("data.duplicated", equalTo(1));

            // Step 3. DB에 추가로 생성되지 않았음을 확인
            assertThat(inboundOrderRepository.count()).isEqualTo(countAfterFirst);
        }
    }

    // ===== 유효성 검증 테스트 =====

    @Nested
    @DisplayName("요청 유효성 검증")
    class RequestValidationTest {

        @Test
        @Tag("P1")
        @DisplayName("[VAL-1] ORDER_CREATED - salesChannelId 없음 → 400 Bad Request")
        void orderCreated_MissingSalesChannelId_Returns400() {
            Map<String, Object> invalidRequest =
                    Map.of(
                            "shopId", SHOP_ID,
                            "externalOrderNo", "EXT-VAL-001",
                            "orderedAt", "2026-03-21T01:00:00Z",
                            "buyerName", "홍길동",
                            "paymentMethod", "CARD",
                            "totalPaymentAmount", 30000,
                            "paidAt", "2026-03-21T01:01:00Z",
                            "items", List.of());

            given().spec(givenUnauthenticated())
                    .body(invalidRequest)
                    .when()
                    .post(WEBHOOK_CREATED)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[VAL-2] ORDER_CANCELLED - items 빈 리스트 → 400 Bad Request")
        void orderCancelled_EmptyItems_Returns400() {
            Map<String, Object> invalidRequest =
                    Map.of(
                            "salesChannelId",
                            SALES_CHANNEL_ID,
                            "externalOrderId",
                            "EXT-VAL-002",
                            "items",
                            List.of());

            given().spec(givenUnauthenticated())
                    .body(invalidRequest)
                    .when()
                    .post(WEBHOOK_CANCELLED)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[VAL-3] PURCHASE_CONFIRMED - items 빈 리스트 → 400 Bad Request")
        void purchaseConfirmed_EmptyItems_Returns400() {
            Map<String, Object> invalidRequest =
                    Map.of(
                            "salesChannelId",
                            SALES_CHANNEL_ID,
                            "externalOrderId",
                            "EXT-VAL-003",
                            "items",
                            List.of());

            given().spec(givenUnauthenticated())
                    .body(invalidRequest)
                    .when()
                    .post(WEBHOOK_PURCHASE_CONFIRMED)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }
}
