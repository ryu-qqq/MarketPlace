package com.ryuqq.marketplace.integration.container.refund;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.ryuqq.marketplace.adapter.out.persistence.order.OrderItemJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.OrderJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderItemJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemHistoryJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.refund.RefundClaimJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.refund.repository.RefundClaimJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.refundoutbox.repository.RefundOutboxJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.claimhistory.repository.ClaimHistoryJpaRepository;
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
 * Refund Command Testcontainers E2E 테스트.
 *
 * <p>MySQL 실제 컨테이너 기반으로 환불 명령 API를 검증합니다.
 *
 * <p>테스트 대상:
 * <ul>
 *   <li>C1~C3: POST /refunds/request/batch - 환불 요청 일괄</li>
 *   <li>C4~C5: POST /refunds/approve/batch - 환불 승인 일괄</li>
 *   <li>C6~C7: POST /refunds/reject/batch - 환불 거절 일괄</li>
 *   <li>C8~C9: PATCH /refunds/hold/batch - 환불 보류/해제 일괄</li>
 *   <li>C10~C11: POST /refunds/{refundClaimId}/histories - 수기 메모 등록</li>
 * </ul>
 */
@Tag("e2e")
@Tag("container")
@Tag("refund")
@Tag("command")
@DisplayName("Refund Command Container E2E 테스트")
class RefundContainerCommandE2ETest extends ContainerE2ETestBase {

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

    // ===== POST /refunds/request/batch =====

    @Nested
    @DisplayName("POST /refunds/request/batch - 환불 요청 일괄")
    class RequestBatchTest {

        @Test
        @Tag("P0")
        @DisplayName("[C1] 정상 환불 요청 → 성공 + RefundClaim 생성")
        void requestBatch_success() {
            // given
            String orderId = "order-refund-req-001";
            orderRepository.save(OrderJpaEntityFixtures.orderedEntity(orderId));
            OrderItemJpaEntity item = OrderItemJpaEntityFixtures.itemWithPrice(orderId, 10000, 2);
            String orderItemId = orderItemRepository.save(item).getId();

            // when
            givenSuperAdmin()
                    .body(Map.of(
                            "items", List.of(Map.of(
                                    "orderId", orderItemId,
                                    "refundQty", 1,
                                    "reasonType", "CHANGE_OF_MIND",
                                    "reasonDetail", "단순 변심"))))
                    .when()
                    .post(REQUEST_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));

            // then
            assertThat(refundClaimRepository.count()).isEqualTo(1);
        }

        @Test
        @Tag("P1")
        @DisplayName("[C2] 빈 items 요청 시 400 에러")
        void requestBatch_emptyItems_badRequest() {
            givenSuperAdmin()
                    .body(Map.of("items", List.of()))
                    .when()
                    .post(REQUEST_BATCH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[C3] 권한 없는 사용자가 환불 요청 시 403")
        void requestBatch_noPermission_forbidden() {
            givenAuthenticatedUser()
                    .body(Map.of(
                            "items", List.of(Map.of(
                                    "orderId", "some-id",
                                    "refundQty", 1,
                                    "reasonType", "CHANGE_OF_MIND",
                                    "reasonDetail", "변심"))))
                    .when()
                    .post(REQUEST_BATCH)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }
    }

    // ===== POST /refunds/approve/batch =====

    @Nested
    @DisplayName("POST /refunds/approve/batch - 환불 승인 일괄")
    class ApproveBatchTest {

        @Test
        @Tag("P0")
        @DisplayName("[C4] REQUESTED 상태 환불 승인 → 성공")
        void approveBatch_success() {
            // given: 주문+주문상품 시딩 + 환불 요청으로 RefundClaim 생성
            String orderId = "order-refund-approve-001";
            orderRepository.save(OrderJpaEntityFixtures.orderedEntity(orderId));
            OrderItemJpaEntity item = OrderItemJpaEntityFixtures.itemWithPrice(orderId, 10000, 1);
            String orderItemId = orderItemRepository.save(item).getId();

            // 환불 요청
            givenSuperAdmin()
                    .body(Map.of(
                            "items", List.of(Map.of(
                                    "orderId", orderItemId,
                                    "refundQty", 1,
                                    "reasonType", "CHANGE_OF_MIND",
                                    "reasonDetail", "변심"))))
                    .when()
                    .post(REQUEST_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            String refundClaimId = refundClaimRepository.findAll().get(0).getId();

            // when: 승인
            givenSuperAdmin()
                    .body(Map.of("refundClaimIds", List.of(refundClaimId)))
                    .when()
                    .post(APPROVE_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));
        }

        @Test
        @Tag("P1")
        @DisplayName("[C5] 존재하지 않는 refundClaimId로 승인 시 403 에러")
        void approveBatch_nonExistentId_forbidden() {
            givenSuperAdmin()
                    .body(Map.of("refundClaimIds", List.of("non-existent-id")))
                    .when()
                    .post(APPROVE_BATCH)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }
    }

    // ===== POST /refunds/reject/batch =====

    @Nested
    @DisplayName("POST /refunds/reject/batch - 환불 거절 일괄")
    class RejectBatchTest {

        @Test
        @Tag("P0")
        @DisplayName("[C6] REQUESTED 상태 환불 거절 → 성공")
        void rejectBatch_success() {
            // given
            String orderId = "order-refund-reject-001";
            orderRepository.save(OrderJpaEntityFixtures.orderedEntity(orderId));
            OrderItemJpaEntity item = OrderItemJpaEntityFixtures.itemWithPrice(orderId, 10000, 1);
            String orderItemId = orderItemRepository.save(item).getId();

            givenSuperAdmin()
                    .body(Map.of(
                            "items", List.of(Map.of(
                                    "orderId", orderItemId,
                                    "refundQty", 1,
                                    "reasonType", "CHANGE_OF_MIND",
                                    "reasonDetail", "변심"))))
                    .when()
                    .post(REQUEST_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            String refundClaimId = refundClaimRepository.findAll().get(0).getId();

            // when
            givenSuperAdmin()
                    .body(Map.of("refundClaimIds", List.of(refundClaimId)))
                    .when()
                    .post(REJECT_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));
        }

        @Test
        @Tag("P1")
        @DisplayName("[C7] 빈 refundClaimIds 요청 시 400 에러")
        void rejectBatch_emptyIds_badRequest() {
            givenSuperAdmin()
                    .body(Map.of("refundClaimIds", List.of()))
                    .when()
                    .post(REJECT_BATCH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    // ===== PATCH /refunds/hold/batch =====

    @Nested
    @DisplayName("PATCH /refunds/hold/batch - 환불 보류/해제 일괄")
    class HoldBatchTest {

        @Test
        @Tag("P0")
        @DisplayName("[C8] 환불 보류 설정 → 성공")
        void holdBatch_hold_success() {
            // given
            String orderId = "order-refund-hold-001";
            orderRepository.save(OrderJpaEntityFixtures.orderedEntity(orderId));
            OrderItemJpaEntity item = OrderItemJpaEntityFixtures.itemWithPrice(orderId, 10000, 1);
            String orderItemId = orderItemRepository.save(item).getId();

            givenSuperAdmin()
                    .body(Map.of(
                            "items", List.of(Map.of(
                                    "orderId", orderItemId,
                                    "refundQty", 1,
                                    "reasonType", "CHANGE_OF_MIND",
                                    "reasonDetail", "변심"))))
                    .when()
                    .post(REQUEST_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            String refundClaimId = refundClaimRepository.findAll().get(0).getId();

            // when: 보류
            givenSuperAdmin()
                    .body(Map.of(
                            "refundClaimIds", List.of(refundClaimId),
                            "isHold", true,
                            "memo", "검수 진행 중"))
                    .when()
                    .patch(HOLD_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));
        }

        @Test
        @Tag("P1")
        @DisplayName("[C9] 보류 해제 → 성공")
        void holdBatch_unhold_success() {
            // given
            String orderId = "order-refund-unhold-001";
            orderRepository.save(OrderJpaEntityFixtures.orderedEntity(orderId));
            OrderItemJpaEntity item = OrderItemJpaEntityFixtures.itemWithPrice(orderId, 10000, 1);
            String orderItemId = orderItemRepository.save(item).getId();

            givenSuperAdmin()
                    .body(Map.of(
                            "items", List.of(Map.of(
                                    "orderId", orderItemId,
                                    "refundQty", 1,
                                    "reasonType", "CHANGE_OF_MIND",
                                    "reasonDetail", "변심"))))
                    .when()
                    .post(REQUEST_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            String refundClaimId = refundClaimRepository.findAll().get(0).getId();

            // 보류 설정
            givenSuperAdmin()
                    .body(Map.of(
                            "refundClaimIds", List.of(refundClaimId),
                            "isHold", true,
                            "memo", "검수 진행 중"))
                    .when()
                    .patch(HOLD_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // when: 보류 해제
            givenSuperAdmin()
                    .body(Map.of(
                            "refundClaimIds", List.of(refundClaimId),
                            "isHold", false,
                            "memo", "검수 완료"))
                    .when()
                    .patch(HOLD_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));
        }
    }

    // ===== POST /refunds/{refundClaimId}/histories =====

    @Nested
    @DisplayName("POST /refunds/{refundClaimId}/histories - 수기 메모 등록")
    class AddMemoTest {

        @Test
        @Tag("P0")
        @DisplayName("[C10] 환불 건에 수기 메모 등록 → 201")
        void addMemo_success() {
            // given
            String orderId = "order-refund-memo-001";
            orderRepository.save(OrderJpaEntityFixtures.orderedEntity(orderId));
            OrderItemJpaEntity item = OrderItemJpaEntityFixtures.itemWithPrice(orderId, 10000, 1);
            String orderItemId = orderItemRepository.save(item).getId();

            givenSuperAdmin()
                    .body(Map.of(
                            "items", List.of(Map.of(
                                    "orderId", orderItemId,
                                    "refundQty", 1,
                                    "reasonType", "CHANGE_OF_MIND",
                                    "reasonDetail", "변심"))))
                    .when()
                    .post(REQUEST_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            String refundClaimId = refundClaimRepository.findAll().get(0).getId();

            // when
            givenSuperAdmin()
                    .body(Map.of("message", "고객과 통화 완료, 수거 예정"))
                    .when()
                    .post(HISTORIES, refundClaimId)
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("data.historyId", notNullValue());
        }

        @Test
        @Tag("P1")
        @DisplayName("[C11] 빈 메모 등록 시 400 에러")
        void addMemo_emptyMessage_badRequest() {
            givenSuperAdmin()
                    .body(Map.of("message", ""))
                    .when()
                    .post(HISTORIES, "any-refund-id")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }
}
