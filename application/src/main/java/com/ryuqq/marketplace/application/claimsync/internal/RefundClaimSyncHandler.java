package com.ryuqq.marketplace.application.claimsync.internal;

import com.ryuqq.marketplace.application.claimsync.dto.external.ExternalClaimPayload;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.order.manager.OrderItemCommandManager;
import com.ryuqq.marketplace.application.order.manager.OrderItemReadManager;
import com.ryuqq.marketplace.application.refund.manager.RefundCommandManager;
import com.ryuqq.marketplace.application.refund.manager.RefundReadManager;
import com.ryuqq.marketplace.domain.claimsync.vo.ClaimSyncAction;
import com.ryuqq.marketplace.domain.claimsync.vo.InternalClaimType;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.order.vo.OrderItemStatus;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
import com.ryuqq.marketplace.domain.refund.id.RefundClaimId;
import com.ryuqq.marketplace.domain.refund.id.RefundClaimNumber;
import com.ryuqq.marketplace.domain.refund.vo.RefundInfo;
import com.ryuqq.marketplace.domain.refund.vo.RefundReason;
import com.ryuqq.marketplace.domain.refund.vo.RefundReasonType;
import com.ryuqq.marketplace.domain.refund.vo.RefundStatus;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * 환불(Refund) 클레임 동기화 핸들러.
 *
 * <p>RETURN 유형의 클레임에 대해 액션 결정과 실행을 캡슐화합니다.
 */
@Component
public class RefundClaimSyncHandler implements ClaimSyncHandler {

    private static final String SYNC_ACTOR = "system-claim-sync";

    private final RefundReadManager refundReadManager;
    private final RefundCommandManager refundCommandManager;
    private final OrderItemReadManager orderItemReadManager;
    private final OrderItemCommandManager orderItemCommandManager;
    private final TimeProvider timeProvider;

    public RefundClaimSyncHandler(
            RefundReadManager refundReadManager,
            RefundCommandManager refundCommandManager,
            OrderItemReadManager orderItemReadManager,
            OrderItemCommandManager orderItemCommandManager,
            TimeProvider timeProvider) {
        this.refundReadManager = refundReadManager;
        this.refundCommandManager = refundCommandManager;
        this.orderItemReadManager = orderItemReadManager;
        this.orderItemCommandManager = orderItemCommandManager;
        this.timeProvider = timeProvider;
    }

    @Override
    public InternalClaimType supportedType() {
        return InternalClaimType.REFUND;
    }

    @Override
    public ClaimSyncAction resolve(ExternalClaimPayload claim, OrderItemId orderItemId) {
        Optional<RefundClaim> existingRefund =
                refundReadManager.findByOrderItemId(orderItemId.value());
        return resolveRefund(claim, existingRefund);
    }

    @Override
    public long execute(
            ClaimSyncAction action,
            ExternalClaimPayload claim,
            OrderItemId orderItemId,
            long sellerId) {
        return switch (action) {
            case REFUND_CREATED -> createRefund(claim, orderItemId, sellerId);
            case REFUND_COLLECTING ->
                    startCollecting(refundReadManager.findByOrderItemId(orderItemId.value()).get());
            case REFUND_COLLECTED ->
                    completeCollection(
                            refundReadManager.findByOrderItemId(orderItemId.value()).get());
            case REFUND_COMPLETED -> completeRefund(claim, orderItemId, sellerId);
            case REFUND_REJECTED ->
                    rejectRefund(refundReadManager.findByOrderItemId(orderItemId.value()).get());
            default -> 0L;
        };
    }

    private ClaimSyncAction resolveRefund(
            ExternalClaimPayload external, Optional<RefundClaim> existingRefund) {
        String claimStatus = external.claimStatus();
        return switch (claimStatus) {
            case "RETURN_REQUEST" ->
                    existingRefund.isEmpty()
                            ? ClaimSyncAction.REFUND_CREATED
                            : ClaimSyncAction.SKIPPED;
            case "COLLECTING" -> {
                if (existingRefund.isEmpty()) {
                    yield ClaimSyncAction.REFUND_CREATED;
                }
                yield existingRefund.get().status() == RefundStatus.REQUESTED
                        ? ClaimSyncAction.REFUND_COLLECTING
                        : ClaimSyncAction.SKIPPED;
            }
            case "COLLECT_DONE" -> {
                if (existingRefund.isEmpty()) {
                    yield ClaimSyncAction.REFUND_CREATED;
                }
                yield existingRefund.get().status() == RefundStatus.COLLECTING
                        ? ClaimSyncAction.REFUND_COLLECTED
                        : ClaimSyncAction.SKIPPED;
            }
            case "RETURN_DONE" -> {
                if (existingRefund.isEmpty()) {
                    yield ClaimSyncAction.REFUND_COMPLETED;
                }
                RefundStatus currentStatus = existingRefund.get().status();
                if (currentStatus == RefundStatus.COLLECTED) {
                    yield ClaimSyncAction.REFUND_COMPLETED;
                }
                yield ClaimSyncAction.SKIPPED;
            }
            case "RETURN_REJECT" -> {
                if (existingRefund.isEmpty()) {
                    yield ClaimSyncAction.SKIPPED;
                }
                yield ClaimSyncAction.REFUND_REJECTED;
            }
            default -> ClaimSyncAction.SKIPPED;
        };
    }

    private long createRefund(ExternalClaimPayload claim, OrderItemId orderItemId, long sellerId) {
        Instant now = timeProvider.now();
        RefundClaimId refundClaimId = RefundClaimId.forNew(UUID.randomUUID().toString());
        RefundClaimNumber claimNumber = RefundClaimNumber.generate();
        int refundQty = resolveQty(claim.requestQuantity());
        RefundReason reason = resolveDefaultRefundReason(claim);

        RefundClaim refundClaim =
                RefundClaim.forNew(
                        refundClaimId,
                        claimNumber,
                        orderItemId,
                        sellerId,
                        refundQty,
                        reason,
                        SYNC_ACTOR,
                        now);

        refundCommandManager.persist(refundClaim);
        requestReturnOrderItem(orderItemId, "환불 요청 동기화", now);
        return 0L;
    }

    private long startCollecting(RefundClaim refundClaim) {
        Instant now = timeProvider.now();
        refundClaim.startCollecting(SYNC_ACTOR, now);
        refundCommandManager.persist(refundClaim);
        return 0L;
    }

    private long completeCollection(RefundClaim refundClaim) {
        Instant now = timeProvider.now();
        refundClaim.completeCollection(SYNC_ACTOR, now);
        refundCommandManager.persist(refundClaim);
        return 0L;
    }

    private long completeRefund(
            ExternalClaimPayload claim, OrderItemId orderItemId, long sellerId) {
        Instant now = timeProvider.now();
        RefundInfo refundInfo = RefundInfo.fullRefund(Money.zero(), "UNKNOWN", now);
        Optional<RefundClaim> existingRefund =
                refundReadManager.findByOrderItemId(orderItemId.value());

        if (existingRefund.isPresent()) {
            RefundClaim refundClaim = existingRefund.get();
            refundClaim.complete(refundInfo, SYNC_ACTOR, now);
            refundCommandManager.persist(refundClaim);
            completeReturnOrderItem(orderItemId, now);
            return 0L;
        }

        // 중간상태 건너뜀: 생성 → 수거중 → 수거완료 → 환불완료 순차 처리
        RefundClaimId refundClaimId = RefundClaimId.forNew(UUID.randomUUID().toString());
        RefundClaimNumber claimNumber = RefundClaimNumber.generate();
        int refundQty = resolveQty(claim.requestQuantity());
        RefundReason reason = resolveDefaultRefundReason(claim);

        RefundClaim refundClaim =
                RefundClaim.forNew(
                        refundClaimId,
                        claimNumber,
                        orderItemId,
                        sellerId,
                        refundQty,
                        reason,
                        SYNC_ACTOR,
                        now);
        refundClaim.startCollecting(SYNC_ACTOR, now);
        refundClaim.completeCollection(SYNC_ACTOR, now);
        refundClaim.complete(refundInfo, SYNC_ACTOR, now);
        refundCommandManager.persist(refundClaim);
        requestReturnOrderItem(orderItemId, "환불 완료 동기화", now);
        completeReturnOrderItem(orderItemId, now);
        return 0L;
    }

    private long rejectRefund(RefundClaim refundClaim) {
        Instant now = timeProvider.now();
        refundClaim.reject(SYNC_ACTOR, now);
        refundCommandManager.persist(refundClaim);
        return 0L;
    }

    private int resolveQty(Integer requestQuantity) {
        return (requestQuantity != null && requestQuantity > 0) ? requestQuantity : 1;
    }

    private RefundReason resolveDefaultRefundReason(ExternalClaimPayload claim) {
        String rawReason = claim.claimReason();
        RefundReasonType reasonType = parseRefundReasonType(rawReason);
        return RefundReason.of(reasonType, null);
    }

    private void requestReturnOrderItem(OrderItemId orderItemId, String reason, Instant now) {
        orderItemReadManager
                .findById(orderItemId)
                .ifPresent(
                        item -> {
                            if (item.status().canTransitionTo(OrderItemStatus.RETURN_REQUESTED)) {
                                item.requestReturn(SYNC_ACTOR, reason, now);
                                orderItemCommandManager.persistAll(java.util.List.of(item));
                            }
                        });
    }

    private void completeReturnOrderItem(OrderItemId orderItemId, Instant now) {
        orderItemReadManager
                .findById(orderItemId)
                .ifPresent(
                        item -> {
                            if (item.status().canTransitionTo(OrderItemStatus.RETURNED)) {
                                item.completeReturn(SYNC_ACTOR, now);
                                orderItemCommandManager.persistAll(java.util.List.of(item));
                            }
                        });
    }

    private RefundReasonType parseRefundReasonType(String rawReason) {
        if (rawReason == null || rawReason.isBlank()) {
            return RefundReasonType.OTHER;
        }
        return switch (rawReason) {
            case "CHANGE_OF_MIND" -> RefundReasonType.CHANGE_OF_MIND;
            case "WRONG_PRODUCT" -> RefundReasonType.WRONG_PRODUCT;
            case "DEFECTIVE" -> RefundReasonType.DEFECTIVE;
            case "DIFFERENT_FROM_DESC" -> RefundReasonType.DIFFERENT_FROM_DESC;
            case "DELAYED_DELIVERY" -> RefundReasonType.DELAYED_DELIVERY;
            default -> RefundReasonType.OTHER;
        };
    }
}
