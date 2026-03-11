package com.ryuqq.marketplace.integration.order;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.notNullValue;

import com.ryuqq.marketplace.adapter.out.persistence.order.OrderItemJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.OrderJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.PaymentJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderHistoryJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.PaymentJpaRepository;
import com.ryuqq.marketplace.integration.E2ETestBase;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * Payment 리팩토링 E2E 통합 테스트.
 *
 * <p>검증 대상:
 *
 * <ul>
 *   <li>payments 테이블 id가 UUIDv7 형식의 String으로 저장됨
 *   <li>payments 테이블에 PAY-YYYYMMDD-XXXX 형식의 paymentNumber가 저장됨
 *   <li>상품주문 목록 조회 응답에 paymentId(String), paymentNumber 포함됨
 *   <li>상품주문 상세 조회 응답에 paymentId(String), paymentNumber 포함됨
 * </ul>
 *
 * <p>테스트 구조:
 *
 * <ul>
 *   <li>Scenario 1: DB 저장 상태 검증 (UUIDv7 id, paymentNumber 형식)
 *   <li>Scenario 2: GET /orders - 목록 조회 응답 검증
 *   <li>Scenario 3: GET /orders/{orderItemId} - 상세 조회 응답 검증
 *   <li>Scenario 4: 전체 플로우 시나리오
 * </ul>
 */
@Tag("e2e")
@Tag("order")
@Tag("payment")
@DisplayName("Payment 리팩토링 E2E 테스트")
class PaymentRefactoringE2ETest extends E2ETestBase {

    private static final String ORDERS_URL = "/orders";
    private static final String ORDER_DETAIL_URL = "/orders/{orderItemId}";

    // UUIDv7 패턴: 8-4-7xxx-xxxx-12 hex digits
    private static final String UUID_V7_PATTERN =
            "^[0-9a-f]{8}-[0-9a-f]{4}-7[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$";
    // PAY-YYYYMMDD-XXXX 패턴
    private static final String PAYMENT_NUMBER_PATTERN = "^PAY-\\d{8}-\\d{4}$";

    @Autowired private OrderJpaRepository orderRepository;
    @Autowired private OrderItemJpaRepository orderItemRepository;
    @Autowired private OrderHistoryJpaRepository orderHistoryRepository;
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

    // ===== 헬퍼 메서드 =====

    /**
     * 기본 주문 + 결제 + 주문상품 세트를 DB에 직접 저장합니다.
     *
     * @param orderId 주문 ID (UUIDv7 문자열)
     * @param paymentId 결제 ID (UUIDv7 문자열)
     * @param paymentNumber 결제 번호 (PAY-YYYYMMDD-XXXX 형식)
     * @return 저장된 OrderItemId
     */
    private Long saveOrderWithPayment(String orderId, String paymentId, String paymentNumber) {
        Instant now = Instant.now();

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
                        now.minusSeconds(60),
                        null,
                        now.minusSeconds(120),
                        now));

        var savedItem = orderItemRepository.save(OrderItemJpaEntityFixtures.defaultItem(orderId));
        return savedItem.getId();
    }

    // ========================================================================
    // Scenario 1: DB 저장 상태 검증
    // ========================================================================

    @Nested
    @DisplayName("Scenario 1: DB 저장 상태 - UUIDv7 id 및 paymentNumber 형식 검증")
    class DbStorageVerificationTest {

        @Test
        @Tag("P0")
        @DisplayName("[DB-01] payments.id가 UUIDv7 형식(String)으로 저장된다")
        void payment_id_is_stored_as_uuid_v7_string() {
            // given: UUIDv7 형식 ID를 직접 지정하여 저장
            String orderId = "01944b2a-1234-7fff-8888-abcdef012301";
            String paymentId = "01944b2a-bbbb-7fff-9999-000000000001";
            String paymentNumber = "PAY-20260310-0001";

            saveOrderWithPayment(orderId, paymentId, paymentNumber);

            // when: DB에서 직접 조회
            Optional<PaymentJpaEntity> found = paymentRepository.findById(paymentId);

            // then
            assertThat(found).isPresent();
            assertThat(found.get().getId()).isEqualTo(paymentId);
            // id가 String 타입으로 저장되어 있음을 assertThat으로 확인
            assertThat(found.get().getId()).isInstanceOf(String.class);
            assertThat(found.get().getId())
                    .matches("[0-9a-f]{8}-[0-9a-f]{4}-7[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}");
        }

        @Test
        @Tag("P0")
        @DisplayName("[DB-02] payments.payment_number가 PAY-YYYYMMDD-XXXX 형식으로 저장된다")
        void payment_number_is_stored_with_correct_format() {
            // given
            String orderId = "01944b2a-1234-7fff-8888-abcdef012302";
            String paymentId = "01944b2a-bbbb-7fff-9999-000000000002";
            String paymentNumber = "PAY-20260310-0002";

            saveOrderWithPayment(orderId, paymentId, paymentNumber);

            // when: DB에서 직접 조회
            Optional<PaymentJpaEntity> found = paymentRepository.findById(paymentId);

            // then
            assertThat(found).isPresent();
            assertThat(found.get().getPaymentNumber()).isEqualTo(paymentNumber);
            assertThat(found.get().getPaymentNumber()).matches("PAY-\\d{8}-\\d{4}");
        }

        @Test
        @Tag("P0")
        @DisplayName("[DB-03] 서로 다른 주문에 대해 payments.id가 고유한 UUIDv7로 저장된다")
        void multiple_payments_have_unique_uuid_v7_ids() {
            // given: 2개의 주문 + 결제 저장
            String orderId1 = "01944b2a-1234-7fff-8888-abcdef012303";
            String orderId2 = "01944b2a-1234-7fff-8888-abcdef012304";
            String paymentId1 = "01944b2a-bbbb-7fff-9999-000000000003";
            String paymentId2 = "01944b2a-bbbb-7fff-9999-000000000004";

            saveOrderWithPayment(orderId1, paymentId1, "PAY-20260310-0003");
            saveOrderWithPayment(orderId2, paymentId2, "PAY-20260310-0004");

            // when: 전체 결제 목록 조회
            List<PaymentJpaEntity> payments = paymentRepository.findAll();

            // then
            assertThat(payments).hasSize(2);
            List<String> ids = payments.stream().map(PaymentJpaEntity::getId).toList();
            assertThat(ids).doesNotHaveDuplicates();
            ids.forEach(id -> assertThat(id).isInstanceOf(String.class));
        }
    }

    // ========================================================================
    // Scenario 2: GET /orders - 목록 조회 응답 검증
    // ========================================================================

    @Nested
    @DisplayName("Scenario 2: GET /orders - 상품주문 목록 조회 응답 검증")
    class ProductOrderListQueryTest {

        @Test
        @Tag("P0")
        @DisplayName("[LIST-01] 목록 조회 응답에 payment.paymentId가 String 타입으로 포함된다")
        void list_response_contains_payment_id_as_string() {
            // given
            String orderId = "01944b2a-1234-7fff-8888-abcdef012311";
            String paymentId = "01944b2a-bbbb-7fff-9999-000000000011";
            saveOrderWithPayment(orderId, paymentId, "PAY-20260310-0011");

            // when & then
            given().spec(givenWithPermission("order:read"))
                    .when()
                    .get(ORDERS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content[0].payment.paymentId", notNullValue())
                    .body("data.content[0].payment.paymentId", equalTo(paymentId));
        }

        @Test
        @Tag("P0")
        @DisplayName("[LIST-02] 목록 조회 응답에 payment.paymentNumber가 포함된다")
        void list_response_contains_payment_number() {
            // given
            String orderId = "01944b2a-1234-7fff-8888-abcdef012312";
            String paymentId = "01944b2a-bbbb-7fff-9999-000000000012";
            String paymentNumber = "PAY-20260310-0012";
            saveOrderWithPayment(orderId, paymentId, paymentNumber);

            // when & then
            given().spec(givenWithPermission("order:read"))
                    .when()
                    .get(ORDERS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content[0].payment.paymentNumber", notNullValue())
                    .body("data.content[0].payment.paymentNumber", equalTo(paymentNumber));
        }

        @Test
        @Tag("P0")
        @DisplayName("[LIST-03] 목록 조회 응답에서 paymentId가 UUIDv7 형식 패턴을 만족한다")
        void list_response_payment_id_matches_uuid_v7_pattern() {
            // given
            String orderId = "01944b2a-1234-7fff-8888-abcdef012313";
            String paymentId = "01944b2a-bbbb-7fff-9999-000000000013";
            saveOrderWithPayment(orderId, paymentId, "PAY-20260310-0013");

            // when & then
            given().spec(givenWithPermission("order:read"))
                    .when()
                    .get(ORDERS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content[0].payment.paymentId", matchesPattern(UUID_V7_PATTERN));
        }

        @Test
        @Tag("P0")
        @DisplayName("[LIST-04] 목록 조회 응답에서 paymentNumber가 PAY-YYYYMMDD-XXXX 형식 패턴을 만족한다")
        void list_response_payment_number_matches_pay_format() {
            // given
            String orderId = "01944b2a-1234-7fff-8888-abcdef012314";
            String paymentId = "01944b2a-bbbb-7fff-9999-000000000014";
            String paymentNumber = "PAY-20260310-0014";
            saveOrderWithPayment(orderId, paymentId, paymentNumber);

            // when & then
            given().spec(givenWithPermission("order:read"))
                    .when()
                    .get(ORDERS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(
                            "data.content[0].payment.paymentNumber",
                            matchesPattern(PAYMENT_NUMBER_PATTERN));
        }

        @Test
        @Tag("P0")
        @DisplayName("[LIST-05] 데이터가 없을 때 목록 조회는 빈 결과를 반환한다")
        void list_returns_empty_when_no_data() {
            // when & then
            given().spec(givenWithPermission("order:read"))
                    .when()
                    .get(ORDERS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue());
        }

        @Test
        @Tag("P1")
        @DisplayName("[LIST-06] 복수 주문 목록 조회 시 각 항목에 paymentId, paymentNumber가 포함된다")
        void list_multiple_orders_each_has_payment_id_and_number() {
            // given: 2건의 주문 저장
            saveOrderWithPayment(
                    "01944b2a-1234-7fff-8888-abcdef012321",
                    "01944b2a-bbbb-7fff-9999-000000000021",
                    "PAY-20260310-0021");
            saveOrderWithPayment(
                    "01944b2a-1234-7fff-8888-abcdef012322",
                    "01944b2a-bbbb-7fff-9999-000000000022",
                    "PAY-20260310-0022");

            // when & then
            given().spec(givenWithPermission("order:read"))
                    .when()
                    .get(ORDERS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content[0].payment.paymentId", notNullValue())
                    .body("data.content[0].payment.paymentNumber", notNullValue())
                    .body("data.content[1].payment.paymentId", notNullValue())
                    .body("data.content[1].payment.paymentNumber", notNullValue());
        }

        @Test
        @Tag("P0")
        @DisplayName("[LIST-AUTH-01] 권한 없는 요청 시 403을 반환한다")
        void list_without_permission_returns_403() {
            // when & then
            given().spec(givenAuthenticatedUser())
                    .when()
                    .get(ORDERS_URL)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[LIST-AUTH-02] 비인증 요청 시 401을 반환한다")
        void list_unauthenticated_returns_401() {
            // when & then
            given().spec(givenUnauthenticated())
                    .when()
                    .get(ORDERS_URL)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }
    }

    // ========================================================================
    // Scenario 3: GET /orders/{orderItemId} - 상세 조회 응답 검증
    // ========================================================================

    @Nested
    @DisplayName("Scenario 3: GET /orders/{orderItemId} - 상품주문 상세 조회 응답 검증")
    class ProductOrderDetailQueryTest {

        @Test
        @Tag("P0")
        @DisplayName("[DETAIL-01] 상세 조회 응답에 payment.paymentId가 String 타입으로 포함된다")
        void detail_response_contains_payment_id_as_string() {
            // given
            String orderId = "01944b2a-1234-7fff-8888-abcdef012331";
            String paymentId = "01944b2a-bbbb-7fff-9999-000000000031";
            Long orderItemId = saveOrderWithPayment(orderId, paymentId, "PAY-20260310-0031");

            // when & then
            given().spec(givenWithPermission("order:read"))
                    .when()
                    .get(ORDER_DETAIL_URL, orderItemId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.payment.paymentId", notNullValue())
                    .body("data.payment.paymentId", equalTo(paymentId));
        }

        @Test
        @Tag("P0")
        @DisplayName("[DETAIL-02] 상세 조회 응답에 payment.paymentNumber가 포함된다")
        void detail_response_contains_payment_number() {
            // given
            String orderId = "01944b2a-1234-7fff-8888-abcdef012332";
            String paymentId = "01944b2a-bbbb-7fff-9999-000000000032";
            String paymentNumber = "PAY-20260310-0032";
            Long orderItemId = saveOrderWithPayment(orderId, paymentId, paymentNumber);

            // when & then
            given().spec(givenWithPermission("order:read"))
                    .when()
                    .get(ORDER_DETAIL_URL, orderItemId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.payment.paymentNumber", notNullValue())
                    .body("data.payment.paymentNumber", equalTo(paymentNumber));
        }

        @Test
        @Tag("P0")
        @DisplayName("[DETAIL-03] 상세 조회 응답에서 paymentId가 UUIDv7 형식 패턴을 만족한다")
        void detail_response_payment_id_matches_uuid_v7_pattern() {
            // given
            String orderId = "01944b2a-1234-7fff-8888-abcdef012333";
            String paymentId = "01944b2a-bbbb-7fff-9999-000000000033";
            Long orderItemId = saveOrderWithPayment(orderId, paymentId, "PAY-20260310-0033");

            // when & then
            given().spec(givenWithPermission("order:read"))
                    .when()
                    .get(ORDER_DETAIL_URL, orderItemId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.payment.paymentId", matchesPattern(UUID_V7_PATTERN));
        }

        @Test
        @Tag("P0")
        @DisplayName("[DETAIL-04] 상세 조회 응답에서 paymentNumber가 PAY-YYYYMMDD-XXXX 형식 패턴을 만족한다")
        void detail_response_payment_number_matches_pay_format() {
            // given
            String orderId = "01944b2a-1234-7fff-8888-abcdef012334";
            String paymentId = "01944b2a-bbbb-7fff-9999-000000000034";
            String paymentNumber = "PAY-20260310-0034";
            Long orderItemId = saveOrderWithPayment(orderId, paymentId, paymentNumber);

            // when & then
            given().spec(givenWithPermission("order:read"))
                    .when()
                    .get(ORDER_DETAIL_URL, orderItemId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.payment.paymentNumber", matchesPattern(PAYMENT_NUMBER_PATTERN));
        }

        @Test
        @Tag("P0")
        @DisplayName("[DETAIL-05] 존재하지 않는 orderItemId 조회 시 404를 반환한다")
        void detail_not_found_returns_404() {
            // when & then
            given().spec(givenWithPermission("order:read"))
                    .when()
                    .get(ORDER_DETAIL_URL, 99999L)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[DETAIL-AUTH-01] 권한 없는 요청 시 403을 반환한다")
        void detail_without_permission_returns_403() {
            // given
            String orderId = "01944b2a-1234-7fff-8888-abcdef012341";
            String paymentId = "01944b2a-bbbb-7fff-9999-000000000041";
            Long orderItemId = saveOrderWithPayment(orderId, paymentId, "PAY-20260310-0041");

            // when & then
            given().spec(givenAuthenticatedUser())
                    .when()
                    .get(ORDER_DETAIL_URL, orderItemId)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[DETAIL-AUTH-02] 비인증 요청 시 401을 반환한다")
        void detail_unauthenticated_returns_401() {
            // when & then
            given().spec(givenUnauthenticated())
                    .when()
                    .get(ORDER_DETAIL_URL, 1L)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }
    }

    // ========================================================================
    // Scenario 4: 전체 플로우 시나리오
    // ========================================================================

    @Nested
    @DisplayName("Scenario 4: 전체 플로우 - 주문 생성 후 목록/상세 조회")
    class FullFlowTest {

        @Test
        @Tag("P0")
        @DisplayName(
                "[FLOW-01] 주문 생성 후 목록 조회 → 상세 조회 플로우에서 paymentId(String), paymentNumber 일관성 검증")
        void full_flow_payment_id_and_number_consistent_in_list_and_detail() {
            // Step 1: 주문 + 결제 데이터 준비
            String orderId = "01944b2a-1234-7fff-8888-abcdef012351";
            String paymentId = "01944b2a-bbbb-7fff-9999-000000000051";
            String paymentNumber = "PAY-20260310-0051";
            Long orderItemId = saveOrderWithPayment(orderId, paymentId, paymentNumber);

            // Step 2: DB 저장 상태 검증 - UUIDv7 id, paymentNumber 저장 확인
            Optional<PaymentJpaEntity> savedPayment = paymentRepository.findById(paymentId);
            assertThat(savedPayment).isPresent();
            assertThat(savedPayment.get().getId()).isEqualTo(paymentId);
            assertThat(savedPayment.get().getId()).isInstanceOf(String.class);
            assertThat(savedPayment.get().getPaymentNumber()).isEqualTo(paymentNumber);
            assertThat(savedPayment.get().getPaymentNumber()).matches("PAY-\\d{8}-\\d{4}");

            // Step 3: 목록 조회 - paymentId(String), paymentNumber 응답 검증
            given().spec(givenWithPermission("order:read"))
                    .when()
                    .get(ORDERS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content[0].payment.paymentId", equalTo(paymentId))
                    .body("data.content[0].payment.paymentNumber", equalTo(paymentNumber));

            // Step 4: 상세 조회 - paymentId(String), paymentNumber 응답 검증
            given().spec(givenWithPermission("order:read"))
                    .when()
                    .get(ORDER_DETAIL_URL, orderItemId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.payment.paymentId", equalTo(paymentId))
                    .body("data.payment.paymentNumber", equalTo(paymentNumber));
        }

        @Test
        @Tag("P0")
        @DisplayName("[FLOW-02] SUPER_ADMIN 권한으로 목록 조회 → 상세 조회 플로우 수행 가능")
        void full_flow_super_admin_can_query_orders_with_payment_info() {
            // Step 1: 데이터 준비
            String orderId = "01944b2a-1234-7fff-8888-abcdef012352";
            String paymentId = "01944b2a-bbbb-7fff-9999-000000000052";
            String paymentNumber = "PAY-20260310-0052";
            Long orderItemId = saveOrderWithPayment(orderId, paymentId, paymentNumber);

            // Step 2: SUPER_ADMIN으로 목록 조회
            given().spec(givenSuperAdmin())
                    .when()
                    .get(ORDERS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content[0].payment.paymentId", equalTo(paymentId))
                    .body("data.content[0].payment.paymentNumber", equalTo(paymentNumber));

            // Step 3: SUPER_ADMIN으로 상세 조회
            given().spec(givenSuperAdmin())
                    .when()
                    .get(ORDER_DETAIL_URL, orderItemId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.payment.paymentId", equalTo(paymentId))
                    .body("data.payment.paymentNumber", equalTo(paymentNumber));
        }
    }
}
