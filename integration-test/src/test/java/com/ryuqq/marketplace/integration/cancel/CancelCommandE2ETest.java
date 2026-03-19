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
 * Cancel Command E2E 테스트.
 *
 * <p>테스트 대상:
 *
 * <ul>
 *   <li>C19: POST /cancels/seller-cancel/batch - 판매자 직접 취소 배치
 *   <li>C20: POST /cancels/approve/batch - 취소 승인 배치
 *   <li>C21: POST /cancels/reject/batch - 취소 거절 배치
 *   <li>C22: POST /cancels/{cancelId}/histories - 메모 등록
 * </ul>
 */
@Tag("e2e")
@Tag("cancel")
@Tag("command")
@DisplayName("Cancel Command E2E 테스트")
class CancelCommandE2ETest extends E2ETestBase {

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
    @DisplayName("C19: POST /cancels/seller-cancel/batch - 판매자 직접 취소")
    class SellerCancelBatchTest {

        @Test
        @Tag("P0")
        @DisplayName("[C19-1] 유효한 OrderItem 1건 판매자 취소 성공")
        void sellerCancelBatch_SingleItem_Success() {
            String orderItemId = seedOrderItem("order-sc-001");

            given().spec(givenSuperAdmin())
                    .body(Map.of("items", List.of(createSellerCancelItem(orderItemId))))
                    .when()
                    .post(SELLER_CANCEL_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalCount", equalTo(1))
                    .body("data.successCount", equalTo(1))
                    .body("data.failureCount", equalTo(0));

            assertThat(cancelRepository.count()).isEqualTo(1);

            var cancelEntity = cancelRepository.findAll().get(0);
            assertThat(cancelEntity.getCancelStatus()).isEqualTo("APPROVED");

            var updatedItem = orderItemRepository.findById(orderItemId).orElseThrow();
            assertThat(updatedItem.getOrderItemStatus()).isEqualTo("CANCELLED");
        }

        @Test
        @Tag("P0")
        @DisplayName("[C19-2] OrderItem 2건 일괄 판매자 취소 성공")
        void sellerCancelBatch_TwoItems_Success() {
            String itemId1 = seedOrderItem("order-sc-002");
            String itemId2 = seedOrderItem("order-sc-003");

            given().spec(givenSuperAdmin())
                    .body(
                            Map.of(
                                    "items",
                                    List.of(
                                            createSellerCancelItem(itemId1),
                                            createSellerCancelItem(itemId2))))
                    .when()
                    .post(SELLER_CANCEL_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalCount", equalTo(2))
                    .body("data.successCount", equalTo(2))
                    .body("data.failureCount", equalTo(0));

            assertThat(cancelRepository.count()).isEqualTo(2);
        }

        @Test
        @Tag("P0")
        @DisplayName("[C19-3] items 빈 목록 요청 - 400")
        void sellerCancelBatch_EmptyItems_Returns400() {
            given().spec(givenSuperAdmin())
                    .body(Map.of("items", List.of()))
                    .when()
                    .post(SELLER_CANCEL_BATCH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C19-4] orderId 누락 - 400")
        void sellerCancelBatch_MissingOrderId_Returns400() {
            given().spec(givenSuperAdmin())
                    .body(
                            Map.of(
                                    "items",
                                    List.of(Map.of("cancelQty", 1, "reasonType", "OUT_OF_STOCK"))))
                    .when()
                    .post(SELLER_CANCEL_BATCH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C19-5] cancelQty = 0 (양수 아님) - 400")
        void sellerCancelBatch_ZeroCancelQty_Returns400() {
            given().spec(givenSuperAdmin())
                    .body(
                            Map.of(
                                    "items",
                                    List.of(
                                            Map.of(
                                                    "orderId",
                                                    "any-id",
                                                    "cancelQty",
                                                    0,
                                                    "reasonType",
                                                    "OUT_OF_STOCK"))))
                    .when()
                    .post(SELLER_CANCEL_BATCH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C19-6] reasonType 누락 - 400")
        void sellerCancelBatch_MissingReasonType_Returns400() {
            given().spec(givenSuperAdmin())
                    .body(Map.of("items", List.of(Map.of("orderId", "any-id", "cancelQty", 1))))
                    .when()
                    .post(SELLER_CANCEL_BATCH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    @Nested
    @DisplayName("C20: POST /cancels/approve/batch - 취소 승인")
    class ApproveCancelBatchTest {

        @Test
        @Tag("P0")
        @DisplayName("[C20-1] REQUESTED 상태 Cancel 승인 성공")
        void approveBatch_RequestedCancel_Success() {
            String orderItemId = seedOrderItem("order-app-001");
            cancelRepository.save(
                    CancelJpaEntityFixtures.requestedEntity(
                            "cancel-approve-001", orderItemId, 10L));

            given().spec(givenSuperAdmin())
                    .body(Map.of("cancelIds", List.of("cancel-approve-001")))
                    .when()
                    .post(APPROVE_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalCount", equalTo(1))
                    .body("data.successCount", equalTo(1))
                    .body("data.failureCount", equalTo(0));

            var approvedCancel = cancelRepository.findById("cancel-approve-001").orElseThrow();
            assertThat(approvedCancel.getCancelStatus()).isEqualTo("APPROVED");

            var updatedItem = orderItemRepository.findById(orderItemId).orElseThrow();
            assertThat(updatedItem.getOrderItemStatus()).isEqualTo("CANCELLED");
        }

        @Test
        @Tag("P0")
        @DisplayName("[C20-2] 복수 Cancel 일괄 승인")
        void approveBatch_TwoCancels_Success() {
            String orderItemId1 = seedOrderItem("order-app-002");
            String orderItemId2 = seedOrderItem("order-app-003");
            cancelRepository.save(
                    CancelJpaEntityFixtures.requestedEntityWithNumber(
                            "cancel-app-001", "CAN-APP-001", orderItemId1, 10L));
            cancelRepository.save(
                    CancelJpaEntityFixtures.requestedEntityWithNumber(
                            "cancel-app-002", "CAN-APP-002", orderItemId2, 10L));

            given().spec(givenSuperAdmin())
                    .body(Map.of("cancelIds", List.of("cancel-app-001", "cancel-app-002")))
                    .when()
                    .post(APPROVE_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalCount", equalTo(2))
                    .body("data.successCount", equalTo(2))
                    .body("data.failureCount", equalTo(0));
        }

        @Test
        @Tag("P0")
        @DisplayName("[C20-3] cancelIds 빈 목록 - 400")
        void approveBatch_EmptyCancelIds_Returns400() {
            given().spec(givenSuperAdmin())
                    .body(Map.of("cancelIds", List.of()))
                    .when()
                    .post(APPROVE_BATCH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[C20-5] REJECTED 상태 Cancel 승인 시도 - 상태 전이 불가 (배치 실패 처리)")
        void approveBatch_RejectedCancel_FailsInBatch() {
            cancelRepository.save(
                    CancelJpaEntityFixtures.rejectedEntity(
                            "cancel-rejected-001", "order-item-rej-001", 10L));

            given().spec(givenSuperAdmin())
                    .body(Map.of("cancelIds", List.of("cancel-rejected-001")))
                    .when()
                    .post(APPROVE_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.failureCount", equalTo(1));
        }
    }

    @Nested
    @DisplayName("C21: POST /cancels/reject/batch - 취소 거절")
    class RejectCancelBatchTest {

        @Test
        @Tag("P0")
        @DisplayName("[C21-1] REQUESTED 상태 Cancel 거절 성공")
        void rejectBatch_RequestedCancel_Success() {
            String orderItemId = seedOrderItem("order-rej-001");
            cancelRepository.save(
                    CancelJpaEntityFixtures.requestedEntity("cancel-reject-001", orderItemId, 10L));

            given().spec(givenSuperAdmin())
                    .body(Map.of("cancelIds", List.of("cancel-reject-001")))
                    .when()
                    .post(REJECT_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalCount", equalTo(1))
                    .body("data.successCount", equalTo(1))
                    .body("data.failureCount", equalTo(0));

            var rejectedCancel = cancelRepository.findById("cancel-reject-001").orElseThrow();
            assertThat(rejectedCancel.getCancelStatus()).isEqualTo("REJECTED");

            var updatedItem = orderItemRepository.findById(orderItemId).orElseThrow();
            assertThat(updatedItem.getOrderItemStatus()).isEqualTo("READY");
        }

        @Test
        @Tag("P0")
        @DisplayName("[C21-2] cancelIds 빈 목록 - 400")
        void rejectBatch_EmptyCancelIds_Returns400() {
            given().spec(givenSuperAdmin())
                    .body(Map.of("cancelIds", List.of()))
                    .when()
                    .post(REJECT_BATCH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[C21-3] APPROVED 상태 Cancel 거절 시도 - 상태 전이 불가 (배치 실패 처리)")
        void rejectBatch_ApprovedCancel_FailsInBatch() {
            cancelRepository.save(
                    CancelJpaEntityFixtures.approvedEntity(
                            "cancel-approved-001", "order-item-app-001", 10L));

            given().spec(givenSuperAdmin())
                    .body(Map.of("cancelIds", List.of("cancel-approved-001")))
                    .when()
                    .post(REJECT_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.failureCount", equalTo(1));
        }
    }

    @Nested
    @DisplayName("C22: POST /cancels/{cancelId}/histories - 메모 등록")
    class AddCancelHistoryMemoTest {

        @Test
        @Tag("P0")
        @DisplayName("[C22-1] 존재하는 cancelId에 메모 등록 성공")
        void addMemo_ExistingCancel_Returns201() {
            String orderItemId = seedOrderItem("order-memo-001");
            cancelRepository.save(
                    CancelJpaEntityFixtures.requestedEntity("cancel-memo-001", orderItemId, 10L));

            given().spec(givenSuperAdmin())
                    .body(Map.of("message", "판매자 메모: 고객 요청으로 취소 처리함"))
                    .when()
                    .post(ADD_HISTORY, "cancel-memo-001")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("data", notNullValue());

            assertThat(claimHistoryRepository.count()).isEqualTo(1);
        }

        @Test
        @Tag("P0")
        @DisplayName("[C22-2] message 빈 문자열 - 400")
        void addMemo_EmptyMessage_Returns400() {
            given().spec(givenSuperAdmin())
                    .body(Map.of("message", ""))
                    .when()
                    .post(ADD_HISTORY, "any-cancel-id")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C22-3] message 누락 - 400")
        void addMemo_MissingMessage_Returns400() {
            given().spec(givenSuperAdmin())
                    .body(Map.of())
                    .when()
                    .post(ADD_HISTORY, "any-cancel-id")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[C22-4] 존재하지 않는 cancelId에 메모 등록 - 404")
        void addMemo_NonExistentCancel_Returns404() {
            given().spec(givenSuperAdmin())
                    .body(Map.of("message", "메모"))
                    .when()
                    .post(ADD_HISTORY, "non-existent-id")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
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
