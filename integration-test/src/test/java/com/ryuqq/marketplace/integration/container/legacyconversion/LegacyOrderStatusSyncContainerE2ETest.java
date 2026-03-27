package com.ryuqq.marketplace.integration.container.legacyconversion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.adapter.out.persistence.canceloutbox.entity.CancelOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.canceloutbox.repository.CancelOutboxJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.LegacyOrderConversionOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.LegacyOrderIdMappingJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.repository.LegacyOrderConversionOutboxJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.repository.LegacyOrderIdMappingJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.OrderItemJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.OrderJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderItemJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.refundoutbox.entity.RefundOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.refundoutbox.repository.RefundOutboxJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.shipment.repository.ShipmentJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.shipmentoutbox.entity.ShipmentOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.shipmentoutbox.entity.ShipmentOutboxJpaEntity.OutboxType;
import com.ryuqq.marketplace.adapter.out.persistence.shipmentoutbox.repository.ShipmentOutboxJpaRepository;
import com.ryuqq.marketplace.application.legacyconversion.dto.result.LegacyOrderCompositeResult;
import com.ryuqq.marketplace.application.legacyconversion.internal.LegacyOrderConversionCoordinator;
import com.ryuqq.marketplace.application.legacyconversion.port.out.query.LegacyOrderCompositeQueryPort;
import com.ryuqq.marketplace.application.order.port.out.query.OrderQueryPort;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderConversionOutbox;
import com.ryuqq.marketplace.domain.legacyconversion.id.LegacyOrderConversionOutboxId;
import com.ryuqq.marketplace.domain.legacyconversion.vo.LegacyConversionOutboxStatus;
import com.ryuqq.marketplace.domain.common.vo.Address;
import com.ryuqq.marketplace.domain.common.vo.Email;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.common.vo.PhoneNumber;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderConversionOutbox;
import com.ryuqq.marketplace.domain.order.aggregate.Order;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.order.id.OrderId;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.order.id.OrderItemNumber;
import com.ryuqq.marketplace.domain.order.id.OrderNumber;
import com.ryuqq.marketplace.domain.order.id.PaymentNumber;
import com.ryuqq.marketplace.domain.order.query.OrderSearchCriteria;
import com.ryuqq.marketplace.domain.order.vo.BuyerInfo;
import com.ryuqq.marketplace.domain.order.vo.BuyerName;
import com.ryuqq.marketplace.domain.order.vo.ExternalOrderItemPrice;
import com.ryuqq.marketplace.domain.order.vo.ExternalOrderReference;
import com.ryuqq.marketplace.domain.order.vo.ExternalProductSnapshot;
import com.ryuqq.marketplace.domain.order.vo.InternalProductReference;
import com.ryuqq.marketplace.domain.order.vo.OrderItemStatus;
import com.ryuqq.marketplace.domain.order.vo.PaymentInfo;
import com.ryuqq.marketplace.domain.order.vo.ReceiverInfo;
import com.ryuqq.marketplace.integration.container.ContainerLegacyE2ETestBase;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * 레거시 주문 상태 동기화 Testcontainers 기반 통합 테스트.
 *
 * <p>실제 MySQL Testcontainers를 사용하여 다음 데이터 흐름을 검증합니다:
 *
 * <ul>
 *   <li>이미 이관된 주문의 상태 변경 감지
 *   <li>OrderItem 상태 업데이트 저장
 *   <li>Shipment/Cancel/Refund 도메인 객체 생성 및 저장
 *   <li>외부 채널 전파용 Outbox 생성
 *   <li>트랜잭션 원자성
 * </ul>
 */
@Tag("legacy")
@Tag("legacyconversion")
@DisplayName("레거시 주문 상태 동기화 Container E2E 테스트")
class LegacyOrderStatusSyncContainerE2ETest extends ContainerLegacyE2ETestBase {

    @Autowired private LegacyOrderConversionCoordinator coordinator;

    // 읽기 의존성 Mock (luxurydb + market Order 조회)
    @MockitoBean private LegacyOrderCompositeQueryPort legacyOrderCompositeQueryPort;
    @MockitoBean private OrderQueryPort orderQueryPort;

    // 쓰기 검증용 Repository (실제 Testcontainers MySQL)
    @Autowired private OrderJpaRepository orderJpaRepository;
    @Autowired private OrderItemJpaRepository orderItemJpaRepository;
    @Autowired private LegacyOrderIdMappingJpaRepository mappingJpaRepository;
    @Autowired private LegacyOrderConversionOutboxJpaRepository conversionOutboxJpaRepository;
    @Autowired private ShipmentJpaRepository shipmentJpaRepository;
    @Autowired private ShipmentOutboxJpaRepository shipmentOutboxJpaRepository;
    @Autowired private CancelOutboxJpaRepository cancelOutboxJpaRepository;
    @Autowired private RefundOutboxJpaRepository refundOutboxJpaRepository;

    private static final long LEGACY_ORDER_ID = 99999L;
    private static final long LEGACY_PAYMENT_ID = 88888L;
    private static final Instant NOW = Instant.now();

    private String orderId;
    private String orderItemId;

    @BeforeEach
    void cleanUp() {
        shipmentOutboxJpaRepository.deleteAll();
        cancelOutboxJpaRepository.deleteAll();
        refundOutboxJpaRepository.deleteAll();
        shipmentJpaRepository.deleteAll();
        conversionOutboxJpaRepository.deleteAll();
        mappingJpaRepository.deleteAll();
        orderItemJpaRepository.deleteAll();
        orderJpaRepository.deleteAll();
    }

    @Nested
    @DisplayName("SC1: 발주확인 동기화 (READY → CONFIRMED)")
    class ConfirmSyncTest {

        @Test
        @DisplayName("레거시 DELIVERY_PENDING 상태 변경 시 OrderItem CONFIRMED + Shipment + ShipmentOutbox 생성")
        void sync_DeliveryPending_CreatesShipmentAndConfirmsOrderItem() {
            // Arrange: market DB에 READY 상태 Order 생성
            setupOrderInDb(OrderItemStatus.READY);
            setupMappingInDb();

            // 레거시 composite: DELIVERY_PENDING (발주확인됨)
            mockLegacyComposite("DELIVERY_PENDING");

            // OrderQueryPort: 도메인 Order 반환
            mockOrderQueryPort(OrderItemStatus.READY);

            // Act: Coordinator 실행 (DB에 저장된 outbox를 reconstitute해서 전달)
            LegacyOrderConversionOutbox outbox = createOutboxInDb();
            coordinator.convert(outbox);

            // Assert: ShipmentOutbox CONFIRM 생성됨
            List<ShipmentOutboxJpaEntity> shipmentOutboxes = shipmentOutboxJpaRepository.findAll();
            assertThat(shipmentOutboxes)
                    .isNotEmpty()
                    .anyMatch(o -> OutboxType.CONFIRM == o.getOutboxType());

            // Assert: Shipment 생성됨
            assertThat(shipmentJpaRepository.findAll()).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("SC2: 취소 동기화 (READY → CANCELLED)")
    class CancelSyncTest {

        @Test
        @DisplayName("레거시 SALE_CANCELLED 상태 변경 시 Cancel + CancelOutbox 생성")
        void sync_SaleCancelled_CreatesCancelAndOutbox() {
            // Arrange
            setupOrderInDb(OrderItemStatus.READY);
            setupMappingInDb();
            mockLegacyComposite("SALE_CANCELLED");
            mockOrderQueryPort(OrderItemStatus.READY);

            // Act
            LegacyOrderConversionOutbox outbox = createOutboxInDb();
            coordinator.convert(outbox);

            // Assert: CancelOutbox 생성됨
            List<CancelOutboxJpaEntity> cancelOutboxes = cancelOutboxJpaRepository.findAll();
            assertThat(cancelOutboxes)
                    .isNotEmpty()
                    .anyMatch(o -> "APPROVE".equals(o.getOutboxType()));
        }
    }

    @Nested
    @DisplayName("SC3: 반품 요청 동기화 (CONFIRMED → RETURN_REQUESTED)")
    class RefundRequestSyncTest {

        @Test
        @DisplayName("레거시 RETURN_REQUEST 상태 변경 시 RefundClaim + RefundOutbox 생성")
        void sync_ReturnRequest_CreatesRefundAndOutbox() {
            // Arrange
            setupOrderInDb(OrderItemStatus.CONFIRMED);
            setupMappingInDb();
            mockLegacyComposite("RETURN_REQUEST");
            mockOrderQueryPort(OrderItemStatus.CONFIRMED);

            // Act
            LegacyOrderConversionOutbox outbox = createOutboxInDb();
            coordinator.convert(outbox);

            // Assert: RefundOutbox REQUEST 생성됨
            List<RefundOutboxJpaEntity> refundOutboxes = refundOutboxJpaRepository.findAll();
            assertThat(refundOutboxes)
                    .isNotEmpty()
                    .anyMatch(o -> "REQUEST".equals(o.getOutboxType()));
        }
    }

    @Nested
    @DisplayName("SC4: 상태 동일 시 건너뜀")
    class SameStatusTest {

        @Test
        @DisplayName("레거시 상태와 market 상태가 동일하면 Outbox가 생성되지 않음")
        void sync_SameStatus_NoOutboxCreated() {
            // Arrange: 둘 다 READY
            setupOrderInDb(OrderItemStatus.READY);
            setupMappingInDb();
            mockLegacyComposite("ORDER_PROCESSING");
            mockOrderQueryPort(OrderItemStatus.READY);

            // Act
            LegacyOrderConversionOutbox outbox = createOutboxInDb();
            coordinator.convert(outbox);

            // Assert: 아무 Outbox도 생성되지 않음
            assertThat(shipmentOutboxJpaRepository.findAll()).isEmpty();
            assertThat(cancelOutboxJpaRepository.findAll()).isEmpty();
            assertThat(refundOutboxJpaRepository.findAll()).isEmpty();
        }
    }

    @Nested
    @DisplayName("SC5: 송장번호 포함 발송 동기화")
    class ShipWithInvoiceTest {

        @Test
        @DisplayName("레거시 DELIVERY_PROCESSING + 송장번호 시 CONFIRM + SHIP Outbox 생성")
        void sync_DeliveryProcessingWithInvoice_CreatesConfirmAndShipOutbox() {
            // Arrange
            setupOrderInDb(OrderItemStatus.READY);
            setupMappingInDb();
            mockLegacyCompositeWithInvoice("DELIVERY_PROCESSING", "CJGLS", "1234567890");
            mockOrderQueryPort(OrderItemStatus.READY);

            // Act
            LegacyOrderConversionOutbox outbox = createOutboxInDb();
            coordinator.convert(outbox);

            // Assert: CONFIRM + SHIP Outbox 2개 생성
            List<ShipmentOutboxJpaEntity> outboxes = shipmentOutboxJpaRepository.findAll();
            assertThat(outboxes).hasSizeGreaterThanOrEqualTo(2);
            assertThat(outboxes)
                    .anyMatch(o -> OutboxType.CONFIRM == o.getOutboxType())
                    .anyMatch(o -> OutboxType.SHIP == o.getOutboxType());
        }
    }

    // ===== 헬퍼 메서드 =====

    private LegacyOrderConversionOutbox createOutboxInDb() {
        LegacyOrderConversionOutboxJpaEntity entity =
                LegacyOrderConversionOutboxJpaEntity.create(
                        null,
                        LEGACY_ORDER_ID,
                        LEGACY_PAYMENT_ID,
                        "PENDING",
                        0,
                        3,
                        null,
                        null,
                        NOW,
                        NOW,
                        0L);
        LegacyOrderConversionOutboxJpaEntity saved = conversionOutboxJpaRepository.save(entity);

        return LegacyOrderConversionOutbox.reconstitute(
                LegacyOrderConversionOutboxId.of(saved.getId()),
                saved.getLegacyOrderId(),
                saved.getLegacyPaymentId(),
                LegacyConversionOutboxStatus.PENDING,
                0,
                3,
                NOW,
                NOW,
                null,
                null,
                0L);
    }

    private void setupOrderInDb(OrderItemStatus status) {
        orderId = UUID.randomUUID().toString();
        OrderJpaEntity orderEntity = OrderJpaEntityFixtures.orderedEntity(orderId);
        orderJpaRepository.save(orderEntity);

        OrderItemJpaEntity itemEntity = OrderItemJpaEntityFixtures.defaultItem(orderId);
        orderItemId = itemEntity.getId();
        orderItemJpaRepository.save(itemEntity);
    }

    private void setupMappingInDb() {
        LegacyOrderIdMappingJpaEntity mapping =
                LegacyOrderIdMappingJpaEntity.create(
                        null,
                        LEGACY_ORDER_ID,
                        LEGACY_PAYMENT_ID,
                        orderId,
                        1L,
                        "SETOF",
                        NOW);
        mappingJpaRepository.save(mapping);
    }

    private void mockLegacyComposite(String orderStatus) {
        LegacyOrderCompositeResult composite = createComposite(orderStatus, null, null);
        given(legacyOrderCompositeQueryPort.fetchOrderComposite(LEGACY_ORDER_ID))
                .willReturn(Optional.of(composite));
    }

    private void mockLegacyCompositeWithInvoice(
            String orderStatus, String companyCode, String invoiceNo) {
        LegacyOrderCompositeResult composite =
                createComposite(orderStatus, companyCode, invoiceNo);
        given(legacyOrderCompositeQueryPort.fetchOrderComposite(LEGACY_ORDER_ID))
                .willReturn(Optional.of(composite));
    }

    private void mockOrderQueryPort(OrderItemStatus status) {
        Order order = buildDomainOrder(status);
        given(orderQueryPort.findById(OrderId.of(orderId))).willReturn(Optional.of(order));
        given(orderQueryPort.findByOrderNumber(any())).willReturn(Optional.empty());
        given(orderQueryPort.existsByExternalOrderNo(any(Long.class), any()))
                .willReturn(false);
        given(orderQueryPort.findByCriteria(any(OrderSearchCriteria.class)))
                .willReturn(Collections.emptyList());
        given(orderQueryPort.countByCriteria(any(OrderSearchCriteria.class))).willReturn(0L);
    }

    private Order buildDomainOrder(OrderItemStatus status) {
        OrderId oId = OrderId.of(orderId);
        OrderItemId oiId = OrderItemId.of(orderItemId);
        OrderNumber orderNumber = OrderNumber.of("ORD-TEST-0001");
        OrderItemNumber itemNumber = OrderItemNumber.of("ORD-TEST-0001-001");

        BuyerInfo buyerInfo =
                BuyerInfo.of(
                        BuyerName.of("홍길동"),
                        Email.of("buyer@example.com"),
                        PhoneNumber.of("010-1234-5678"));
        PaymentInfo paymentInfo =
                PaymentInfo.of(
                        PaymentNumber.of("PAY-TEST-001"), "CARD", Money.of(20000), NOW);
        ExternalOrderReference externalRef =
                ExternalOrderReference.of(1L, 0L, "SETOF", "SETOF", "EXT-001", NOW);
        InternalProductReference internalProduct =
                InternalProductReference.of(100L, 200L, 1L, 5L, "SKU-001", "상품", "브랜드", "셀러", null);
        ExternalProductSnapshot externalProduct =
                ExternalProductSnapshot.of("EXT-P-001", null, "상품", "옵션", null);
        ExternalOrderItemPrice price =
                ExternalOrderItemPrice.of(
                        Money.of(10000), 1, Money.of(10000), Money.zero(), Money.zero(), Money.of(10000));
        ReceiverInfo receiverInfo =
                ReceiverInfo.of(
                        "김수령",
                        PhoneNumber.of("010-9876-5432"),
                        Address.of("12345", "서울시 강남구", null),
                        null);

        int cancelledQty = status == OrderItemStatus.CANCELLED ? 1 : 0;
        int returnedQty = status == OrderItemStatus.RETURNED ? 1 : 0;

        OrderItem item =
                OrderItem.reconstitute(
                        oiId,
                        itemNumber,
                        internalProduct,
                        externalProduct,
                        price,
                        receiverInfo,
                        status,
                        null,
                        cancelledQty,
                        returnedQty,
                        List.of());

        return Order.reconstitute(
                oId, orderNumber, buyerInfo, paymentInfo, externalRef, NOW, NOW, List.of(item));
    }

    private LegacyOrderCompositeResult createComposite(
            String orderStatus, String companyCode, String invoiceNo) {
        return new LegacyOrderCompositeResult(
                LEGACY_ORDER_ID,
                LEGACY_PAYMENT_ID,
                1000L,
                1L,
                999L,
                20000L,
                orderStatus,
                1,
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
