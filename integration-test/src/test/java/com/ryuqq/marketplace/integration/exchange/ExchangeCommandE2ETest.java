package com.ryuqq.marketplace.integration.exchange;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.ryuqq.marketplace.adapter.out.persistence.claimhistory.repository.ClaimHistoryJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.exchange.ExchangeClaimJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.exchange.repository.ExchangeClaimJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.OrderItemJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.OrderJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.refund.repository.RefundClaimJpaRepository;
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
 * 교환(Exchange) Command 엔드포인트 E2E 테스트.
 *
 * <p>테스트 대상:
 *
 * <ul>
 *   <li>POST /exchanges/request/batch - 교환 요청
 *   <li>POST /exchanges/approve/batch - 교환 승인 (수거 시작)
 *   <li>POST /exchanges/collect/batch - 수거 완료
 *   <li>POST /exchanges/prepare/batch - 준비 완료
 *   <li>POST /exchanges/reject/batch - 교환 거절
 *   <li>POST /exchanges/ship/batch - 재배송 출고
 *   <li>POST /exchanges/complete/batch - 교환 완료
 *   <li>POST /exchanges/convert-to-refund/batch - 교환 → 환불 전환
 *   <li>PATCH /exchanges/hold/batch - 보류/보류 해제
 *   <li>POST /exchanges/{exchangeClaimId}/histories - 이력 메모 추가
 * </ul>
 *
 * <p>P0 시나리오 우선 구현 (C01~C10 중 필수 케이스)
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("e2e")
@Tag("exchange")
@Tag("command")
@DisplayName("교환 Command API E2E 테스트")
class ExchangeCommandE2ETest extends E2ETestBase {

    private static final String BASE_URL = "/exchanges";
    private static final String REQUEST_BATCH_URL = "/exchanges/request/batch";
    private static final String APPROVE_BATCH_URL = "/exchanges/approve/batch";
    private static final String COLLECT_BATCH_URL = "/exchanges/collect/batch";
    private static final String PREPARE_BATCH_URL = "/exchanges/prepare/batch";
    private static final String REJECT_BATCH_URL = "/exchanges/reject/batch";
    private static final String SHIP_BATCH_URL = "/exchanges/ship/batch";
    private static final String COMPLETE_BATCH_URL = "/exchanges/complete/batch";
    private static final String CONVERT_TO_REFUND_BATCH_URL = "/exchanges/convert-to-refund/batch";
    private static final String HOLD_BATCH_URL = "/exchanges/hold/batch";

    @Autowired private ExchangeClaimJpaRepository exchangeClaimRepository;
    @Autowired private ClaimHistoryJpaRepository claimHistoryRepository;
    @Autowired private OrderItemJpaRepository orderItemRepository;
    @Autowired private OrderJpaRepository orderRepository;
    @Autowired private RefundClaimJpaRepository refundClaimRepository;

    @BeforeEach
    void setUp() {
        cleanUp();
    }

    @AfterEach
    void tearDown() {
        cleanUp();
    }

    private void cleanUp() {
        claimHistoryRepository.deleteAll();
        exchangeClaimRepository.deleteAll();
        refundClaimRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
    }

    // ===== 헬퍼 메서드 =====

    /**
     * 주문 + 주문상품을 DB에 저장하고 OrderItemId를 반환합니다.
     *
     * @param orderId 주문 ID
     * @return 저장된 OrderItem의 ID (UUID String)
     */
    private Long seedOrderItem(String orderId) {
        orderRepository.save(OrderJpaEntityFixtures.orderedEntity(orderId));
        var savedItem = orderItemRepository.save(OrderItemJpaEntityFixtures.confirmedItem(orderId));
        return savedItem.getId();
    }

    private Map<String, Object> createRequestExchangeBody(Long orderItemId) {
        Map<String, Object> item = new HashMap<>();
        item.put("orderId", orderItemId);
        item.put("exchangeQty", 1);
        item.put("reasonType", "SIZE_CHANGE");
        item.put("reasonDetail", "사이즈 교환 요청");
        item.put("originalProductId", 1000);
        item.put("originalSkuCode", "SKU-RED-M");
        item.put("targetProductGroupId", 1001);
        item.put("targetProductId", 2001);
        item.put("targetSkuCode", "SKU-RED-XL");
        item.put("targetQuantity", 1);

        return Map.of("items", List.of(item));
    }

    private Map<String, Object> createApproveBody(List<String> exchangeClaimIds) {
        return Map.of("exchangeClaimIds", exchangeClaimIds);
    }

    private Map<String, Object> createCollectBody(List<String> exchangeClaimIds) {
        return Map.of("exchangeClaimIds", exchangeClaimIds);
    }

    private Map<String, Object> createPrepareBody(List<String> exchangeClaimIds) {
        return Map.of("exchangeClaimIds", exchangeClaimIds);
    }

    private Map<String, Object> createRejectBody(List<String> exchangeClaimIds) {
        return Map.of("exchangeClaimIds", exchangeClaimIds);
    }

    private Map<String, Object> createCompleteBody(List<String> exchangeClaimIds) {
        return Map.of("exchangeClaimIds", exchangeClaimIds);
    }

    private Map<String, Object> createConvertToRefundBody(List<String> exchangeClaimIds) {
        return Map.of("exchangeClaimIds", exchangeClaimIds);
    }

    private Map<String, Object> createShipBody(
            String exchangeClaimId,
            String linkedOrderId,
            String deliveryCompany,
            String trackingNumber) {
        Map<String, Object> shipItem = new HashMap<>();
        shipItem.put("exchangeClaimId", exchangeClaimId);
        shipItem.put("linkedOrderId", linkedOrderId);
        shipItem.put("deliveryCompany", deliveryCompany);
        shipItem.put("trackingNumber", trackingNumber);
        return Map.of("items", List.of(shipItem));
    }

    private Map<String, Object> createHoldBody(
            List<String> exchangeClaimIds, boolean isHold, String memo) {
        Map<String, Object> body = new HashMap<>();
        body.put("exchangeClaimIds", exchangeClaimIds);
        body.put("isHold", isHold);
        if (memo != null) {
            body.put("memo", memo);
        }
        return body;
    }

    // ===== C01. POST /exchanges/request/batch - 교환 요청 =====

    @Nested
    @DisplayName("POST /exchanges/request/batch - 교환 요청")
    class RequestExchangeBatchTest {

        @Test
        @Tag("P0")
        @DisplayName("[C01-1] 유효한 요청으로 교환 생성 성공 - successCount=1, DB에 REQUESTED 상태 저장")
        void requestBatch_validRequest_returnsSuccessAndSavesEntity() {
            // given: ORDERED 상태 OrderItem 저장
            Long orderItemId = seedOrderItem("order-exc-001");

            Map<String, Object> requestBody = createRequestExchangeBody(orderItemId);

            // when
            Response response =
                    given().spec(givenSuperAdmin())
                            .body(requestBody)
                            .when()
                            .post(REQUEST_BATCH_URL);

            // then
            response.then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1))
                    .body("data.failureCount", equalTo(0));

            // DB 검증
            assertThat(exchangeClaimRepository.findAll()).hasSize(1);
            var saved = exchangeClaimRepository.findAll().get(0);
            assertThat(saved.getExchangeStatus()).isEqualTo("REQUESTED");
        }

        @Test
        @Tag("P0")
        @DisplayName("[C01-2] items 빈 목록 요청 - 400 반환")
        void requestBatch_emptyItems_returns400() {
            // given
            Map<String, Object> requestBody = Map.of("items", List.of());

            // when & then
            given().spec(givenSuperAdmin())
                    .body(requestBody)
                    .when()
                    .post(REQUEST_BATCH_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C01-3] orderId 누락 - 400 반환")
        void requestBatch_missingOrderId_returns400() {
            // given: orderId 누락된 요청
            Map<String, Object> item = new HashMap<>();
            item.put("exchangeQty", 1);
            item.put("reasonType", "SIZE_CHANGE");
            item.put("originalSkuCode", "SKU-RED-M");
            item.put("targetSkuCode", "SKU-RED-XL");
            item.put("targetQuantity", 1);
            Map<String, Object> requestBody = Map.of("items", List.of(item));

            // when & then
            given().spec(givenSuperAdmin())
                    .body(requestBody)
                    .when()
                    .post(REQUEST_BATCH_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C01-4] exchangeQty = 0 (비양수) - 400 반환 (@Positive 위반)")
        void requestBatch_zeroExchangeQty_returns400() {
            // given
            Long orderItemId = seedOrderItem("order-exc-qty-001");
            Map<String, Object> item = new HashMap<>();
            item.put("orderId", orderItemId);
            item.put("exchangeQty", 0);
            item.put("reasonType", "SIZE_CHANGE");
            item.put("originalSkuCode", "SKU-RED-M");
            item.put("targetSkuCode", "SKU-RED-XL");
            item.put("targetQuantity", 1);
            Map<String, Object> requestBody = Map.of("items", List.of(item));

            // when & then
            given().spec(givenSuperAdmin())
                    .body(requestBody)
                    .when()
                    .post(REQUEST_BATCH_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    // ===== C02. POST /exchanges/approve/batch - 교환 승인 =====

    @Nested
    @DisplayName("POST /exchanges/approve/batch - 교환 승인 (수거 시작)")
    class ApproveExchangeBatchTest {

        @Test
        @Tag("P0")
        @DisplayName("[C02-1] REQUESTED 상태 교환 승인 성공 - status가 COLLECTING으로 변경")
        void approveBatch_requestedStatus_statusBecomesCollecting() {
            // given: REQUESTED 상태 교환 건 + 연관 OrderItem
            Long orderItemId = seedOrderItem("order-approve-001");
            exchangeClaimRepository.save(
                    ExchangeClaimJpaEntityFixtures.requestedEntityWithOrderItemId(
                            "approve-001", orderItemId));

            Map<String, Object> requestBody = createApproveBody(List.of("approve-001"));

            // when
            Response response =
                    given().spec(givenSuperAdmin())
                            .body(requestBody)
                            .when()
                            .post(APPROVE_BATCH_URL);

            // then
            response.then().statusCode(HttpStatus.OK.value()).body("data.successCount", equalTo(1));

            // DB 검증
            var updated = exchangeClaimRepository.findById("approve-001").orElseThrow();
            assertThat(updated.getExchangeStatus()).isEqualTo("COLLECTING");
        }

        @Test
        @Tag("P0")
        @DisplayName("[C02-2] 이미 COLLECTING 상태 → 잘못된 상태 전이 실패 (배치 부분 실패)")
        void approveBatch_alreadyCollectingStatus_failsWithPartialFailure() {
            // given: COLLECTING 상태 교환 건
            exchangeClaimRepository.save(
                    ExchangeClaimJpaEntityFixtures.entityWithStatus(
                            "collecting-001", "COLLECTING"));

            Map<String, Object> requestBody = createApproveBody(List.of("collecting-001"));

            // when
            Response response =
                    given().spec(givenSuperAdmin())
                            .body(requestBody)
                            .when()
                            .post(APPROVE_BATCH_URL);

            // then: 배치 처리로 전체 실패 또는 부분 실패
            int statusCode = response.statusCode();
            assertThat(statusCode).isIn(HttpStatus.OK.value(), HttpStatus.BAD_REQUEST.value());

            if (statusCode == HttpStatus.OK.value()) {
                assertThat(response.jsonPath().getInt("data.failureCount"))
                        .isGreaterThanOrEqualTo(1);
            }

            // DB 검증: 상태가 변경되지 않아야 함
            var unchanged = exchangeClaimRepository.findById("collecting-001").orElseThrow();
            assertThat(unchanged.getExchangeStatus()).isEqualTo("COLLECTING");
        }

        @Test
        @Tag("P0")
        @DisplayName("[C02-3] 빈 목록 - 400 반환")
        void approveBatch_emptyIds_returns400() {
            // given
            Map<String, Object> requestBody = createApproveBody(List.of());

            // when & then
            given().spec(givenSuperAdmin())
                    .body(requestBody)
                    .when()
                    .post(APPROVE_BATCH_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    // ===== C03. POST /exchanges/collect/batch - 수거 완료 =====

    @Nested
    @DisplayName("POST /exchanges/collect/batch - 수거 완료")
    class CollectExchangeBatchTest {

        @Test
        @Tag("P0")
        @DisplayName("[C03-1] COLLECTING 상태 교환 수거 완료 성공 - status가 COLLECTED로 변경")
        void collectBatch_collectingStatus_statusBecomesCollected() {
            // given: COLLECTING 상태 교환 건 + 연관 OrderItem
            Long orderItemId = seedOrderItem("order-collect-001");
            exchangeClaimRepository.save(
                    ExchangeClaimJpaEntityFixtures.entityWithStatus("collect-001", "COLLECTING"));

            Map<String, Object> requestBody = createCollectBody(List.of("collect-001"));

            // when
            Response response =
                    given().spec(givenSuperAdmin())
                            .body(requestBody)
                            .when()
                            .post(COLLECT_BATCH_URL);

            // then
            response.then().statusCode(HttpStatus.OK.value()).body("data.successCount", equalTo(1));

            // DB 검증
            var updated = exchangeClaimRepository.findById("collect-001").orElseThrow();
            assertThat(updated.getExchangeStatus()).isEqualTo("COLLECTED");
        }

        @Test
        @Tag("P0")
        @DisplayName("[C03-2] REQUESTED 상태에서 수거 완료 시도 → 잘못된 전이 실패")
        void collectBatch_requestedStatus_failsInvalidTransition() {
            // given: REQUESTED 상태 교환 건
            exchangeClaimRepository.save(
                    ExchangeClaimJpaEntityFixtures.requestedEntity("collect-fail-001"));

            Map<String, Object> requestBody = createCollectBody(List.of("collect-fail-001"));

            // when
            Response response =
                    given().spec(givenSuperAdmin())
                            .body(requestBody)
                            .when()
                            .post(COLLECT_BATCH_URL);

            // then: 배치 부분 실패 또는 400
            int statusCode = response.statusCode();
            assertThat(statusCode).isIn(HttpStatus.OK.value(), HttpStatus.BAD_REQUEST.value());

            if (statusCode == HttpStatus.OK.value()) {
                assertThat(response.jsonPath().getInt("data.failureCount"))
                        .isGreaterThanOrEqualTo(1);
            }

            // DB 검증: 상태가 변경되지 않아야 함
            var unchanged = exchangeClaimRepository.findById("collect-fail-001").orElseThrow();
            assertThat(unchanged.getExchangeStatus()).isEqualTo("REQUESTED");
        }

        @Test
        @Tag("P0")
        @DisplayName("[C03-3] 빈 목록 - 400 반환")
        void collectBatch_emptyIds_returns400() {
            // given
            Map<String, Object> requestBody = createCollectBody(List.of());

            // when & then
            given().spec(givenSuperAdmin())
                    .body(requestBody)
                    .when()
                    .post(COLLECT_BATCH_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    // ===== C04. POST /exchanges/prepare/batch - 준비 완료 =====

    @Nested
    @DisplayName("POST /exchanges/prepare/batch - 준비 완료")
    class PrepareExchangeBatchTest {

        @Test
        @Tag("P0")
        @DisplayName("[C04-1] COLLECTED 상태 교환 준비 완료 성공 - status가 PREPARING으로 변경")
        void prepareBatch_collectedStatus_statusBecomePreparing() {
            // given: COLLECTED 상태 교환 건 + 연관 OrderItem
            seedOrderItem("order-prepare-001");
            exchangeClaimRepository.save(
                    ExchangeClaimJpaEntityFixtures.entityWithStatus("prepare-001", "COLLECTED"));

            Map<String, Object> requestBody = createPrepareBody(List.of("prepare-001"));

            // when
            Response response =
                    given().spec(givenSuperAdmin())
                            .body(requestBody)
                            .when()
                            .post(PREPARE_BATCH_URL);

            // then
            response.then().statusCode(HttpStatus.OK.value()).body("data.successCount", equalTo(1));

            // DB 검증
            var updated = exchangeClaimRepository.findById("prepare-001").orElseThrow();
            assertThat(updated.getExchangeStatus()).isEqualTo("PREPARING");
        }
    }

    // ===== C05. POST /exchanges/reject/batch - 교환 거절 =====

    @Nested
    @DisplayName("POST /exchanges/reject/batch - 교환 거절")
    class RejectExchangeBatchTest {

        @Test
        @Tag("P0")
        @DisplayName("[C05-1] REQUESTED 상태 교환 거절 성공 - status가 REJECTED로 변경")
        void rejectBatch_requestedStatus_statusBecomesRejected() {
            // given: REQUESTED 상태 교환 건 + 연관 OrderItem
            Long orderItemId = seedOrderItem("order-reject-001");
            exchangeClaimRepository.save(
                    ExchangeClaimJpaEntityFixtures.requestedEntityWithOrderItemId(
                            "reject-001", orderItemId));

            Map<String, Object> requestBody = createRejectBody(List.of("reject-001"));

            // when
            Response response =
                    given().spec(givenSuperAdmin()).body(requestBody).when().post(REJECT_BATCH_URL);

            // then
            response.then().statusCode(HttpStatus.OK.value()).body("data.successCount", equalTo(1));

            // DB 검증
            var updated = exchangeClaimRepository.findById("reject-001").orElseThrow();
            assertThat(updated.getExchangeStatus()).isEqualTo("REJECTED");
        }

        @Test
        @Tag("P0")
        @DisplayName("[C05-4] COMPLETED 상태 교환 거절 시도 → 잘못된 전이 실패")
        void rejectBatch_completedStatus_failsInvalidTransition() {
            // given: COMPLETED 상태 교환 건 (종료 상태)
            exchangeClaimRepository.save(
                    ExchangeClaimJpaEntityFixtures.entityWithStatus(
                            "reject-completed-001", "COMPLETED"));

            Map<String, Object> requestBody = createRejectBody(List.of("reject-completed-001"));

            // when
            Response response =
                    given().spec(givenSuperAdmin()).body(requestBody).when().post(REJECT_BATCH_URL);

            // then: 배치 부분 실패 또는 400
            int statusCode = response.statusCode();
            assertThat(statusCode).isIn(HttpStatus.OK.value(), HttpStatus.BAD_REQUEST.value());

            if (statusCode == HttpStatus.OK.value()) {
                assertThat(response.jsonPath().getInt("data.failureCount"))
                        .isGreaterThanOrEqualTo(1);
            }

            // DB 검증: 상태가 변경되지 않아야 함
            var unchanged = exchangeClaimRepository.findById("reject-completed-001").orElseThrow();
            assertThat(unchanged.getExchangeStatus()).isEqualTo("COMPLETED");
        }

        @Test
        @Tag("P0")
        @DisplayName("[C05-5] 빈 목록 - 400 반환")
        void rejectBatch_emptyIds_returns400() {
            // given
            Map<String, Object> requestBody = createRejectBody(List.of());

            // when & then
            given().spec(givenSuperAdmin())
                    .body(requestBody)
                    .when()
                    .post(REJECT_BATCH_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    // ===== C06. POST /exchanges/ship/batch - 재배송 출고 =====

    @Nested
    @DisplayName("POST /exchanges/ship/batch - 재배송 출고")
    class ShipExchangeBatchTest {

        @Test
        @Tag("P0")
        @DisplayName("[C06-1] PREPARING 상태 교환 재배송 출고 성공 - status가 SHIPPING으로 변경")
        void shipBatch_preparingStatus_statusBecomesShipping() {
            // given: PREPARING 상태 교환 건 + 연관 OrderItem
            seedOrderItem("order-ship-001");
            exchangeClaimRepository.save(
                    ExchangeClaimJpaEntityFixtures.entityWithStatus("ship-001", "PREPARING"));

            Map<String, Object> requestBody =
                    createShipBody("ship-001", "ORDER-20260319-0001", "CJ대한통운", "1234567890");

            // when
            Response response =
                    given().spec(givenSuperAdmin()).body(requestBody).when().post(SHIP_BATCH_URL);

            // then
            response.then().statusCode(HttpStatus.OK.value()).body("data.successCount", equalTo(1));

            // DB 검증
            var updated = exchangeClaimRepository.findById("ship-001").orElseThrow();
            assertThat(updated.getExchangeStatus()).isEqualTo("SHIPPING");
            assertThat(updated.getLinkedOrderId()).isEqualTo("ORDER-20260319-0001");
        }

        @Test
        @Tag("P0")
        @DisplayName("[C06-2] items 빈 목록 - 400 반환")
        void shipBatch_emptyItems_returns400() {
            // given
            Map<String, Object> requestBody = Map.of("items", List.of());

            // when & then
            given().spec(givenSuperAdmin())
                    .body(requestBody)
                    .when()
                    .post(SHIP_BATCH_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    // ===== C07. POST /exchanges/complete/batch - 교환 완료 =====

    @Nested
    @DisplayName("POST /exchanges/complete/batch - 교환 완료")
    class CompleteExchangeBatchTest {

        @Test
        @Tag("P0")
        @DisplayName("[C07-1] SHIPPING 상태 교환 완료 성공 - status가 COMPLETED로 변경, completedAt 설정")
        void completeBatch_shippingStatus_statusBecomesCompleted() {
            // given: SHIPPING 상태 교환 건 + 연관 OrderItem
            seedOrderItem("order-complete-001");
            exchangeClaimRepository.save(
                    ExchangeClaimJpaEntityFixtures.entityWithStatus("complete-001", "SHIPPING"));

            Map<String, Object> requestBody = createCompleteBody(List.of("complete-001"));

            // when
            Response response =
                    given().spec(givenSuperAdmin())
                            .body(requestBody)
                            .when()
                            .post(COMPLETE_BATCH_URL);

            // then
            response.then().statusCode(HttpStatus.OK.value()).body("data.successCount", equalTo(1));

            // DB 검증
            var updated = exchangeClaimRepository.findById("complete-001").orElseThrow();
            assertThat(updated.getExchangeStatus()).isEqualTo("COMPLETED");
            assertThat(updated.getCompletedAt()).isNotNull();
        }

        @Test
        @Tag("P0")
        @DisplayName("[C07-2] PREPARING 상태에서 완료 시도 → 잘못된 전이 실패")
        void completeBatch_preparingStatus_failsInvalidTransition() {
            // given: PREPARING 상태 교환 건
            exchangeClaimRepository.save(
                    ExchangeClaimJpaEntityFixtures.entityWithStatus(
                            "complete-fail-001", "PREPARING"));

            Map<String, Object> requestBody = createCompleteBody(List.of("complete-fail-001"));

            // when
            Response response =
                    given().spec(givenSuperAdmin())
                            .body(requestBody)
                            .when()
                            .post(COMPLETE_BATCH_URL);

            // then: 배치 부분 실패 또는 400
            int statusCode = response.statusCode();
            assertThat(statusCode).isIn(HttpStatus.OK.value(), HttpStatus.BAD_REQUEST.value());

            if (statusCode == HttpStatus.OK.value()) {
                assertThat(response.jsonPath().getInt("data.failureCount"))
                        .isGreaterThanOrEqualTo(1);
            }

            // DB 검증: 상태가 변경되지 않아야 함
            var unchanged = exchangeClaimRepository.findById("complete-fail-001").orElseThrow();
            assertThat(unchanged.getExchangeStatus()).isEqualTo("PREPARING");
        }

        @Test
        @Tag("P0")
        @DisplayName("[C07-3] 빈 목록 - 400 반환")
        void completeBatch_emptyIds_returns400() {
            // given
            Map<String, Object> requestBody = createCompleteBody(List.of());

            // when & then
            given().spec(givenSuperAdmin())
                    .body(requestBody)
                    .when()
                    .post(COMPLETE_BATCH_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    // ===== C08. POST /exchanges/convert-to-refund/batch - 교환 → 환불 전환 =====

    @Nested
    @DisplayName("POST /exchanges/convert-to-refund/batch - 교환 → 환불 전환")
    class ConvertToRefundBatchTest {

        @Test
        @Tag("P0")
        @DisplayName("[C08-1] REQUESTED 상태 교환 건 환불 전환 성공 - 교환 취소 + 환불 건 생성")
        void convertToRefundBatch_requestedStatus_exchangeCancelledAndRefundCreated() {
            // given: REQUESTED 상태 교환 건 + 연관 OrderItem
            Long orderItemId = seedOrderItem("order-cvt-001");
            exchangeClaimRepository.save(
                    ExchangeClaimJpaEntityFixtures.requestedEntityWithOrderItemId(
                            "cvt-001", orderItemId));

            Map<String, Object> requestBody = createConvertToRefundBody(List.of("cvt-001"));

            // when
            Response response =
                    given().spec(givenSuperAdmin())
                            .body(requestBody)
                            .when()
                            .post(CONVERT_TO_REFUND_BATCH_URL);

            // then
            response.then().statusCode(HttpStatus.OK.value()).body("data.successCount", equalTo(1));

            // DB 검증: 교환 건 취소 상태 확인
            var updated = exchangeClaimRepository.findById("cvt-001").orElseThrow();
            assertThat(updated.getExchangeStatus()).isEqualTo("CANCELLED");

            // DB 검증: 환불 건 신규 생성 확인
            assertThat(refundClaimRepository.findAll()).isNotEmpty();
        }

        @Test
        @Tag("P0")
        @DisplayName("[C08-3] 빈 목록 - 400 반환")
        void convertToRefundBatch_emptyIds_returns400() {
            // given
            Map<String, Object> requestBody = createConvertToRefundBody(List.of());

            // when & then
            given().spec(givenSuperAdmin())
                    .body(requestBody)
                    .when()
                    .post(CONVERT_TO_REFUND_BATCH_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    // ===== C09. PATCH /exchanges/hold/batch - 보류/보류 해제 =====

    @Nested
    @DisplayName("PATCH /exchanges/hold/batch - 보류/보류 해제")
    class HoldExchangeBatchTest {

        @Test
        @Tag("P0")
        @DisplayName("[C09-1] 교환 건 보류 설정 성공 - holdReason, holdAt 설정됨")
        void holdBatch_setHold_holdInfoSaved() {
            // given
            exchangeClaimRepository.save(
                    ExchangeClaimJpaEntityFixtures.requestedEntity("hold-001"));

            Map<String, Object> requestBody = createHoldBody(List.of("hold-001"), true, "CS 확인 필요");

            // when
            given().spec(givenSuperAdmin())
                    .body(requestBody)
                    .when()
                    .patch(HOLD_BATCH_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // DB 검증
            var updated = exchangeClaimRepository.findById("hold-001").orElseThrow();
            assertThat(updated.getHoldReason()).isEqualTo("CS 확인 필요");
            assertThat(updated.getHoldAt()).isNotNull();
        }

        @Test
        @Tag("P0")
        @DisplayName("[C09-2] 보류 해제 성공 - holdReason, holdAt이 null로 초기화됨")
        void holdBatch_releaseHold_holdInfoCleared() {
            // given: 먼저 보류 설정 후 해제 테스트
            exchangeClaimRepository.save(
                    ExchangeClaimJpaEntityFixtures.requestedEntity("unhold-001"));

            // Step 1: 보류 설정
            Map<String, Object> holdBody = createHoldBody(List.of("unhold-001"), true, "검토 필요");
            given().spec(givenSuperAdmin())
                    .body(holdBody)
                    .when()
                    .patch(HOLD_BATCH_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // 보류 설정 확인
            var held = exchangeClaimRepository.findById("unhold-001").orElseThrow();
            assertThat(held.getHoldAt()).isNotNull();

            // Step 2: 보류 해제
            Map<String, Object> releaseBody = createHoldBody(List.of("unhold-001"), false, null);
            given().spec(givenSuperAdmin())
                    .body(releaseBody)
                    .when()
                    .patch(HOLD_BATCH_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // DB 검증
            var released = exchangeClaimRepository.findById("unhold-001").orElseThrow();
            assertThat(released.getHoldReason()).isNull();
            assertThat(released.getHoldAt()).isNull();
        }

        @Test
        @Tag("P0")
        @DisplayName("[C09-5] 빈 목록 - 400 반환")
        void holdBatch_emptyIds_returns400() {
            // given
            Map<String, Object> requestBody = createHoldBody(List.of(), true, "메모");

            // when & then
            given().spec(givenSuperAdmin())
                    .body(requestBody)
                    .when()
                    .patch(HOLD_BATCH_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    // ===== C10. POST /exchanges/{exchangeClaimId}/histories - 이력 메모 추가 =====

    @Nested
    @DisplayName("POST /exchanges/{exchangeClaimId}/histories - 이력 메모 추가")
    class AddHistoryMemoTest {

        @Test
        @Tag("P0")
        @DisplayName("[C10-1] 존재하는 교환 건에 메모 추가 성공 - 이력 DB 저장 확인")
        void addMemo_existingExchange_historyCreated() {
            // given
            exchangeClaimRepository.save(
                    ExchangeClaimJpaEntityFixtures.requestedEntity("history-001"));

            Map<String, Object> requestBody = Map.of("message", "CS 팀 확인 완료. 교환 처리 진행.");

            // when
            given().spec(givenSuperAdmin())
                    .body(requestBody)
                    .when()
                    .post(BASE_URL + "/{exchangeClaimId}/histories", "history-001")
                    .then()
                    .statusCode(HttpStatus.CREATED.value());

            // DB 검증
            var histories = claimHistoryRepository.findAll();
            assertThat(histories).isNotEmpty();
            var history =
                    histories.stream()
                            .filter(h -> "history-001".equals(h.getClaimId()))
                            .findFirst()
                            .orElseThrow();
            assertThat(history.getClaimType()).isEqualTo("EXCHANGE");
        }

        @Test
        @Tag("P0")
        @DisplayName("[C10-2] 존재하지 않는 교환 건에 메모 추가 시 404가 반환된다")
        void addMemo_nonExistentExchange_returns404() {
            // when & then
            Map<String, Object> requestBody = Map.of("message", "존재하지 않는 교환 건 메모");

            given().spec(givenSuperAdmin())
                    .body(requestBody)
                    .when()
                    .post(BASE_URL + "/{exchangeClaimId}/histories", "non-existent-id")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }
    }
}
