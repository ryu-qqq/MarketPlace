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
 * Refund Command E2E 테스트.
 *
 * <p>테스트 대상:
 *
 * <ul>
 *   <li>C14: POST /api/v1/market/refunds/request/batch - 환불 요청 배치
 *   <li>C15: POST /api/v1/market/refunds/approve/batch - 환불 승인 배치
 *   <li>C16: POST /api/v1/market/refunds/reject/batch - 환불 거절 배치
 *   <li>C17: PATCH /api/v1/market/refunds/hold/batch - 환불 보류 배치
 *   <li>C18: POST /api/v1/market/refunds/{refundClaimId}/histories - ClaimHistory 메모 추가
 * </ul>
 *
 * <p>V4 간극 규칙: RequestRefundBatchApiRequest의 orderId 필드 = 내부 orderItemId
 */
@Tag("e2e")
@Tag("refund")
@Tag("command")
@DisplayName("Refund Command E2E 테스트")
class RefundCommandE2ETest extends E2ETestBase {

    private static final String REFUND_REQUEST_BATCH = "/refunds/request/batch";
    private static final String REFUND_APPROVE_BATCH = "/refunds/approve/batch";
    private static final String REFUND_REJECT_BATCH = "/refunds/reject/batch";
    private static final String REFUND_HOLD_BATCH = "/refunds/hold/batch";
    private static final String REFUND_HISTORY = "/refunds/{refundClaimId}/histories";

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
    private String seedOrderItem(String orderId) {
        OrderJpaEntity order = OrderJpaEntityFixtures.orderedEntity(orderId);
        orderRepository.save(order);
        OrderItemJpaEntity item = OrderItemJpaEntityFixtures.defaultItem(orderId);
        return orderItemRepository.save(item).getId();
    }

    private Map<String, Object> refundRequestItem(String orderItemId, int qty, String reasonType) {
        Map<String, Object> item = new HashMap<>();
        // V4 간극: orderId 필드 = 내부 orderItemId
        item.put("orderId", orderItemId);
        item.put("refundQty", qty);
        item.put("reasonType", reasonType);
        return item;
    }

    private Map<String, Object> refundRequestItemWithDetail(
            String orderItemId, int qty, String reasonType, String reasonDetail) {
        Map<String, Object> item = refundRequestItem(orderItemId, qty, reasonType);
        item.put("reasonDetail", reasonDetail);
        return item;
    }

    // ===== C14: 환불 요청 배치 =====

    @Nested
    @DisplayName("C14: POST /refunds/request/batch - 환불 요청 배치")
    class RequestRefundBatchTest {

        @Test
        @Tag("P0")
        @DisplayName("[CMD-01] 환불 요청 배치 성공 - 단건")
        void requestBatch_SingleItem_SuccessfullyCreated() {
            // Seed: READY 상태 OrderItem 1건
            String orderItemId = seedOrderItem("order-req-001");

            Response response =
                    given().spec(givenSuperAdmin())
                            .body(
                                    Map.of(
                                            "items",
                                            List.of(
                                                    refundRequestItemWithDetail(
                                                            orderItemId,
                                                            1,
                                                            "CHANGE_OF_MIND",
                                                            "단순 변심입니다"))))
                            .when()
                            .post(REFUND_REQUEST_BATCH);

            response.then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalCount", equalTo(1))
                    .body("data.successCount", equalTo(1))
                    .body("data.failureCount", equalTo(0));

            // DB 검증: RefundClaim 생성 확인
            assertThat(refundClaimRepository.findAll()).hasSizeGreaterThanOrEqualTo(1);
            // Outbox 검증: RefundOutbox 생성 확인
            assertThat(refundOutboxRepository.findAll()).hasSizeGreaterThanOrEqualTo(1);
        }

        @Test
        @Tag("P0")
        @DisplayName("[CMD-02] 환불 요청 배치 성공 - 다건")
        void requestBatch_MultipleItems_AllCreated() {
            // Seed: OrderItem 2건
            String orderItemId1 = seedOrderItem("order-req-002");
            String orderItemId2 = seedOrderItem("order-req-003");

            given().spec(givenSuperAdmin())
                    .body(
                            Map.of(
                                    "items",
                                    List.of(
                                            refundRequestItem(orderItemId1, 1, "CHANGE_OF_MIND"),
                                            refundRequestItem(orderItemId2, 2, "DEFECTIVE"))))
                    .when()
                    .post(REFUND_REQUEST_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalCount", equalTo(2))
                    .body("data.successCount", equalTo(2))
                    .body("data.failureCount", equalTo(0));

            // DB 검증
            assertThat(refundClaimRepository.count()).isEqualTo(2);
        }

        @Test
        @Tag("P1")
        @DisplayName("[CMD-03] 환불 요청 - reasonDetail 없이 요청 (optional 필드)")
        void requestBatch_WithoutReasonDetail_Succeeds() {
            String orderItemId = seedOrderItem("order-req-004");

            given().spec(givenSuperAdmin())
                    .body(
                            Map.of(
                                    "items",
                                    List.of(refundRequestItem(orderItemId, 1, "CHANGE_OF_MIND"))))
                    .when()
                    .post(REFUND_REQUEST_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));
        }

        @Test
        @Tag("P0")
        @DisplayName("[CMD-04] 환불 요청 Validation 실패 - items 빈 목록 → 400")
        void requestBatch_EmptyItems_Returns400() {
            given().spec(givenSuperAdmin())
                    .body(Map.of("items", List.of()))
                    .when()
                    .post(REFUND_REQUEST_BATCH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[CMD-05] 환불 요청 Validation 실패 - orderId 누락 → 400")
        void requestBatch_MissingOrderId_Returns400() {
            Map<String, Object> itemWithoutOrderId = new HashMap<>();
            itemWithoutOrderId.put("refundQty", 1);
            itemWithoutOrderId.put("reasonType", "CHANGE_OF_MIND");

            given().spec(givenSuperAdmin())
                    .body(Map.of("items", List.of(itemWithoutOrderId)))
                    .when()
                    .post(REFUND_REQUEST_BATCH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[CMD-06] 환불 요청 Validation 실패 - refundQty = 0 (Positive 위반) → 400")
        void requestBatch_ZeroQty_Returns400() {
            given().spec(givenSuperAdmin())
                    .body(
                            Map.of(
                                    "items",
                                    List.of(
                                            Map.of(
                                                    "orderId", "xxx",
                                                    "refundQty", 0,
                                                    "reasonType", "CHANGE_OF_MIND"))))
                    .when()
                    .post(REFUND_REQUEST_BATCH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[AUTH-C14] 비인증 요청으로 환불 요청 배치 시도 → 401")
        void requestBatch_Unauthenticated_Returns401() {
            given().spec(givenUnauthenticated())
                    .body(
                            Map.of(
                                    "items",
                                    List.of(
                                            Map.of(
                                                    "orderId", "xxx",
                                                    "refundQty", 1,
                                                    "reasonType", "CHANGE_OF_MIND"))))
                    .when()
                    .post(REFUND_REQUEST_BATCH)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }
    }

    // ===== C15: 환불 승인 배치 =====

    @Nested
    @DisplayName("C15: POST /refunds/approve/batch - 환불 승인 배치")
    class ApproveRefundBatchTest {

        @Test
        @Tag("P0")
        @DisplayName("[CMD-08] 환불 승인 배치 성공 - REQUESTED → COLLECTING")
        void approveBatch_RequestedStatus_TransitionsToCollecting() {
            // Seed: REQUESTED 상태 RefundClaim 2건 직접 시딩 (서로 다른 orderItemId)
            String id1 = "ref-approve-01";
            String id2 = "ref-approve-02";
            refundClaimRepository.save(
                    RefundClaimJpaEntityFixtures.requestedEntity(
                            id1, "01900000-0000-7000-0000-000000000011", 10L));
            refundClaimRepository.save(
                    RefundClaimJpaEntityFixtures.requestedEntity(
                            id2, "01900000-0000-7000-0000-000000000012", 10L));

            given().spec(givenSuperAdmin())
                    .body(Map.of("refundClaimIds", List.of(id1, id2)))
                    .when()
                    .post(REFUND_APPROVE_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalCount", equalTo(2))
                    .body("data.successCount", equalTo(2))
                    .body("data.failureCount", equalTo(0));

            // DB 검증: COLLECTING 상태 전이 확인
            assertThat(refundClaimRepository.findById(id1))
                    .isPresent()
                    .get()
                    .extracting(e -> e.getRefundStatus())
                    .isEqualTo("COLLECTING");
        }

        @Test
        @Tag("P0")
        @DisplayName("[CMD-10] 환불 승인 Validation 실패 - refundClaimIds 빈 목록 → 400")
        void approveBatch_EmptyIds_Returns400() {
            given().spec(givenSuperAdmin())
                    .body(Map.of("refundClaimIds", List.of()))
                    .when()
                    .post(REFUND_APPROVE_BATCH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    // ===== C16: 환불 거절 배치 =====

    @Nested
    @DisplayName("C16: POST /refunds/reject/batch - 환불 거절 배치")
    class RejectRefundBatchTest {

        @Test
        @Tag("P0")
        @DisplayName("[CMD-11] 환불 거절 배치 성공 - REQUESTED → REJECTED")
        void rejectBatch_RequestedStatus_TransitionsToRejected() {
            // Seed: REQUESTED 상태 RefundClaim 1건 직접 시딩
            String id1 = "ref-reject-01";
            refundClaimRepository.save(RefundClaimJpaEntityFixtures.requestedEntity(id1));

            given().spec(givenSuperAdmin())
                    .body(Map.of("refundClaimIds", List.of(id1)))
                    .when()
                    .post(REFUND_REJECT_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));

            // DB 검증: REJECTED 상태 전이 확인
            assertThat(refundClaimRepository.findById(id1))
                    .isPresent()
                    .get()
                    .extracting(e -> e.getRefundStatus())
                    .isEqualTo("REJECTED");
        }

        @Test
        @Tag("P1")
        @DisplayName("[CMD-12] 환불 거절 - COLLECTING 상태에서 거절 가능 (rejectable)")
        void rejectBatch_CollectingStatus_Succeeds() {
            // COLLECTING은 REJECTED 허용 (REJECTABLE = {REQUESTED, COLLECTING, COLLECTED})
            String collectingId = "ref-reject-collecting-01";
            refundClaimRepository.save(RefundClaimJpaEntityFixtures.collectingEntity(collectingId));

            given().spec(givenSuperAdmin())
                    .body(Map.of("refundClaimIds", List.of(collectingId)))
                    .when()
                    .post(REFUND_REJECT_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));
        }

        @Test
        @Tag("P0")
        @DisplayName("[CMD-14] 환불 거절 Validation 실패 - 빈 목록 → 400")
        void rejectBatch_EmptyIds_Returns400() {
            given().spec(givenSuperAdmin())
                    .body(Map.of("refundClaimIds", List.of()))
                    .when()
                    .post(REFUND_REJECT_BATCH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    // ===== C17: 환불 보류 배치 =====

    @Nested
    @DisplayName("C17: PATCH /refunds/hold/batch - 환불 보류 배치")
    class HoldRefundBatchTest {

        @Test
        @Tag("P0")
        @DisplayName("[CMD-15] 환불 보류 설정 성공 - memo 있음")
        void holdBatch_WithMemo_SetsHoldReason() {
            // Seed: holdReason=null 상태 REQUESTED RefundClaim 2건 (서로 다른 orderItemId)
            String id1 = "ref-hold-01";
            String id2 = "ref-hold-02";
            refundClaimRepository.save(
                    RefundClaimJpaEntityFixtures.requestedEntity(
                            id1, "01900000-0000-7000-0000-000000000011", 10L));
            refundClaimRepository.save(
                    RefundClaimJpaEntityFixtures.requestedEntity(
                            id2, "01900000-0000-7000-0000-000000000012", 10L));

            given().spec(givenSuperAdmin())
                    .body(
                            Map.of(
                                    "refundClaimIds",
                                    List.of(id1, id2),
                                    "isHold",
                                    true,
                                    "memo",
                                    "CS 확인 필요"))
                    .when()
                    .patch(REFUND_HOLD_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(2));

            // DB 검증: holdReason 설정 확인
            assertThat(refundClaimRepository.findById(id1))
                    .isPresent()
                    .get()
                    .extracting(e -> e.getHoldReason())
                    .isEqualTo("CS 확인 필요");
        }

        @Test
        @Tag("P0")
        @DisplayName("[CMD-16] 환불 보류 설정 - null memo → 기본값 \"보류 처리\" 적용")
        void holdBatch_NullMemo_AppliesDefaultHoldReason() {
            // Seed: REQUESTED 상태 RefundClaim 1건
            String id = "ref-hold-null-01";
            refundClaimRepository.save(RefundClaimJpaEntityFixtures.requestedEntity(id));

            // memo 필드 없이 요청 (null memo)
            given().spec(givenSuperAdmin())
                    .body(Map.of("refundClaimIds", List.of(id), "isHold", true))
                    .when()
                    .patch(REFUND_HOLD_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));

            // DB 검증: 기본값 "보류 처리" 적용 확인 (RefundClaim.hold() 도메인 로직)
            assertThat(refundClaimRepository.findById(id))
                    .isPresent()
                    .get()
                    .extracting(e -> e.getHoldReason())
                    .isEqualTo("보류 처리");
        }

        @Test
        @Tag("P0")
        @DisplayName("[CMD-17] 환불 보류 해제 성공")
        void holdBatch_ReleaseHold_ClearsHoldInfo() {
            // Seed: holdReason 설정된 RefundClaim 1건
            String id = "ref-hold-release-01";
            refundClaimRepository.save(RefundClaimJpaEntityFixtures.heldEntity(id, "CS 확인 필요"));

            given().spec(givenSuperAdmin())
                    .body(Map.of("refundClaimIds", List.of(id), "isHold", false))
                    .when()
                    .patch(REFUND_HOLD_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));

            // DB 검증: holdReason, holdAt 모두 null로 변경 확인
            var released = refundClaimRepository.findById(id).orElseThrow();
            assertThat(released.getHoldReason()).isNull();
            assertThat(released.getHoldAt()).isNull();
        }

        @Test
        @Tag("P0")
        @DisplayName("[CMD-20] 환불 보류 Validation 실패 - 빈 목록 → 400")
        void holdBatch_EmptyIds_Returns400() {
            given().spec(givenSuperAdmin())
                    .body(Map.of("refundClaimIds", List.of(), "isHold", true))
                    .when()
                    .patch(REFUND_HOLD_BATCH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    // ===== C18: ClaimHistory 메모 추가 =====

    @Nested
    @DisplayName("C18: POST /refunds/{refundClaimId}/histories - ClaimHistory 메모 추가")
    class AddClaimHistoryMemoTest {

        @Test
        @Tag("P0")
        @DisplayName("[CMD-21] ClaimHistory 메모 추가 성공")
        void addMemo_ValidRefundClaim_CreatesHistory() {
            // Seed: REQUESTED 상태 RefundClaim 1건
            String refundClaimId = "ref-history-01";
            refundClaimRepository.save(RefundClaimJpaEntityFixtures.requestedEntity(refundClaimId));

            given().spec(givenSuperAdmin())
                    .body(Map.of("message", "CS 처리 메모입니다"))
                    .when()
                    .post(REFUND_HISTORY, refundClaimId)
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("data.historyId", notNullValue());

            // DB 검증: ClaimHistory 생성 확인
            assertThat(claimHistoryRepository.count()).isEqualTo(1);
        }

        @Test
        @Tag("P1")
        @DisplayName("[CMD-22] ClaimHistory 메모 추가 - 존재하지 않는 refundClaimId로도 이력이 생성된다")
        void addMemo_NonExistentRefundClaimId_CreatesHistory() {
            given().spec(givenSuperAdmin())
                    .body(Map.of("message", "메모"))
                    .when()
                    .post(REFUND_HISTORY, "01900000-0000-7000-0000-000000000999")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("data.historyId", notNullValue());
        }
    }
}
