package com.ryuqq.marketplace.application.legacyconversion.internal;

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
import com.ryuqq.marketplace.domain.cancel.id.CancelId;
import com.ryuqq.marketplace.domain.cancel.id.CancelNumber;
import com.ryuqq.marketplace.domain.cancel.outbox.aggregate.CancelOutbox;
import com.ryuqq.marketplace.domain.cancel.outbox.vo.CancelOutboxType;
import com.ryuqq.marketplace.domain.cancel.vo.CancelReason;
import com.ryuqq.marketplace.domain.cancel.vo.CancelReasonType;
import com.ryuqq.marketplace.domain.cancel.vo.CancelStatus;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.order.vo.OrderItemStatus;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
import com.ryuqq.marketplace.domain.refund.id.RefundClaimId;
import com.ryuqq.marketplace.domain.refund.id.RefundClaimNumber;
import com.ryuqq.marketplace.domain.refund.outbox.aggregate.RefundOutbox;
import com.ryuqq.marketplace.domain.refund.outbox.vo.RefundOutboxType;
import com.ryuqq.marketplace.domain.refund.vo.RefundReason;
import com.ryuqq.marketplace.domain.refund.vo.RefundReasonType;
import com.ryuqq.marketplace.domain.refund.vo.RefundStatus;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import com.ryuqq.marketplace.domain.shipment.id.ShipmentId;
import com.ryuqq.marketplace.domain.shipment.id.ShipmentNumber;
import com.ryuqq.marketplace.domain.shipment.outbox.aggregate.ShipmentOutbox;
import com.ryuqq.marketplace.domain.shipment.outbox.vo.ShipmentOutboxType;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentStatus;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레거시 주문 상태 동기화 Facade.
 *
 * <p>트랜잭션 내에서 OrderItem 상태 업데이트 + Shipment/Cancel/Refund 생성 + Outbox 저장을 원자적으로 처리합니다.
 */
@Component
public class LegacyOrderStatusUpdateFacade {

    private static final Logger log = LoggerFactory.getLogger(LegacyOrderStatusUpdateFacade.class);

    private static final String LEGACY_SYNC_ACTOR = "LEGACY_STATUS_SYNC";

    private final OrderCommandManager orderCommandManager;
    private final ShipmentReadManager shipmentReadManager;
    private final ShipmentCommandManager shipmentCommandManager;
    private final ShipmentOutboxCommandManager shipmentOutboxCommandManager;
    private final CancelCommandManager cancelCommandManager;
    private final CancelOutboxCommandManager cancelOutboxCommandManager;
    private final RefundCommandManager refundClaimCommandManager;
    private final RefundOutboxCommandManager refundOutboxCommandManager;

    public LegacyOrderStatusUpdateFacade(
            OrderCommandManager orderCommandManager,
            ShipmentReadManager shipmentReadManager,
            ShipmentCommandManager shipmentCommandManager,
            ShipmentOutboxCommandManager shipmentOutboxCommandManager,
            CancelCommandManager cancelCommandManager,
            CancelOutboxCommandManager cancelOutboxCommandManager,
            RefundCommandManager refundClaimCommandManager,
            RefundOutboxCommandManager refundOutboxCommandManager) {
        this.orderCommandManager = orderCommandManager;
        this.shipmentReadManager = shipmentReadManager;
        this.shipmentCommandManager = shipmentCommandManager;
        this.shipmentOutboxCommandManager = shipmentOutboxCommandManager;
        this.cancelCommandManager = cancelCommandManager;
        this.cancelOutboxCommandManager = cancelOutboxCommandManager;
        this.refundClaimCommandManager = refundClaimCommandManager;
        this.refundOutboxCommandManager = refundOutboxCommandManager;
    }

    /**
     * 레거시 상태 변경을 market DB에 동기화합니다.
     *
     * @param syncBundle 동기화 번들 (Order, OrderItem, 현재/목표 상태)
     * @param composite 레거시 주문 복합 조회 결과
     * @param now 현재 시각
     */
    @Transactional
    public void syncStatus(
            LegacyOrderStatusSyncBundle syncBundle,
            LegacyOrderCompositeResult composite,
            Instant now) {

        OrderItem orderItem = syncBundle.orderItem();
        OrderItemId orderItemId = orderItem.id();
        LegacyOrderStatusMapper.OrderStatusResolution resolution = syncBundle.resolution();
        OrderItemStatus currentStatus = syncBundle.currentStatus();

        // 1. 배송 상태 동기화 (Shipment 생성/Outbox)
        if (resolution.needsShipment()) {
            syncShipmentStatus(orderItemId, resolution.shipmentStatus(), composite, now);
        }

        // 2. 취소 동기화
        if (resolution.hasCancel()) {
            syncCancelStatus(orderItem, composite, resolution.cancelStatus(), now);
        }

        // 3. 반품 동기화
        if (resolution.hasRefund()) {
            syncRefundStatus(orderItem, composite, resolution.refundStatus(), now);
        }

        // 4. OrderItem 상태 전환 (도메인 메서드 사용)
        applyOrderItemTransition(orderItem, currentStatus, resolution, now);

        // 5. Order 영속화
        orderCommandManager.persist(syncBundle.order());

        log.info(
                "상태 동기화 저장 완료: legacyOrderId={}, orderItemId={}",
                syncBundle.legacyOrderId(),
                orderItemId.value());
    }

    /**
     * OrderItem 상태 전환을 적용합니다.
     *
     * <p>현재 상태에서 목표 상태로의 전환 경로를 결정하고, 도메인 메서드를 호출합니다. 전환 불가능한 경우 (이미 전환된 상태 등) 로그만 남기고 건너뜁니다.
     */
    private void applyOrderItemTransition(
            OrderItem orderItem,
            OrderItemStatus currentStatus,
            LegacyOrderStatusMapper.OrderStatusResolution resolution,
            Instant now) {

        if (resolution.hasCancel()
                && resolution.cancelStatus() == CancelStatus.COMPLETED
                && currentStatus != OrderItemStatus.CANCELLED) {
            orderItem.cancel(LEGACY_SYNC_ACTOR, "레거시 상태 동기화", now);
            return;
        }

        if (resolution.hasRefund()) {
            if (currentStatus == OrderItemStatus.CONFIRMED
                    && resolution.refundStatus() != RefundStatus.REJECTED) {
                orderItem.requestReturn(LEGACY_SYNC_ACTOR, "레거시 상태 동기화", now);
                if (resolution.refundStatus() == RefundStatus.COMPLETED) {
                    orderItem.completeReturn(LEGACY_SYNC_ACTOR, now);
                }
                return;
            }
            if (currentStatus == OrderItemStatus.RETURN_REQUESTED
                    && resolution.refundStatus() == RefundStatus.COMPLETED) {
                orderItem.completeReturn(LEGACY_SYNC_ACTOR, now);
                return;
            }
        }

        if (resolution.needsShipment() && currentStatus == OrderItemStatus.READY) {
            orderItem.confirm(LEGACY_SYNC_ACTOR, now);
        }
    }

    // === Shipment 동기화 ===

    private void syncShipmentStatus(
            OrderItemId orderItemId,
            ShipmentStatus targetShipmentStatus,
            LegacyOrderCompositeResult composite,
            Instant now) {

        Optional<Shipment> existingShipment = shipmentReadManager.findByOrderItemId(orderItemId);

        if (existingShipment.isEmpty()) {
            Shipment shipment =
                    Shipment.forNew(
                            ShipmentId.forNew(UUID.randomUUID().toString()),
                            ShipmentNumber.generate(),
                            orderItemId,
                            now);
            shipmentCommandManager.persist(shipment);

            ShipmentOutbox outbox =
                    ShipmentOutbox.forNew(orderItemId, ShipmentOutboxType.CONFIRM, "{}", now);
            shipmentOutboxCommandManager.persist(outbox);
        }

        // 송장 정보가 있고 발송 이상 상태인 경우 SHIP Outbox 생성
        if (targetShipmentStatus.ordinal() >= ShipmentStatus.IN_TRANSIT.ordinal()
                && composite.invoiceNo() != null
                && !composite.invoiceNo().isBlank()) {

            String payload = buildShipPayload(composite);
            ShipmentOutbox shipOutbox =
                    ShipmentOutbox.forNew(orderItemId, ShipmentOutboxType.SHIP, payload, now);
            shipmentOutboxCommandManager.persist(shipOutbox);
        }

        // 배송 완료 상태인 경우 DELIVER Outbox 생성
        if (targetShipmentStatus == ShipmentStatus.DELIVERED) {
            ShipmentOutbox deliverOutbox =
                    ShipmentOutbox.forNew(orderItemId, ShipmentOutboxType.DELIVER, "{}", now);
            shipmentOutboxCommandManager.persist(deliverOutbox);
        }
    }

    // === Cancel 동기화 ===

    private void syncCancelStatus(
            OrderItem orderItem,
            LegacyOrderCompositeResult composite,
            CancelStatus cancelStatus,
            Instant now) {

        Cancel cancel =
                Cancel.reconstitute(
                        CancelId.generate(),
                        CancelNumber.generate(),
                        orderItem.id(),
                        composite.legacySellerId(),
                        composite.quantity(),
                        com.ryuqq.marketplace.domain.cancel.vo.CancelType.SELLER_CANCEL,
                        cancelStatus,
                        new CancelReason(CancelReasonType.OTHER, "레거시 상태 동기화"),
                        null,
                        LEGACY_SYNC_ACTOR,
                        LEGACY_SYNC_ACTOR,
                        now,
                        cancelStatus == CancelStatus.APPROVED
                                        || cancelStatus == CancelStatus.COMPLETED
                                ? now
                                : null,
                        cancelStatus == CancelStatus.COMPLETED ? now : null,
                        now,
                        now);

        cancelCommandManager.persist(cancel);

        CancelOutboxType outboxType =
                cancelStatus == CancelStatus.COMPLETED || cancelStatus == CancelStatus.APPROVED
                        ? CancelOutboxType.APPROVE
                        : CancelOutboxType.SELLER_CANCEL;

        CancelOutbox outbox = CancelOutbox.forNew(orderItem.id(), outboxType, "{}", now);
        cancelOutboxCommandManager.persist(outbox);
    }

    // === Refund 동기화 ===

    private void syncRefundStatus(
            OrderItem orderItem,
            LegacyOrderCompositeResult composite,
            RefundStatus refundStatus,
            Instant now) {

        RefundClaim refundClaim =
                RefundClaim.reconstitute(
                        RefundClaimId.forNew(UUID.randomUUID().toString()),
                        RefundClaimNumber.generate(),
                        orderItem.id(),
                        composite.legacySellerId(),
                        composite.quantity(),
                        refundStatus,
                        RefundReason.of(RefundReasonType.OTHER, "레거시 상태 동기화"),
                        null,
                        null,
                        null,
                        LEGACY_SYNC_ACTOR,
                        refundStatus != RefundStatus.REQUESTED ? LEGACY_SYNC_ACTOR : null,
                        now,
                        refundStatus != RefundStatus.REQUESTED ? now : null,
                        refundStatus == RefundStatus.COMPLETED ? now : null,
                        now,
                        now);

        refundClaimCommandManager.persist(refundClaim);

        RefundOutboxType outboxType = resolveRefundOutboxType(refundStatus);
        RefundOutbox outbox = RefundOutbox.forNew(orderItem.id(), outboxType, "{}", now);
        refundOutboxCommandManager.persist(outbox);
    }

    private RefundOutboxType resolveRefundOutboxType(RefundStatus status) {
        return switch (status) {
            case REQUESTED -> RefundOutboxType.REQUEST;
            case COLLECTING -> RefundOutboxType.APPROVE;
            case COLLECTED -> RefundOutboxType.COLLECT;
            case REJECTED -> RefundOutboxType.REJECT;
            case COMPLETED -> RefundOutboxType.COMPLETE;
            case CANCELLED -> RefundOutboxType.REJECT;
        };
    }

    private String buildShipPayload(LegacyOrderCompositeResult composite) {
        String companyCode = composite.companyCode() != null ? composite.companyCode() : "";
        String invoiceNo = composite.invoiceNo() != null ? composite.invoiceNo() : "";
        return String.format(
                "{\"companyCode\":\"%s\",\"invoiceNo\":\"%s\"}", companyCode, invoiceNo);
    }
}
