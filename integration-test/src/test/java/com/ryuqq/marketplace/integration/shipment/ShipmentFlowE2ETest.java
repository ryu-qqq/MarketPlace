package com.ryuqq.marketplace.integration.shipment;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.ryuqq.marketplace.adapter.out.persistence.shipment.ShipmentJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.shipment.repository.ShipmentJpaRepository;
import com.ryuqq.marketplace.integration.E2ETestBase;
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
 * Shipment 전체 플로우 E2E 테스트.
 *
 * <p>테스트 대상:
 *
 * <ul>
 *   <li>C1→Q2: 발주확인(배치) → 상세 조회 (READY→PREPARING 상태 전이)
 *   <li>C1→C2→Q2: 발주확인 → 송장등록(배치) → 상세 조회 (PREPARING→SHIPPED)
 *   <li>C2: 단건 송장등록 (주문ID 기반)
 *   <li>Q1: 배송 요약 조회 (상태별 카운트)
 *   <li>Q2: 배송 목록 페이징 조회 + 필터
 * </ul>
 */
@Tag("e2e")
@Tag("shipment")
@Tag("flow")
@DisplayName("Shipment Flow E2E 테스트")
class ShipmentFlowE2ETest extends E2ETestBase {

    private static final String SHIPMENTS = "/shipments";
    private static final String CONFIRM_BATCH = "/shipments/confirm/batch";
    private static final String SHIP_BATCH = "/shipments/ship/batch";
    private static final String SHIP_SINGLE = "/shipments/orders/{orderId}/ship";
    private static final String SHIPMENT_SUMMARY = "/shipments/summary";
    private static final String SHIPMENT_DETAIL = "/shipments/{shipmentId}";

    @Autowired private ShipmentJpaRepository shipmentRepository;

    @BeforeEach
    void setUp() {
        shipmentRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        shipmentRepository.deleteAll();
    }

    @Nested
    @DisplayName("C1→Q2: 발주확인 → 조회")
    class ConfirmAndQueryTest {

        @Test
        @Tag("P0")
        @DisplayName("[FLOW-1] 발주확인 배치 → 상세 조회 - READY→PREPARING 상태 전이 확인")
        void confirmBatch_ThenGetDetail_StatusChangedToPreparing() {
            // Seed: READY 상태 배송 2건
            var s1 =
                    shipmentRepository.save(
                            ShipmentJpaEntityFixtures.readyEntity("ship-confirm-001"));
            var s2 =
                    shipmentRepository.save(
                            ShipmentJpaEntityFixtures.readyEntityWithOrderId(
                                    "ship-confirm-002", "order-confirm-002"));

            // Step 1: POST - 발주확인 배치
            given().spec(givenSuperAdmin())
                    .body(Map.of("shipmentIds", List.of(s1.getId(), s2.getId())))
                    .when()
                    .post(CONFIRM_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalCount", equalTo(2))
                    .body("data.successCount", equalTo(2))
                    .body("data.failureCount", equalTo(0))
                    .body("data.results.size()", equalTo(2))
                    .body("data.results.findAll { it.success == true }.size()", equalTo(2));

            // Step 2: GET - 상세 조회로 PREPARING 상태 확인
            given().spec(givenSuperAdmin())
                    .when()
                    .get(SHIPMENT_DETAIL, s1.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.shipmentId", equalTo(s1.getId()))
                    .body("data.status", equalTo("PREPARING"))
                    .body("data.orderConfirmedAt", notNullValue());

            given().spec(givenSuperAdmin())
                    .when()
                    .get(SHIPMENT_DETAIL, s2.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.status", equalTo("PREPARING"));
        }

        @Test
        @Tag("P0")
        @DisplayName("[FLOW-2] 발주확인 - 존재하지 않는 ID 포함 시 partial failure")
        void confirmBatch_NonExistentId_PartialFailure() {
            var s1 =
                    shipmentRepository.save(
                            ShipmentJpaEntityFixtures.readyEntity("ship-partial-001"));

            given().spec(givenSuperAdmin())
                    .body(Map.of("shipmentIds", List.of(s1.getId(), "non-existent-id")))
                    .when()
                    .post(CONFIRM_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalCount", equalTo(2))
                    .body("data.successCount", equalTo(1))
                    .body("data.failureCount", equalTo(1))
                    .body(
                            "data.results.find { it.id == 'non-existent-id' }.success",
                            equalTo(false))
                    .body(
                            "data.results.find { it.id == 'non-existent-id' }.errorMessage",
                            notNullValue());
        }

        @Test
        @DisplayName("[FLOW-3] 발주확인 - 빈 목록 요청 시 400 Bad Request")
        void confirmBatch_EmptyList_Returns400() {
            given().spec(givenSuperAdmin())
                    .body(Map.of("shipmentIds", List.of()))
                    .when()
                    .post(CONFIRM_BATCH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    @Nested
    @DisplayName("C1→C2→Q2: 발주확인 → 출하 → 조회")
    class ConfirmShipAndQueryTest {

        @Test
        @Tag("P0")
        @DisplayName("[FLOW-4] 발주확인 → 송장등록(배치) → 상세 조회 전체 플로우")
        void confirmThenShipBatch_ThenGetDetail_FullFlow() {
            // Seed: READY 배송
            var s1 =
                    shipmentRepository.save(ShipmentJpaEntityFixtures.readyEntity("ship-flow-001"));

            // Step 1: 발주확인
            given().spec(givenSuperAdmin())
                    .body(Map.of("shipmentIds", List.of(s1.getId())))
                    .when()
                    .post(CONFIRM_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));

            // Step 2: 송장등록 배치
            Map<String, Object> shipItem = new HashMap<>();
            shipItem.put("shipmentId", s1.getId());
            shipItem.put("trackingNumber", "1234567890");
            shipItem.put("courierCode", "CJ");
            shipItem.put("courierName", "CJ대한통운");
            shipItem.put("shipmentMethodType", "COURIER");

            given().spec(givenSuperAdmin())
                    .body(Map.of("items", List.of(shipItem)))
                    .when()
                    .post(SHIP_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalCount", equalTo(1))
                    .body("data.successCount", equalTo(1))
                    .body("data.failureCount", equalTo(0));

            // Step 3: 상세 조회 - SHIPPED 상태 + 송장정보 확인
            given().spec(givenSuperAdmin())
                    .when()
                    .get(SHIPMENT_DETAIL, s1.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.status", equalTo("SHIPPED"))
                    .body("data.trackingNumber", equalTo("1234567890"))
                    .body("data.shipmentMethod.type", equalTo("COURIER"))
                    .body("data.shipmentMethod.courierCode", equalTo("CJ"))
                    .body("data.shipmentMethod.courierName", equalTo("CJ대한통운"))
                    .body("data.shippedAt", notNullValue());
        }

        @Test
        @Tag("P0")
        @DisplayName("[FLOW-5] 복수 배송 발주확인 → 송장등록 배치 → 조회")
        void confirmThenShipBatch_MultipleShipments_AllShipped() {
            // Seed: READY 배송 2건
            var s1 =
                    shipmentRepository.save(
                            ShipmentJpaEntityFixtures.readyEntity("ship-multi-001"));
            var s2 =
                    shipmentRepository.save(
                            ShipmentJpaEntityFixtures.readyEntityWithOrderId(
                                    "ship-multi-002", "order-multi-002"));

            // Step 1: 발주확인 배치
            given().spec(givenSuperAdmin())
                    .body(Map.of("shipmentIds", List.of(s1.getId(), s2.getId())))
                    .when()
                    .post(CONFIRM_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(2));

            // Step 2: 송장등록 배치 (2건)
            Map<String, Object> item1 = new HashMap<>();
            item1.put("shipmentId", s1.getId());
            item1.put("trackingNumber", "TRK-001");
            item1.put("courierCode", "CJ");
            item1.put("courierName", "CJ대한통운");
            item1.put("shipmentMethodType", "COURIER");

            Map<String, Object> item2 = new HashMap<>();
            item2.put("shipmentId", s2.getId());
            item2.put("trackingNumber", "TRK-002");
            item2.put("courierCode", "LOTTE");
            item2.put("courierName", "롯데택배");
            item2.put("shipmentMethodType", "COURIER");

            given().spec(givenSuperAdmin())
                    .body(Map.of("items", List.of(item1, item2)))
                    .when()
                    .post(SHIP_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalCount", equalTo(2))
                    .body("data.successCount", equalTo(2));

            // Step 3: 각각 상세 조회
            given().spec(givenSuperAdmin())
                    .when()
                    .get(SHIPMENT_DETAIL, s1.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.status", equalTo("SHIPPED"))
                    .body("data.trackingNumber", equalTo("TRK-001"));

            given().spec(givenSuperAdmin())
                    .when()
                    .get(SHIPMENT_DETAIL, s2.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.status", equalTo("SHIPPED"))
                    .body("data.trackingNumber", equalTo("TRK-002"));
        }
    }

    @Nested
    @DisplayName("C2: 단건 송장등록 (주문ID 기반)")
    class ShipSingleTest {

        @Test
        @Tag("P1")
        @DisplayName("[FLOW-6] 단건 주문별 송장등록 → 상세 조회")
        void shipSingle_ThenGetDetail_Shipped() {
            // Seed: READY 배송 (특정 orderId)
            String orderId = "order-single-001";
            var s1 =
                    shipmentRepository.save(
                            ShipmentJpaEntityFixtures.readyEntityWithOrderId(
                                    "ship-single-001", orderId));

            // Step 1: 발주확인
            given().spec(givenSuperAdmin())
                    .body(Map.of("shipmentIds", List.of(s1.getId())))
                    .when()
                    .post(CONFIRM_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));

            // Step 2: 단건 송장등록
            Map<String, Object> shipRequest = new HashMap<>();
            shipRequest.put("trackingNumber", "9876543210");
            shipRequest.put("courierCode", "LOTTE");
            shipRequest.put("courierName", "롯데택배");
            shipRequest.put("shipmentMethodType", "COURIER");

            given().spec(givenSuperAdmin())
                    .body(shipRequest)
                    .when()
                    .post(SHIP_SINGLE, orderId)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // Step 3: 상세 조회 - SHIPPED 상태 확인
            given().spec(givenSuperAdmin())
                    .when()
                    .get(SHIPMENT_DETAIL, s1.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.status", equalTo("SHIPPED"))
                    .body("data.trackingNumber", equalTo("9876543210"))
                    .body("data.shipmentMethod.courierName", equalTo("롯데택배"));
        }
    }

    @Nested
    @DisplayName("Q1: 배송 요약 조회")
    class ShipmentSummaryTest {

        @Test
        @Tag("P1")
        @DisplayName("[QUERY-1] 배송 상태별 요약 조회 - 다양한 상태 카운트")
        void getSummary_MultipleStatuses_ReturnsCorrectCounts() {
            // Seed: 다양한 상태의 배송 데이터
            shipmentRepository.save(ShipmentJpaEntityFixtures.readyEntity("ready-001"));
            shipmentRepository.save(ShipmentJpaEntityFixtures.readyEntity("ready-002"));
            shipmentRepository.save(
                    ShipmentJpaEntityFixtures.entityWithStatus("shipped-001", "SHIPPED"));
            shipmentRepository.save(
                    ShipmentJpaEntityFixtures.entityWithStatus("delivered-001", "DELIVERED"));

            given().spec(givenSuperAdmin())
                    .when()
                    .get(SHIPMENT_SUMMARY)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue());
        }
    }

    @Nested
    @DisplayName("Q2: 배송 목록 조회")
    class ShipmentListTest {

        @Test
        @Tag("P1")
        @DisplayName("[QUERY-2] 배송 목록 페이징 조회")
        void getList_Paged_ReturnsCorrectly() {
            // Seed: 3건
            shipmentRepository.save(ShipmentJpaEntityFixtures.readyEntity("list-001"));
            shipmentRepository.save(ShipmentJpaEntityFixtures.readyEntity("list-002"));
            shipmentRepository.save(
                    ShipmentJpaEntityFixtures.entityWithStatus("list-003", "SHIPPED"));

            given().spec(givenSuperAdmin())
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get(SHIPMENTS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue());
        }

        @Test
        @DisplayName("[QUERY-3] 배송 목록 상태 필터링 조회")
        void getList_StatusFilter_FiltersCorrectly() {
            shipmentRepository.save(ShipmentJpaEntityFixtures.readyEntity("filter-001"));
            shipmentRepository.save(
                    ShipmentJpaEntityFixtures.entityWithStatus("filter-002", "SHIPPED"));
            shipmentRepository.save(
                    ShipmentJpaEntityFixtures.entityWithStatus("filter-003", "SHIPPED"));

            given().spec(givenSuperAdmin())
                    .queryParam("statuses", "SHIPPED")
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get(SHIPMENTS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue());
        }
    }

    @Nested
    @DisplayName("인증/인가 테스트")
    @Tag("auth")
    class AuthorizationTest {

        @Test
        @DisplayName("[AUTH-1] 비인증 요청으로 발주확인 시도 - 401")
        void confirmBatch_Unauthenticated_Returns401() {
            given().spec(givenUnauthenticated())
                    .body(Map.of("shipmentIds", List.of("any-id")))
                    .when()
                    .post(CONFIRM_BATCH)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }

        @Test
        @DisplayName("[AUTH-2] 비인증 요청으로 상세 조회 시도 - 401")
        void getDetail_Unauthenticated_Returns401() {
            given().spec(givenUnauthenticated())
                    .when()
                    .get(SHIPMENT_DETAIL, "any-id")
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }

        @Test
        @DisplayName("[AUTH-3] 권한 없는 사용자 발주확인 시도 - 403")
        void confirmBatch_NoPermission_Returns403() {
            given().spec(givenSellerUser("org-001", "product-group:write"))
                    .body(Map.of("shipmentIds", List.of("any-id")))
                    .when()
                    .post(CONFIRM_BATCH)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }
    }
}
