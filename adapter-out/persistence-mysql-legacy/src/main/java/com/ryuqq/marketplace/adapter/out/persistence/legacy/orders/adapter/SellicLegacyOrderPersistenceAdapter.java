package com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity.LegacyExternalOrderEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity.LegacyInterlockingOrderEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity.LegacyOrderEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity.LegacyOrderHistoryEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity.LegacyPaymentBillEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity.LegacyPaymentEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity.LegacyPaymentSnapshotShippingAddressEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity.LegacySettlementEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity.LegacyShipmentEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.repository.LegacyExternalOrderJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.repository.LegacyInterlockingOrderJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.repository.LegacyOrderHistoryJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.repository.LegacyOrderJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.repository.LegacyPaymentBillJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.repository.LegacyPaymentJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.repository.LegacyPaymentSnapshotShippingAddressJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.repository.LegacySettlementJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.repository.LegacyShipmentJpaRepository;
import com.ryuqq.marketplace.application.legacy.sellicorder.dto.command.IssueSellicLegacyOrderCommand;
import com.ryuqq.marketplace.application.legacy.sellicorder.port.out.SellicLegacyOrderPersistencePort;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 셀릭 주문 luxurydb 저장 Adapter.
 *
 * <p>{@link SellicLegacyOrderPersistencePort} 구현체. 단일 트랜잭션으로 payment → orders → shipment →
 * settlement → external_order → interlocking_order → payment_snapshot_shipping_address →
 * orders_history 순서로 INSERT합니다.
 */
@Component
public class SellicLegacyOrderPersistenceAdapter implements SellicLegacyOrderPersistencePort {

    private static final String SYSTEM_OPERATOR = "SELLIC_BATCH";

    private final LegacyPaymentJpaRepository paymentRepository;
    private final LegacyPaymentBillJpaRepository paymentBillRepository;
    private final LegacyOrderJpaRepository orderRepository;
    private final LegacyShipmentJpaRepository shipmentRepository;
    private final LegacySettlementJpaRepository settlementRepository;
    private final LegacyExternalOrderJpaRepository externalOrderRepository;
    private final LegacyInterlockingOrderJpaRepository interlockingOrderRepository;
    private final LegacyPaymentSnapshotShippingAddressJpaRepository shippingAddressRepository;
    private final LegacyOrderHistoryJpaRepository historyRepository;

    public SellicLegacyOrderPersistenceAdapter(
            LegacyPaymentJpaRepository paymentRepository,
            LegacyPaymentBillJpaRepository paymentBillRepository,
            LegacyOrderJpaRepository orderRepository,
            LegacyShipmentJpaRepository shipmentRepository,
            LegacySettlementJpaRepository settlementRepository,
            LegacyExternalOrderJpaRepository externalOrderRepository,
            LegacyInterlockingOrderJpaRepository interlockingOrderRepository,
            LegacyPaymentSnapshotShippingAddressJpaRepository shippingAddressRepository,
            LegacyOrderHistoryJpaRepository historyRepository) {
        this.paymentRepository = paymentRepository;
        this.paymentBillRepository = paymentBillRepository;
        this.orderRepository = orderRepository;
        this.shipmentRepository = shipmentRepository;
        this.settlementRepository = settlementRepository;
        this.externalOrderRepository = externalOrderRepository;
        this.interlockingOrderRepository = interlockingOrderRepository;
        this.shippingAddressRepository = shippingAddressRepository;
        this.historyRepository = historyRepository;
    }

    @Override
    @Transactional("legacyTransactionManager")
    public long persist(IssueSellicLegacyOrderCommand command) {
        // 1. Payment
        LegacyPaymentEntity payment = savePayment(command.payment());
        long paymentId = payment.getId();

        // 2. Payment Bill
        savePaymentBill(paymentId, command.payment());

        // 3. Payment Snapshot Shipping Address
        LegacyPaymentSnapshotShippingAddressEntity shippingAddress =
                saveShippingAddress(paymentId, command.shippingAddress());

        // 4. Order
        LegacyOrderEntity order = saveOrder(paymentId, command.order());
        long orderId = order.getId();

        // 5. Shipment
        saveShipment(orderId, shippingAddress.getId(), command.shipment());

        // 6. Settlement
        saveSettlement(orderId, command.settlement());

        // 7. External Order
        saveExternalOrder(paymentId, orderId, command.externalOrder());

        // 8. Interlocking Order
        saveInterlockingOrder(paymentId, orderId, command.interlockingOrder());

        // 9. Order History (초기 이력)
        saveOrderHistory(orderId, command.order().orderStatus());

        return orderId;
    }

    private LegacyPaymentEntity savePayment(IssueSellicLegacyOrderCommand.Payment cmd) {
        LocalDateTime paymentDate =
                LocalDateTime.ofInstant(cmd.paymentDate(), ZoneId.of("Asia/Seoul"));
        LegacyPaymentEntity entity =
                LegacyPaymentEntity.create(
                        cmd.userId(),
                        cmd.paymentAmount(),
                        cmd.paymentStatus(),
                        cmd.siteName(),
                        paymentDate,
                        SYSTEM_OPERATOR);
        return paymentRepository.save(entity);
    }

    private void savePaymentBill(long paymentId, IssueSellicLegacyOrderCommand.Payment cmd) {
        LegacyPaymentBillEntity entity =
                LegacyPaymentBillEntity.create(
                        paymentId,
                        cmd.userId(),
                        cmd.paymentAmount(),
                        cmd.buyerName(),
                        cmd.buyerEmail(),
                        cmd.buyerPhone(),
                        cmd.paymentUniqueId(),
                        cmd.paymentChannel(),
                        SYSTEM_OPERATOR);
        paymentBillRepository.save(entity);
    }

    private LegacyPaymentSnapshotShippingAddressEntity saveShippingAddress(
            long paymentId, IssueSellicLegacyOrderCommand.ShippingAddress cmd) {
        LegacyPaymentSnapshotShippingAddressEntity entity =
                LegacyPaymentSnapshotShippingAddressEntity.create(
                        paymentId,
                        cmd.receiverName(),
                        cmd.phoneNumber(),
                        cmd.addressLine1(),
                        cmd.zipCode(),
                        cmd.deliveryRequest());
        return shippingAddressRepository.save(entity);
    }

    private LegacyOrderEntity saveOrder(long paymentId, IssueSellicLegacyOrderCommand.Order cmd) {
        LegacyOrderEntity entity =
                LegacyOrderEntity.create(
                        paymentId,
                        cmd.productId(),
                        cmd.sellerId(),
                        cmd.userId(),
                        cmd.orderAmount(),
                        cmd.orderStatus(),
                        cmd.quantity(),
                        SYSTEM_OPERATOR);
        return orderRepository.save(entity);
    }

    private void saveShipment(
            long orderId, long shippingAddressId, IssueSellicLegacyOrderCommand.Shipment cmd) {
        LegacyShipmentEntity entity =
                LegacyShipmentEntity.create(
                        orderId,
                        shippingAddressId,
                        cmd.senderName(),
                        cmd.senderEmail(),
                        cmd.senderPhone(),
                        cmd.deliveryStatus(),
                        SYSTEM_OPERATOR);
        shipmentRepository.save(entity);
    }

    private void saveSettlement(long orderId, IssueSellicLegacyOrderCommand.Settlement cmd) {
        LegacySettlementEntity entity =
                LegacySettlementEntity.create(orderId, cmd.sellerCommissionRate(), SYSTEM_OPERATOR);
        settlementRepository.save(entity);
    }

    private void saveExternalOrder(
            long paymentId, long orderId, IssueSellicLegacyOrderCommand.ExternalOrder cmd) {
        LegacyExternalOrderEntity entity =
                LegacyExternalOrderEntity.create(
                        cmd.siteId(),
                        paymentId,
                        orderId,
                        cmd.externalIdx(),
                        cmd.externalOrderPkId());
        externalOrderRepository.save(entity);
    }

    private void saveInterlockingOrder(
            long paymentId, long orderId, IssueSellicLegacyOrderCommand.InterlockingOrder cmd) {
        LegacyInterlockingOrderEntity entity =
                LegacyInterlockingOrderEntity.create(
                        cmd.externalIdx(),
                        cmd.externalOrderId(),
                        cmd.interlockingSiteId(),
                        cmd.siteName(),
                        paymentId,
                        orderId);
        interlockingOrderRepository.save(entity);
    }

    private void saveOrderHistory(long orderId, String orderStatus) {
        LegacyOrderHistoryEntity entity =
                LegacyOrderHistoryEntity.create(orderId, orderStatus, "셀릭 주문 수신", "SELLIC_BATCH");
        historyRepository.save(entity);
    }
}
