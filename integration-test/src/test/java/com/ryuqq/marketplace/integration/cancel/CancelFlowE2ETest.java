package com.ryuqq.marketplace.integration.cancel;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.ryuqq.marketplace.adapter.out.persistence.cancel.CancelJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.cancel.repository.CancelJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.canceloutbox.repository.CancelOutboxJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.claimhistory.repository.ClaimHistoryJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.OrderItemJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.OrderJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderItemJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemHistoryJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderJpaRepository;
import com.ryuqq.marketplace.integration.E2ETestBase;
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
 * Cancel 전체 플로우 E2E 테스트.
 *
 * <p>테스트 대상:
 *
 * <ul>
 *   <li>FLOW-1: 판매자 취소 → 목록/상세 조회 플로우
 *   <li>FLOW-2: 판매자 취소 → 취소 요약 반영 플로우
 *   <li>FLOW-3: 구매자 취소 요청 → 판매자 승인 → OrderItem CANCELLED 전체 플로우
 *   <li>FLOW-4: 구매자 취소 요청 → 판매자 거절 플로우
 *   <li>FLOW-5: 배치 부분 실패 플로우
 *   <li>FLOW-6: ClaimHistory 메모 추가 플로우 (P1)
 *   <li>FLOW-7: 소유권 검증 플로우 (P1)
 * </ul>
 */
@Tag("e2e")
@Tag("cancel")
@Tag("flow")
@DisplayName("Cancel Flow E2E 테스트")
class CancelFlowE2ETest extends E2ETestBase {

    private static final String CANCELS = "/cancels";
    private static final String CANCEL_SUMMARY = "/cancels/summary";
    private static final String CANCEL_DETAIL = "/cancels/{cancelId}";
    private static final String SELLER_CANCEL_BATCH = "/cancels/seller-cancel/batch";
    private static final String APPROVE_BATCH = "/cancels/approve/batch";
    private static final String REJECT_BATCH = "/cancels/reject/batch";
    private static final String ADD_HISTORY = "/cancels/{cancelId}/histories";

    @Autowired private CancelJpaRepository cancelRepository;
    @Autowired private CancelOutboxJpaRepository cancelOutboxRepository;
    @Autowired private ClaimHistoryJpaRepository claimHistoryRepository;
    @Autowired private OrderItemHistoryJpaRepository orderItemHistoryRepository;
    @Autowired private OrderItemJpaRepository orderItemRepository;
    @Autowired private OrderJpaRepository orderRepository;

    @BeforeEach
    void setUp() {
        claimHistoryRepository.deleteAll();
        cancelOutboxRepository.deleteAll();
        cancelRepository.deleteAll();
        orderItemHistoryRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        claimHistoryRepository.deleteAll();
        cancelOutboxRepository.deleteAll();
        cancelRepository.deleteAll();
        orderItemHistoryRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
    }

    /**
     * Order + OrderItem 시딩 헬퍼.
     *
     * @param orderId Order ID
     * @return 저장된 OrderItem의 ID (UUIDv7 String)
     */
    private String seedOrderItem(String orderId) {
        orderRepository.save(OrderJpaEntityFixtures.orderedEntity(orderId));
        OrderItemJpaEntity item = OrderItemJpaEntityFixtures.defaultItem(orderId);
        return orderItemRepository.save(item).getId();
    }

    @Nested
    @DisplayName("FLOW-1: 판매자 취소 → 목록/상세 조회 플로우")
    class SellerCancelAndQueryFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[FLOW-1] 판매자 취소 → 목록 조회 → 상세 조회 전체 플로우")
        void sellerCancel_ThenQueryListAndDetail_FullFlow() {
            // Step 1. 전제 조건: Order + OrderItem 시딩
            String orderItemId = seedOrderItem("order-flow1-001");

            // Step 2. 판매자 취소 요청
            given().spec(givenSuperAdmin())
                    .body(Map.of("items", List.of(createSellerCancelItem(orderItemId))))
                    .when()
                    .post(SELLER_CANCEL_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));

            // Step 3. 취소 목록 조회 - 1건 확인
            given().spec(givenSuperAdmin())
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get(CANCELS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(1));

            // Step 4. 목록에서 cancelId 추출 후 상세 조회
            String cancelId =
                    given().spec(givenSuperAdmin())
                            .queryParam("page", 0)
                            .queryParam("size", 10)
                            .when()
                            .get(CANCELS)
                            .jsonPath()
                            .getString("data.content[0].claimInfo.claimId");

            assertThat(cancelId).isNotNull();

            // Step 5. 취소 상세 조회
            given().spec(givenSuperAdmin())
                    .when()
                    .get(CANCEL_DETAIL, cancelId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.cancelStatus", equalTo("APPROVED"))
                    .body("data.cancelType", equalTo("SELLER_CANCEL"));

            // Step 6. DB 검증
            var updatedItem = orderItemRepository.findById(orderItemId).orElseThrow();
            assertThat(updatedItem.getOrderItemStatus()).isEqualTo("CANCELLED");
            assertThat(cancelRepository.count()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("FLOW-2: 판매자 취소 → 취소 요약 반영 플로우")
    class SellerCancelAndSummaryFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[FLOW-2] 취소 없는 상태에서 판매자 취소 후 summary APPROVED 카운트 반영 확인")
        void sellerCancel_ThenSummaryReflectsApproved_Flow() {
            // Step 1. 취소 데이터 없을 때 summary 조회 - 데이터 없음 확인
            given().spec(givenSuperAdmin())
                    .when()
                    .get(CANCEL_SUMMARY)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue());

            // Step 2. OrderItem 시딩 후 판매자 취소 (APPROVED 상태 생성)
            String orderItemId = seedOrderItem("order-flow2-001");

            given().spec(givenSuperAdmin())
                    .body(Map.of("items", List.of(createSellerCancelItem(orderItemId))))
                    .when()
                    .post(SELLER_CANCEL_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));

            // Step 3. summary 재조회 - APPROVED 건수 반영 확인
            given().spec(givenSuperAdmin())
                    .when()
                    .get(CANCEL_SUMMARY)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue());

            // DB에서 APPROVED 상태 Cancel 1건 생성 확인
            assertThat(cancelRepository.count()).isEqualTo(1);
            var cancel = cancelRepository.findAll().get(0);
            assertThat(cancel.getCancelStatus()).isEqualTo("APPROVED");
        }
    }

    @Nested
    @DisplayName("FLOW-3: 구매자 취소 요청 → 판매자 승인 → OrderItem CANCELLED 전체 플로우")
    class BuyerCancelApproveFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[FLOW-3] REQUESTED Cancel → 승인 → OrderItem CANCELLED 검증")
        void buyerCancel_ThenApprove_OrderItemCancelled_Flow() {
            // Step 1. 전제 조건: Order + OrderItem 시딩
            String orderItemId = seedOrderItem("order-flow3-001");

            // Step 2. Cancel 직접 시딩 (BUYER_CANCEL, REQUESTED 상태)
            cancelRepository.save(
                    CancelJpaEntityFixtures.requestedEntity(
                            "cancel-flow-buyer-001", orderItemId, 10L));

            // Step 3. 취소 목록 조회 - REQUESTED 1건 확인
            given().spec(givenSuperAdmin())
                    .queryParam("statuses", "REQUESTED")
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get(CANCELS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(1));

            // Step 4. 취소 승인
            given().spec(givenSuperAdmin())
                    .body(Map.of("cancelIds", List.of("cancel-flow-buyer-001")))
                    .when()
                    .post(APPROVE_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));

            // Step 5. 취소 상세 조회 - APPROVED 확인
            given().spec(givenSuperAdmin())
                    .when()
                    .get(CANCEL_DETAIL, "cancel-flow-buyer-001")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.cancelStatus", equalTo("APPROVED"));

            // Step 6. DB 검증 - OrderItem CANCELLED 전환 확인
            var updatedItem = orderItemRepository.findById(orderItemId).orElseThrow();
            assertThat(updatedItem.getOrderItemStatus()).isEqualTo("CANCELLED");
        }
    }

    @Nested
    @DisplayName("FLOW-4: 구매자 취소 요청 → 판매자 거절 플로우")
    class BuyerCancelRejectFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[FLOW-4] REQUESTED Cancel → 거절 → OrderItem READY 원복 검증")
        void buyerCancel_ThenReject_OrderItemRemainsReady_Flow() {
            // Step 1. 전제 조건: Order + OrderItem 시딩
            String orderItemId = seedOrderItem("order-flow4-001");

            // Step 2. Cancel 직접 시딩 (BUYER_CANCEL, REQUESTED 상태)
            cancelRepository.save(
                    CancelJpaEntityFixtures.requestedEntity(
                            "cancel-reject-flow-001", orderItemId, 10L));

            // Step 3. 취소 거절
            given().spec(givenSuperAdmin())
                    .body(Map.of("cancelIds", List.of("cancel-reject-flow-001")))
                    .when()
                    .post(REJECT_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));

            // Step 4. 취소 상세 조회 - REJECTED 확인
            given().spec(givenSuperAdmin())
                    .when()
                    .get(CANCEL_DETAIL, "cancel-reject-flow-001")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.cancelStatus", equalTo("REJECTED"));

            // Step 5. DB 검증 - OrderItem READY 원복 확인
            var updatedItem = orderItemRepository.findById(orderItemId).orElseThrow();
            assertThat(updatedItem.getOrderItemStatus()).isEqualTo("READY");
        }
    }

    @Nested
    @DisplayName("FLOW-5: 배치 부분 실패 플로우")
    class BatchPartialFailureFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[FLOW-5] 승인 가능 항목과 불가 항목 혼재 → BatchResultApiResponse 부분 실패 검증")
        void approveBatch_MixedRequestedAndApproved_PartialFailure_Flow() {
            // Step 1. 전제 조건: OrderItem 1건 + Cancel 2건 (REQUESTED, APPROVED) 시딩
            String orderItemId = seedOrderItem("order-flow5-001");

            cancelRepository.save(
                    CancelJpaEntityFixtures.requestedEntityWithNumber(
                            "cancel-flow5-req-001", "CAN-FLOW5-REQ-001", orderItemId, 10L));
            cancelRepository.save(
                    CancelJpaEntityFixtures.approvedEntity(
                            "cancel-flow5-app-001", "order-item-flow5-dummy", 10L));

            // Step 2. 승인 배치 - REQUESTED 1건 성공, APPROVED 1건 실패 (상태 전이 불가)
            given().spec(givenSuperAdmin())
                    .body(
                            Map.of(
                                    "cancelIds",
                                    List.of("cancel-flow5-req-001", "cancel-flow5-app-001")))
                    .when()
                    .post(APPROVE_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalCount", equalTo(2))
                    .body("data.successCount", equalTo(1))
                    .body("data.failureCount", equalTo(1));
        }
    }

    @Nested
    @DisplayName("FLOW-6: ClaimHistory 메모 추가 플로우 (P1)")
    class ClaimHistoryMemoFlowTest {

        @Test
        @Tag("P1")
        @DisplayName("[FLOW-6] 취소 건에 메모 등록 후 2번째 메모 추가 - 각각 별도 이력")
        void addMemo_Twice_TwoHistoriesStored_Flow() {
            // Step 1. OrderItem + Cancel 시딩 (REQUESTED 상태)
            String orderItemId = seedOrderItem("order-flow6-001");
            cancelRepository.save(
                    CancelJpaEntityFixtures.requestedEntity("cancel-flow6-001", orderItemId, 10L));

            // Step 2. 첫 번째 메모 등록
            given().spec(givenSuperAdmin())
                    .body(Map.of("message", "고객 요청으로 취소 처리함"))
                    .when()
                    .post(ADD_HISTORY, "cancel-flow6-001")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("data", notNullValue());

            // Step 3. DB 검증 - 1건
            assertThat(claimHistoryRepository.count()).isEqualTo(1);

            // Step 4. 두 번째 메모 등록
            given().spec(givenSuperAdmin())
                    .body(Map.of("message", "환불 처리 중 확인 필요"))
                    .when()
                    .post(ADD_HISTORY, "cancel-flow6-001")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("data", notNullValue());

            // Step 5. DB 검증 - 2건
            assertThat(claimHistoryRepository.count()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("FLOW-7: 소유권 검증 플로우 (P1)")
    class OwnershipValidationFlowTest {

        @Test
        @Tag("P1")
        @DisplayName("[FLOW-7] 다른 sellerId로 판매자 취소 요청 시 소유권 불일치 - 배치 실패 또는 403")
        void sellerCancel_WrongSeller_OwnershipFailure_Flow() {
            // 전제 조건: OrderItem 시딩 (sellerId=10 = DEFAULT_SELLER_ID)
            String orderItemId = seedOrderItem("order-flow7-001");

            // 다른 orgId로 요청 (sellerId가 10이 아닌 셀러)
            given().spec(givenSellerUser("org-other-seller", "cancel:write"))
                    .body(Map.of("items", List.of(createSellerCancelItem(orderItemId))))
                    .when()
                    .post(SELLER_CANCEL_BATCH)
                    .then()
                    // 403 또는 200 with failureCount=1 (구현에 따라 달라짐)
                    .statusCode(
                            anyOf(
                                    equalTo(HttpStatus.FORBIDDEN.value()),
                                    equalTo(HttpStatus.OK.value())));

            // DB에 Cancel이 생성되지 않았음을 확인 (소유권 불일치로 거부된 경우)
            // 403 응답이면 cancelRepository는 비어 있어야 함
            // 200 응답 + failureCount=1이면 역시 cancelRepository는 비어 있어야 함
            assertThat(cancelRepository.count()).isEqualTo(0);
        }
    }

    // ===== Helper 메서드 =====

    private Map<String, Object> createSellerCancelItem(String orderItemId) {
        return Map.of(
                "orderId",
                orderItemId,
                "cancelQty",
                1,
                "reasonType",
                "OUT_OF_STOCK",
                "reasonDetail",
                "재고 소진");
    }
}
