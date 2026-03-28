package com.ryuqq.marketplace.application.legacyconversion.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.application.cancel.manager.CancelCommandManager;
import com.ryuqq.marketplace.application.cancel.manager.CancelOutboxCommandManager;
import com.ryuqq.marketplace.application.legacyconversion.dto.bundle.LegacyOrderStatusSyncBundle;
import com.ryuqq.marketplace.application.legacyconversion.dto.result.LegacyOrderCompositeResult;
import com.ryuqq.marketplace.application.order.manager.OrderCommandManager;
import com.ryuqq.marketplace.application.refund.manager.RefundCommandManager;
import com.ryuqq.marketplace.application.refund.manager.RefundOutboxCommandManager;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentCommandManager;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentOutboxCommandManager;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentReadManager;
import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import com.ryuqq.marketplace.domain.cancel.outbox.aggregate.CancelOutbox;
import com.ryuqq.marketplace.domain.cancel.outbox.vo.CancelOutboxType;
import com.ryuqq.marketplace.domain.cancel.vo.CancelStatus;
import com.ryuqq.marketplace.domain.order.OrderFixtures;
import com.ryuqq.marketplace.domain.order.aggregate.Order;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.order.vo.OrderItemStatus;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
import com.ryuqq.marketplace.domain.refund.outbox.aggregate.RefundOutbox;
import com.ryuqq.marketplace.domain.refund.outbox.vo.RefundOutboxType;
import com.ryuqq.marketplace.domain.refund.vo.RefundStatus;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import com.ryuqq.marketplace.domain.shipment.outbox.aggregate.ShipmentOutbox;
import com.ryuqq.marketplace.domain.shipment.outbox.vo.ShipmentOutboxType;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * LegacyOrderStatusUpdateFacade 단위 테스트.
 *
 * <p>상태 변경 시 도메인 객체 생성, Outbox 생성, OrderItem 상태 전환을 검증합니다.
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyOrderStatusUpdateFacade 단위 테스트")
class LegacyOrderStatusUpdateFacadeTest {

    @Mock private OrderCommandManager orderCommandManager;
    @Mock private ShipmentReadManager shipmentReadManager;
    @Mock private ShipmentCommandManager shipmentCommandManager;
    @Mock private ShipmentOutboxCommandManager shipmentOutboxCommandManager;
    @Mock private CancelCommandManager cancelCommandManager;
    @Mock private CancelOutboxCommandManager cancelOutboxCommandManager;
    @Mock private RefundCommandManager refundCommandManager;
    @Mock private RefundOutboxCommandManager refundOutboxCommandManager;

    @InjectMocks private LegacyOrderStatusUpdateFacade facade;

    @Captor private ArgumentCaptor<ShipmentOutbox> shipmentOutboxCaptor;
    @Captor private ArgumentCaptor<CancelOutbox> cancelOutboxCaptor;
    @Captor private ArgumentCaptor<RefundOutbox> refundOutboxCaptor;
    @Captor private ArgumentCaptor<Order> orderCaptor;

    private static final long LEGACY_ORDER_ID = 12345L;
    private static final Instant NOW = Instant.parse("2026-03-27T10:00:00Z");

    @Nested
    @DisplayName("발주확인 (READY → CONFIRMED)")
    class ConfirmTest {

        @Test
        @DisplayName("Shipment 생성 + CONFIRM Outbox + OrderItem CONFIRMED 전환")
        void syncStatus_WithDeliveryPending_CreatesShipmentAndConfirms() {
            // given
            Order order = OrderFixtures.reconstitutedOrder();
            OrderItem orderItem = order.items().get(0);
            assertThat(orderItem.status()).isEqualTo(OrderItemStatus.READY);

            LegacyOrderStatusMapper.OrderStatusResolution resolution =
                    LegacyOrderStatusMapper.OrderStatusResolution.normalOrder(ShipmentStatus.READY);

            LegacyOrderStatusSyncBundle bundle =
                    new LegacyOrderStatusSyncBundle(
                            order, orderItem, OrderItemStatus.READY, resolution, LEGACY_ORDER_ID);

            LegacyOrderCompositeResult composite = createComposite("DELIVERY_PENDING");

            given(shipmentReadManager.findByOrderItemId(orderItem.id()))
                    .willReturn(Optional.empty());

            // when
            facade.syncStatus(bundle, composite, NOW);

            // then — Shipment 생성
            then(shipmentCommandManager).should().persist(any(Shipment.class));

            // then — ShipmentOutbox CONFIRM 생성
            then(shipmentOutboxCommandManager).should().persist(shipmentOutboxCaptor.capture());
            assertThat(shipmentOutboxCaptor.getValue().outboxType())
                    .isEqualTo(ShipmentOutboxType.CONFIRM);

            // then — OrderItem CONFIRMED 전환
            assertThat(orderItem.status()).isEqualTo(OrderItemStatus.CONFIRMED);

            // then — Order 영속화
            then(orderCommandManager).should().persist(order);

            // then — Cancel/Refund 미생성
            then(cancelCommandManager).shouldHaveNoInteractions();
            then(refundCommandManager).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("취소 (→ CANCELLED)")
    class CancelTest {

        @Test
        @DisplayName("Cancel 생성 + APPROVE Outbox + OrderItem CANCELLED 전환")
        void syncStatus_WithCancelCompleted_CreatesCancelAndCancels() {
            // given
            Order order = OrderFixtures.reconstitutedOrder();
            OrderItem orderItem = order.items().get(0);

            LegacyOrderStatusMapper.OrderStatusResolution resolution =
                    LegacyOrderStatusMapper.OrderStatusResolution.withCancel(
                            ShipmentStatus.DELIVERED, CancelStatus.COMPLETED);

            LegacyOrderStatusSyncBundle bundle =
                    new LegacyOrderStatusSyncBundle(
                            order, orderItem, OrderItemStatus.READY, resolution, LEGACY_ORDER_ID);

            LegacyOrderCompositeResult composite = createComposite("SALE_CANCELLED");

            given(shipmentReadManager.findByOrderItemId(orderItem.id()))
                    .willReturn(Optional.empty());

            // when
            facade.syncStatus(bundle, composite, NOW);

            // then — Cancel 생성
            then(cancelCommandManager).should().persist(any(Cancel.class));

            // then — CancelOutbox APPROVE 생성
            then(cancelOutboxCommandManager).should().persist(cancelOutboxCaptor.capture());
            assertThat(cancelOutboxCaptor.getValue().outboxType())
                    .isEqualTo(CancelOutboxType.APPROVE);

            // then — OrderItem CANCELLED 전환
            assertThat(orderItem.status()).isEqualTo(OrderItemStatus.CANCELLED);

            // then — Order 영속화
            then(orderCommandManager).should().persist(order);
        }
    }

    @Nested
    @DisplayName("반품 요청 (CONFIRMED → RETURN_REQUESTED)")
    class RefundRequestTest {

        @Test
        @DisplayName("RefundClaim 생성 + REQUEST Outbox + OrderItem RETURN_REQUESTED 전환")
        void syncStatus_WithReturnRequest_CreatesRefundAndRequestsReturn() {
            // given
            Order order =
                    Order.reconstitute(
                            OrderFixtures.defaultOrderId(),
                            OrderFixtures.defaultOrderNumber(),
                            OrderFixtures.defaultBuyerInfo(),
                            OrderFixtures.defaultPaymentInfo(),
                            OrderFixtures.defaultExternalOrderReference(),
                            NOW.minusSeconds(86400),
                            NOW,
                            List.of(OrderFixtures.confirmedOrderItem()));

            OrderItem orderItem = order.items().get(0);
            assertThat(orderItem.status()).isEqualTo(OrderItemStatus.CONFIRMED);

            LegacyOrderStatusMapper.OrderStatusResolution resolution =
                    LegacyOrderStatusMapper.OrderStatusResolution.withRefund(
                            ShipmentStatus.DELIVERED, RefundStatus.REQUESTED);

            LegacyOrderStatusSyncBundle bundle =
                    new LegacyOrderStatusSyncBundle(
                            order,
                            orderItem,
                            OrderItemStatus.CONFIRMED,
                            resolution,
                            LEGACY_ORDER_ID);

            LegacyOrderCompositeResult composite = createComposite("RETURN_REQUEST");

            given(shipmentReadManager.findByOrderItemId(orderItem.id()))
                    .willReturn(Optional.empty());

            // when
            facade.syncStatus(bundle, composite, NOW);

            // then — RefundClaim 생성
            then(refundCommandManager).should().persist(any(RefundClaim.class));

            // then — RefundOutbox REQUEST 생성
            then(refundOutboxCommandManager).should().persist(refundOutboxCaptor.capture());
            assertThat(refundOutboxCaptor.getValue().outboxType())
                    .isEqualTo(RefundOutboxType.REQUEST);

            // then — OrderItem RETURN_REQUESTED 전환
            assertThat(orderItem.status()).isEqualTo(OrderItemStatus.RETURN_REQUESTED);

            // then — Order 영속화
            then(orderCommandManager).should().persist(order);
        }
    }

    @Nested
    @DisplayName("반품 완료 (CONFIRMED → RETURN_REQUESTED → RETURNED)")
    class RefundCompleteTest {

        @Test
        @DisplayName("RefundClaim 생성 + COMPLETE Outbox + OrderItem RETURNED 전환")
        void syncStatus_WithReturnCompleted_CreatesRefundAndCompletes() {
            // given
            Order order =
                    Order.reconstitute(
                            OrderFixtures.defaultOrderId(),
                            OrderFixtures.defaultOrderNumber(),
                            OrderFixtures.defaultBuyerInfo(),
                            OrderFixtures.defaultPaymentInfo(),
                            OrderFixtures.defaultExternalOrderReference(),
                            NOW.minusSeconds(86400),
                            NOW,
                            List.of(OrderFixtures.confirmedOrderItem()));

            OrderItem orderItem = order.items().get(0);

            LegacyOrderStatusMapper.OrderStatusResolution resolution =
                    LegacyOrderStatusMapper.OrderStatusResolution.withRefund(
                            ShipmentStatus.DELIVERED, RefundStatus.COMPLETED);

            LegacyOrderStatusSyncBundle bundle =
                    new LegacyOrderStatusSyncBundle(
                            order,
                            orderItem,
                            OrderItemStatus.CONFIRMED,
                            resolution,
                            LEGACY_ORDER_ID);

            LegacyOrderCompositeResult composite = createComposite("RETURN_REQUEST_COMPLETED");

            given(shipmentReadManager.findByOrderItemId(orderItem.id()))
                    .willReturn(Optional.empty());

            // when
            facade.syncStatus(bundle, composite, NOW);

            // then — RefundClaim 생성
            then(refundCommandManager).should().persist(any(RefundClaim.class));

            // then — RefundOutbox COMPLETE 생성
            then(refundOutboxCommandManager).should().persist(refundOutboxCaptor.capture());
            assertThat(refundOutboxCaptor.getValue().outboxType())
                    .isEqualTo(RefundOutboxType.COMPLETE);

            // then — OrderItem RETURNED 전환
            assertThat(orderItem.status()).isEqualTo(OrderItemStatus.RETURNED);

            // then — Order 영속화
            then(orderCommandManager).should().persist(order);
        }
    }

    @Nested
    @DisplayName("배송 중 + 송장번호 존재 시 SHIP Outbox 추가 생성")
    class ShipOutboxTest {

        @Test
        @DisplayName("IN_TRANSIT + 송장번호가 있으면 CONFIRM + SHIP Outbox 두 개 생성")
        void syncStatus_WithInTransitAndInvoiceNo_CreatesBothConfirmAndShipOutbox() {
            // given
            Order order = OrderFixtures.reconstitutedOrder();
            OrderItem orderItem = order.items().get(0);

            LegacyOrderStatusMapper.OrderStatusResolution resolution =
                    LegacyOrderStatusMapper.OrderStatusResolution.normalOrder(
                            ShipmentStatus.IN_TRANSIT);

            LegacyOrderStatusSyncBundle bundle =
                    new LegacyOrderStatusSyncBundle(
                            order, orderItem, OrderItemStatus.READY, resolution, LEGACY_ORDER_ID);

            LegacyOrderCompositeResult composite =
                    createCompositeWithInvoice("DELIVERY_PROCESSING", "CJGLS", "1234567890");

            given(shipmentReadManager.findByOrderItemId(orderItem.id()))
                    .willReturn(Optional.empty());

            // when
            facade.syncStatus(bundle, composite, NOW);

            // then — Shipment 생성
            then(shipmentCommandManager).should().persist(any(Shipment.class));

            // then — ShipmentOutbox 두 번 호출 (CONFIRM, SHIP)
            then(shipmentOutboxCommandManager)
                    .should(times(2))
                    .persist(shipmentOutboxCaptor.capture());

            List<ShipmentOutbox> capturedOutboxes = shipmentOutboxCaptor.getAllValues();
            assertThat(capturedOutboxes)
                    .extracting(ShipmentOutbox::outboxType)
                    .containsExactly(ShipmentOutboxType.CONFIRM, ShipmentOutboxType.SHIP);

            // then — OrderItem CONFIRMED 전환
            assertThat(orderItem.status()).isEqualTo(OrderItemStatus.CONFIRMED);
        }
    }

    private LegacyOrderCompositeResult createComposite(String orderStatus) {
        return new LegacyOrderCompositeResult(
                LEGACY_ORDER_ID,
                67890L,
                1000L,
                1L,
                999L,
                20000L,
                orderStatus,
                2,
                NOW,
                100L,
                "테스트 상품",
                1L,
                "브랜드",
                1L,
                30000L,
                20000L,
                0L,
                0L,
                List.of(),
                null,
                null,
                null,
                null,
                "홍길동",
                "010-1234-5678",
                "12345",
                "서울시 강남구",
                null,
                null,
                null,
                null,
                null,
                List.of());
    }

    private LegacyOrderCompositeResult createCompositeWithInvoice(
            String orderStatus, String companyCode, String invoiceNo) {
        return new LegacyOrderCompositeResult(
                LEGACY_ORDER_ID,
                67890L,
                1000L,
                1L,
                999L,
                20000L,
                orderStatus,
                2,
                NOW,
                100L,
                "테스트 상품",
                1L,
                "브랜드",
                1L,
                30000L,
                20000L,
                0L,
                0L,
                List.of(),
                null,
                null,
                null,
                null,
                "홍길동",
                "010-1234-5678",
                "12345",
                "서울시 강남구",
                null,
                null,
                invoiceNo,
                companyCode,
                NOW,
                List.of());
    }
}
