package com.ryuqq.marketplace.application.claimsync.internal;

import com.ryuqq.marketplace.application.cancel.manager.CancelCommandManager;
import com.ryuqq.marketplace.application.cancel.manager.CancelReadManager;
import com.ryuqq.marketplace.application.claimsync.dto.external.ExternalClaimPayload;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.order.manager.OrderItemCommandManager;
import com.ryuqq.marketplace.application.order.manager.OrderItemReadManager;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentCommandManager;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentReadManager;
import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import com.ryuqq.marketplace.domain.cancel.id.CancelId;
import com.ryuqq.marketplace.domain.cancel.id.CancelNumber;
import com.ryuqq.marketplace.domain.cancel.vo.CancelReason;
import com.ryuqq.marketplace.domain.cancel.vo.CancelReasonType;
import com.ryuqq.marketplace.domain.cancel.vo.CancelRefundInfo;
import com.ryuqq.marketplace.domain.cancel.vo.CancelStatus;
import com.ryuqq.marketplace.domain.claimsync.vo.ClaimSyncAction;
import com.ryuqq.marketplace.domain.claimsync.vo.InternalClaimType;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * 취소(Cancel) 클레임 동기화 핸들러.
 *
 * <p>CANCEL, ADMIN_CANCEL 유형의 클레임에 대해 액션 결정과 실행을 캡슐화합니다.
 */
@Component
@SuppressWarnings("PMD.GodClass")
public class CancelClaimSyncHandler implements ClaimSyncHandler {

    private static final String SYNC_ACTOR = "system-claim-sync";

    private final CancelReadManager cancelReadManager;
    private final CancelCommandManager cancelCommandManager;
    private final OrderItemReadManager orderItemReadManager;
    private final OrderItemCommandManager orderItemCommandManager;
    private final ShipmentReadManager shipmentReadManager;
    private final ShipmentCommandManager shipmentCommandManager;
    private final TimeProvider timeProvider;

    public CancelClaimSyncHandler(
            CancelReadManager cancelReadManager,
            CancelCommandManager cancelCommandManager,
            OrderItemReadManager orderItemReadManager,
            OrderItemCommandManager orderItemCommandManager,
            ShipmentReadManager shipmentReadManager,
            ShipmentCommandManager shipmentCommandManager,
            TimeProvider timeProvider) {
        this.cancelReadManager = cancelReadManager;
        this.cancelCommandManager = cancelCommandManager;
        this.orderItemReadManager = orderItemReadManager;
        this.orderItemCommandManager = orderItemCommandManager;
        this.shipmentReadManager = shipmentReadManager;
        this.shipmentCommandManager = shipmentCommandManager;
        this.timeProvider = timeProvider;
    }

    @Override
    public InternalClaimType supportedType() {
        return InternalClaimType.CANCEL;
    }

    @Override
    public ClaimSyncAction resolve(ExternalClaimPayload claim, OrderItemId orderItemId) {
        Optional<Cancel> existingCancel = cancelReadManager.findByOrderItemId(orderItemId);
        return switch (claim.claimType()) {
            case "CANCEL" -> resolveCancel(claim, existingCancel);
            case "ADMIN_CANCEL" -> resolveAdminCancel(claim, existingCancel);
            default -> ClaimSyncAction.SKIPPED;
        };
    }

    @Override
    public long execute(
            ClaimSyncAction action,
            ExternalClaimPayload claim,
            OrderItemId orderItemId,
            long sellerId) {
        return switch (action) {
            case CANCEL_CREATED -> createCancel(claim, orderItemId, sellerId);
            case CANCEL_APPROVED ->
                    approveCancel(cancelReadManager.findByOrderItemId(orderItemId).get());
            case CANCEL_COMPLETED -> completeCancel(claim, orderItemId, sellerId);
            case CANCEL_WITHDRAWN ->
                    withdrawCancel(cancelReadManager.findByOrderItemId(orderItemId).get());
            default -> 0L;
        };
    }

    private ClaimSyncAction resolveCancel(
            ExternalClaimPayload external, Optional<Cancel> existingCancel) {
        String claimStatus = external.claimStatus();
        return switch (claimStatus) {
            case "CANCEL_REQUEST" ->
                    existingCancel.isEmpty()
                            ? ClaimSyncAction.CANCEL_CREATED
                            : ClaimSyncAction.SKIPPED;
            case "CANCELING" -> {
                if (existingCancel.isEmpty()) {
                    yield ClaimSyncAction.CANCEL_CREATED;
                }
                yield existingCancel.get().status() == CancelStatus.REQUESTED
                        ? ClaimSyncAction.CANCEL_APPROVED
                        : ClaimSyncAction.SKIPPED;
            }
            case "CANCEL_DONE" -> {
                if (existingCancel.isEmpty()) {
                    yield ClaimSyncAction.CANCEL_COMPLETED;
                }
                CancelStatus currentStatus = existingCancel.get().status();
                if (currentStatus == CancelStatus.APPROVED) {
                    yield ClaimSyncAction.CANCEL_COMPLETED;
                }
                yield ClaimSyncAction.SKIPPED;
            }
            case "CANCEL_REJECT" -> {
                if (existingCancel.isEmpty()) {
                    yield ClaimSyncAction.SKIPPED;
                }
                yield existingCancel.get().status() == CancelStatus.REQUESTED
                        ? ClaimSyncAction.CANCEL_WITHDRAWN
                        : ClaimSyncAction.SKIPPED;
            }
            default -> ClaimSyncAction.SKIPPED;
        };
    }

    private ClaimSyncAction resolveAdminCancel(
            ExternalClaimPayload external, Optional<Cancel> existingCancel) {
        String claimStatus = external.claimStatus();
        return switch (claimStatus) {
            case "ADMIN_CANCELING" ->
                    existingCancel.isEmpty()
                            ? ClaimSyncAction.CANCEL_CREATED
                            : ClaimSyncAction.SKIPPED;
            case "ADMIN_CANCEL_DONE" -> {
                if (existingCancel.isEmpty()) {
                    yield ClaimSyncAction.CANCEL_COMPLETED;
                }
                yield existingCancel.get().status() == CancelStatus.APPROVED
                        ? ClaimSyncAction.CANCEL_COMPLETED
                        : ClaimSyncAction.SKIPPED;
            }
            case "ADMIN_CANCEL_REJECT" -> {
                if (existingCancel.isEmpty()) {
                    yield ClaimSyncAction.SKIPPED;
                }
                yield existingCancel.get().status() == CancelStatus.REQUESTED
                        ? ClaimSyncAction.CANCEL_WITHDRAWN
                        : ClaimSyncAction.SKIPPED;
            }
            default -> ClaimSyncAction.SKIPPED;
        };
    }

    private long createCancel(ExternalClaimPayload claim, OrderItemId orderItemId, long sellerId) {
        Instant now = timeProvider.now();
        CancelId cancelId = CancelId.generate();
        CancelNumber cancelNumber = CancelNumber.generate();
        int cancelQty = resolveQty(claim.requestQuantity());
        CancelReason reason = resolveDefaultCancelReason(claim);

        Cancel cancel;
        if ("ADMIN_CANCEL".equals(claim.claimType())) {
            cancel =
                    Cancel.forSellerCancel(
                            cancelId,
                            cancelNumber,
                            orderItemId,
                            sellerId,
                            cancelQty,
                            reason,
                            SYNC_ACTOR,
                            now);
            cancelOrderItem(orderItemId, "관리자 취소 동기화", now);
        } else {
            cancel =
                    Cancel.forBuyerCancel(
                            cancelId,
                            cancelNumber,
                            orderItemId,
                            sellerId,
                            cancelQty,
                            reason,
                            SYNC_ACTOR,
                            now);
        }

        cancelCommandManager.persist(cancel);
        return 0L;
    }

    private long approveCancel(Cancel cancel) {
        Instant now = timeProvider.now();
        cancel.approve(SYNC_ACTOR, now);
        cancelCommandManager.persist(cancel);
        cancelOrderItem(cancel.orderItemId(), "취소 승인 동기화", now);
        return 0L;
    }

    private long completeCancel(
            ExternalClaimPayload claim, OrderItemId orderItemId, long sellerId) {
        Instant now = timeProvider.now();
        CancelRefundInfo refundInfo =
                CancelRefundInfo.of(Money.zero(), "UNKNOWN", "PENDING", now, null);
        Optional<Cancel> existingCancel = cancelReadManager.findByOrderItemId(orderItemId);

        if (existingCancel.isPresent()) {
            Cancel cancel = existingCancel.get();
            cancel.complete(refundInfo, SYNC_ACTOR, now);
            cancelCommandManager.persist(cancel);
            cancelOrderItem(orderItemId, "취소 완료 동기화", now);
            return 0L;
        }

        // 중간상태 건너뜀: 생성 → 승인 → 완료 순차 처리
        CancelId cancelId = CancelId.generate();
        CancelNumber cancelNumber = CancelNumber.generate();
        int cancelQty = resolveQty(claim.requestQuantity());
        CancelReason reason = resolveDefaultCancelReason(claim);

        Cancel cancel;
        if ("ADMIN_CANCEL".equals(claim.claimType())) {
            cancel =
                    Cancel.forSellerCancel(
                            cancelId,
                            cancelNumber,
                            orderItemId,
                            sellerId,
                            cancelQty,
                            reason,
                            SYNC_ACTOR,
                            now);
        } else {
            cancel =
                    Cancel.forBuyerCancel(
                            cancelId,
                            cancelNumber,
                            orderItemId,
                            sellerId,
                            cancelQty,
                            reason,
                            SYNC_ACTOR,
                            now);
            cancel.approve(SYNC_ACTOR, now);
        }
        cancel.complete(refundInfo, SYNC_ACTOR, now);
        cancelCommandManager.persist(cancel);
        cancelOrderItem(orderItemId, "취소 완료 동기화", now);
        return 0L;
    }

    private long withdrawCancel(Cancel cancel) {
        Instant now = timeProvider.now();
        cancel.withdraw(now);
        cancelCommandManager.persist(cancel);
        return 0L;
    }

    /** OrderItem을 CANCELLED로 전환하고, 연결된 Shipment가 PREPARING이면 함께 취소합니다. */
    private void cancelOrderItem(OrderItemId orderItemId, String reason, Instant now) {
        orderItemReadManager
                .findById(orderItemId)
                .ifPresent(
                        item -> {
                            if (item.status()
                                    .canTransitionTo(
                                            com.ryuqq.marketplace.domain.order.vo.OrderItemStatus
                                                    .CANCELLED)) {
                                item.cancel(SYNC_ACTOR, reason, now);
                                orderItemCommandManager.persistAll(List.of(item));
                                cancelAssociatedShipment(orderItemId, now);
                            }
                        });
    }

    private void cancelAssociatedShipment(OrderItemId orderItemId, Instant now) {
        shipmentReadManager
                .findByOrderItemId(orderItemId)
                .ifPresent(
                        shipment -> {
                            if (shipment.status() == ShipmentStatus.PREPARING) {
                                shipment.cancel(now);
                                shipmentCommandManager.persist(shipment);
                            }
                        });
    }

    private int resolveQty(Integer requestQuantity) {
        return (requestQuantity != null && requestQuantity > 0) ? requestQuantity : 1;
    }

    private CancelReason resolveDefaultCancelReason(ExternalClaimPayload claim) {
        String rawReason = claim.claimReason();
        CancelReasonType reasonType = parseCancelReasonType(rawReason);
        String detail =
                (reasonType == CancelReasonType.OTHER)
                        ? (claim.claimDetailedReason() != null
                                ? claim.claimDetailedReason()
                                : "외부 채널 취소")
                        : null;
        return new CancelReason(reasonType, detail);
    }

    private CancelReasonType parseCancelReasonType(String rawReason) {
        if (rawReason == null || rawReason.isBlank()) {
            return CancelReasonType.OTHER;
        }
        return switch (rawReason) {
            case "CHANGE_OF_MIND" -> CancelReasonType.CHANGE_OF_MIND;
            case "WRONG_ORDER" -> CancelReasonType.WRONG_ORDER;
            case "FOUND_CHEAPER" -> CancelReasonType.FOUND_CHEAPER;
            case "OUT_OF_STOCK" -> CancelReasonType.OUT_OF_STOCK;
            case "PRODUCT_DISCONTINUED" -> CancelReasonType.PRODUCT_DISCONTINUED;
            case "DELIVERY_TOO_SLOW" -> CancelReasonType.DELIVERY_TOO_SLOW;
            default -> CancelReasonType.OTHER;
        };
    }
}
