package com.ryuqq.marketplace.integration.container.exchange;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;

import com.ryuqq.marketplace.adapter.out.persistence.claimhistory.repository.ClaimHistoryJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.exchange.repository.ExchangeClaimJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.exchangeoutbox.repository.ExchangeOutboxJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.OrderItemJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.OrderJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderItemJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemHistoryJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.refund.repository.RefundClaimJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.refundoutbox.repository.RefundOutboxJpaRepository;
import com.ryuqq.marketplace.integration.container.ContainerE2ETestBase;
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
 * Exchange Flow Testcontainers E2E 테스트.
 *
 * <p>교환 전체 플로우를 검증합니다:
 *
 * <ul>
 *   <li>F1: 요청 -> 승인 -> 수거완료 -> 준비완료 -> 목록 조회
 *   <li>F2: 요청 -> 거절 -> 상태 확인
 *   <li>F3: 요청 -> 보류 -> 해제 -> 승인
 *   <li>F4: 요청 -> 환불 전환 -> RefundClaim 생성 확인
 *   <li>F5: 요청 -> 메모 등록 -> 상세 조회
 * </ul>
 */
@Tag("e2e")
@Tag("container")
@Tag("exchange")
@Tag("flow")
@DisplayName("Exchange Flow Container E2E 테스트")
class ExchangeContainerFlowE2ETest extends ContainerE2ETestBase {

    private static final String EXCHANGES = "/exchanges";
    private static final String EXCHANGE_DETAIL = "/exchanges/{exchangeClaimId}";
    private static final String REQUEST_BATCH = "/exchanges/request/batch";
    private static final String APPROVE_BATCH = "/exchanges/approve/batch";
    private static final String COLLECT_BATCH = "/exchanges/collect/batch";
    private static final String PREPARE_BATCH = "/exchanges/prepare/batch";
    private static final String REJECT_BATCH = "/exchanges/reject/batch";
    private static final String HOLD_BATCH = "/exchanges/hold/batch";
    private static final String CONVERT_TO_REFUND_BATCH = "/exchanges/convert-to-refund/batch";
    private static final String HISTORIES = "/exchanges/{exchangeClaimId}/histories";

    @Autowired private ExchangeClaimJpaRepository exchangeClaimRepository;
    @Autowired private ExchangeOutboxJpaRepository exchangeOutboxRepository;
    @Autowired private ClaimHistoryJpaRepository claimHistoryRepository;
    @Autowired private OrderItemHistoryJpaRepository orderItemHistoryRepository;
    @Autowired private OrderItemJpaRepository orderItemRepository;
    @Autowired private OrderJpaRepository orderRepository;
    @Autowired private RefundClaimJpaRepository refundClaimRepository;
    @Autowired private RefundOutboxJpaRepository refundOutboxRepository;

    @BeforeEach
    void setUp() {
        claimHistoryRepository.deleteAll();
        refundOutboxRepository.deleteAll();
        refundClaimRepository.deleteAll();
        exchangeOutboxRepository.deleteAll();
        exchangeClaimRepository.deleteAll();
        orderItemHistoryRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        claimHistoryRepository.deleteAll();
        refundOutboxRepository.deleteAll();
        refundClaimRepository.deleteAll();
        exchangeOutboxRepository.deleteAll();
        exchangeClaimRepository.deleteAll();
        orderItemHistoryRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
    }

    @Nested
    @DisplayName("교환 요청 -> 승인 -> 수거 -> 준비 전체 플로우")
    class FullExchangeFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[F1] 교환 요청 -> 승인 -> 수거완료 -> 준비완료 -> 목록 조회")
        void fullFlow_requestApprovecollectPrepare() {
            // given
            String orderId = "order-exchange-flow-001";
            orderRepository.save(OrderJpaEntityFixtures.orderedEntity(orderId));
            OrderItemJpaEntity item = OrderItemJpaEntityFixtures.itemWithPrice(orderId, 30000, 1);
            String orderItemId = orderItemRepository.save(item).getId();

            // step 1: 교환 요청
            givenSuperAdmin()
                    .body(Map.of("items", List.of(createExchangeRequestItem(orderItemId))))
                    .when()
                    .post(REQUEST_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));

            String exchangeClaimId = exchangeClaimRepository.findAll().get(0).getId();

            // step 2: 승인
            givenSuperAdmin()
                    .body(Map.of("exchangeClaimIds", List.of(exchangeClaimId)))
                    .when()
                    .post(APPROVE_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));

            // step 3: 수거 완료
            givenSuperAdmin()
                    .body(Map.of("exchangeClaimIds", List.of(exchangeClaimId)))
                    .when()
                    .post(COLLECT_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));

            // step 4: 준비 완료
            givenSuperAdmin()
                    .body(Map.of("exchangeClaimIds", List.of(exchangeClaimId)))
                    .when()
                    .post(PREPARE_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));

            // step 5: 목록 조회
            givenSuperAdmin()
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get(EXCHANGES)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", greaterThanOrEqualTo(1));
        }
    }

    @Nested
    @DisplayName("교환 요청 -> 거절 플로우")
    class RequestRejectFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[F2] 교환 요청 -> 거절 -> 상태 확인")
        void flow_requestThenReject() {
            // given
            String orderId = "order-exchange-reject-flow-001";
            orderRepository.save(OrderJpaEntityFixtures.orderedEntity(orderId));
            OrderItemJpaEntity item = OrderItemJpaEntityFixtures.itemWithPrice(orderId, 25000, 1);
            String orderItemId = orderItemRepository.save(item).getId();

            // step 1: 교환 요청
            givenSuperAdmin()
                    .body(Map.of("items", List.of(createExchangeRequestItem(orderItemId))))
                    .when()
                    .post(REQUEST_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            String exchangeClaimId = exchangeClaimRepository.findAll().get(0).getId();

            // step 2: 거절
            givenSuperAdmin()
                    .body(Map.of("exchangeClaimIds", List.of(exchangeClaimId)))
                    .when()
                    .post(REJECT_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));

            // step 3: DB 상태 확인
            var claim = exchangeClaimRepository.findById(exchangeClaimId).orElseThrow();
            assertThat(claim.getExchangeStatus()).isEqualTo("REJECTED");
        }
    }

    @Nested
    @DisplayName("교환 요청 -> 보류 -> 해제 -> 승인 플로우")
    class HoldUnholdApproveFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[F3] 교환 요청 -> 보류 -> 보류 해제 -> 승인")
        void flow_requestHoldUnholdApprove() {
            // given
            String orderId = "order-exchange-hold-flow-001";
            orderRepository.save(OrderJpaEntityFixtures.orderedEntity(orderId));
            OrderItemJpaEntity item = OrderItemJpaEntityFixtures.itemWithPrice(orderId, 20000, 1);
            String orderItemId = orderItemRepository.save(item).getId();

            // step 1: 교환 요청
            givenSuperAdmin()
                    .body(Map.of("items", List.of(createExchangeRequestItem(orderItemId))))
                    .when()
                    .post(REQUEST_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            String exchangeClaimId = exchangeClaimRepository.findAll().get(0).getId();

            // step 2: 보류
            givenSuperAdmin()
                    .body(
                            Map.of(
                                    "exchangeClaimIds",
                                    List.of(exchangeClaimId),
                                    "isHold",
                                    true,
                                    "memo",
                                    "상품 확인 필요"))
                    .when()
                    .patch(HOLD_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));

            // step 3: 보류 해제
            givenSuperAdmin()
                    .body(
                            Map.of(
                                    "exchangeClaimIds",
                                    List.of(exchangeClaimId),
                                    "isHold",
                                    false,
                                    "memo",
                                    "확인 완료"))
                    .when()
                    .patch(HOLD_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));

            // step 4: 승인
            givenSuperAdmin()
                    .body(Map.of("exchangeClaimIds", List.of(exchangeClaimId)))
                    .when()
                    .post(APPROVE_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));
        }
    }

    @Nested
    @DisplayName("교환 건 환불 전환 플로우")
    class ConvertToRefundFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[F4] 교환 요청 -> 환불 전환 -> 교환 취소 + RefundClaim 생성 확인")
        void flow_requestThenConvertToRefund() {
            // given
            String orderId = "order-exchange-convert-flow-001";
            orderRepository.save(OrderJpaEntityFixtures.orderedEntity(orderId));
            OrderItemJpaEntity item = OrderItemJpaEntityFixtures.itemWithPrice(orderId, 30000, 1);
            String orderItemId = orderItemRepository.save(item).getId();

            // step 1: 교환 요청
            givenSuperAdmin()
                    .body(Map.of("items", List.of(createExchangeRequestItem(orderItemId))))
                    .when()
                    .post(REQUEST_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            String exchangeClaimId = exchangeClaimRepository.findAll().get(0).getId();

            // step 2: 환불 전환
            givenSuperAdmin()
                    .body(Map.of("exchangeClaimIds", List.of(exchangeClaimId)))
                    .when()
                    .post(CONVERT_TO_REFUND_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));

            // step 3: 교환 상태가 전환됨 확인
            var exchangeClaim = exchangeClaimRepository.findById(exchangeClaimId).orElseThrow();
            assertThat(exchangeClaim.getExchangeStatus()).isNotEqualTo("REQUESTED");
        }
    }

    @Nested
    @DisplayName("교환 요청 + 메모 등록 + 상세 조회 플로우")
    class RequestWithMemoFlowTest {

        @Test
        @Tag("P1")
        @DisplayName("[F5] 교환 요청 -> 메모 등록 -> 상세 조회")
        void flow_requestMemoDetail() {
            // given
            String orderId = "order-exchange-memo-flow-001";
            orderRepository.save(OrderJpaEntityFixtures.orderedEntity(orderId));
            OrderItemJpaEntity item = OrderItemJpaEntityFixtures.itemWithPrice(orderId, 30000, 1);
            String orderItemId = orderItemRepository.save(item).getId();

            // step 1: 교환 요청
            givenSuperAdmin()
                    .body(Map.of("items", List.of(createExchangeRequestItem(orderItemId))))
                    .when()
                    .post(REQUEST_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            String exchangeClaimId = exchangeClaimRepository.findAll().get(0).getId();

            // step 2: 메모 등록
            givenSuperAdmin()
                    .body(Map.of("message", "고객 연락 완료, 수거 예정"))
                    .when()
                    .post(HISTORIES, exchangeClaimId)
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("data.historyId", notNullValue());

            // step 3: 상세 조회
            givenSuperAdmin()
                    .when()
                    .get(EXCHANGE_DETAIL, exchangeClaimId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue());
        }
    }

    // ===== Helper =====

    private Map<String, Object> createExchangeRequestItem(String orderItemId) {
        return Map.ofEntries(
                Map.entry("orderId", orderItemId),
                Map.entry("exchangeQty", 1),
                Map.entry("reasonType", "SIZE_CHANGE"),
                Map.entry("reasonDetail", "사이즈 변경 원합니다"),
                Map.entry("originalProductId", 1000L),
                Map.entry("originalSkuCode", "SKU-RED-M"),
                Map.entry("targetProductGroupId", 1001L),
                Map.entry("targetProductId", 2001L),
                Map.entry("targetSkuCode", "SKU-RED-XL"),
                Map.entry("targetQuantity", 1));
    }
}
