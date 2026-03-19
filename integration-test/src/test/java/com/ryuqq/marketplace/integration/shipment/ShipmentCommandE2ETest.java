package com.ryuqq.marketplace.integration.shipment;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.ryuqq.marketplace.adapter.out.persistence.order.OrderItemJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.OrderJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderItemJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemHistoryJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.shipment.repository.ShipmentJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.shipmentoutbox.repository.ShipmentOutboxJpaRepository;
import com.ryuqq.marketplace.integration.E2ETestBase;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * 배송 Command API E2E 테스트.
 *
 * <p>테스트 대상:
 *
 * <ul>
 *   <li>C01: POST /shipments/confirm/batch - 발주확인 배치
 *   <li>C02: POST /shipments/ship/batch - 송장등록 배치
 *   <li>C03: POST /shipments/orders/{orderId}/ship - 단건 송장등록
 * </ul>
 *
 * <p>시나리오 문서 참조: oms_order_shipment_scenarios.md
 *
 * <p>tearDown 순서 (FK 제약 위반 방지):
 *
 * <ol>
 *   <li>ShipmentOutboxJpaRepository
 *   <li>ShipmentJpaRepository
 *   <li>OrderItemHistoryJpaRepository
 *   <li>OrderItemJpaRepository
 *   <li>OrderJpaRepository
 * </ol>
 */
@Tag("e2e")
@Tag("shipment")
@Tag("command")
@DisplayName("배송 Command API E2E 테스트")
class ShipmentCommandE2ETest extends E2ETestBase {

    private static final String CONFIRM_BATCH_URL = "/shipments/confirm/batch";
    private static final String SHIP_BATCH_URL = "/shipments/ship/batch";
    private static final String SHIP_SINGLE_URL = "/shipments/orders/{orderId}/ship";
    private static final String SHIPMENTS_URL = "/shipments";

    @Autowired private OrderJpaRepository orderRepository;
    @Autowired private OrderItemJpaRepository orderItemRepository;
    @Autowired private OrderItemHistoryJpaRepository orderHistoryRepository;
    @Autowired private ShipmentJpaRepository shipmentRepository;
    @Autowired private ShipmentOutboxJpaRepository outboxRepository;

    @BeforeEach
    void setUp() {
        cleanUp();
    }

    @AfterEach
    void tearDown() {
        cleanUp();
    }

    private void cleanUp() {
        outboxRepository.deleteAll();
        shipmentRepository.deleteAll();
        orderHistoryRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
    }

    /**
     * Order + OrderItem 세트 저장 헬퍼.
     *
     * @param orderId 주문 ID
     * @return 저장된 orderItemId (UUIDv7 String)
     */
    private String seedOrderItem(String orderId) {
        orderRepository.save(OrderJpaEntityFixtures.orderedEntity(orderId));
        OrderItemJpaEntity savedItem =
                orderItemRepository.save(OrderItemJpaEntityFixtures.defaultItem(orderId));
        return savedItem.getId();
    }

    /**
     * 발주확인(confirm/batch) 헬퍼 - orderItemId 목록으로 Shipment 생성.
     *
     * @param orderItemIds 발주확인할 orderItemId 목록
     */
    private void confirmBatch(List<String> orderItemIds) {
        given().spec(givenSuperAdmin())
                .body(Map.of("orderIds", orderItemIds))
                .when()
                .post(CONFIRM_BATCH_URL)
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    /**
     * 송장등록 Request Body 생성 헬퍼.
     *
     * @param orderItemId 주문상품 ID
     * @param trackingNumber 송장번호
     * @param courierCode 택배사 코드
     * @param courierName 택배사명
     */
    private Map<String, Object> createShipItem(
            String orderItemId, String trackingNumber, String courierCode, String courierName) {
        Map<String, Object> item = new HashMap<>();
        item.put("orderId", orderItemId);
        item.put("trackingNumber", trackingNumber);
        item.put("courierCode", courierCode);
        item.put("courierName", courierName);
        item.put("shipmentMethodType", "COURIER");
        return item;
    }

    /** 단건 송장등록 Request Body 생성 헬퍼. */
    private Map<String, Object> createSingleShipRequest(
            String trackingNumber, String courierCode, String courierName) {
        Map<String, Object> request = new HashMap<>();
        request.put("trackingNumber", trackingNumber);
        request.put("courierCode", courierCode);
        request.put("courierName", courierName);
        request.put("shipmentMethodType", "COURIER");
        return request;
    }

    // ========================================================================
    // C01: POST /shipments/confirm/batch - 발주확인 배치
    // ========================================================================

    @Nested
    @DisplayName("POST /shipments/confirm/batch - 발주확인 배치")
    class ConfirmBatchTest {

        @Test
        @Tag("P0")
        @DisplayName("[SHIPMENT-C01-01] 유효한 orderItemId 2건으로 발주확인 성공 - Shipment 생성 + Outbox 검증")
        void shouldConfirmBatchSuccessfullyAndCreateShipments() {
            // given: READY 상태 OrderItem 2건 저장
            String itemId1 = seedOrderItem("c01-order-001");
            String itemId2 = seedOrderItem("c01-order-002");

            // when: 발주확인 배치 요청
            given().spec(givenSuperAdmin())
                    .body(Map.of("orderIds", List.of(itemId1, itemId2)))
                    .when()
                    .post(CONFIRM_BATCH_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalCount", equalTo(2))
                    .body("data.successCount", equalTo(2))
                    .body("data.failureCount", equalTo(0))
                    .body("data.results", notNullValue())
                    .body("data.results.size()", equalTo(2))
                    .body("data.results.findAll { it.success == true }.size()", equalTo(2));

            // then: DB에 Shipment 2건 생성 확인
            assertThat(shipmentRepository.findAll()).hasSize(2);

            // then: Outbox 이벤트 저장 확인 (발주확인 이벤트 >= 2건)
            assertThat(outboxRepository.findAll().size()).isGreaterThanOrEqualTo(2);
        }

        @Test
        @Tag("P0")
        @DisplayName("[SHIPMENT-C01-02] 일부 존재하지 않는 orderItemId 포함 - 존재하는 1건만 처리 (부분 성공)")
        void shouldProcessOnlyExistingOrderItemIds() {
            // given: READY 상태 OrderItem 1건만 저장
            String itemId1 = seedOrderItem("c01-partial-001");

            // when: 존재하는 1건 + 존재하지 않는 1건으로 요청
            given().spec(givenSuperAdmin())
                    .body(
                            Map.of(
                                    "orderIds",
                                    List.of(itemId1, "01940001-0000-7000-8000-000000000999")))
                    .when()
                    .post(CONFIRM_BATCH_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalCount", equalTo(1))
                    .body("data.successCount", equalTo(1));

            // then: Shipment 1건만 생성됨
            assertThat(shipmentRepository.findAll()).hasSize(1);
        }

        @Test
        @Tag("P0")
        @DisplayName("[SHIPMENT-C01-03] 빈 orderItemIds 목록 요청 → 400 Bad Request (@NotEmpty 검증 실패)")
        void shouldReturn400WhenOrderItemIdsIsEmpty() {
            // when & then
            given().spec(givenSuperAdmin())
                    .body(Map.of("orderIds", List.of()))
                    .when()
                    .post(CONFIRM_BATCH_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[SHIPMENT-C01-04] orderItemIds 필드 자체 누락 → 400 Bad Request")
        void shouldReturn400WhenOrderItemIdsFieldMissing() {
            // when & then
            given().spec(givenSuperAdmin())
                    .body(Map.of())
                    .when()
                    .post(CONFIRM_BATCH_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[SHIPMENT-C01-05] 권한 없는 사용자 발주확인 → 403")
        void shouldReturn403WhenUserHasNoShipmentWritePermission() {
            // when & then: shipment:write 권한 없는 사용자 (다른 권한만 보유)
            given().spec(givenWithPermission("order:read"))
                    .body(Map.of("orderIds", List.of("01940001-0000-7000-8000-000000000001")))
                    .when()
                    .post(CONFIRM_BATCH_URL)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[SHIPMENT-C01-06] 비인증 사용자 발주확인 → 401")
        void shouldReturn401WhenUnauthenticatedForConfirmBatch() {
            // when & then
            given().spec(givenUnauthenticated())
                    .body(Map.of("orderIds", List.of("01940001-0000-7000-8000-000000000001")))
                    .when()
                    .post(CONFIRM_BATCH_URL)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }
    }

    // ========================================================================
    // C02: POST /shipments/ship/batch - 송장등록 배치
    // ========================================================================

    @Nested
    @DisplayName("POST /shipments/ship/batch - 송장등록 배치")
    class ShipBatchTest {

        @Test
        @Tag("P0")
        @DisplayName("[SHIPMENT-C02-01] 발주확인 후 단건 송장등록 배치 성공 - Shipment SHIPPED 상태 검증")
        void shouldShipBatchSuccessfullyAfterConfirm() {
            // given: READY OrderItem 1건 저장 → confirm/batch로 발주확인 완료
            String itemId = seedOrderItem("c02-order-001");
            confirmBatch(List.of(itemId));

            // when: 송장등록 배치
            given().spec(givenSuperAdmin())
                    .body(
                            Map.of(
                                    "items",
                                    List.of(createShipItem(itemId, "1234567890", "CJ", "CJ대한통운"))))
                    .when()
                    .post(SHIP_BATCH_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalCount", equalTo(1))
                    .body("data.successCount", equalTo(1))
                    .body("data.failureCount", equalTo(0));

            // then: Shipment 상태가 SHIPPED로 변경됨
            var shipments = shipmentRepository.findAll();
            assertThat(shipments).hasSize(1);
            assertThat(shipments.get(0).getStatus()).isEqualTo("SHIPPED");

            // then: Outbox에 출고 이벤트 저장 확인
            assertThat(outboxRepository.findAll()).isNotEmpty();
        }

        @Test
        @Tag("P0")
        @DisplayName("[SHIPMENT-C02-02] 복수 건 송장등록 배치 성공 - 2건 모두 SHIPPED 전환")
        void shouldShipBatchSuccessfullyForMultipleItems() {
            // given: READY OrderItem 2건 저장 → confirm/batch
            String itemId1 = seedOrderItem("c02-multi-001");
            String itemId2 = seedOrderItem("c02-multi-002");
            confirmBatch(List.of(itemId1, itemId2));

            // when: 2건 송장등록 배치
            given().spec(givenSuperAdmin())
                    .body(
                            Map.of(
                                    "items",
                                    List.of(
                                            createShipItem(itemId1, "TRK-001", "CJ", "CJ대한통운"),
                                            createShipItem(itemId2, "TRK-002", "LOTTE", "롯데택배"))))
                    .when()
                    .post(SHIP_BATCH_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalCount", equalTo(2))
                    .body("data.successCount", equalTo(2))
                    .body("data.failureCount", equalTo(0));

            // then: 2건 모두 SHIPPED 상태
            var shipments = shipmentRepository.findAll();
            assertThat(shipments).hasSize(2);
            shipments.forEach(s -> assertThat(s.getStatus()).isEqualTo("SHIPPED"));
        }

        @Test
        @Tag("P0")
        @DisplayName("[SHIPMENT-C02-03] trackingNumber 빈 문자열 → 400 (@NotBlank 검증 실패)")
        void shouldReturn400WhenTrackingNumberIsBlank() {
            // when & then
            given().spec(givenSuperAdmin())
                    .body(
                            Map.of(
                                    "items",
                                    List.of(
                                            Map.of(
                                                    "orderId", "some-uuid",
                                                    "trackingNumber", "",
                                                    "courierCode", "CJ",
                                                    "courierName", "CJ대한통운",
                                                    "shipmentMethodType", "COURIER"))))
                    .when()
                    .post(SHIP_BATCH_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[SHIPMENT-C02-04] courierCode 빈 문자열 → 400 (@NotBlank 검증 실패)")
        void shouldReturn400WhenCourierCodeIsBlank() {
            // when & then
            given().spec(givenSuperAdmin())
                    .body(
                            Map.of(
                                    "items",
                                    List.of(
                                            Map.of(
                                                    "orderId", "some-uuid",
                                                    "trackingNumber", "1234567890",
                                                    "courierCode", "",
                                                    "courierName", "CJ대한통운",
                                                    "shipmentMethodType", "COURIER"))))
                    .when()
                    .post(SHIP_BATCH_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[SHIPMENT-C02-05] items 빈 목록 → 400 (@NotEmpty 검증 실패)")
        void shouldReturn400WhenItemsIsEmpty() {
            // when & then
            given().spec(givenSuperAdmin())
                    .body(Map.of("items", List.of()))
                    .when()
                    .post(SHIP_BATCH_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[SHIPMENT-C02-07] 비인증 사용자 송장등록 배치 → 401")
        void shouldReturn401WhenUnauthenticatedForShipBatch() {
            // when & then
            given().spec(givenUnauthenticated())
                    .body(Map.of("items", List.of()))
                    .when()
                    .post(SHIP_BATCH_URL)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }
    }

    // ========================================================================
    // C03: POST /shipments/orders/{orderId}/ship - 단건 송장등록
    // ========================================================================

    @Nested
    @DisplayName("POST /shipments/orders/{orderId}/ship - 단건 송장등록")
    class ShipSingleTest {

        @Test
        @Tag("P0")
        @DisplayName("[SHIPMENT-C03-02] trackingNumber 빈 문자열 → 400 (@NotBlank 검증 실패)")
        void shouldReturn400WhenTrackingNumberIsBlankForSingle() {
            // when & then
            given().spec(givenSuperAdmin())
                    .body(
                            Map.of(
                                    "trackingNumber", "",
                                    "courierCode", "CJ",
                                    "courierName", "CJ대한통운",
                                    "shipmentMethodType", "COURIER"))
                    .when()
                    .post(SHIP_SINGLE_URL, "some-order-id")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[SHIPMENT-C03-04] 비인증 사용자 단건 송장등록 → 401")
        void shouldReturn401WhenUnauthenticatedForShipSingle() {
            // when & then
            given().spec(givenUnauthenticated())
                    .body(createSingleShipRequest("9876543210", "LOTTE", "롯데택배"))
                    .when()
                    .post(SHIP_SINGLE_URL, "any-id")
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[SHIPMENT-C03-01] 발주확인 후 단건 송장등록 성공 - Shipment SHIPPED 상태 검증")
        void shouldShipSingleSuccessfullyAfterConfirm() {
            // given: READY OrderItem 1건 → confirm/batch 완료
            String orderItemId = seedOrderItem("c03-single-001");
            confirmBatch(List.of(orderItemId));

            // when: 단건 송장등록
            given().spec(givenSuperAdmin())
                    .body(createSingleShipRequest("9876543210", "LOTTE", "롯데택배"))
                    .when()
                    .post(SHIP_SINGLE_URL, orderItemId)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // then: 해당 OrderItem과 연결된 Shipment 상태 == SHIPPED
            var shipments = shipmentRepository.findAll();
            assertThat(shipments).hasSize(1);
            assertThat(shipments.get(0).getStatus()).isEqualTo("SHIPPED");
        }

        @Test
        @Tag("P1")
        @DisplayName("[SHIPMENT-C03-03] 발주확인 전 단건 송장등록 시도 → 400 또는 404 (Shipment 미존재)")
        void shouldReturn4xxWhenShipmentNotExistsForSingleShip() {
            // given: READY 상태 OrderItem만 존재 (Shipment 없음, confirm 안 함)
            String orderItemId = seedOrderItem("c03-noconfirm-001");

            // when & then: Shipment가 없으므로 처리 불가
            int statusCode =
                    given().spec(givenSuperAdmin())
                            .body(createSingleShipRequest("9876543210", "LOTTE", "롯데택배"))
                            .when()
                            .post(SHIP_SINGLE_URL, orderItemId)
                            .getStatusCode();

            assertThat(statusCode)
                    .isIn(HttpStatus.BAD_REQUEST.value(), HttpStatus.NOT_FOUND.value());
        }
    }

    // ========================================================================
    // 전체 플로우 시나리오
    // ========================================================================

    @Nested
    @DisplayName("전체 플로우 시나리오")
    class FullFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[FLOW-01] 발주확인(배치) → 출고(배치) → 배송목록 조회 정상 플로우")
        void shouldCompleteConfirmThenShipBatchThenListFlow() {
            // given: READY OrderItem 2건 저장
            String itemId1 = seedOrderItem("flow01-order-001");
            String itemId2 = seedOrderItem("flow01-order-002");

            // Step 1: 발주확인 배치
            given().spec(givenSuperAdmin())
                    .body(Map.of("orderIds", List.of(itemId1, itemId2)))
                    .when()
                    .post(CONFIRM_BATCH_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(2))
                    .body("data.failureCount", equalTo(0));

            // DB 검증: Shipment 2건 생성
            assertThat(shipmentRepository.findAll()).hasSize(2);

            // Step 2: 출고(배치) 송장 등록
            given().spec(givenSuperAdmin())
                    .body(
                            Map.of(
                                    "items",
                                    List.of(
                                            createShipItem(
                                                    itemId1, "TRK-FLOW01-001", "CJ", "CJ대한통운"),
                                            createShipItem(
                                                    itemId2, "TRK-FLOW01-002", "LOTTE", "롯데택배"))))
                    .when()
                    .post(SHIP_BATCH_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalCount", equalTo(2))
                    .body("data.successCount", equalTo(2));

            // DB 검증: Shipment 상태 SHIPPED
            shipmentRepository
                    .findAll()
                    .forEach(s -> assertThat(s.getStatus()).isEqualTo("SHIPPED"));

            // Outbox 검증: 출고 이벤트 저장
            assertThat(outboxRepository.findAll()).isNotEmpty();

            // Step 3: 배송 목록 조회
            given().spec(givenSuperAdmin())
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get(SHIPMENTS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue());
        }

        @Test
        @Tag("P0")
        @DisplayName("[FLOW-04] 배치 처리 - 존재하지 않는 ID 포함 시 존재하는 건만 처리 (skip 방식)")
        void shouldSkipNonExistentOrderItemIdInBatch() {
            // given: READY OrderItem 1건 저장
            String itemId1 = seedOrderItem("flow04-order-001");

            // Step 1: 존재하는 1건 + 존재하지 않는 1건으로 발주확인 배치
            given().spec(givenSuperAdmin())
                    .body(
                            Map.of(
                                    "orderIds",
                                    List.of(itemId1, "01940001-0000-7000-8000-non-existent-999")))
                    .when()
                    .post(CONFIRM_BATCH_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalCount", equalTo(1))
                    .body("data.successCount", equalTo(1))
                    .body("data.failureCount", equalTo(0));

            // Step 2: 배송 목록 조회 - Shipment 1건만 존재
            Response listResponse =
                    given().spec(givenSuperAdmin())
                            .queryParam("page", 0)
                            .queryParam("size", 10)
                            .when()
                            .get(SHIPMENTS_URL);

            listResponse.then().statusCode(HttpStatus.OK.value()).body("data", notNullValue());
            assertThat(shipmentRepository.findAll()).hasSize(1);
        }

        @Test
        @Tag("P1")
        @DisplayName("[FLOW-02] 발주확인 → 출고(단건) → 배송상세 조회 플로우")
        void shouldCompleteConfirmThenShipSingleThenDetailFlow() {
            // given: READY OrderItem 1건 저장
            String orderItemId = seedOrderItem("flow02-order-001");

            // Step 1: 발주확인 배치
            given().spec(givenSuperAdmin())
                    .body(Map.of("orderIds", List.of(orderItemId)))
                    .when()
                    .post(CONFIRM_BATCH_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));

            // Step 2: 단건 송장등록
            given().spec(givenSuperAdmin())
                    .body(createSingleShipRequest("9876543210", "LOTTE", "롯데택배"))
                    .when()
                    .post(SHIP_SINGLE_URL, orderItemId)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // Step 3: 배송 목록 조회로 shipmentId 추출
            Response listResponse =
                    given().spec(givenSuperAdmin())
                            .queryParam("page", 0)
                            .queryParam("size", 10)
                            .when()
                            .get(SHIPMENTS_URL);

            listResponse.then().statusCode(HttpStatus.OK.value()).body("data", notNullValue());

            // Step 4: 배송 상세 조회 (shipmentRepository에서 ID 추출)
            var shipments = shipmentRepository.findAll();
            assertThat(shipments).hasSize(1);
            String shipmentId = shipments.get(0).getId();

            given().spec(givenSuperAdmin())
                    .when()
                    .get("/shipments/{shipmentId}", shipmentId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue());
        }
    }
}
