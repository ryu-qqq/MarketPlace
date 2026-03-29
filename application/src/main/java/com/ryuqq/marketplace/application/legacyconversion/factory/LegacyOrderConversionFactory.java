package com.ryuqq.marketplace.application.legacyconversion.factory;

import com.ryuqq.marketplace.application.common.port.out.IdGeneratorPort;
import com.ryuqq.marketplace.application.legacyconversion.dto.bundle.LegacyOrderConversionBundle;
import com.ryuqq.marketplace.application.legacyconversion.dto.result.LegacyOrderCompositeResult;
import com.ryuqq.marketplace.application.legacyconversion.dto.result.LegacyOrderHistoryEntry;
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
import com.ryuqq.marketplace.domain.order.aggregate.OrderItemHistory;
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
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentMethod;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentMethodType;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * 레거시 주문 → 내부 도메인 객체 변환 Factory.
 *
 * <p>순수 변환 로직만 담당합니다. DB 조회/저장 없음. LegacyOrderCompositeResult + 채널/상태/내부ID 정보를 받아 도메인 객체를 조립합니다.
 *
 * <p>shipment 테이블의 운송장/택배사 정보와 orders_history 테이블의 타임스탬프/사유를 활용하여 실제 레거시 데이터를 최대한 보존합니다.
 */
@Component
@SuppressWarnings({"PMD.ExcessiveImports", "PMD.GodClass"})
public class LegacyOrderConversionFactory {

    private final IdGeneratorPort idGeneratorPort;

    public LegacyOrderConversionFactory(IdGeneratorPort idGeneratorPort) {
        this.idGeneratorPort = idGeneratorPort;
    }

    private static final String LEGACY_ACTOR = "LEGACY_MIGRATION";
    private static final long DEFAULT_SHOP_ID = 0L;

    private static final Set<String> CANCEL_STATUSES =
            Set.of(
                    "SALE_CANCELLED",
                    "SALE_CANCELLED_COMPLETED",
                    "CANCEL_REQUEST",
                    "CANCEL_REQUEST_CONFIRMED",
                    "CANCEL_REQUEST_COMPLETED");

    private static final Set<String> REFUND_STATUSES =
            Set.of(
                    "RETURN_REQUEST",
                    "RETURN_REQUEST_CONFIRMED",
                    "RETURN_REQUEST_COMPLETED",
                    "RETURN_REQUEST_REJECTED");

    /**
     * 레거시 주문 데이터로부터 전체 변환 번들을 생성합니다.
     *
     * @param composite luxurydb 복합 조회 결과 (shipment + history 포함)
     * @param channel 채널 식별 결과
     * @param statusResolution 상태 매핑 결과
     * @param externalOrderNo 결정된 외부 주문번호
     * @param legacyPaymentId 레거시 결제 ID
     * @param resolvedIds 내부 ID 변환 결과
     * @param now 현재 시각
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
        Long orderItemId = null; // auto_increment — persist 후 PersistenceFacade에서 할당
        OrderNumber orderNumber = OrderNumber.generate();

        Order order =
                buildOrder(
                        orderId,
                        orderItemId,
                        orderNumber,
                        composite,
                        channel,
                        statusResolution,
                        externalOrderNo,
                        resolvedIds,
                        now);

        Shipment shipment =
                statusResolution.needsShipment()
                        ? buildShipment(orderItemId, composite, statusResolution, now)
                        : null;

        Cancel cancel =
                statusResolution.hasCancel()
                        ? buildCancel(orderItemId, composite, statusResolution, now)
                        : null;

        RefundClaim refundClaim =
                statusResolution.hasRefund()
                        ? buildRefund(orderItemId, composite, statusResolution, now)
                        : null;

        LegacyOrderIdMapping mapping =
                LegacyOrderIdMapping.forNew(
                        composite.legacyOrderId(),
                        legacyPaymentId,
                        orderId,
                        orderItemId,
                        channel.salesChannelId(),
                        channel.channelName(),
                        now);

        return new LegacyOrderConversionBundle(order, shipment, cancel, refundClaim, mapping);
    }

    // === Order 빌드 ===

    private Order buildOrder(
            String orderId,
            Long orderItemId,
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
        ExternalOrderReference externalRef =
                ExternalOrderReference.of(
                        channel.salesChannelId(),
                        DEFAULT_SHOP_ID,
                        channel.channelName(),
                        channel.channelName(),
                        externalOrderNo,
                        composite.orderDate());

        OrderItem orderItem =
                buildOrderItem(
                        orderItemId, orderNumber, composite, statusResolution, resolvedIds, now);

        return Order.reconstitute(
                id,
                orderNumber,
                buyerInfo,
                paymentInfo,
                externalRef,
                composite.orderDate(),
                now,
                List.of(orderItem));
    }

    private OrderItem buildOrderItem(
            Long orderItemId,
            OrderNumber orderNumber,
            LegacyOrderCompositeResult composite,
            LegacyOrderStatusMapper.OrderStatusResolution statusResolution,
            LegacyOrderResolvedIds resolvedIds,
            Instant now) {

        OrderItemId id = OrderItemId.forNew();
        OrderItemNumber itemNumber = OrderItemNumber.generate(orderNumber, 1);

        InternalProductReference internalProduct =
                InternalProductReference.of(
                        resolvedIds.internalProductGroupId(),
                        resolvedIds.internalProductId(),
                        null,
                        null,
                        null,
                        composite.productGroupName(),
                        resolvedIds.brandName(),
                        resolvedIds.sellerName(),
                        composite.mainImageUrl());

        String externalProductId =
                composite.externalOrderPkId() != null
                        ? composite.externalOrderPkId()
                        : String.valueOf(composite.legacyProductId());

        String externalOptionName =
                composite.optionValues() != null && !composite.optionValues().isEmpty()
                        ? String.join(", ", composite.optionValues())
                        : null;

        ExternalProductSnapshot externalProduct =
                ExternalProductSnapshot.of(
                        externalProductId,
                        null,
                        composite.productGroupName(),
                        externalOptionName,
                        composite.mainImageUrl());

        Money unitPrice = Money.of((int) composite.currentPrice());
        Money totalAmount = unitPrice.multiply(composite.quantity());
        ExternalOrderItemPrice price =
                ExternalOrderItemPrice.of(
                        unitPrice,
                        composite.quantity(),
                        totalAmount,
                        Money.zero(),
                        Money.zero(),
                        totalAmount);

        ReceiverInfo receiverInfo = buildReceiverInfo(composite);
        OrderItemStatus itemStatus = resolveOrderItemStatus(statusResolution);

        // 이관 이력 1건 생성 (null → 현재 상태)
        OrderItemHistory migrationHistory =
                OrderItemHistory.of(
                        id,
                        null,
                        itemStatus,
                        LEGACY_ACTOR,
                        "레거시 주문 이관 (legacyOrderId=" + composite.legacyOrderId() + ")",
                        composite.orderDate() != null ? composite.orderDate() : now);

        return OrderItem.reconstitute(
                id,
                itemNumber,
                internalProduct,
                externalProduct,
                price,
                receiverInfo,
                itemStatus,
                null,
                0,
                0,
                List.of(migrationHistory));
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
        // 배송 시작 이후(READY/IN_TRANSIT/DELIVERED 등)는 CONFIRMED
        if (statusResolution.needsShipment()) {
            return OrderItemStatus.CONFIRMED;
        }
        return OrderItemStatus.READY;
    }

    // === Shipment 빌드 ===

    private Shipment buildShipment(
            Long orderItemId,
            LegacyOrderCompositeResult composite,
            LegacyOrderStatusMapper.OrderStatusResolution statusResolution,
            Instant now) {

        ShipmentId shipmentId = ShipmentId.forNew(UUID.randomUUID().toString());
        ShipmentNumber shipmentNumber = ShipmentNumber.generate();
        ShipmentStatus targetStatus = statusResolution.shipmentStatus();

        // shipment 테이블에서 가져온 실제 운송장/택배사 정보
        String trackingNumber = composite.invoiceNo();
        ShipmentMethod shipmentMethod = resolveShipmentMethod(composite.companyCode());

        // orders_history에서 실제 타임스탬프 추출
        Instant orderConfirmedAt =
                resolveHistoryTimestamp(
                        composite, "DELIVERY_PENDING", now, targetStatus, ShipmentStatus.PREPARING);
        Instant shippedAt =
                resolveHistoryTimestamp(
                        composite,
                        "DELIVERY_PROCESSING",
                        now,
                        targetStatus,
                        ShipmentStatus.SHIPPED);
        Instant deliveredAt =
                resolveHistoryTimestamp(
                        composite,
                        "DELIVERY_COMPLETED",
                        now,
                        targetStatus,
                        ShipmentStatus.DELIVERED);

        // shipment.INSERT_DATE를 orderConfirmedAt fallback으로 사용
        if (orderConfirmedAt == null
                && targetStatus.ordinal() >= ShipmentStatus.PREPARING.ordinal()
                && composite.shipmentCreatedAt() != null) {
            orderConfirmedAt = composite.shipmentCreatedAt();
        }

        return Shipment.reconstitute(
                shipmentId,
                shipmentNumber,
                OrderItemId.of(orderItemId),
                targetStatus,
                shipmentMethod,
                trackingNumber,
                orderConfirmedAt,
                shippedAt,
                deliveredAt,
                now,
                now);
    }

    /**
     * orders_history에서 특정 상태 전환 시각을 추출합니다. 해당 상태 이력이 없고, 현재 상태가 해당 단계 이상이면 now를 fallback으로 사용합니다.
     */
    private Instant resolveHistoryTimestamp(
            LegacyOrderCompositeResult composite,
            String historyStatus,
            Instant now,
            ShipmentStatus currentStatus,
            ShipmentStatus targetStage) {

        Optional<Instant> fromHistory =
                composite.histories().stream()
                        .filter(h -> historyStatus.equals(h.orderStatus()))
                        .map(LegacyOrderHistoryEntry::changedAt)
                        .findFirst();

        if (fromHistory.isPresent()) {
            return fromHistory.get();
        }

        // 이력이 없지만 현재 상태가 해당 단계 이상이면 fallback
        if (currentStatus.ordinal() >= targetStage.ordinal()) {
            return now;
        }

        return null;
    }

    private ShipmentMethod resolveShipmentMethod(String companyCode) {
        if (companyCode == null || companyCode.isBlank() || "REFER_DETAIL".equals(companyCode)) {
            return null;
        }
        return ShipmentMethod.of(ShipmentMethodType.COURIER, companyCode, companyCode);
    }

    // === Cancel 빌드 ===

    private Cancel buildCancel(
            Long orderItemId,
            LegacyOrderCompositeResult composite,
            LegacyOrderStatusMapper.OrderStatusResolution statusResolution,
            Instant now) {

        CancelId cancelId = CancelId.generate();
        CancelNumber cancelNumber = CancelNumber.generate();
        CancelStatus cancelStatus = statusResolution.cancelStatus();

        // orders_history에서 취소 사유 + 시각 추출
        Optional<LegacyOrderHistoryEntry> cancelHistory =
                findClaimHistory(composite, CANCEL_STATUSES);

        CancelReason reason = resolveCancelReason(cancelHistory);
        CancelType cancelType = resolveCancelType(cancelHistory);

        Instant requestedAt = cancelHistory.map(LegacyOrderHistoryEntry::changedAt).orElse(now);
        Instant processedAt =
                (cancelStatus == CancelStatus.APPROVED || cancelStatus == CancelStatus.COMPLETED)
                        ? resolveProcessedAt(composite, CANCEL_STATUSES, now)
                        : null;
        Instant completedAt =
                cancelStatus == CancelStatus.COMPLETED
                        ? resolveCompletedAt(composite, CANCEL_STATUSES, now)
                        : null;
        String processedBy = cancelStatus != CancelStatus.REQUESTED ? LEGACY_ACTOR : null;

        return Cancel.reconstitute(
                cancelId,
                cancelNumber,
                OrderItemId.of(orderItemId),
                composite.legacySellerId(),
                composite.quantity(),
                cancelType,
                cancelStatus,
                reason,
                null,
                LEGACY_ACTOR,
                processedBy,
                requestedAt,
                processedAt,
                completedAt,
                now,
                now);
    }

    private CancelReason resolveCancelReason(Optional<LegacyOrderHistoryEntry> history) {
        if (history.isPresent()) {
            String changeReason = history.get().changeReason();
            String detailReason = history.get().changeDetailReason();
            if (changeReason != null && !changeReason.isBlank()) {
                CancelReasonType reasonType = mapCancelReasonType(changeReason);
                String reasonText =
                        detailReason != null && !detailReason.isBlank()
                                ? changeReason + " - " + detailReason
                                : changeReason;
                return new CancelReason(reasonType, reasonText);
            }
        }
        return new CancelReason(CancelReasonType.OTHER, "레거시 이관 데이터");
    }

    private CancelReasonType mapCancelReasonType(String changeReason) {
        if (changeReason.contains("변심")) {
            return CancelReasonType.CHANGE_OF_MIND;
        }
        if (changeReason.contains("실수") || changeReason.contains("오류")) {
            return CancelReasonType.WRONG_ORDER;
        }
        if (changeReason.contains("품절") || changeReason.contains("재고")) {
            return CancelReasonType.OUT_OF_STOCK;
        }
        if (changeReason.contains("구매자") || changeReason.contains("고객")) {
            return CancelReasonType.CHANGE_OF_MIND;
        }
        return CancelReasonType.OTHER;
    }

    private CancelType resolveCancelType(Optional<LegacyOrderHistoryEntry> history) {
        if (history.isPresent()) {
            String changeReason = history.get().changeReason();
            if (changeReason != null
                    && (changeReason.contains("품절")
                            || changeReason.contains("재고")
                            || changeReason.contains("판매자"))) {
                return CancelType.SELLER_CANCEL;
            }
        }
        return CancelType.BUYER_CANCEL;
    }

    // === RefundClaim 빌드 ===

    private RefundClaim buildRefund(
            Long orderItemId,
            LegacyOrderCompositeResult composite,
            LegacyOrderStatusMapper.OrderStatusResolution statusResolution,
            Instant now) {

        RefundClaimId refundId = RefundClaimId.forNew(UUID.randomUUID().toString());
        RefundClaimNumber refundNumber = RefundClaimNumber.generate();
        RefundStatus refundStatus = statusResolution.refundStatus();

        // orders_history에서 반품 사유 + 시각 추출
        Optional<LegacyOrderHistoryEntry> refundHistory =
                findClaimHistory(composite, REFUND_STATUSES);

        RefundReason reason = resolveRefundReason(refundHistory);

        Instant requestedAt = refundHistory.map(LegacyOrderHistoryEntry::changedAt).orElse(now);
        Instant processedAt =
                refundStatus != RefundStatus.REQUESTED
                        ? resolveProcessedAt(composite, REFUND_STATUSES, now)
                        : null;
        Instant completedAt =
                refundStatus == RefundStatus.COMPLETED
                        ? resolveCompletedAt(composite, REFUND_STATUSES, now)
                        : null;
        String processedBy = refundStatus != RefundStatus.REQUESTED ? LEGACY_ACTOR : null;

        return RefundClaim.reconstitute(
                refundId,
                refundNumber,
                OrderItemId.of(orderItemId),
                composite.legacySellerId(),
                composite.quantity(),
                refundStatus,
                reason,
                null,
                null,
                null,
                LEGACY_ACTOR,
                processedBy,
                requestedAt,
                processedAt,
                completedAt,
                now,
                now);
    }

    private RefundReason resolveRefundReason(Optional<LegacyOrderHistoryEntry> history) {
        if (history.isPresent()) {
            String changeReason = history.get().changeReason();
            String detailReason = history.get().changeDetailReason();
            if (changeReason != null && !changeReason.isBlank()) {
                RefundReasonType reasonType = mapRefundReasonType(changeReason);
                String reasonText =
                        detailReason != null && !detailReason.isBlank()
                                ? changeReason + " - " + detailReason
                                : changeReason;
                return RefundReason.of(reasonType, reasonText);
            }
        }
        return RefundReason.of(RefundReasonType.OTHER, "레거시 이관 데이터");
    }

    private RefundReasonType mapRefundReasonType(String changeReason) {
        if (changeReason.contains("변심")) {
            return RefundReasonType.CHANGE_OF_MIND;
        }
        if (changeReason.contains("불량") || changeReason.contains("하자")) {
            return RefundReasonType.DEFECTIVE;
        }
        if (changeReason.contains("오배송") || changeReason.contains("다른")) {
            return RefundReasonType.WRONG_PRODUCT;
        }
        return RefundReasonType.OTHER;
    }

    // === 이력 조회 헬퍼 ===

    /** orders_history에서 해당 claim 유형의 첫 번째(가장 오래된) 이력을 찾습니다. 사유가 있는 이력을 우선합니다. */
    private Optional<LegacyOrderHistoryEntry> findClaimHistory(
            LegacyOrderCompositeResult composite, Set<String> statusSet) {
        List<LegacyOrderHistoryEntry> matching =
                composite.histories().stream()
                        .filter(h -> statusSet.contains(h.orderStatus()))
                        .toList();

        // 사유가 있는 이력 우선
        Optional<LegacyOrderHistoryEntry> withReason =
                matching.stream()
                        .filter(h -> h.changeReason() != null && !h.changeReason().isBlank())
                        .findFirst();

        if (withReason.isPresent()) {
            return withReason;
        }

        return matching.stream().findFirst();
    }

    private Instant resolveProcessedAt(
            LegacyOrderCompositeResult composite, Set<String> statusSet, Instant fallback) {
        // 해당 유형의 마지막 이력 시각 (처리 완료 시점에 가까움)
        return composite.histories().stream()
                .filter(h -> statusSet.contains(h.orderStatus()))
                .reduce((first, second) -> second)
                .map(LegacyOrderHistoryEntry::changedAt)
                .orElse(fallback);
    }

    private Instant resolveCompletedAt(
            LegacyOrderCompositeResult composite, Set<String> statusSet, Instant fallback) {
        // COMPLETED 상태의 이력이 있으면 그 시각, 없으면 마지막 이력
        return composite.histories().stream()
                .filter(
                        h ->
                                statusSet.contains(h.orderStatus())
                                        && (h.orderStatus().contains("COMPLETED")
                                                || h.orderStatus().contains("CANCELLED_COMPLETED")))
                .map(LegacyOrderHistoryEntry::changedAt)
                .findFirst()
                .orElse(resolveProcessedAt(composite, statusSet, fallback));
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
                PaymentNumber.of(
                        "LEGACY-" + composite.legacyPaymentId() + "-" + composite.legacyOrderId()),
                "LEGACY",
                Money.of((int) composite.orderAmount()),
                composite.orderDate());
    }

    private ReceiverInfo buildReceiverInfo(LegacyOrderCompositeResult composite) {
        String name = composite.receiverName() != null ? composite.receiverName() : "미상";
        Address address =
                Address.of(
                        composite.receiverZipCode() != null ? composite.receiverZipCode() : "00000",
                        composite.receiverAddress() != null ? composite.receiverAddress() : "주소 없음",
                        composite.receiverAddressDetail());
        return ReceiverInfo.of(
                name,
                buildSafePhoneNumber(composite.receiverPhone()),
                address,
                composite.deliveryRequest());
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
