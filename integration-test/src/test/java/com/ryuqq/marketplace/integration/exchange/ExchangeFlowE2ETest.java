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
 * 교환(Exchange) 전체 플로우 E2E 테스트.
 *
 * <p>테스트 대상 플로우:
 *
 * <ul>
 *   <li>FLOW-1: 교환 요청 → 승인 → 수거 완료 → 준비 → 출고 → 완료 (해피패스 전체)
 *   <li>FLOW-2: 교환 요청 → 거절
 *   <li>FLOW-3: 교환 → 환불 전환 (ConvertToRefund)
 *   <li>FLOW-4: 보류 토글 (보류 설정 → 확인 → 해제)
 * </ul>
 *
 * <p>P0 시나리오 우선 구현
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("e2e")
@Tag("exchange")
@Tag("flow")
@DisplayName("교환 전체 플로우 E2E 테스트")
class ExchangeFlowE2ETest extends E2ETestBase {

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

    private Long seedOrderItem(String orderId) {
        orderRepository.save(OrderJpaEntityFixtures.orderedEntity(orderId));
        var savedItem = orderItemRepository.save(OrderItemJpaEntityFixtures.defaultItem(orderId));
        return savedItem.getId();
    }

    private String requestExchange(Long orderItemId) {
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
        Map<String, Object> requestBody = Map.of("items", List.of(item));

        Response response =
                given().spec(givenSuperAdmin()).body(requestBody).when().post(REQUEST_BATCH_URL);

        response.then().statusCode(HttpStatus.OK.value()).body("data.successCount", equalTo(1));

        // 생성된 교환 건 ID 조회 (DB에서 가장 최근 건)
        var saved = exchangeClaimRepository.findAll();
        assertThat(saved).isNotEmpty();
        return saved.get(0).getId();
    }

    private void approveBatch(String exchangeClaimId) {
        given().spec(givenSuperAdmin())
                .body(Map.of("exchangeClaimIds", List.of(exchangeClaimId)))
                .when()
                .post(APPROVE_BATCH_URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("data.successCount", equalTo(1));
    }

    private void collectBatch(String exchangeClaimId) {
        given().spec(givenSuperAdmin())
                .body(Map.of("exchangeClaimIds", List.of(exchangeClaimId)))
                .when()
                .post(COLLECT_BATCH_URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("data.successCount", equalTo(1));
    }

    private void prepareBatch(String exchangeClaimId) {
        given().spec(givenSuperAdmin())
                .body(Map.of("exchangeClaimIds", List.of(exchangeClaimId)))
                .when()
                .post(PREPARE_BATCH_URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("data.successCount", equalTo(1));
    }

    private void rejectBatch(String exchangeClaimId) {
        given().spec(givenSuperAdmin())
                .body(Map.of("exchangeClaimIds", List.of(exchangeClaimId)))
                .when()
                .post(REJECT_BATCH_URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("data.successCount", equalTo(1));
    }

    private void shipBatch(String exchangeClaimId, String linkedOrderId) {
        Map<String, Object> shipItem = new HashMap<>();
        shipItem.put("exchangeClaimId", exchangeClaimId);
        shipItem.put("linkedOrderId", linkedOrderId);
        shipItem.put("deliveryCompany", "CJ대한통운");
        shipItem.put("trackingNumber", "TRK-FLOW-001");

        given().spec(givenSuperAdmin())
                .body(Map.of("items", List.of(shipItem)))
                .when()
                .post(SHIP_BATCH_URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("data.successCount", equalTo(1));
    }

    private void completeBatch(String exchangeClaimId) {
        given().spec(givenSuperAdmin())
                .body(Map.of("exchangeClaimIds", List.of(exchangeClaimId)))
                .when()
                .post(COMPLETE_BATCH_URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("data.successCount", equalTo(1));
    }

    private String getExchangeStatus(String exchangeClaimId) {
        return given().spec(givenSuperAdmin())
                .when()
                .get(BASE_URL + "/{exchangeClaimId}", exchangeClaimId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .jsonPath()
                .getString("data.claimInfo.status");
    }

    // ===== 전체 플로우 시나리오 =====

    @Nested
    @DisplayName("전체 플로우 시나리오")
    class FullFlowTest {

        @Test
        @Tag("P0")
        @DisplayName(
                "[FLOW-1] 교환 해피패스 전체 - REQUESTED→COLLECTING→COLLECTED→PREPARING→SHIPPING→COMPLETED")
        void flow1_happyPath_requestToCompleted() {
            // given: ORDERED 상태 OrderItem 1건
            Long orderItemId = seedOrderItem("order-flow1-001");

            // Step 1: 교환 요청
            String exchangeClaimId = requestExchange(orderItemId);

            var afterRequest = exchangeClaimRepository.findById(exchangeClaimId).orElseThrow();
            assertThat(afterRequest.getExchangeStatus()).isEqualTo("REQUESTED");

            // Step 2: 승인 (REQUESTED → COLLECTING)
            approveBatch(exchangeClaimId);

            var afterApprove = exchangeClaimRepository.findById(exchangeClaimId).orElseThrow();
            assertThat(afterApprove.getExchangeStatus()).isEqualTo("COLLECTING");

            // Step 3: 수거 완료 (COLLECTING → COLLECTED)
            collectBatch(exchangeClaimId);

            var afterCollect = exchangeClaimRepository.findById(exchangeClaimId).orElseThrow();
            assertThat(afterCollect.getExchangeStatus()).isEqualTo("COLLECTED");

            // Step 4: 준비 완료 (COLLECTED → PREPARING)
            prepareBatch(exchangeClaimId);

            var afterPrepare = exchangeClaimRepository.findById(exchangeClaimId).orElseThrow();
            assertThat(afterPrepare.getExchangeStatus()).isEqualTo("PREPARING");

            // Step 5: 재배송 출고 (PREPARING → SHIPPING)
            shipBatch(exchangeClaimId, "ORDER-20260319-FLOW1");

            var afterShip = exchangeClaimRepository.findById(exchangeClaimId).orElseThrow();
            assertThat(afterShip.getExchangeStatus()).isEqualTo("SHIPPING");
            assertThat(afterShip.getLinkedOrderId()).isEqualTo("ORDER-20260319-FLOW1");

            // Step 6: 교환 완료 (SHIPPING → COMPLETED)
            completeBatch(exchangeClaimId);

            var afterComplete = exchangeClaimRepository.findById(exchangeClaimId).orElseThrow();
            assertThat(afterComplete.getExchangeStatus()).isEqualTo("COMPLETED");
            assertThat(afterComplete.getCompletedAt()).isNotNull();

            // Step 7: 상세 조회 - 최종 상태 API 응답 검증
            given().spec(givenSuperAdmin())
                    .when()
                    .get(BASE_URL + "/{exchangeClaimId}", exchangeClaimId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.claimInfo.status", equalTo("COMPLETED"))
                    .body("data.claimInfo.linkedOrderId", not(emptyOrNullString()));
        }

        @Test
        @Tag("P0")
        @DisplayName("[FLOW-2] 교환 요청 → 거절 - REQUESTED → REJECTED")
        void flow2_requestToRejected() {
            // given: ORDERED 상태 OrderItem 1건
            Long orderItemId = seedOrderItem("order-flow2-001");

            // Step 1: 교환 요청
            String exchangeClaimId = requestExchange(orderItemId);

            // Step 2: 거절 (REQUESTED → REJECTED)
            rejectBatch(exchangeClaimId);

            var afterReject = exchangeClaimRepository.findById(exchangeClaimId).orElseThrow();
            assertThat(afterReject.getExchangeStatus()).isEqualTo("REJECTED");

            // Step 3: 상세 조회 - REJECTED 상태 확인
            given().spec(givenSuperAdmin())
                    .when()
                    .get(BASE_URL + "/{exchangeClaimId}", exchangeClaimId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.claimInfo.status", equalTo("REJECTED"));
        }

        @Test
        @Tag("P0")
        @DisplayName("[FLOW-3] 교환 → 환불 전환 - ConvertToRefund 플로우")
        void flow3_exchangeToRefundConversion() {
            // given: ORDERED 상태 OrderItem 1건
            Long orderItemId = seedOrderItem("order-flow3-001");

            // Step 1: 교환 요청
            String exchangeClaimId = requestExchange(orderItemId);

            // Step 2: 교환 → 환불 전환
            given().spec(givenSuperAdmin())
                    .body(Map.of("exchangeClaimIds", List.of(exchangeClaimId)))
                    .when()
                    .post(CONVERT_TO_REFUND_BATCH_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));

            // Step 3: 교환 건 상태 확인 (CANCELLED)
            var afterConvert = exchangeClaimRepository.findById(exchangeClaimId).orElseThrow();
            assertThat(afterConvert.getExchangeStatus()).isEqualTo("CANCELLED");

            // Step 4: API 조회 검증
            given().spec(givenSuperAdmin())
                    .when()
                    .get(BASE_URL + "/{exchangeClaimId}", exchangeClaimId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.claimInfo.status", equalTo("CANCELLED"));

            // Step 5: 환불 건 신규 생성 확인
            assertThat(refundClaimRepository.findAll()).isNotEmpty();
        }

        @Test
        @Tag("P0")
        @DisplayName("[FLOW-4] 보류 토글 - 보류 설정 → 확인 → 보류 해제")
        void flow4_holdToggle_setAndRelease() {
            // given
            exchangeClaimRepository.save(
                    ExchangeClaimJpaEntityFixtures.requestedEntity("hold-flow-001"));

            // Step 1: 보류 설정
            given().spec(givenSuperAdmin())
                    .body(
                            Map.of(
                                    "exchangeClaimIds",
                                    List.of("hold-flow-001"),
                                    "isHold",
                                    true,
                                    "memo",
                                    "검토 필요"))
                    .when()
                    .patch(HOLD_BATCH_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // DB 검증: holdReason, holdAt 설정 확인
            var afterHold = exchangeClaimRepository.findById("hold-flow-001").orElseThrow();
            assertThat(afterHold.getHoldReason()).isEqualTo("검토 필요");
            assertThat(afterHold.getHoldAt()).isNotNull();

            // Step 2: 상세 조회 - 보류 중 상태 확인
            given().spec(givenSuperAdmin())
                    .when()
                    .get(BASE_URL + "/{exchangeClaimId}", "hold-flow-001")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue());

            // Step 3: 보류 해제
            given().spec(givenSuperAdmin())
                    .body(Map.of("exchangeClaimIds", List.of("hold-flow-001"), "isHold", false))
                    .when()
                    .patch(HOLD_BATCH_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // DB 검증: holdReason, holdAt null 초기화 확인
            var afterRelease = exchangeClaimRepository.findById("hold-flow-001").orElseThrow();
            assertThat(afterRelease.getHoldReason()).isNull();
            assertThat(afterRelease.getHoldAt()).isNull();

            // Step 4: 상세 조회 - 보류 해제 확인
            given().spec(givenSuperAdmin())
                    .when()
                    .get(BASE_URL + "/{exchangeClaimId}", "hold-flow-001")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue());
        }
    }

    // ===== 인증/인가 시나리오 =====

    @Nested
    @DisplayName("인증/인가 시나리오")
    class AuthScenarioTest {

        @Test
        @Tag("P1")
        @DisplayName("[AUTH-1] 비인증 요청으로 Command API 호출 - 401 반환")
        void unauthenticated_commandApi_returns401() {
            // when & then
            given().spec(givenUnauthenticated())
                    .body(Map.of("exchangeClaimIds", List.of("some-id")))
                    .when()
                    .post(APPROVE_BATCH_URL)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[AUTH-2] 비인증 요청으로 Query API 호출 - 401 반환")
        void unauthenticated_queryApi_returns401() {
            // when & then
            given().spec(givenUnauthenticated())
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }
    }
}
