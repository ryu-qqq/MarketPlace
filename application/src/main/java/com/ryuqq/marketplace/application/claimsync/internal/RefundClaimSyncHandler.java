package com.ryuqq.marketplace.application.claimsync.internal;

import com.ryuqq.marketplace.application.claimhistory.factory.ClaimHistoryFactory;
import com.ryuqq.marketplace.application.claimhistory.manager.ClaimHistoryCommandManager;
import com.ryuqq.marketplace.application.claimsync.dto.external.ExternalClaimPayload;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.order.manager.OrderItemCommandManager;
import com.ryuqq.marketplace.application.order.manager.OrderItemReadManager;
import com.ryuqq.marketplace.application.refund.manager.RefundCommandManager;
import com.ryuqq.marketplace.application.refund.manager.RefundReadManager;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * 환불(Refund) 클레임 동기화 핸들러.
 *
 * <p>RETURN 유형의 클레임에 대해 액션 결정과 실행을 캡슐화합니다.
 */
@Component
@SuppressWarnings("PMD.GodClass")
public class RefundClaimSyncHandler implements ClaimSyncHandler {

    private static final String SYNC_ACTOR = "system-claim-sync";

    private final RefundReadManager refundReadManager;
    private final RefundCommandManager refundCommandManager;
    private final OrderItemReadManager orderItemReadManager;
    private final OrderItemCommandManager orderItemCommandManager;
    private final ClaimHistoryFactory historyFactory;
    private final ClaimHistoryCommandManager historyCommandManager;
    private final TimeProvider timeProvider;

    public RefundClaimSyncHandler(
            RefundReadManager refundReadManager,
            RefundCommandManager refundCommandManager,
            OrderItemReadManager orderItemReadManager,
            OrderItemCommandManager orderItemCommandManager,
            ClaimHistoryFactory historyFactory,
            ClaimHistoryCommandManager historyCommandManager,
            TimeProvider timeProvider) {
        this.refundReadManager = refundReadManager;
        this.refundCommandManager = refundCommandManager;
        this.orderItemReadManager = orderItemReadManager;
        this.orderItemCommandManager = orderItemCommandManager;
        this.historyFactory = historyFactory;
        this.historyCommandManager = historyCommandManager;
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
        recordHistory(refundClaim.idValue(), null, "REQUESTED", refundQty);
        return 0L;
    }

    private long startCollecting(RefundClaim refundClaim) {
        Instant now = timeProvider.now();
        String fromStatus = refundClaim.status().name();
        refundClaim.startCollecting(SYNC_ACTOR, now);
        refundCommandManager.persist(refundClaim);
        recordHistory(refundClaim.idValue(), fromStatus, "COLLECTING", refundClaim.refundQty());
        return 0L;
    }

    private long completeCollection(RefundClaim refundClaim) {
        Instant now = timeProvider.now();
        String fromStatus = refundClaim.status().name();
        refundClaim.completeCollection(SYNC_ACTOR, now);
        refundCommandManager.persist(refundClaim);
        recordHistory(refundClaim.idValue(), fromStatus, "COLLECTED", refundClaim.refundQty());
        return 0L;
    }

    private long completeRefund(
            ExternalClaimPayload claim, OrderItemId orderItemId, long sellerId) {
        Instant now = timeProvider.now();
        Optional<RefundClaim> existingRefund =
                refundReadManager.findByOrderItemId(orderItemId.value());

        if (existingRefund.isPresent()) {
            RefundClaim refundClaim = existingRefund.get();
            String fromStatus = refundClaim.status().name();
            Money refundAmount = resolveRefundAmount(orderItemId, refundClaim.refundQty());
            RefundInfo refundInfo = RefundInfo.fullRefund(refundAmount, "EXTERNAL_CHANNEL", now);
            refundClaim.complete(refundInfo, SYNC_ACTOR, now);
            refundCommandManager.persist(refundClaim);
            partialReturnOrderItem(orderItemId, refundClaim.refundQty(), "환불 완료 동기화", now);
            recordHistory(refundClaim.idValue(), fromStatus, "COMPLETED", refundClaim.refundQty());
            return 0L;
        }

        // 중간상태 건너뜀: 생성 → 수거중 → 수거완료 → 환불완료 순차 처리
        RefundClaimId refundClaimId = RefundClaimId.forNew(UUID.randomUUID().toString());
        RefundClaimNumber claimNumber = RefundClaimNumber.generate();
        int refundQty = resolveQty(claim.requestQuantity());
        RefundReason reason = resolveDefaultRefundReason(claim);
        Money refundAmount = resolveRefundAmount(orderItemId, refundQty);
        RefundInfo refundInfo = RefundInfo.fullRefund(refundAmount, "EXTERNAL_CHANNEL", now);

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
        partialReturnOrderItem(orderItemId, refundQty, "환불 완료 동기화", now);
        recordHistory(refundClaim.idValue(), null, "COMPLETED", refundQty);
        return 0L;
    }

    private long rejectRefund(RefundClaim refundClaim) {
        Instant now = timeProvider.now();
        String fromStatus = refundClaim.status().name();
        refundClaim.reject(SYNC_ACTOR, now);
        refundCommandManager.persist(refundClaim);
        recordHistory(refundClaim.idValue(), fromStatus, "REJECTED", refundClaim.refundQty());
        return 0L;
    }

    /** 클레임 이력을 생성하고 저장한다. 수량 정보를 message에 포함. */
    private void recordHistory(String claimId, String fromStatus, String toStatus, int qty) {
        String from = fromStatus != null ? fromStatus : "NEW";
        historyCommandManager.persist(
                historyFactory.createStatusChangeBySystemWithQty(
                        ClaimType.REFUND, claimId, from, toStatus, qty));
    }

    /** 반품 요청: OrderItem을 RETURN_REQUESTED 상태로 변경 (수량 누적 없음). */
    private void requestReturnOrderItem(OrderItemId orderItemId, String reason, Instant now) {
        orderItemReadManager
                .findById(orderItemId)
                .ifPresent(
                        item -> {
                            if (item.status()
                                    .canTransitionTo(
                                            com.ryuqq.marketplace.domain.order.vo.OrderItemStatus
                                                    .RETURN_REQUESTED)) {
                                item.requestReturn(SYNC_ACTOR, reason, now);
                                orderItemCommandManager.persistAll(List.of(item));
                            }
                        });
    }

    /**
     * 부분반품 수량만큼 OrderItem의 returnedQty를 증가시킨다.
     * 전체 수량이 소진되면 RETURNED 상태 전환.
     */
    private void partialReturnOrderItem(
            OrderItemId orderItemId, int returnQty, String reason, Instant now) {
        orderItemReadManager
                .findById(orderItemId)
                .ifPresent(
                        item -> {
                            if (item.remainingReturnableQty() > 0) {
                                int effectiveQty =
                                        Math.min(returnQty, item.remainingReturnableQty());
                                item.partialReturn(effectiveQty, SYNC_ACTOR, reason, now);
                                orderItemCommandManager.persistAll(List.of(item));
                            }
                        });
    }

    /** 단가 x refundQty로 부분환불 금액을 계산한다. */
    private Money resolveRefundAmount(OrderItemId orderItemId, int refundQty) {
        return orderItemReadManager
                .findById(orderItemId)
                .map(
                        item -> {
                            int unitPrice =
                                    item.price().paymentAmount().value() / item.quantity();
                            return Money.of(unitPrice * refundQty);
                        })
                .orElse(Money.zero());
    }

    private int resolveQty(Integer requestQuantity) {
        return (requestQuantity != null && requestQuantity > 0) ? requestQuantity : 1;
    }

    private RefundReason resolveDefaultRefundReason(ExternalClaimPayload claim) {
        String rawReason = claim.claimReason();
        RefundReasonType reasonType = parseRefundReasonType(rawReason);
        return RefundReason.of(reasonType, null);
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
