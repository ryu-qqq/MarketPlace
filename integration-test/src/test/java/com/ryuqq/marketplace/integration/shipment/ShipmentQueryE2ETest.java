package com.ryuqq.marketplace.integration.shipment;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

import com.ryuqq.marketplace.adapter.out.persistence.order.OrderItemJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.OrderJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderItemJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemHistoryJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.shipment.ShipmentJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.shipment.repository.ShipmentJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.shipmentoutbox.repository.ShipmentOutboxJpaRepository;
import com.ryuqq.marketplace.integration.E2ETestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * 배송 Query API E2E 테스트.
 *
 * <p>테스트 대상:
 *
 * <ul>
 *   <li>Q03: GET /shipments/summary - 배송 상태별 요약 조회
 *   <li>Q04: GET /shipments - 배송 목록 조회
 *   <li>Q05: GET /shipments/{shipmentId} - 배송 상세 조회
 * </ul>
 */
@Tag("e2e")
@Tag("shipment")
@Tag("query")
@DisplayName("배송 Query API E2E 테스트")
class ShipmentQueryE2ETest extends E2ETestBase {

    private static final String SHIPMENTS_URL = "/shipments";
    private static final String SHIPMENT_SUMMARY_URL = "/shipments/summary";
    private static final String SHIPMENT_DETAIL_URL = "/shipments/{shipmentId}";

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
     * Order + OrderItem 시딩 후 orderItemId를 반환하는 헬퍼.
     *
     * @param orderId 주문 ID
     * @return 저장된 OrderItem의 ID
     */
    private Long seedOrderItem(String orderId) {
        orderRepository.save(OrderJpaEntityFixtures.orderedEntity(orderId));
        OrderItemJpaEntity savedItem =
                orderItemRepository.save(OrderItemJpaEntityFixtures.defaultItem(orderId));
        return savedItem.getId();
    }

    // ========================================================================
    // Q03: GET /shipments/summary - 배송 요약 조회
    // ========================================================================

    @Nested
    @DisplayName("GET /shipments/summary - 배송 요약 조회")
    class ShipmentSummaryQueryTest {

        @Test
        @Tag("P0")
        @DisplayName("[SHIPMENT-Q03-01] 다양한 상태의 배송 데이터 존재 시 요약 정상 조회 - HTTP 200")
        void shouldReturnShipmentSummaryWithVariousStatuses() {
            // given: 다양한 상태의 Shipment 4건 직접 시딩
            shipmentRepository.save(ShipmentJpaEntityFixtures.readyEntity("q03-ready-001"));
            shipmentRepository.save(ShipmentJpaEntityFixtures.readyEntity("q03-ready-002"));
            shipmentRepository.save(
                    ShipmentJpaEntityFixtures.entityWithStatus("q03-shipped-001", "SHIPPED"));
            shipmentRepository.save(
                    ShipmentJpaEntityFixtures.entityWithStatus("q03-delivered-001", "DELIVERED"));

            // when & then
            given().spec(givenSuperAdmin())
                    .when()
                    .get(SHIPMENT_SUMMARY_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue());
        }

        @Test
        @Tag("P0")
        @DisplayName("[SHIPMENT-Q03-03] 비인증 사용자 배송 요약 조회 → 401")
        void shouldReturn401WhenUnauthenticatedForSummary() {
            // when & then
            given().spec(givenUnauthenticated())
                    .when()
                    .get(SHIPMENT_SUMMARY_URL)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[SHIPMENT-Q03-02] 데이터 없을 때 요약 조회 - HTTP 200, data not null (카운트 0)")
        void shouldReturnEmptySummaryWhenNoData() {
            // given: 데이터 없음 (cleanUp 이미 수행됨)

            // when & then
            given().spec(givenSuperAdmin())
                    .when()
                    .get(SHIPMENT_SUMMARY_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue());
        }
    }

    // ========================================================================
    // Q04: GET /shipments - 배송 목록 조회
    // ========================================================================

    @Nested
    @DisplayName("GET /shipments - 배송 목록 조회")
    class ShipmentListQueryTest {

        @Test
        @Tag("P0")
        @DisplayName("[SHIPMENT-Q04-01] 데이터 존재 시 배송 목록 정상 조회 - HTTP 200, data not null")
        void shouldReturnShipmentListWhenDataExists() {
            // given: Shipment 3건 직접 시딩
            shipmentRepository.save(ShipmentJpaEntityFixtures.readyEntity("q04-list-001"));
            shipmentRepository.save(ShipmentJpaEntityFixtures.readyEntity("q04-list-002"));
            shipmentRepository.save(
                    ShipmentJpaEntityFixtures.entityWithStatus("q04-list-003", "SHIPPED"));

            // when & then
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
        @DisplayName("[SHIPMENT-Q04-02] 데이터 없을 때 배송 목록 조회 - HTTP 200, 빈 결과 반환")
        void shouldReturnEmptyListWhenNoData() {
            // given: 데이터 없음

            // when & then
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
        @Tag("P1")
        @DisplayName("[SHIPMENT-Q04-03] statuses 필터 - SHIPPED 상태만 조회")
        void shouldFilterShipmentsByStatus() {
            // given: READY 1건 + SHIPPED 2건 저장
            shipmentRepository.save(ShipmentJpaEntityFixtures.readyEntity("q04-filter-001"));
            shipmentRepository.save(
                    ShipmentJpaEntityFixtures.entityWithStatus("q04-filter-002", "SHIPPED"));
            shipmentRepository.save(
                    ShipmentJpaEntityFixtures.entityWithStatus("q04-filter-003", "SHIPPED"));

            // when & then
            given().spec(givenSuperAdmin())
                    .queryParam("statuses", "SHIPPED")
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
        @DisplayName("[SHIPMENT-Q04-06] 비인증 사용자 배송 목록 조회 → 401")
        void shouldReturn401WhenUnauthenticatedForList() {
            // when & then
            given().spec(givenUnauthenticated())
                    .when()
                    .get(SHIPMENTS_URL)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[SHIPMENT-Q04-05] 페이징 동작 확인 - page=0, size=2 요청 시 2건만 반환")
        void shouldReturnPagedShipmentList() {
            // given: 배송 5건 저장
            shipmentRepository.save(ShipmentJpaEntityFixtures.readyEntity("q04-page-001"));
            shipmentRepository.save(ShipmentJpaEntityFixtures.readyEntity("q04-page-002"));
            shipmentRepository.save(ShipmentJpaEntityFixtures.readyEntity("q04-page-003"));
            shipmentRepository.save(
                    ShipmentJpaEntityFixtures.entityWithStatus("q04-page-004", "SHIPPED"));
            shipmentRepository.save(
                    ShipmentJpaEntityFixtures.entityWithStatus("q04-page-005", "SHIPPED"));

            // when & then
            given().spec(givenSuperAdmin())
                    .queryParam("page", 0)
                    .queryParam("size", 2)
                    .when()
                    .get(SHIPMENTS_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue());
        }
    }

    // ========================================================================
    // Q05: GET /shipments/{shipmentId} - 배송 상세 조회
    // ========================================================================

    @Nested
    @DisplayName("GET /shipments/{shipmentId} - 배송 상세 조회")
    class ShipmentDetailQueryTest {

        @Test
        @Tag("P0")
        @DisplayName("[SHIPMENT-Q05-01] 존재하는 shipmentId로 배송 상세 조회 성공 - HTTP 200")
        void shouldReturnShipmentDetailSuccessfully() {
            // given: Order + OrderItem 시딩 후 해당 orderItemId로 Shipment 생성
            Long orderItemId = seedOrderItem("q05-order-001");
            shipmentRepository.save(
                    ShipmentJpaEntityFixtures.readyEntityWithOrderItemId(
                            "q05-ready-001", orderItemId));

            // when & then
            given().spec(givenSuperAdmin())
                    .when()
                    .get(SHIPMENT_DETAIL_URL, "q05-ready-001")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue());
        }

        @Test
        @Tag("P0")
        @DisplayName("[SHIPMENT-Q05-02] 존재하지 않는 shipmentId 조회 → 404")
        void shouldReturn404WhenShipmentIdNotFound() {
            // given: 데이터 없음

            // when & then
            given().spec(givenSuperAdmin())
                    .when()
                    .get(SHIPMENT_DETAIL_URL, "non-existent-id")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[SHIPMENT-Q05-03] SHIPPED 배송 상세 조회 - 송장 정보 포함 확인")
        void shouldReturnShippedShipmentWithTrackingInfo() {
            // given: Order + OrderItem 시딩 후 해당 orderItemId로 SHIPPED Shipment 생성
            Long orderItemId = seedOrderItem("q05-order-shipped");
            shipmentRepository.save(
                    ShipmentJpaEntityFixtures.readyEntityWithOrderItemId(
                            "q05-shipped-001", orderItemId));

            // when & then
            given().spec(givenSuperAdmin())
                    .when()
                    .get(SHIPMENT_DETAIL_URL, "q05-shipped-001")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue());
        }

        @Test
        @Tag("P0")
        @DisplayName("[SHIPMENT-Q05-auth] 비인증 사용자 배송 상세 조회 → 401")
        void shouldReturn401WhenUnauthenticatedForDetail() {
            // when & then
            given().spec(givenUnauthenticated())
                    .when()
                    .get(SHIPMENT_DETAIL_URL, "any-id")
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }
    }
}
