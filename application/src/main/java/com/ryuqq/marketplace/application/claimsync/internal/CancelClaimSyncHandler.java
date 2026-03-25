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
        List<Cancel> existingCancels = cancelReadManager.findAllByOrderItemId(orderItemId);
        Optional<Cancel> latestCancel =
                existingCancels.isEmpty() ? Optional.empty() : Optional.of(existingCancels.getLast());
        return switch (claim.claimType()) {
            case "CANCEL" -> resolveCancel(claim, latestCancel, existingCancels, orderItemId);
            case "ADMIN_CANCEL" -> resolveAdminCancel(claim, latestCancel);
            default -> ClaimSyncAction.SKIPPED;
        };
    }

    @Override
    public long execute(
            ClaimSyncAction action,
            ExternalClaimPayload claim,
            OrderItemId orderItemId,
            long sellerId) {
        int cancelQty = resolveQty(claim.requestQuantity());
        List<Cancel> allCancels = cancelReadManager.findAllByOrderItemId(orderItemId);
        Optional<Cancel> latestCancel =
                allCancels.isEmpty() ? Optional.empty() : Optional.of(allCancels.getLast());
        return switch (action) {
            case CANCEL_CREATED -> createCancel(claim, orderItemId, sellerId, cancelQty);
            case CANCEL_APPROVED -> approveCancel(latestCancel.get());
            case CANCEL_COMPLETED -> completeCancel(claim, orderItemId, sellerId, cancelQty);
            case CANCEL_WITHDRAWN -> withdrawCancel(latestCancel.get());
            default -> 0L;
        };
    }

    private ClaimSyncAction resolveCancel(
            ExternalClaimPayload external,
            Optional<Cancel> latestCancel,
            List<Cancel> allCancels,
            OrderItemId orderItemId) {
        String claimStatus = external.claimStatus();
        return switch (claimStatus) {
            case "CANCEL_REQUEST" -> {
                if (hasRemainingCancelableQty(allCancels, orderItemId)) {
                    yield ClaimSyncAction.CANCEL_CREATED;
                }
                yield ClaimSyncAction.SKIPPED;
            }
            case "CANCELING" -> {
                if (latestCancel.isEmpty()) {
                    yield ClaimSyncAction.CANCEL_CREATED;
                }
                yield latestCancel.get().status() == CancelStatus.REQUESTED
                        ? ClaimSyncAction.CANCEL_APPROVED
                        : ClaimSyncAction.SKIPPED;
            }
            case "CANCEL_DONE" -> {
                if (latestCancel.isEmpty()) {
                    yield ClaimSyncAction.CANCEL_COMPLETED;
                }
                CancelStatus currentStatus = latestCancel.get().status();
                if (currentStatus == CancelStatus.APPROVED
                        || currentStatus == CancelStatus.REQUESTED) {
                    yield ClaimSyncAction.CANCEL_COMPLETED;
                }
                yield ClaimSyncAction.SKIPPED;
            }
            case "CANCEL_REJECT" -> {
                if (latestCancel.isEmpty()) {
                    yield ClaimSyncAction.SKIPPED;
                }
                yield latestCancel.get().status() == CancelStatus.REQUESTED
                        ? ClaimSyncAction.CANCEL_WITHDRAWN
                        : ClaimSyncAction.SKIPPED;
            }
            default -> ClaimSyncAction.SKIPPED;
        };
    }

    /** 해당 OrderItem에 취소 가능한 잔여 수량이 있는지 확인한다. */
    private boolean hasRemainingCancelableQty(
            List<Cancel> allCancels, OrderItemId orderItemId) {
        return orderItemReadManager
                .findById(orderItemId)
                .map(item -> item.remainingCancelableQty() > 0)
                .orElse(false);
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

    private long createCancel(
            ExternalClaimPayload claim, OrderItemId orderItemId, long sellerId, int cancelQty) {
        Instant now = timeProvider.now();
        CancelId cancelId = CancelId.generate();
        CancelNumber cancelNumber = CancelNumber.generate();
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
            partialCancelOrderItem(orderItemId, cancelQty, "관리자 취소 동기화", now);
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
        partialCancelOrderItem(
                cancel.orderItemId(), cancel.cancelQty(), "취소 승인 동기화", now);
        return 0L;
    }

    private long completeCancel(
            ExternalClaimPayload claim, OrderItemId orderItemId, long sellerId, int cancelQty) {
        Instant now = timeProvider.now();
        Money refundAmount = resolveRefundAmount(orderItemId, cancelQty);
        String refundMethod = resolveRefundMethod(orderItemId);
        CancelRefundInfo refundInfo =
                CancelRefundInfo.of(refundAmount, refundMethod, "COMPLETED", now, null);
        List<Cancel> allCancels = cancelReadManager.findAllByOrderItemId(orderItemId);
        Optional<Cancel> latestCancel =
                allCancels.isEmpty() ? Optional.empty() : Optional.of(allCancels.getLast());

        if (latestCancel.isPresent()) {
            Cancel cancel = latestCancel.get();
            cancel.complete(refundInfo, SYNC_ACTOR, now);
            cancelCommandManager.persist(cancel);
            partialCancelOrderItem(orderItemId, cancel.cancelQty(), "취소 완료 동기화", now);
            return 0L;
        }

        // 중간상태 건너뜀: 생성 → 승인 → 완료 순차 처리
        CancelId cancelId = CancelId.generate();
        CancelNumber cancelNumber = CancelNumber.generate();
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
        partialCancelOrderItem(orderItemId, cancelQty, "취소 완료 동기화", now);
        return 0L;
    }

    private long withdrawCancel(Cancel cancel) {
        Instant now = timeProvider.now();
        cancel.withdraw(now);
        cancelCommandManager.persist(cancel);
        return 0L;
    }

    /**
     * 부분취소 수량만큼 OrderItem의 cancelledQty를 증가시킨다.
     * 전체 수량이 소진되면 CANCELLED 상태 전환 + Shipment 취소.
     */
    private void partialCancelOrderItem(
            OrderItemId orderItemId, int cancelQty, String reason, Instant now) {
        orderItemReadManager
                .findById(orderItemId)
                .ifPresent(
                        item -> {
                            if (item.remainingCancelableQty() > 0) {
                                int effectiveQty =
                                        Math.min(cancelQty, item.remainingCancelableQty());
                                item.partialCancel(effectiveQty, SYNC_ACTOR, reason, now);
                                orderItemCommandManager.persistAll(List.of(item));
                                if (item.isFullyCancelled()) {
                                    cancelAssociatedShipment(orderItemId, now);
                                }
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

    /** 단가 × cancelQty로 부분환불 금액을 계산한다. */
    private Money resolveRefundAmount(OrderItemId orderItemId, int cancelQty) {
        return orderItemReadManager
                .findById(orderItemId)
                .map(
                        item -> {
                            int unitPrice =
                                    item.price().paymentAmount().value() / item.quantity();
                            return Money.of(unitPrice * cancelQty);
                        })
                .orElse(Money.zero());
    }

    /** 주문의 결제수단을 환불수단으로 사용한다. */
    private String resolveRefundMethod(OrderItemId orderItemId) {
        // TODO: Order.paymentInfo().paymentMethod() 조회 필요. 현재는 기본값 사용.
        return "EXTERNAL_CHANNEL";
    }

    private int resolveQty(Integer requestQuantity) {
        return (requestQuantity != null && requestQuantity > 0) ? requestQuantity : 1;
    }

    private CancelReason resolveDefaultCancelReason(ExternalClaimPayload claim) {
        String rawReason = claim.claimReason();
        CancelReasonType reasonType = parseCancelReasonType(rawReason);
        String detail = claim.claimDetailedReason();
        if (detail == null || detail.isBlank()) {
            detail = rawReason;
        }
        return new CancelReason(reasonType, detail);
    }

    private CancelReasonType parseCancelReasonType(String rawReason) {
        if (rawReason == null || rawReason.isBlank()) {
            return CancelReasonType.OTHER;
        }
        return switch (rawReason) {
            // 내부 코드
            case "CHANGE_OF_MIND" -> CancelReasonType.CHANGE_OF_MIND;
            case "WRONG_ORDER" -> CancelReasonType.WRONG_ORDER;
            case "FOUND_CHEAPER" -> CancelReasonType.FOUND_CHEAPER;
            case "OUT_OF_STOCK" -> CancelReasonType.OUT_OF_STOCK;
            case "PRODUCT_DISCONTINUED" -> CancelReasonType.PRODUCT_DISCONTINUED;
            case "DELIVERY_TOO_SLOW" -> CancelReasonType.DELIVERY_TOO_SLOW;
            // 네이버 커머스 코드
            case "INTENT_CHANGED" -> CancelReasonType.CHANGE_OF_MIND;
            case "COLOR_AND_SIZE" -> CancelReasonType.WRONG_ORDER;
            case "PRODUCT_UNSATISFIED" -> CancelReasonType.OTHER;
            case "DELAYED_DELIVERY" -> CancelReasonType.DELIVERY_TOO_SLOW;
            case "SOLD_OUT" -> CancelReasonType.OUT_OF_STOCK;
            case "INCORRECT_INFO" -> CancelReasonType.OTHER;
            default -> CancelReasonType.OTHER;
        };
    }
}
