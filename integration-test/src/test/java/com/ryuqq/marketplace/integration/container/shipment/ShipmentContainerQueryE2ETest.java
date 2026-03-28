package com.ryuqq.marketplace.integration.container.shipment;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import com.ryuqq.marketplace.adapter.out.persistence.order.OrderItemJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.OrderJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.PaymentJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderItemJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.PaymentJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.shipment.entity.ShipmentJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.shipment.repository.ShipmentJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.shipmentoutbox.repository.ShipmentOutboxJpaRepository;
import com.ryuqq.marketplace.integration.container.ContainerE2ETestBase;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * Shipment Query Testcontainers E2E 테스트.
 *
 * <p>MySQL 실제 컨테이너 기반으로 배송 조회 API를 검증합니다.
 *
 * <p>테스트 대상:
 *
 * <ul>
 *   <li>Q1~Q4: GET /shipments/summary - 배송 상태별 요약 조회
 *   <li>Q5~Q10: GET /shipments - 배송 목록 조회 (V4)
 *   <li>Q11~Q15: GET /shipments/{shipmentId} - 배송 상세 조회
 * </ul>
 */
@Tag("e2e")
@Tag("container")
@Tag("shipment")
@Tag("query")
@DisplayName("Shipment Query Container E2E 테스트")
class ShipmentContainerQueryE2ETest extends ContainerE2ETestBase {

    private static final String SHIPMENTS = "/shipments";
    private static final String SHIPMENT_SUMMARY = "/shipments/summary";
    private static final String SHIPMENT_DETAIL = "/shipments/{shipmentId}";

    @Autowired private ShipmentJpaRepository shipmentRepository;
    @Autowired private ShipmentOutboxJpaRepository shipmentOutboxRepository;
    @Autowired private OrderJpaRepository orderRepository;
    @Autowired private OrderItemJpaRepository orderItemRepository;
    @Autowired private PaymentJpaRepository paymentRepository;

    @BeforeEach
    void setUp() {
        shipmentOutboxRepository.deleteAll();
        shipmentRepository.deleteAll();
        paymentRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
    }

    // ===== Helper 메서드 =====

    /** 주문 + 주문상품 + 배송 데이터 세트를 생성한다. */
    private ShipmentJpaEntity createShipmentWithOrder(String status) {
        String orderId = UUID.randomUUID().toString();
        String shipmentId = UUID.randomUUID().toString();

        OrderJpaEntity order = OrderJpaEntityFixtures.orderedEntity(orderId);
        orderRepository.save(order);

        OrderItemJpaEntity orderItem = createOrderItem(null, orderId);
        OrderItemJpaEntity savedOrderItem = orderItemRepository.save(orderItem);
        Long orderItemId = savedOrderItem.getId();

        Instant now = Instant.now();
        ShipmentJpaEntity shipment = createShipmentEntity(shipmentId, orderItemId, status, now);
        return shipmentRepository.save(shipment);
    }

    private OrderItemJpaEntity createOrderItem(Long itemId, String orderId) {
        Instant now = Instant.now();
        return OrderItemJpaEntity.create(
                itemId,
                "ORD-" + UUID.randomUUID().toString().substring(0, 8) + "-001",
                orderId,
                OrderItemJpaEntityFixtures.DEFAULT_PRODUCT_GROUP_ID,
                1L,
                5L,
                OrderItemJpaEntityFixtures.DEFAULT_PRODUCT_ID,
                OrderItemJpaEntityFixtures.DEFAULT_SKU_CODE,
                "테스트 상품 그룹",
                "테스트 브랜드",
                "테스트 셀러",
                "https://example.com/img.jpg",
                OrderItemJpaEntityFixtures.DEFAULT_EXTERNAL_PRODUCT_ID,
                OrderItemJpaEntityFixtures.DEFAULT_EXTERNAL_OPTION_ID,
                OrderItemJpaEntityFixtures.DEFAULT_EXTERNAL_PRODUCT_NAME,
                OrderItemJpaEntityFixtures.DEFAULT_EXTERNAL_OPTION_NAME,
                OrderItemJpaEntityFixtures.DEFAULT_EXTERNAL_IMAGE_URL,
                OrderItemJpaEntityFixtures.DEFAULT_UNIT_PRICE,
                OrderItemJpaEntityFixtures.DEFAULT_QUANTITY,
                OrderItemJpaEntityFixtures.DEFAULT_TOTAL_AMOUNT,
                OrderItemJpaEntityFixtures.DEFAULT_DISCOUNT_AMOUNT,
                0,
                OrderItemJpaEntityFixtures.DEFAULT_PAYMENT_AMOUNT,
                OrderItemJpaEntityFixtures.DEFAULT_RECEIVER_NAME,
                OrderItemJpaEntityFixtures.DEFAULT_RECEIVER_PHONE,
                OrderItemJpaEntityFixtures.DEFAULT_RECEIVER_ZIPCODE,
                OrderItemJpaEntityFixtures.DEFAULT_RECEIVER_ADDRESS,
                OrderItemJpaEntityFixtures.DEFAULT_RECEIVER_ADDRESS_DETAIL,
                OrderItemJpaEntityFixtures.DEFAULT_DELIVERY_REQUEST,
                "CONFIRMED",
                null,
                0,
                0,
                now,
                now);
    }

    private ShipmentJpaEntity createShipmentEntity(
            String id, Long orderItemId, String status, Instant now) {
        boolean hasTracking = !"READY".equals(status) && !"PREPARING".equals(status);
        Instant confirmedAt = "READY".equals(status) ? null : now.minusSeconds(3600);
        Instant shippedAt = hasTracking ? now.minusSeconds(1800) : null;
        Instant deliveredAt = "DELIVERED".equals(status) ? now : null;

        return ShipmentJpaEntity.create(
                id,
                "SHP-" + id.substring(0, 8),
                orderItemId,
                status,
                hasTracking ? "COURIER" : null,
                hasTracking ? "CJ" : null,
                hasTracking ? "CJ대한통운" : null,
                hasTracking ? "TRK-" + id.substring(0, 8) : null,
                confirmedAt,
                shippedAt,
                deliveredAt,
                now.minusSeconds(7200),
                now,
                null);
    }

    // ===== GET /shipments/summary =====

    @Nested
    @DisplayName("GET /shipments/summary - 배송 상태별 요약 조회")
    class GetShipmentSummaryTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q1] 상태별 배송 데이터 존재 시 각 상태별 건수를 정확히 반환한다")
        void getSummary_WithMultipleStatuses_ReturnsCorrectCounts() {
            createShipmentWithOrder("READY");
            createShipmentWithOrder("READY");
            createShipmentWithOrder("PREPARING");
            createShipmentWithOrder("SHIPPED");
            createShipmentWithOrder("SHIPPED");
            createShipmentWithOrder("SHIPPED");
            createShipmentWithOrder("DELIVERED");

            given().spec(givenSuperAdmin())
                    .when()
                    .get(SHIPMENT_SUMMARY)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.ready", equalTo(2))
                    .body("data.preparing", equalTo(1))
                    .body("data.shipped", equalTo(3))
                    .body("data.delivered", equalTo(1))
                    .body("data.failed", equalTo(0))
                    .body("data.cancelled", equalTo(0));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q2] 배송 데이터 없을 때 모든 상태별 건수 0 반환")
        void getSummary_NoData_ReturnsAllZeros() {
            given().spec(givenSuperAdmin())
                    .when()
                    .get(SHIPMENT_SUMMARY)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.ready", equalTo(0))
                    .body("data.preparing", equalTo(0))
                    .body("data.shipped", equalTo(0))
                    .body("data.inTransit", equalTo(0))
                    .body("data.delivered", equalTo(0))
                    .body("data.failed", equalTo(0))
                    .body("data.cancelled", equalTo(0));
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q3] 권한 없는 사용자의 요약 조회 시도 시 403 반환")
        void getSummary_WithoutPermission_Returns403() {
            given().spec(givenAuthenticatedUser())
                    .when()
                    .get(SHIPMENT_SUMMARY)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q4] CANCELLED, FAILED 상태 배송 존재 시 정확히 집계한다")
        void getSummary_WithCancelledAndFailed_ReturnsCorrectCounts() {
            createShipmentWithOrder("CANCELLED");
            createShipmentWithOrder("CANCELLED");
            createShipmentWithOrder("FAILED");

            given().spec(givenSuperAdmin())
                    .when()
                    .get(SHIPMENT_SUMMARY)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.cancelled", equalTo(2))
                    .body("data.failed", equalTo(1));
        }
    }

    // ===== GET /shipments =====

    @Nested
    @DisplayName("GET /shipments - 배송 목록 조회 (V4)")
    class SearchShipmentsTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q5] 배송 데이터 3건 존재 시 기본 조회 → 3건 반환")
        void searchShipments_WithData_ReturnsAll() {
            createShipmentWithOrder("READY");
            createShipmentWithOrder("SHIPPED");
            createShipmentWithOrder("DELIVERED");

            given().spec(givenSuperAdmin())
                    .queryParam("page", 0)
                    .queryParam("size", 20)
                    .when()
                    .get(SHIPMENTS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(3))
                    .body("data.totalElements", equalTo(3));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q6] 배송 데이터 없을 때 조회 → 빈 페이지 반환")
        void searchShipments_NoData_ReturnsEmptyPage() {
            given().spec(givenSuperAdmin())
                    .queryParam("page", 0)
                    .queryParam("size", 20)
                    .when()
                    .get(SHIPMENTS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(0))
                    .body("data.totalElements", equalTo(0));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q7] 상태 필터(READY) 적용 시 해당 상태만 반환한다")
        void searchShipments_FilterByReady_ReturnsReadyOnly() {
            createShipmentWithOrder("READY");
            createShipmentWithOrder("READY");
            createShipmentWithOrder("SHIPPED");

            given().spec(givenSuperAdmin())
                    .queryParam("statuses", "READY")
                    .queryParam("page", 0)
                    .queryParam("size", 20)
                    .when()
                    .get(SHIPMENTS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(2))
                    .body("data.content[0].status", equalTo("READY"));
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q8] 페이징: size=1 요청 시 1건만 반환하고 totalElements는 전체 건수")
        void searchShipments_PageSize1_ReturnsOnlyOne() {
            createShipmentWithOrder("READY");
            createShipmentWithOrder("SHIPPED");
            createShipmentWithOrder("DELIVERED");

            given().spec(givenSuperAdmin())
                    .queryParam("page", 0)
                    .queryParam("size", 1)
                    .when()
                    .get(SHIPMENTS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(1))
                    .body("data.totalElements", equalTo(3));
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q9] 목록 조회 응답에 V4 구조 필드(shipmentMethod, orderProduct, receiverInfo 등)가 포함된다")
        void searchShipments_ResponseContainsV4Fields() {
            createShipmentWithOrder("SHIPPED");

            given().spec(givenSuperAdmin())
                    .queryParam("page", 0)
                    .queryParam("size", 20)
                    .when()
                    .get(SHIPMENTS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(1))
                    .body("data.content[0].shipmentNumber", notNullValue())
                    .body("data.content[0].status", equalTo("SHIPPED"))
                    .body("data.content[0].shipmentMethod", notNullValue())
                    .body("data.content[0].orderProduct", notNullValue())
                    .body("data.content[0].receiverInfo", notNullValue())
                    .body("data.content[0].externalOrderInfo", notNullValue());
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q10] 권한 없는 사용자의 목록 조회 시도 시 403 반환")
        void searchShipments_WithoutPermission_Returns403() {
            given().spec(givenAuthenticatedUser())
                    .queryParam("page", 0)
                    .queryParam("size", 20)
                    .when()
                    .get(SHIPMENTS)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }
    }

    // ===== GET /shipments/{shipmentId} =====

    @Nested
    @DisplayName("GET /shipments/{shipmentId} - 배송 상세 조회")
    class GetShipmentDetailTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q11] SHIPPED 상태 배송 상세 조회 시 배송/주문/상품주문/수령인 정보가 모두 포함된다")
        void getShipmentDetail_ShippedShipment_ReturnsFullDetail() {
            ShipmentJpaEntity shipment = createShipmentWithOrder("SHIPPED");

            given().spec(givenSuperAdmin())
                    .when()
                    .get(SHIPMENT_DETAIL, shipment.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue())
                    .body("data.shipment", notNullValue())
                    .body("data.shipment.shipmentId", equalTo(shipment.getId()))
                    .body("data.shipment.status", equalTo("SHIPPED"))
                    .body("data.order", notNullValue())
                    .body("data.productOrder", notNullValue())
                    .body("data.receiver", notNullValue());
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q12] 존재하지 않는 배송 ID 상세 조회 시 404 반환")
        void getShipmentDetail_NonExistentId_Returns404() {
            given().spec(givenSuperAdmin())
                    .when()
                    .get(SHIPMENT_DETAIL, "non-existent-shipment-id")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q13] READY 상태 배송 상세 조회 시 trackingNumber가 null이다")
        void getShipmentDetail_ReadyShipment_TrackingNumberIsNull() {
            ShipmentJpaEntity shipment = createShipmentWithOrder("READY");

            given().spec(givenSuperAdmin())
                    .when()
                    .get(SHIPMENT_DETAIL, shipment.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.shipment.status", equalTo("READY"));
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q14] 결제 정보가 있는 주문의 배송 상세 조회 시 payment 필드가 포함된다")
        void getShipmentDetail_WithPayment_ReturnsPaymentInfo() {
            String orderId = UUID.randomUUID().toString();
            String shipmentId = UUID.randomUUID().toString();
            String paymentId = UUID.randomUUID().toString();

            orderRepository.save(OrderJpaEntityFixtures.orderedEntity(orderId));
            OrderItemJpaEntity savedItem = orderItemRepository.save(createOrderItem(null, orderId));
            Long orderItemId = savedItem.getId();
            paymentRepository.save(PaymentJpaEntityFixtures.completedEntity(paymentId, orderId));

            Instant now = Instant.now();
            ShipmentJpaEntity shipment =
                    shipmentRepository.save(
                            createShipmentEntity(shipmentId, orderItemId, "SHIPPED", now));

            given().spec(givenSuperAdmin())
                    .when()
                    .get(SHIPMENT_DETAIL, shipment.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.shipment", notNullValue())
                    .body("data.payment", notNullValue())
                    .body("data.payment.paymentStatus", equalTo("COMPLETED"));
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q15] 권한 없는 사용자의 상세 조회 시도 시 403 반환")
        void getShipmentDetail_WithoutPermission_Returns403() {
            ShipmentJpaEntity shipment = createShipmentWithOrder("SHIPPED");

            given().spec(givenAuthenticatedUser())
                    .when()
                    .get(SHIPMENT_DETAIL, shipment.getId())
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }
    }

    // ===== 일관성 검증 =====

    @Nested
    @DisplayName("목록 조회 → 상세 조회 일관성 검증")
    class SearchAndDetailConsistencyTest {

        @Test
        @Tag("P2")
        @DisplayName("[Q16] 목록 조회에서 반환된 shipmentNumber로 상세 조회 시 동일한 데이터 반환")
        void searchShipments_ThenGetDetail_DataConsistent() {
            ShipmentJpaEntity shipment = createShipmentWithOrder("SHIPPED");

            // 목록 조회에서 shipmentNumber 확인
            String shipmentNumber =
                    given().spec(givenSuperAdmin())
                            .queryParam("page", 0)
                            .queryParam("size", 20)
                            .when()
                            .get(SHIPMENTS)
                            .jsonPath()
                            .getString("data.content[0].shipmentNumber");

            // 상세 조회에서 동일한 shipmentNumber 확인
            given().spec(givenSuperAdmin())
                    .when()
                    .get(SHIPMENT_DETAIL, shipment.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.shipment.shipmentNumber", equalTo(shipmentNumber));
        }
    }
}
