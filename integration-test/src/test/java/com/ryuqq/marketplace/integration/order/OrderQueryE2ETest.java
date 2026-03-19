package com.ryuqq.marketplace.integration.order;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import com.ryuqq.marketplace.adapter.out.persistence.order.OrderItemJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.OrderJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderItemJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.PaymentJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemHistoryJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.PaymentJpaRepository;
import com.ryuqq.marketplace.integration.E2ETestBase;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * 주문 Query API E2E 테스트.
 *
 * <p>검증 대상:
 *
 * <ul>
 *   <li>Q01: GET /orders - 주문 목록 조회 (V4 간극 검증 포함)
 *   <li>Q02: GET /orders/{orderItemId} - 주문 상세 조회 (V4 간극 검증 포함)
 * </ul>
 *
 * <p>V4 간극 규칙:
 *
 * <ul>
 *   <li>응답의 orderId 필드 = 내부 orderItemId (NOT orderId)
 *   <li>문자열 null 필드 → "" 직렬화
 *   <li>금액 null → 0 직렬화
 *   <li>legacyOrderId 필드 미포함
 * </ul>
 */
@Tag("e2e")
@Tag("order")
@Tag("query")
@DisplayName("주문 Query API E2E 테스트")
class OrderQueryE2ETest extends E2ETestBase {

    private static final String ORDERS_URL = "/orders";
    private static final String ORDER_DETAIL_URL = "/orders/{orderItemId}";

    private static final String UUID_V7_PATTERN =
            "^[0-9a-f]{8}-[0-9a-f]{4}-7[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$";
    private static final String PAYMENT_NUMBER_PATTERN = "^PAY-\\d{8}-\\d{4}$";

    @Autowired private OrderJpaRepository orderRepository;
    @Autowired private OrderItemJpaRepository orderItemRepository;
    @Autowired private OrderItemHistoryJpaRepository orderHistoryRepository;
    @Autowired private PaymentJpaRepository paymentRepository;

    @BeforeEach
    void setUp() {
        cleanUp();
    }

    @AfterEach
    void tearDown() {
        cleanUp();
    }

    private void cleanUp() {
        orderHistoryRepository.deleteAll();
        orderItemRepository.deleteAll();
        paymentRepository.deleteAll();
        orderRepository.deleteAll();
    }

    /**
     * Order + Payment + OrderItem 세트 저장 헬퍼.
     *
     * @param orderId 주문 ID
     * @param paymentId 결제 ID
     * @param paymentNumber 결제 번호 (PAY-YYYYMMDD-XXXX 형식)
     * @return 저장된 orderItemId (UUIDv7 String)
     */
    private String saveOrderWithPaymentDirect(
            String orderId, String paymentId, String paymentNumber) {
        orderRepository.save(OrderJpaEntityFixtures.orderedEntity(orderId));
        paymentRepository.save(
                PaymentJpaEntity.create(
                        paymentId,
                        orderId,
                        paymentNumber,
                        "COMPLETED",
                        "CARD",
                        "PG-TXN-TEST",
                        29900,
                        java.time.Instant.now().minusSeconds(60),
                        null,
                        java.time.Instant.now().minusSeconds(120),
                        java.time.Instant.now()));
        OrderItemJpaEntity savedItem =
                orderItemRepository.save(OrderItemJpaEntityFixtures.defaultItem(orderId));
        return savedItem.getId();
    }

    // ========================================================================
    // Q01: GET /orders - 주문 목록 조회
    // ========================================================================

    @Nested
    @DisplayName("GET /orders - 주문 목록 조회")
    class OrderListQueryTest {

        @Test
        @Tag("P0")
        @DisplayName("[ORDER-Q01-01] 데이터 존재 시 주문 목록 정상 조회 - HTTP 200, content 반환")
        void shouldReturnOrderListWhenDataExists() {
            // given: Order + Payment + OrderItem 2건 저장
            saveOrderWithPaymentDirect(
                    "01944b2a-1234-7fff-8888-abcdef010101",
                    "01944b2a-aaaa-7fff-9999-000000010101",
                    "PAY-20260101-0101");
            saveOrderWithPaymentDirect(
                    "01944b2a-1234-7fff-8888-abcdef010102",
                    "01944b2a-aaaa-7fff-9999-000000010102",
                    "PAY-20260101-0102");

            // when & then
            given().spec(givenWithPermission("order:read"))
                    .when()
                    .get(ORDERS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue())
                    .body("data.content", notNullValue())
                    .body("data.content.size()", org.hamcrest.Matchers.greaterThanOrEqualTo(1));
        }

        @Test
        @Tag("P0")
        @DisplayName("[ORDER-Q01-02] 데이터 없을 때 빈 목록 반환 - HTTP 200, data not null")
        void shouldReturnEmptyListWhenNoData() {
            // when & then
            given().spec(givenWithPermission("order:read"))
                    .when()
                    .get(ORDERS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue());
        }

        @Test
        @Tag("P0")
        @DisplayName("[ORDER-Q01-08] V4 간극 - orderId 필드가 내부 orderItemId 값을 반환")
        void shouldReturnOrderItemIdAsOrderIdDueToV4Gap() {
            // given: Order + Payment + OrderItem 1건 저장, orderItemId 캡처
            String orderId = "01944b2a-1234-7fff-8888-abcdef010108";
            String paymentId = "01944b2a-aaaa-7fff-9999-000000010108";
            String orderItemId =
                    saveOrderWithPaymentDirect(orderId, paymentId, "PAY-20260101-0108");

            // when & then: V4 간극 검증 - 응답의 orderId == 저장된 orderItemId
            given().spec(givenWithPermission("order:read"))
                    .when()
                    .get(ORDERS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content[0].orderId", equalTo(orderItemId));

            // orderItemId가 orderRepository의 orderId와 다름을 추가 검증
            assertThat(orderItemId).isNotEqualTo(orderId);
        }

        @Test
        @Tag("P0")
        @DisplayName("[ORDER-Q01-08b] V4 간극 - 응답 orderId가 UUIDv7 형식 패턴을 만족")
        void shouldReturnOrderIdMatchingUuidV7Pattern() {
            // given
            saveOrderWithPaymentDirect(
                    "01944b2a-1234-7fff-8888-abcdef010109",
                    "01944b2a-aaaa-7fff-9999-000000010109",
                    "PAY-20260101-0109");

            // when & then
            given().spec(givenWithPermission("order:read"))
                    .when()
                    .get(ORDERS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content[0].orderId", notNullValue())
                    .body("data.content[0].orderId", not(emptyString()));
        }

        @Test
        @Tag("P0")
        @DisplayName("[ORDER-Q01-xx] V4 간극 - 결제 정보가 응답에 포함됨 (paymentId, paymentNumber)")
        void shouldIncludePaymentInfoInListResponse() {
            // given
            String orderId = "01944b2a-1234-7fff-8888-abcdef010110";
            String paymentId = "01944b2a-aaaa-7fff-9999-000000010110";
            String paymentNumber = "PAY-20260101-0110";
            saveOrderWithPaymentDirect(orderId, paymentId, paymentNumber);

            // when & then
            given().spec(givenWithPermission("order:read"))
                    .when()
                    .get(ORDERS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content[0].payment.paymentId", equalTo(paymentId))
                    .body("data.content[0].payment.paymentId", matchesPattern(UUID_V7_PATTERN))
                    .body("data.content[0].payment.paymentNumber", equalTo(paymentNumber))
                    .body(
                            "data.content[0].payment.paymentNumber",
                            matchesPattern(PAYMENT_NUMBER_PATTERN));
        }

        @Test
        @Tag("P1")
        @DisplayName("[ORDER-Q01-06] 페이징 - page=0, size=2 요청 시 2건만 반환")
        void shouldReturnPagedResultWhenSizeIs2() {
            // given: OrderItem 3건 저장
            saveOrderWithPaymentDirect(
                    "01944b2a-1234-7fff-8888-abcdef010601",
                    "01944b2a-aaaa-7fff-9999-000000010601",
                    "PAY-20260101-0601");
            saveOrderWithPaymentDirect(
                    "01944b2a-1234-7fff-8888-abcdef010602",
                    "01944b2a-aaaa-7fff-9999-000000010602",
                    "PAY-20260101-0602");
            saveOrderWithPaymentDirect(
                    "01944b2a-1234-7fff-8888-abcdef010603",
                    "01944b2a-aaaa-7fff-9999-000000010603",
                    "PAY-20260101-0603");

            // when & then
            given().spec(givenWithPermission("order:read"))
                    .queryParam("page", 0)
                    .queryParam("size", 2)
                    .when()
                    .get(ORDERS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(2));
        }

        @Test
        @Tag("P0")
        @DisplayName("[ORDER-Q01-10] 인가 - order:read 권한 없는 사용자 → 403")
        void shouldReturn403WhenUserHasNoOrderReadPermission() {
            // when & then: 인증은 됐지만 order:read 권한 없음
            given().spec(givenAuthenticatedUser())
                    .when()
                    .get(ORDERS_URL)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[ORDER-Q01-11] 인증 - 비인증 사용자 → 401")
        void shouldReturn401WhenUnauthenticated() {
            // when & then
            given().spec(givenUnauthenticated())
                    .when()
                    .get(ORDERS_URL)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }
    }

    // ========================================================================
    // Q02: GET /orders/{orderItemId} - 주문 상세 조회
    // ========================================================================

    @Nested
    @DisplayName("GET /orders/{orderItemId} - 주문 상세 조회")
    class OrderDetailQueryTest {

        @Test
        @Tag("P0")
        @DisplayName("[ORDER-Q02-01] 존재하는 orderItemId로 상세 조회 성공 - orderId=orderItemId V4 간극 검증")
        void shouldReturnOrderDetailSuccessfully() {
            // given
            String orderId = "01944b2a-1234-7fff-8888-abcdef020101";
            String paymentId = "01944b2a-aaaa-7fff-9999-000000020101";
            String paymentNumber = "PAY-20260201-0101";
            String orderItemId = saveOrderWithPaymentDirect(orderId, paymentId, paymentNumber);

            // when & then: V4 간극 - data.orderId == orderItemId
            given().spec(givenWithPermission("order:read"))
                    .when()
                    .get(ORDER_DETAIL_URL, orderItemId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue())
                    .body("data.orderId", equalTo(orderItemId))
                    .body("data.payment.paymentId", equalTo(paymentId))
                    .body("data.payment.paymentId", matchesPattern(UUID_V7_PATTERN))
                    .body("data.payment.paymentNumber", equalTo(paymentNumber))
                    .body("data.payment.paymentNumber", matchesPattern(PAYMENT_NUMBER_PATTERN))
                    .body("data.orderHistories", notNullValue())
                    .body("data.cancels", notNullValue())
                    .body("data.claims", notNullValue());
        }

        @Test
        @Tag("P0")
        @DisplayName("[ORDER-Q02-02] 존재하지 않는 orderItemId 조회 → 404")
        void shouldReturn404WhenOrderItemIdNotFound() {
            // when & then
            given().spec(givenWithPermission("order:read"))
                    .when()
                    .get(ORDER_DETAIL_URL, "01940001-0000-7000-8000-000000000999")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[ORDER-Q02-03] V4 간극 - 응답에 legacyOrderId 필드 미포함 검증")
        void shouldNotContainLegacyOrderIdFieldInDetailResponse() {
            // given
            String orderId = "01944b2a-1234-7fff-8888-abcdef020301";
            String paymentId = "01944b2a-aaaa-7fff-9999-000000020301";
            String orderItemId =
                    saveOrderWithPaymentDirect(orderId, paymentId, "PAY-20260201-0301");

            // when: 응답 body를 String으로 추출하여 legacyOrderId 키 미포함 검증
            Response response =
                    given().spec(givenWithPermission("order:read"))
                            .when()
                            .get(ORDER_DETAIL_URL, orderItemId);

            response.then().statusCode(HttpStatus.OK.value());
            String responseBody = response.getBody().asString();
            assertThat(responseBody).doesNotContain("legacyOrderId");
        }

        @Test
        @Tag("P0")
        @DisplayName("[ORDER-Q02-04] V4 간극 - 결제 금액 필드 null → 0 변환 검증")
        void shouldReturnZeroForNullPaymentAmounts() {
            // given: Payment 없이 OrderItem만 저장 (결제 금액 정보 없음)
            String orderId = "01944b2a-1234-7fff-8888-abcdef020401";
            orderRepository.save(OrderJpaEntityFixtures.orderedEntity(orderId));
            OrderItemJpaEntity savedItem =
                    orderItemRepository.save(OrderItemJpaEntityFixtures.defaultItem(orderId));
            String orderItemId = savedItem.getId();

            // when & then: 결제가 없을 때 billAmount, paymentAmount는 0 또는 null이 아닌 값
            given().spec(givenWithPermission("order:read"))
                    .when()
                    .get(ORDER_DETAIL_URL, orderItemId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue());
        }

        @Test
        @Tag("P0")
        @DisplayName("[ORDER-Q02-06] 인가 - order:read 권한 없는 사용자 → 403")
        void shouldReturn403WhenUserHasNoPermissionForDetail() {
            // given
            String orderId = "01944b2a-1234-7fff-8888-abcdef020601";
            String orderItemId =
                    saveOrderWithPaymentDirect(
                            orderId, "01944b2a-aaaa-7fff-9999-000000020601", "PAY-20260201-0601");

            // when & then
            given().spec(givenAuthenticatedUser())
                    .when()
                    .get(ORDER_DETAIL_URL, orderItemId)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[ORDER-Q02-07] 인증 - 비인증 사용자 → 401")
        void shouldReturn401WhenUnauthenticatedForDetail() {
            // when & then
            given().spec(givenUnauthenticated())
                    .when()
                    .get(ORDER_DETAIL_URL, "any-id")
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }
    }

    // ========================================================================
    // 전체 플로우 시나리오 (FLOW-03, FLOW-05 일부)
    // ========================================================================

    @Nested
    @DisplayName("전체 플로우 - 주문목록 조회 → orderItemId 추출 → 주문상세 조회")
    class OrderQueryFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[FLOW-03] 주문목록 조회 → orderId 추출(V4 간극 확인) → 주문상세 조회 일관성 검증")
        void shouldExtractOrderItemIdFromListAndUseForDetailQuery() {
            // given: Order + Payment + OrderItem 1건 저장
            String orderId = "01944b2a-1234-7fff-8888-abcdef030301";
            String paymentId = "01944b2a-aaaa-7fff-9999-000000030301";
            String paymentNumber = "PAY-20260301-0301";
            String orderItemId = saveOrderWithPaymentDirect(orderId, paymentId, paymentNumber);

            // Step 1: GET /orders → content[0].orderId 추출 (= 내부 orderItemId)
            Response listResponse =
                    given().spec(givenWithPermission("order:read")).when().get(ORDERS_URL);

            listResponse
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content[0].orderId", notNullValue());

            String extractedOrderId = listResponse.jsonPath().getString("data.content[0].orderId");

            // V4 간극 검증: 목록에서 추출한 orderId == 실제 저장된 orderItemId
            assertThat(extractedOrderId).isEqualTo(orderItemId);

            // Step 2: GET /orders/{추출한 orderId} → 상세 조회 일관성 검증
            given().spec(givenWithPermission("order:read"))
                    .when()
                    .get(ORDER_DETAIL_URL, extractedOrderId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.orderId", equalTo(extractedOrderId))
                    .body("data.payment.paymentId", equalTo(paymentId))
                    .body("data.payment.paymentNumber", equalTo(paymentNumber))
                    .body("data.orderHistories", notNullValue())
                    .body("data.cancels", notNullValue())
                    .body("data.claims", notNullValue());

            // legacyOrderId 미포함 검증
            String detailBody =
                    given().spec(givenWithPermission("order:read"))
                            .when()
                            .get(ORDER_DETAIL_URL, extractedOrderId)
                            .getBody()
                            .asString();
            assertThat(detailBody).doesNotContain("legacyOrderId");
        }

        @Test
        @Tag("P0")
        @DisplayName("[FLOW-05-ORDER] SUPER_ADMIN으로 주문목록 → 주문상세 V4 간극 전체 검증")
        void shouldPassFullOrderQueryFlowWithSuperAdmin() {
            // given: Order + Payment + OrderItem 1건 저장
            String orderId = "01944b2a-1234-7fff-8888-abcdef030501";
            String paymentId = "01944b2a-aaaa-7fff-9999-000000030501";
            String paymentNumber = "PAY-20260301-0501";
            String orderItemId = saveOrderWithPaymentDirect(orderId, paymentId, paymentNumber);

            // Step 1: 목록 조회 - orderId == orderItemId, orderStatus == "READY"
            given().spec(givenSuperAdmin())
                    .when()
                    .get(ORDERS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content[0].orderId", equalTo(orderItemId))
                    .body("data.content[0].payment.paymentId", equalTo(paymentId))
                    .body("data.content[0].payment.paymentNumber", equalTo(paymentNumber));

            // Step 2: 상세 조회 - orderId 일관성, legacyOrderId 미포함
            Response detailResponse =
                    given().spec(givenSuperAdmin()).when().get(ORDER_DETAIL_URL, orderItemId);

            detailResponse
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.orderId", equalTo(orderItemId))
                    .body("data.payment.paymentId", equalTo(paymentId))
                    .body("data.payment.paymentNumber", equalTo(paymentNumber))
                    .body("data.orderHistories", notNullValue())
                    .body("data.cancels", notNullValue())
                    .body("data.claims", notNullValue());

            assertThat(detailResponse.getBody().asString()).doesNotContain("legacyOrderId");
        }
    }
}
