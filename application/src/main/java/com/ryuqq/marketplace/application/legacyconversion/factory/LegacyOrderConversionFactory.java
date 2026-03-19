package com.ryuqq.marketplace.application.legacyconversion.factory;

import com.ryuqq.marketplace.application.legacyconversion.dto.bundle.LegacyOrderConversionBundle;
import com.ryuqq.marketplace.application.legacyconversion.dto.result.LegacyOrderCompositeResult;
import com.ryuqq.marketplace.application.legacyconversion.dto.result.LegacyOrderResolvedIds;
import com.ryuqq.marketplace.application.legacyconversion.internal.LegacyOrderChannelResolver;
import com.ryuqq.marketplace.application.legacyconversion.internal.LegacyOrderStatusMapper;
import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import com.ryuqq.marketplace.domain.cancel.id.CancelId;
import com.ryuqq.marketplace.domain.cancel.id.CancelNumber;
import com.ryuqq.marketplace.domain.cancel.vo.CancelReason;
import com.ryuqq.marketplace.domain.cancel.vo.CancelReasonType;
import com.ryuqq.marketplace.domain.cancel.vo.CancelStatus;
import com.ryuqq.marketplace.domain.cancel.vo.CancelType;
import com.ryuqq.marketplace.domain.common.vo.Address;
import com.ryuqq.marketplace.domain.common.vo.Email;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.common.vo.PhoneNumber;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderIdMapping;
import com.ryuqq.marketplace.domain.order.aggregate.Order;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.order.id.OrderId;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.order.id.OrderItemNumber;
import com.ryuqq.marketplace.domain.order.id.OrderNumber;
import com.ryuqq.marketplace.domain.order.id.PaymentNumber;
import com.ryuqq.marketplace.domain.order.vo.BuyerInfo;
import com.ryuqq.marketplace.domain.order.vo.BuyerName;
import com.ryuqq.marketplace.domain.order.vo.ExternalOrderItemPrice;
import com.ryuqq.marketplace.domain.order.vo.ExternalOrderReference;
import com.ryuqq.marketplace.domain.order.vo.ExternalProductSnapshot;
import com.ryuqq.marketplace.domain.order.vo.InternalProductReference;
import com.ryuqq.marketplace.domain.order.vo.OrderItemStatus;
import com.ryuqq.marketplace.domain.order.vo.PaymentInfo;
import com.ryuqq.marketplace.domain.order.vo.ReceiverInfo;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
import com.ryuqq.marketplace.domain.refund.id.RefundClaimId;
import com.ryuqq.marketplace.domain.refund.id.RefundClaimNumber;
import com.ryuqq.marketplace.domain.refund.vo.RefundReason;
import com.ryuqq.marketplace.domain.refund.vo.RefundReasonType;
import com.ryuqq.marketplace.domain.refund.vo.RefundStatus;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import com.ryuqq.marketplace.domain.shipment.id.ShipmentId;
import com.ryuqq.marketplace.domain.shipment.id.ShipmentNumber;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentStatus;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * 레거시 주문 → 내부 도메인 객체 변환 Factory.
 *
 * <p>순수 변환 로직만 담당합니다. DB 조회/저장 없음.
 * LegacyOrderCompositeResult + 채널/상태/내부ID 정보를 받아 도메인 객체를 조립합니다.
 */
@Component
@SuppressWarnings("PMD.ExcessiveImports")
public class LegacyOrderConversionFactory {

    private static final String LEGACY_ACTOR = "LEGACY_MIGRATION";
    private static final long DEFAULT_SHOP_ID = 0L;

    /**
     * 레거시 주문 데이터로부터 전체 변환 번들을 생성합니다.
     *
     * @param composite        luxurydb 복합 조회 결과
     * @param channel          채널 식별 결과
     * @param statusResolution 상태 매핑 결과
     * @param externalOrderNo  결정된 외부 주문번호
     * @param legacyPaymentId  레거시 결제 ID
     * @param resolvedIds      내부 ID 변환 결과
     * @param now              현재 시각
     * @return 변환 번들 (Order + Cancel/Refund + Mapping)
     */
    public LegacyOrderConversionBundle create(
            LegacyOrderCompositeResult composite,
            LegacyOrderChannelResolver.ChannelResolution channel,
            LegacyOrderStatusMapper.OrderStatusResolution statusResolution,
            String externalOrderNo,
            long legacyPaymentId,
            LegacyOrderResolvedIds resolvedIds,
            Instant now) {

        String orderId = UUID.randomUUID().toString();
        String orderItemId = UUID.randomUUID().toString();
        OrderNumber orderNumber = OrderNumber.generate();

        Order order = buildOrder(orderId, orderItemId, orderNumber, composite, channel,
                statusResolution, externalOrderNo, resolvedIds, now);

        Shipment shipment = statusResolution.needsShipment()
                ? buildShipment(orderItemId, statusResolution, now)
                : null;

        Cancel cancel = statusResolution.hasCancel()
                ? buildCancel(orderItemId, composite, statusResolution, now)
                : null;

        RefundClaim refundClaim = statusResolution.hasRefund()
                ? buildRefund(orderItemId, composite, statusResolution, now)
                : null;

        LegacyOrderIdMapping mapping = LegacyOrderIdMapping.forNew(
                composite.legacyOrderId(),
                legacyPaymentId,
                orderId,
                channel.salesChannelId(),
                channel.channelName(),
                now);

        return new LegacyOrderConversionBundle(order, shipment, cancel, refundClaim, mapping);
    }

    // === Order 빌드 ===

    private Order buildOrder(
            String orderId,
            String orderItemId,
            OrderNumber orderNumber,
            LegacyOrderCompositeResult composite,
            LegacyOrderChannelResolver.ChannelResolution channel,
            LegacyOrderStatusMapper.OrderStatusResolution statusResolution,
            String externalOrderNo,
            LegacyOrderResolvedIds resolvedIds,
            Instant now) {

        OrderId id = OrderId.forNew(orderId);
        BuyerInfo buyerInfo = buildBuyerInfo(composite);
        PaymentInfo paymentInfo = buildPaymentInfo(composite);
        ExternalOrderReference externalRef = ExternalOrderReference.of(
                channel.salesChannelId(),
                DEFAULT_SHOP_ID,
                channel.channelName(),
                channel.channelName(),
                externalOrderNo,
                composite.orderDate());

        OrderItem orderItem = buildOrderItem(
                orderItemId, orderNumber, composite, statusResolution, resolvedIds);

        return Order.reconstitute(
                id, orderNumber,
                buyerInfo, paymentInfo, externalRef,
                composite.orderDate(), now,
                List.of(orderItem));
    }

    private OrderItem buildOrderItem(
            String orderItemId,
            OrderNumber orderNumber,
            LegacyOrderCompositeResult composite,
            LegacyOrderStatusMapper.OrderStatusResolution statusResolution,
            LegacyOrderResolvedIds resolvedIds) {

        OrderItemId id = OrderItemId.forNew(orderItemId);
        OrderItemNumber itemNumber = OrderItemNumber.generate(orderNumber, 1);

        // 내부 ID 사용 (변환된 값), seller_name/brand_name 스냅샷
        InternalProductReference internalProduct = InternalProductReference.of(
                resolvedIds.internalProductGroupId(),
                resolvedIds.internalProductId(),
                null,
                null,
                null,
                composite.productGroupName(),
                resolvedIds.brandName(),
                resolvedIds.sellerName(),
                composite.mainImageUrl());

        String externalProductId = composite.externalOrderPkId() != null
                ? composite.externalOrderPkId()
                : String.valueOf(composite.legacyProductId());

        String externalOptionName = composite.optionValues() != null && !composite.optionValues().isEmpty()
                ? String.join(", ", composite.optionValues())
                : null;

        ExternalProductSnapshot externalProduct = ExternalProductSnapshot.of(
                externalProductId, null,
                composite.productGroupName(), externalOptionName,
                composite.mainImageUrl());

        Money unitPrice = Money.of((int) composite.currentPrice());
        Money totalAmount = unitPrice.multiply(composite.quantity());
        ExternalOrderItemPrice price = ExternalOrderItemPrice.of(
                unitPrice, composite.quantity(), totalAmount, Money.zero(), totalAmount);

        ReceiverInfo receiverInfo = buildReceiverInfo(composite);
        OrderItemStatus itemStatus = resolveOrderItemStatus(statusResolution);

        return OrderItem.reconstitute(id, itemNumber, internalProduct, externalProduct,
                price, receiverInfo, itemStatus, List.of());
    }

    private OrderItemStatus resolveOrderItemStatus(
            LegacyOrderStatusMapper.OrderStatusResolution statusResolution) {
        if (statusResolution.hasCancel()
                && statusResolution.cancelStatus() == CancelStatus.COMPLETED) {
            return OrderItemStatus.CANCELLED;
        }
        if (statusResolution.hasRefund()
                && statusResolution.refundStatus() == RefundStatus.COMPLETED) {
            return OrderItemStatus.RETURNED;
        }
        if (statusResolution.hasRefund()
                && (statusResolution.refundStatus() == RefundStatus.REQUESTED
                        || statusResolution.refundStatus() == RefundStatus.COLLECTING
                        || statusResolution.refundStatus() == RefundStatus.COLLECTED)) {
            return OrderItemStatus.RETURN_REQUESTED;
        }
        return OrderItemStatus.READY;
    }

    // === Shipment 빌드 ===

    private Shipment buildShipment(
            String orderItemId,
            LegacyOrderStatusMapper.OrderStatusResolution statusResolution,
            Instant now) {

        ShipmentId shipmentId = ShipmentId.forNew(UUID.randomUUID().toString());
        ShipmentNumber shipmentNumber = ShipmentNumber.generate();
        ShipmentStatus targetStatus = statusResolution.shipmentStatus();

        return Shipment.reconstitute(
                shipmentId,
                shipmentNumber,
                OrderItemId.of(orderItemId),
                targetStatus,
                null,
                null,
                targetStatus.ordinal() >= ShipmentStatus.PREPARING.ordinal() ? now : null,
                targetStatus.ordinal() >= ShipmentStatus.SHIPPED.ordinal() ? now : null,
                targetStatus == ShipmentStatus.DELIVERED ? now : null,
                now,
                now);
    }

    // === Cancel 빌드 ===

    private Cancel buildCancel(
            String orderItemId,
            LegacyOrderCompositeResult composite,
            LegacyOrderStatusMapper.OrderStatusResolution statusResolution,
            Instant now) {

        CancelId cancelId = CancelId.generate();
        CancelNumber cancelNumber = CancelNumber.generate();
        CancelReason reason = new CancelReason(CancelReasonType.OTHER, "레거시 이관 데이터");
        CancelStatus cancelStatus = statusResolution.cancelStatus();

        Instant processedAt = (cancelStatus == CancelStatus.APPROVED
                || cancelStatus == CancelStatus.COMPLETED) ? now : null;
        Instant completedAt = cancelStatus == CancelStatus.COMPLETED ? now : null;
        String processedBy = cancelStatus != CancelStatus.REQUESTED ? LEGACY_ACTOR : null;

        return Cancel.reconstitute(
                cancelId, cancelNumber, OrderItemId.of(orderItemId),
                composite.legacySellerId(), composite.quantity(),
                CancelType.BUYER_CANCEL, cancelStatus, reason, null,
                LEGACY_ACTOR, processedBy,
                now, processedAt, completedAt, now, now);
    }

    // === RefundClaim 빌드 ===

    private RefundClaim buildRefund(
            String orderItemId,
            LegacyOrderCompositeResult composite,
            LegacyOrderStatusMapper.OrderStatusResolution statusResolution,
            Instant now) {

        RefundClaimId refundId = RefundClaimId.forNew(UUID.randomUUID().toString());
        RefundClaimNumber refundNumber = RefundClaimNumber.generate();
        RefundReason reason = RefundReason.of(RefundReasonType.OTHER, "레거시 이관 데이터");
        RefundStatus refundStatus = statusResolution.refundStatus();

        Instant processedAt = refundStatus != RefundStatus.REQUESTED ? now : null;
        Instant completedAt = refundStatus == RefundStatus.COMPLETED ? now : null;
        String processedBy = refundStatus != RefundStatus.REQUESTED ? LEGACY_ACTOR : null;

        return RefundClaim.reconstitute(
                refundId, refundNumber, OrderItemId.of(orderItemId),
                composite.legacySellerId(), composite.quantity(),
                refundStatus, reason, null, null, null,
                LEGACY_ACTOR, processedBy,
                now, processedAt, completedAt, now, now);
    }

    // === VO 빌드 ===

    private BuyerInfo buildBuyerInfo(LegacyOrderCompositeResult composite) {
        String name = composite.receiverName() != null ? composite.receiverName() : "미상";
        return BuyerInfo.of(
                BuyerName.of(name),
                Email.of("legacy-" + composite.legacyUserId() + "@placeholder.com"),
                buildSafePhoneNumber(composite.receiverPhone()));
    }

    private PaymentInfo buildPaymentInfo(LegacyOrderCompositeResult composite) {
        return PaymentInfo.of(
                PaymentNumber.of("LEGACY-" + composite.legacyPaymentId() + "-" + composite.legacyOrderId()),
                "LEGACY",
                Money.of((int) composite.orderAmount()),
                composite.orderDate());
    }

    private ReceiverInfo buildReceiverInfo(LegacyOrderCompositeResult composite) {
        String name = composite.receiverName() != null ? composite.receiverName() : "미상";
        Address address = Address.of(
                composite.receiverZipCode() != null ? composite.receiverZipCode() : "00000",
                composite.receiverAddress() != null ? composite.receiverAddress() : "주소 없음",
                composite.receiverAddressDetail());
        return ReceiverInfo.of(name, buildSafePhoneNumber(composite.receiverPhone()),
                address, composite.deliveryRequest());
    }

    private PhoneNumber buildSafePhoneNumber(String rawPhone) {
        if (rawPhone != null && !rawPhone.isBlank()) {
            try {
                return PhoneNumber.of(rawPhone);
            } catch (IllegalArgumentException ignored) {
                // 유효하지 않은 전화번호는 placeholder로 대체
            }
        }
        return PhoneNumber.of("000-0000-0000");
    }
}
