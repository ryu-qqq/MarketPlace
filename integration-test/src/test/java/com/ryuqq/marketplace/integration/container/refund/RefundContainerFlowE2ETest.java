package com.ryuqq.marketplace.integration.container.refund;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;

import com.ryuqq.marketplace.adapter.out.persistence.claimhistory.repository.ClaimHistoryJpaRepository;
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
 * Refund Flow Testcontainers E2E 테스트.
 *
 * <p>환불 전체 플로우를 검증합니다:
 *
 * <ul>
 *   <li>F1: 요청 -> 승인 -> 목록 조회 -> 상세 조회
 *   <li>F2: 요청 -> 거절 -> 상태 확인
 *   <li>F3: 요청 -> 보류 -> 보류 해제 -> 승인
 *   <li>F4: 부분환불 + 추가환불 전체 플로우
 * </ul>
 */
@Tag("e2e")
@Tag("container")
@Tag("refund")
@Tag("flow")
@DisplayName("Refund Flow Container E2E 테스트")
class RefundContainerFlowE2ETest extends ContainerE2ETestBase {

    private static final String REFUNDS = "/refunds";
    private static final String REFUND_DETAIL = "/refunds/{refundClaimId}";
    private static final String REQUEST_BATCH = "/refunds/request/batch";
    private static final String APPROVE_BATCH = "/refunds/approve/batch";
    private static final String REJECT_BATCH = "/refunds/reject/batch";
    private static final String HOLD_BATCH = "/refunds/hold/batch";
    private static final String HISTORIES = "/refunds/{refundClaimId}/histories";

    @Autowired private RefundClaimJpaRepository refundClaimRepository;
    @Autowired private RefundOutboxJpaRepository refundOutboxRepository;
    @Autowired private ClaimHistoryJpaRepository claimHistoryRepository;
    @Autowired private OrderItemHistoryJpaRepository orderItemHistoryRepository;
    @Autowired private OrderItemJpaRepository orderItemRepository;
    @Autowired private OrderJpaRepository orderRepository;

    @BeforeEach
    void setUp() {
        claimHistoryRepository.deleteAll();
        refundOutboxRepository.deleteAll();
        refundClaimRepository.deleteAll();
        orderItemHistoryRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        claimHistoryRepository.deleteAll();
        refundOutboxRepository.deleteAll();
        refundClaimRepository.deleteAll();
        orderItemHistoryRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
    }

    @Nested
    @DisplayName("환불 요청 -> 승인 -> 조회 전체 플로우")
    class RequestApproveQueryFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[F1] 환불 요청 -> 승인 -> 목록 조회 -> 상세 조회 정상 플로우")
        void fullFlow_requestApproveQuery() {
            // given
            String orderId = "order-refund-flow-001";
            orderRepository.save(OrderJpaEntityFixtures.orderedEntity(orderId));
            OrderItemJpaEntity item = OrderItemJpaEntityFixtures.itemWithPrice(orderId, 29900, 1);
            String orderItemId = orderItemRepository.save(item).getId();

            // step 1: 환불 요청
            givenSuperAdmin()
                    .body(
                            Map.of(
                                    "items",
                                    List.of(
                                            Map.of(
                                                    "orderId",
                                                    orderItemId,
                                                    "refundQty",
                                                    1,
                                                    "reasonType",
                                                    "CHANGE_OF_MIND",
                                                    "reasonDetail",
                                                    "단순 변심"))))
                    .when()
                    .post(REQUEST_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));

            String refundClaimId = refundClaimRepository.findAll().get(0).getId();

            // step 2: 승인
            givenSuperAdmin()
                    .body(Map.of("refundClaimIds", List.of(refundClaimId)))
                    .when()
                    .post(APPROVE_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));

            // step 3: 목록 조회
            givenSuperAdmin()
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get(REFUNDS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", greaterThanOrEqualTo(1));

            // step 4: 상세 조회
            givenSuperAdmin()
                    .when()
                    .get(REFUND_DETAIL, refundClaimId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue());
        }
    }

    @Nested
    @DisplayName("환불 요청 -> 거절 플로우")
    class RequestRejectFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[F2] 환불 요청 -> 거절 -> 상태 확인")
        void flow_requestThenReject() {
            // given
            String orderId = "order-refund-reject-flow-001";
            orderRepository.save(OrderJpaEntityFixtures.orderedEntity(orderId));
            OrderItemJpaEntity item = OrderItemJpaEntityFixtures.itemWithPrice(orderId, 15000, 1);
            String orderItemId = orderItemRepository.save(item).getId();

            // step 1: 환불 요청
            givenSuperAdmin()
                    .body(
                            Map.of(
                                    "items",
                                    List.of(
                                            Map.of(
                                                    "orderId",
                                                    orderItemId,
                                                    "refundQty",
                                                    1,
                                                    "reasonType",
                                                    "DEFECTIVE",
                                                    "reasonDetail",
                                                    "상품 불량"))))
                    .when()
                    .post(REQUEST_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            String refundClaimId = refundClaimRepository.findAll().get(0).getId();

            // step 2: 거절
            givenSuperAdmin()
                    .body(Map.of("refundClaimIds", List.of(refundClaimId)))
                    .when()
                    .post(REJECT_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));

            // step 3: DB 상태 확인
            var claim = refundClaimRepository.findById(refundClaimId).orElseThrow();
            assertThat(claim.getRefundStatus()).isEqualTo("REJECTED");
        }
    }

    @Nested
    @DisplayName("환불 요청 -> 보류 -> 해제 -> 승인 플로우")
    class HoldUnholdApproveFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[F3] 환불 요청 -> 보류 -> 보류 해제 -> 승인")
        void flow_requestHoldUnholdApprove() {
            // given
            String orderId = "order-refund-hold-flow-001";
            orderRepository.save(OrderJpaEntityFixtures.orderedEntity(orderId));
            OrderItemJpaEntity item = OrderItemJpaEntityFixtures.itemWithPrice(orderId, 20000, 1);
            String orderItemId = orderItemRepository.save(item).getId();

            // step 1: 환불 요청
            givenSuperAdmin()
                    .body(
                            Map.of(
                                    "items",
                                    List.of(
                                            Map.of(
                                                    "orderId",
                                                    orderItemId,
                                                    "refundQty",
                                                    1,
                                                    "reasonType",
                                                    "CHANGE_OF_MIND",
                                                    "reasonDetail",
                                                    "변심"))))
                    .when()
                    .post(REQUEST_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            String refundClaimId = refundClaimRepository.findAll().get(0).getId();

            // step 2: 보류
            givenSuperAdmin()
                    .body(
                            Map.of(
                                    "refundClaimIds",
                                    List.of(refundClaimId),
                                    "isHold",
                                    true,
                                    "memo",
                                    "상품 상태 확인 필요"))
                    .when()
                    .patch(HOLD_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));

            // step 3: 보류 해제
            givenSuperAdmin()
                    .body(
                            Map.of(
                                    "refundClaimIds",
                                    List.of(refundClaimId),
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
                    .body(Map.of("refundClaimIds", List.of(refundClaimId)))
                    .when()
                    .post(APPROVE_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));
        }
    }

    @Nested
    @DisplayName("환불 요청 + 메모 등록 플로우")
    class RequestWithMemoFlowTest {

        @Test
        @Tag("P1")
        @DisplayName("[F4] 환불 요청 -> 메모 등록 -> 상세 조회")
        void flow_requestMemoDetail() {
            // given
            String orderId = "order-refund-memo-flow-001";
            orderRepository.save(OrderJpaEntityFixtures.orderedEntity(orderId));
            OrderItemJpaEntity item = OrderItemJpaEntityFixtures.itemWithPrice(orderId, 10000, 2);
            String orderItemId = orderItemRepository.save(item).getId();

            // step 1: 환불 요청
            givenSuperAdmin()
                    .body(
                            Map.of(
                                    "items",
                                    List.of(
                                            Map.of(
                                                    "orderId",
                                                    orderItemId,
                                                    "refundQty",
                                                    1,
                                                    "reasonType",
                                                    "CHANGE_OF_MIND",
                                                    "reasonDetail",
                                                    "부분 환불"))))
                    .when()
                    .post(REQUEST_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            String refundClaimId = refundClaimRepository.findAll().get(0).getId();

            // step 2: 메모 등록
            givenSuperAdmin()
                    .body(Map.of("message", "고객 통화 완료"))
                    .when()
                    .post(HISTORIES, refundClaimId)
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("data.historyId", notNullValue());

            // step 3: 상세 조회
            givenSuperAdmin()
                    .when()
                    .get(REFUND_DETAIL, refundClaimId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue());
        }
    }
}
