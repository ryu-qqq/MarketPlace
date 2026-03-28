package com.ryuqq.marketplace.integration.refund;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.ryuqq.marketplace.adapter.out.persistence.claimhistory.repository.ClaimHistoryJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.OrderItemJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.OrderJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderItemJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.refund.RefundClaimJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.refund.repository.RefundClaimJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.refundoutbox.repository.RefundOutboxJpaRepository;
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
 * Refund 전체 플로우 E2E 테스트.
 *
 * <p>테스트 대상:
 *
 * <ul>
 *   <li>FLOW-01: 환불 요청 → 승인 (해피패스)
 *   <li>FLOW-02: 환불 요청 → 거절
 *   <li>FLOW-03: 보류 설정 → 보류 해제 토글
 *   <li>FLOW-04: null 보류 사유 → 기본값 "보류 처리" 검증
 *   <li>FLOW-05: 배치 부분 실패 (혼합 상태)
 * </ul>
 *
 * <p>RefundStatus 상태 전이 규칙:
 *
 * <ul>
 *   <li>REQUESTED → COLLECTING (approve)
 *   <li>COLLECTING → COLLECTED (collection_completable)
 *   <li>COLLECTED → COMPLETED (completable)
 *   <li>REQUESTED/COLLECTING/COLLECTED → REJECTED (rejectable)
 *   <li>REQUESTED/COLLECTING → CANCELLED (cancellable)
 * </ul>
 */
@Tag("e2e")
@Tag("refund")
@Tag("flow")
@DisplayName("Refund 전체 플로우 E2E 테스트")
class RefundFlowE2ETest extends E2ETestBase {

    private static final String REFUNDS = "/refunds";
    private static final String REFUND_REQUEST_BATCH = "/refunds/request/batch";
    private static final String REFUND_APPROVE_BATCH = "/refunds/approve/batch";
    private static final String REFUND_REJECT_BATCH = "/refunds/reject/batch";
    private static final String REFUND_HOLD_BATCH = "/refunds/hold/batch";
    private static final String REFUND_DETAIL = "/refunds/{refundClaimId}";

    @Autowired private OrderJpaRepository orderRepository;
    @Autowired private OrderItemJpaRepository orderItemRepository;
    @Autowired private RefundClaimJpaRepository refundClaimRepository;
    @Autowired private RefundOutboxJpaRepository refundOutboxRepository;
    @Autowired private ClaimHistoryJpaRepository claimHistoryRepository;

    @BeforeEach
    void setUp() {
        claimHistoryRepository.deleteAll();
        refundOutboxRepository.deleteAll();
        refundClaimRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        claimHistoryRepository.deleteAll();
        refundOutboxRepository.deleteAll();
        refundClaimRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
    }

    // ===== 공통 시딩 헬퍼 =====

    /**
     * Order + OrderItem 시딩.
     *
     * <p>V4 간극 규칙: 환불 요청 시 orderId 필드에 orderItemId를 전달해야 함.
     *
     * @return 저장된 OrderItem의 ID
     */
    private Long seedOrderItem(String orderId) {
        OrderJpaEntity order = OrderJpaEntityFixtures.orderedEntity(orderId);
        orderRepository.save(order);
        OrderItemJpaEntity item = OrderItemJpaEntityFixtures.defaultItem(orderId);
        return orderItemRepository.save(item).getId();
    }

    private Map<String, Object> buildRefundRequestBody(Long orderItemId, String reasonType) {
        Map<String, Object> item = new HashMap<>();
        // V4 간극: orderId 필드 = 내부 orderItemId
        item.put("orderId", orderItemId);
        item.put("refundQty", 1);
        item.put("reasonType", reasonType);
        return Map.of("items", List.of(item));
    }

    // ===== FLOW-01: 환불 요청 → 승인 해피패스 =====

    @Nested
    @DisplayName("FLOW-01: 환불 요청 → 승인 전체 해피패스")
    class RequestToApproveHappyPathTest {

        @Test
        @Tag("P0")
        @DisplayName("[FLOW-01] 환불 요청 → 목록 조회 → 승인 → 상세 조회 COLLECTING 확인")
        void requestThenApprove_FullHappyPath_StatusBecomesCollecting() {
            // Step 1: OrderItem 시딩 (READY 상태)
            Long orderItemId = seedOrderItem("flow-01-order-001");

            // Step 2: 환불 요청 (C14)
            Response requestResponse =
                    given().spec(givenSuperAdmin())
                            .body(buildRefundRequestBody(orderItemId, "CHANGE_OF_MIND"))
                            .when()
                            .post(REFUND_REQUEST_BATCH);

            requestResponse
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));

            // DB 검증: RefundClaim 생성 확인 + refundClaimId 캡처
            assertThat(refundClaimRepository.findAll()).hasSizeGreaterThanOrEqualTo(1);
            assertThat(refundOutboxRepository.findAll()).hasSizeGreaterThanOrEqualTo(1);
            String refundClaimId = refundClaimRepository.findAll().get(0).getId();

            // Step 3: 환불 목록 조회 - 생성 확인 (Q10)
            given().spec(givenSuperAdmin())
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get(REFUNDS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(greaterThanOrEqualTo(1)));

            // Step 4: 환불 승인 (C15)
            given().spec(givenSuperAdmin())
                    .body(Map.of("refundClaimIds", List.of(refundClaimId)))
                    .when()
                    .post(REFUND_APPROVE_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));

            // DB 검증: COLLECTING 상태 전이 확인
            assertThat(refundClaimRepository.findById(refundClaimId))
                    .isPresent()
                    .get()
                    .extracting(e -> e.getRefundStatus())
                    .isEqualTo("COLLECTING");

            // Step 5: 상세 조회 - COLLECTING 상태 확인 (Q11)
            given().spec(givenSuperAdmin())
                    .when()
                    .get(REFUND_DETAIL, refundClaimId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.claimInfo.status", equalTo("COLLECTING"));
        }
    }

    // ===== FLOW-02: 환불 요청 → 거절 플로우 =====

    @Nested
    @DisplayName("FLOW-02: 환불 요청 → 거절 플로우")
    class RequestToRejectFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[FLOW-02] 환불 요청 → 거절 → 상세 조회 REJECTED 확인")
        void requestThenReject_StatusBecomesRejected() {
            // Step 1: OrderItem 시딩 후 환불 요청 (C14)
            Long orderItemId = seedOrderItem("flow-02-order-001");

            given().spec(givenSuperAdmin())
                    .body(buildRefundRequestBody(orderItemId, "CHANGE_OF_MIND"))
                    .when()
                    .post(REFUND_REQUEST_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));

            String refundClaimId = refundClaimRepository.findAll().get(0).getId();

            // Step 2: 환불 거절 (C16)
            given().spec(givenSuperAdmin())
                    .body(Map.of("refundClaimIds", List.of(refundClaimId)))
                    .when()
                    .post(REFUND_REJECT_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));

            // DB 검증: REJECTED 상태 전이 확인
            assertThat(refundClaimRepository.findById(refundClaimId))
                    .isPresent()
                    .get()
                    .extracting(e -> e.getRefundStatus())
                    .isEqualTo("REJECTED");

            // Step 3: 상세 조회 - REJECTED 확인 (Q11)
            given().spec(givenSuperAdmin())
                    .when()
                    .get(REFUND_DETAIL, refundClaimId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.claimInfo.status", equalTo("REJECTED"));
        }
    }

    // ===== FLOW-03: 보류 설정 → 보류 해제 토글 플로우 =====

    @Nested
    @DisplayName("FLOW-03: 보류 설정 → 보류 해제 토글 플로우")
    class HoldToggleFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[FLOW-03] 보류 설정 → 상세 조회 holdInfo 확인 → 보류 해제 → 해제 후 상세 조회")
        void holdThenRelease_ToggleHoldStatus_Succeeds() {
            // Step 1: REQUESTED 상태 RefundClaim 직접 시딩
            String refundClaimId = "flow-03-ref-001";
            refundClaimRepository.save(RefundClaimJpaEntityFixtures.requestedEntity(refundClaimId));

            // Step 2: 보류 설정 (C17, isHold=true)
            given().spec(givenSuperAdmin())
                    .body(
                            Map.of(
                                    "refundClaimIds",
                                    List.of(refundClaimId),
                                    "isHold",
                                    true,
                                    "memo",
                                    "보류 사유"))
                    .when()
                    .patch(REFUND_HOLD_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));

            // DB 검증: holdReason, holdAt 설정 확인
            var held = refundClaimRepository.findById(refundClaimId).orElseThrow();
            assertThat(held.getHoldReason()).isEqualTo("보류 사유");
            assertThat(held.getHoldAt()).isNotNull();

            // Step 3: 보류 상세 조회 (Q11)
            given().spec(givenSuperAdmin())
                    .when()
                    .get(REFUND_DETAIL, refundClaimId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue());

            // Step 4: 보류 해제 (C17, isHold=false)
            given().spec(givenSuperAdmin())
                    .body(Map.of("refundClaimIds", List.of(refundClaimId), "isHold", false))
                    .when()
                    .patch(REFUND_HOLD_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));

            // DB 검증: holdReason, holdAt 모두 null로 해제 확인
            var released = refundClaimRepository.findById(refundClaimId).orElseThrow();
            assertThat(released.getHoldReason()).isNull();
            assertThat(released.getHoldAt()).isNull();

            // Step 5: 해제 후 상세 조회 확인 (Q11)
            given().spec(givenSuperAdmin())
                    .when()
                    .get(REFUND_DETAIL, refundClaimId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue());
        }
    }

    // ===== FLOW-04: null 보류 사유 → 기본값 "보류 처리" 검증 =====

    @Nested
    @DisplayName("FLOW-04: null 보류 사유 → 기본값 \"보류 처리\" 도메인 규칙 검증")
    class NullHoldReasonDefaultValueTest {

        @Test
        @Tag("P0")
        @DisplayName("[FLOW-04] memo 없이 보류 설정 → RefundClaim.hold() 기본값 \"보류 처리\" 적용 확인")
        void holdBatch_NullMemo_AppliesDefaultHoldReason() {
            // Step 1: REQUESTED 상태 RefundClaim 직접 시딩
            String refundClaimId = "flow-04-ref-001";
            refundClaimRepository.save(RefundClaimJpaEntityFixtures.requestedEntity(refundClaimId));

            // Step 2: memo 없이 보류 설정 (memo 필드 자체를 전송하지 않음)
            given().spec(givenSuperAdmin())
                    .body(Map.of("refundClaimIds", List.of(refundClaimId), "isHold", true))
                    .when()
                    .patch(REFUND_HOLD_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));

            // Step 3: DB 직접 검증 - 도메인 기본값 "보류 처리" 적용 확인
            var saved = refundClaimRepository.findById(refundClaimId).orElseThrow();
            assertThat(saved.getHoldReason()).isEqualTo("보류 처리");
            assertThat(saved.getHoldAt()).isNotNull();
        }
    }

    // ===== FLOW-05: 배치 부분 실패 플로우 (혼합 상태) =====

    @Nested
    @DisplayName("FLOW-05: 배치 부분 실패 플로우 (혼합 상태)")
    class BatchPartialFailureFlowTest {

        @Test
        @Tag("P1")
        @DisplayName(
                "[FLOW-05] 승인 배치 - REQUESTED(성공) + COMPLETED(실패) 혼합 → successCount=1,"
                        + " failureCount=1")
        void approveBatch_MixedStatuses_PartialSuccess() {
            // Step 1: RefundClaim 시딩
            String id1 = "flow-05-ref-requested";
            String id2 = "flow-05-ref-completed";
            // REQUESTED 상태 1건 (승인 가능)
            refundClaimRepository.save(RefundClaimJpaEntityFixtures.requestedEntity(id1));
            // COMPLETED 상태 1건 (승인 불가 - 종료 상태)
            refundClaimRepository.save(RefundClaimJpaEntityFixtures.completedEntity(id2));

            // Step 2: 승인 배치 (C15) - 혼합 결과
            // 배치 API는 항상 200 반환. 상태 전이 실패는 failureCount로 처리.
            given().spec(givenSuperAdmin())
                    .body(Map.of("refundClaimIds", List.of(id1, id2)))
                    .when()
                    .post(REFUND_APPROVE_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalCount", equalTo(2))
                    .body("data.successCount", equalTo(1))
                    .body("data.failureCount", equalTo(1));

            // Step 3: 부분 성공 결과 검증
            // id1: REQUESTED → COLLECTING 전환 성공
            assertThat(refundClaimRepository.findById(id1))
                    .isPresent()
                    .get()
                    .extracting(e -> e.getRefundStatus())
                    .isEqualTo("COLLECTING");

            // id2: COMPLETED 상태 그대로 유지 (상태 전이 불가)
            assertThat(refundClaimRepository.findById(id2))
                    .isPresent()
                    .get()
                    .extracting(e -> e.getRefundStatus())
                    .isEqualTo("COMPLETED");
        }
    }
}
