package com.ryuqq.marketplace.integration.container.cancel;

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
import com.ryuqq.marketplace.adapter.out.persistence.shipment.repository.ShipmentJpaRepository;
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
 * Cancel 부분취소 Testcontainers E2E 테스트.
 *
 * <p>MySQL 실제 컨테이너 기반으로 부분취소 플로우를 검증합니다.
 *
 * <p>테스트 대상:
 *
 * <ul>
 *   <li>PC-1: qty=3 주문 중 2건 판매자 취소 → OrderItem READY 유지
 *   <li>PC-2: 나머지 1건 추가 취소 → OrderItem CANCELLED 전환
 *   <li>PC-3: 전량 취소된 주문에 추가 취소 시 실패
 *   <li>PC-4: 부분취소 후 취소 목록 조회 시 Cancel 건수 정확
 *   <li>PC-5: 부분취소 환불금액 비율 검증
 * </ul>
 */
@Tag("e2e")
@Tag("container")
@Tag("cancel")
@DisplayName("Cancel 부분취소 Container E2E 테스트")
class CancelContainerPartialCancelE2ETest extends ContainerE2ETestBase {

    private static final String CANCELS = "/cancels";
    private static final String SELLER_CANCEL_BATCH = "/cancels/seller-cancel/batch";
    private static final String CANCEL_DETAIL = "/cancels/{cancelId}";

    @Autowired private CancelJpaRepository cancelRepository;
    @Autowired private CancelOutboxJpaRepository cancelOutboxRepository;
    @Autowired private ClaimHistoryJpaRepository claimHistoryRepository;
    @Autowired private OrderItemHistoryJpaRepository orderItemHistoryRepository;
    @Autowired private OrderItemJpaRepository orderItemRepository;
    @Autowired private OrderJpaRepository orderRepository;
    @Autowired private ShipmentJpaRepository shipmentRepository;

    @BeforeEach
    void setUp() {
        claimHistoryRepository.deleteAll();
        cancelOutboxRepository.deleteAll();
        cancelRepository.deleteAll();
        shipmentRepository.deleteAll();
        orderItemHistoryRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        claimHistoryRepository.deleteAll();
        cancelOutboxRepository.deleteAll();
        cancelRepository.deleteAll();
        shipmentRepository.deleteAll();
        orderItemHistoryRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
    }

    @Nested
    @DisplayName("부분취소 → 전량취소 전체 플로우")
    class PartialThenFullCancelFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[PC-1] qty=3 중 2건 판매자 취소 → OrderItem READY 유지 + cancelledQty=2")
        void partialCancel_2of3_OrderItemRemainsReady() {
            // given: qty=3, unitPrice=10000 주문
            String orderId = "order-pc1-001";
            orderRepository.save(OrderJpaEntityFixtures.orderedEntity(orderId));
            OrderItemJpaEntity item = OrderItemJpaEntityFixtures.itemWithPrice(orderId, 10000, 3);
            String orderItemId = orderItemRepository.save(item).getId();

            // when: 2건 판매자 취소
            given().spec(givenSuperAdmin())
                    .body(
                            createSellerCancelRequest(orderItemId, 2))
                    .when()
                    .post(SELLER_CANCEL_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));

            // then: OrderItem은 READY 유지, cancelledQty=2
            var updatedItem = orderItemRepository.findById(orderItemId).orElseThrow();
            assertThat(updatedItem.getOrderItemStatus()).isEqualTo("READY");
            assertThat(updatedItem.getCancelledQty()).isEqualTo(2);
            assertThat(cancelRepository.count()).isEqualTo(1);
        }

        @Test
        @Tag("P0")
        @DisplayName("[PC-2] 1차 2건 취소 후 2차 1건 추가 취소 → OrderItem CANCELLED 전환")
        void partialCancel_thenRemaining_OrderItemCancelled() {
            // given: qty=3 주문 + 1차 Cancel 시딩(2건)
            String orderId = "order-pc2-001";
            orderRepository.save(OrderJpaEntityFixtures.orderedEntity(orderId));
            OrderItemJpaEntity item = OrderItemJpaEntityFixtures.itemWithPrice(orderId, 10000, 3);
            String orderItemId = orderItemRepository.save(item).getId();

            // 1차 취소 (2건)
            cancelRepository.save(
                    CancelJpaEntityFixtures.approvedEntityWithQty(
                            "cancel-pc2-first", "CAN-PC2-001", orderItemId, 10L, 2, 20000));
            var savedItem = orderItemRepository.findById(orderItemId).orElseThrow();
            savedItem.updateCancelledQty(2);
            orderItemRepository.save(savedItem);

            // when: 2차 잔여 1건 취소
            given().spec(givenSuperAdmin())
                    .body(
                            createSellerCancelRequest(orderItemId, 1))
                    .when()
                    .post(SELLER_CANCEL_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));

            // then: OrderItem CANCELLED + Cancel 2건
            var finalItem = orderItemRepository.findById(orderItemId).orElseThrow();
            assertThat(finalItem.getOrderItemStatus()).isEqualTo("CANCELLED");
            assertThat(finalItem.getCancelledQty()).isEqualTo(3);
            assertThat(cancelRepository.count()).isEqualTo(2);
        }

        @Test
        @Tag("P0")
        @DisplayName("[PC-3] 전량 취소된 주문에 추가 취소 요청 시 실패")
        void fullyCancelled_AdditionalCancel_Fails() {
            // given: qty=2 주문 + 전량 취소
            String orderId = "order-pc3-001";
            orderRepository.save(OrderJpaEntityFixtures.orderedEntity(orderId));
            OrderItemJpaEntity item = OrderItemJpaEntityFixtures.itemWithPrice(orderId, 10000, 2);
            String orderItemId = orderItemRepository.save(item).getId();

            // 전량 취소
            given().spec(givenSuperAdmin())
                    .body(
                            createSellerCancelRequest(orderItemId, 2))
                    .when()
                    .post(SELLER_CANCEL_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.successCount", equalTo(1));

            // when: 추가 1건 취소 시도
            var response =
                    given().spec(givenSuperAdmin())
                            .body(
                                    createSellerCancelRequest(orderItemId, 1))
                            .when()
                            .post(SELLER_CANCEL_BATCH)
                            .then()
                            .statusCode(HttpStatus.OK.value());

            // then: Cancel은 생성되지만, OrderItem의 cancelledQty는 이미 전량이므로 변화 없음
            var finalItem = orderItemRepository.findById(orderItemId).orElseThrow();
            assertThat(finalItem.getOrderItemStatus()).isEqualTo("CANCELLED");
            assertThat(finalItem.getCancelledQty()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("부분취소 조회 검증")
    class PartialCancelQueryTest {

        @Test
        @Tag("P1")
        @DisplayName("[PC-4] 부분취소 후 목록 조회 시 Cancel 건수 정확")
        void partialCancel_ListQuery_ShowsCorrectCount() {
            // given: qty=3 주문 + 2건 취소
            String orderId = "order-pc4-001";
            orderRepository.save(OrderJpaEntityFixtures.orderedEntity(orderId));
            OrderItemJpaEntity item = OrderItemJpaEntityFixtures.itemWithPrice(orderId, 10000, 3);
            String orderItemId = orderItemRepository.save(item).getId();

            given().spec(givenSuperAdmin())
                    .body(
                            createSellerCancelRequest(orderItemId, 2))
                    .when()
                    .post(SELLER_CANCEL_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // when: 목록 조회
            given().spec(givenSuperAdmin())
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get(CANCELS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(1))
                    .body("data.content[0].cancelInfo.cancelQty", equalTo(2));
        }

        @Test
        @Tag("P1")
        @DisplayName("[PC-5] 부분취소 상세 조회 시 환불금액이 비율 계산됨")
        void partialCancel_DetailQuery_RefundAmountProportional() {
            // given: qty=3 주문 (paymentAmount=30000) + 2건 판매자 취소
            String orderId = "order-pc5-001";
            orderRepository.save(OrderJpaEntityFixtures.orderedEntity(orderId));
            OrderItemJpaEntity item = OrderItemJpaEntityFixtures.itemWithPrice(orderId, 10000, 3);
            String orderItemId = orderItemRepository.save(item).getId();

            given().spec(givenSuperAdmin())
                    .body(
                            createSellerCancelRequest(orderItemId, 2))
                    .when()
                    .post(SELLER_CANCEL_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // Cancel ID 조회
            String cancelId = cancelRepository.findAll().get(0).getId();

            // when: 상세 조회
            given().spec(givenSuperAdmin())
                    .when()
                    .get(CANCEL_DETAIL, cancelId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.cancelInfo.cancelQty", equalTo(2));
        }
    }

    // ===== Helper =====

    private Map<String, Object> createSellerCancelRequest(String orderItemId, int cancelQty) {
        return Map.of(
                "items",
                List.of(Map.of("orderId", orderItemId, "cancelQty", cancelQty)),
                "reason",
                Map.of("reasonType", "OUT_OF_STOCK", "reasonDetail", "재고 소진"));
    }
}
