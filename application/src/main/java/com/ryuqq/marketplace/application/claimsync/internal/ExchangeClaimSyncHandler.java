package com.ryuqq.marketplace.application.claimsync.internal;

import com.ryuqq.marketplace.application.claim.manager.ClaimShipmentCommandManager;
import com.ryuqq.marketplace.application.claimhistory.factory.ClaimHistoryFactory;
import com.ryuqq.marketplace.application.claimhistory.manager.ClaimHistoryCommandManager;
import com.ryuqq.marketplace.application.claimsync.dto.external.ExternalClaimPayload;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.exchange.internal.ExchangeSettlementProcessor;
import com.ryuqq.marketplace.application.exchange.manager.ExchangeCommandManager;
import com.ryuqq.marketplace.application.exchange.manager.ExchangeReadManager;
import com.ryuqq.marketplace.application.order.manager.OrderItemCommandManager;
import com.ryuqq.marketplace.application.order.manager.OrderItemReadManager;
import com.ryuqq.marketplace.domain.claim.aggregate.ClaimShipment;
import com.ryuqq.marketplace.domain.claim.id.ClaimShipmentId;
import com.ryuqq.marketplace.domain.claim.vo.FeePayer;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;
import com.ryuqq.marketplace.domain.claimsync.vo.ClaimSyncAction;
import com.ryuqq.marketplace.domain.claimsync.vo.InternalClaimType;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import com.ryuqq.marketplace.domain.exchange.exception.ExchangeErrorCode;
import com.ryuqq.marketplace.domain.exchange.exception.ExchangeException;
import com.ryuqq.marketplace.domain.exchange.id.ExchangeClaimId;
import com.ryuqq.marketplace.domain.exchange.id.ExchangeClaimNumber;
import com.ryuqq.marketplace.domain.exchange.vo.AmountAdjustment;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeOption;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeReason;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeReasonType;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeStatus;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 교환(Exchange) 클레임 동기화 핸들러.
 *
 * <p>EXCHANGE 유형의 클레임에 대해 액션 결정과 실행을 캡슐화합니다.
 */
@Component
@SuppressWarnings({"PMD.GodClass", "PMD.ExcessiveImports"})
public class ExchangeClaimSyncHandler implements ClaimSyncHandler {

    private static final Logger log = LoggerFactory.getLogger(ExchangeClaimSyncHandler.class);
    private static final String SYNC_ACTOR = "system-claim-sync";

    private final ExchangeReadManager exchangeReadManager;
    private final ExchangeCommandManager exchangeCommandManager;
    private final OrderItemReadManager orderItemReadManager;
    private final OrderItemCommandManager orderItemCommandManager;
    private final ClaimHistoryFactory historyFactory;
    private final ClaimHistoryCommandManager historyCommandManager;
    private final ClaimShipmentCommandManager claimShipmentCommandManager;
    private final TimeProvider timeProvider;
    private final ExchangeSettlementProcessor exchangeSettlementProcessor;

    public ExchangeClaimSyncHandler(
            ExchangeReadManager exchangeReadManager,
            ExchangeCommandManager exchangeCommandManager,
            OrderItemReadManager orderItemReadManager,
            OrderItemCommandManager orderItemCommandManager,
            ClaimHistoryFactory historyFactory,
            ClaimHistoryCommandManager historyCommandManager,
            ClaimShipmentCommandManager claimShipmentCommandManager,
            TimeProvider timeProvider,
            ExchangeSettlementProcessor exchangeSettlementProcessor) {
        this.exchangeReadManager = exchangeReadManager;
        this.exchangeCommandManager = exchangeCommandManager;
        this.orderItemReadManager = orderItemReadManager;
        this.orderItemCommandManager = orderItemCommandManager;
        this.historyFactory = historyFactory;
        this.historyCommandManager = historyCommandManager;
        this.claimShipmentCommandManager = claimShipmentCommandManager;
        this.timeProvider = timeProvider;
        this.exchangeSettlementProcessor = exchangeSettlementProcessor;
    }

    @Override
    public InternalClaimType supportedType() {
        return InternalClaimType.EXCHANGE;
    }

    @Override
    public ClaimSyncAction resolve(ExternalClaimPayload claim, OrderItemId orderItemId) {
        Optional<ExchangeClaim> existing = exchangeReadManager.findByOrderItemId(orderItemId);
        return resolveExchange(claim, existing);
    }

    @Override
    public long execute(
            ClaimSyncAction action,
            ExternalClaimPayload claim,
            OrderItemId orderItemId,
            long sellerId) {
        return switch (action) {
            case EXCHANGE_CREATED -> createExchange(claim, orderItemId, sellerId);
            case EXCHANGE_COLLECTING ->
                    startCollecting(
                            exchangeReadManager.findByOrderItemId(orderItemId).get(), claim);
            case EXCHANGE_COLLECTED ->
                    completeCollection(exchangeReadManager.findByOrderItemId(orderItemId).get());
            case EXCHANGE_SHIPPING ->
                    startShipping(exchangeReadManager.findByOrderItemId(orderItemId).get());
            case EXCHANGE_COMPLETED -> completeExchange(claim, orderItemId, sellerId);
            case EXCHANGE_REJECTED ->
                    rejectExchange(exchangeReadManager.findByOrderItemId(orderItemId).get());
            case EXCHANGE_HELD ->
                    holdExchange(
                            exchangeReadManager.findByOrderItemId(orderItemId).get(),
                            claim.holdbackReason());
            case EXCHANGE_HOLD_RELEASED ->
                    releaseHoldExchange(exchangeReadManager.findByOrderItemId(orderItemId).get());
            default -> 0L;
        };
    }

    private ClaimSyncAction resolveExchange(
            ExternalClaimPayload external, Optional<ExchangeClaim> existing) {
        String holdbackStatus = external.holdbackStatus();
        if (holdbackStatus != null && existing.isPresent()) {
            if ("HOLDBACK".equals(holdbackStatus)) {
                return ClaimSyncAction.EXCHANGE_HELD;
            }
            if ("RELEASED".equals(holdbackStatus)) {
                return ClaimSyncAction.EXCHANGE_HOLD_RELEASED;
            }
        }

        String claimStatus = external.claimStatus();
        return switch (claimStatus) {
            case "EXCHANGE_REQUEST" ->
                    existing.isEmpty() ? ClaimSyncAction.EXCHANGE_CREATED : ClaimSyncAction.SKIPPED;
            case "COLLECTING" -> {
                if (existing.isEmpty()) {
                    yield ClaimSyncAction.EXCHANGE_CREATED;
                }
                yield existing.get().status() == ExchangeStatus.REQUESTED
                        ? ClaimSyncAction.EXCHANGE_COLLECTING
                        : ClaimSyncAction.SKIPPED;
            }
            case "COLLECT_DONE" -> {
                if (existing.isEmpty()) {
                    yield ClaimSyncAction.EXCHANGE_CREATED;
                }
                yield existing.get().status() == ExchangeStatus.COLLECTING
                        ? ClaimSyncAction.EXCHANGE_COLLECTED
                        : ClaimSyncAction.SKIPPED;
            }
            case "EXCHANGE_REDELIVERING" -> {
                if (existing.isEmpty()) {
                    yield ClaimSyncAction.EXCHANGE_CREATED;
                }
                ExchangeStatus currentStatus = existing.get().status();
                if (currentStatus == ExchangeStatus.COLLECTED
                        || currentStatus == ExchangeStatus.PREPARING) {
                    yield ClaimSyncAction.EXCHANGE_SHIPPING;
                }
                yield ClaimSyncAction.SKIPPED;
            }
            case "EXCHANGE_DONE" -> {
                if (existing.isEmpty()) {
                    yield ClaimSyncAction.EXCHANGE_COMPLETED;
                }
                ExchangeStatus currentStatus = existing.get().status();
                if (!currentStatus.isActive()) {
                    yield ClaimSyncAction.SKIPPED;
                }
                yield ClaimSyncAction.EXCHANGE_COMPLETED;
            }
            case "EXCHANGE_REJECT" -> {
                if (existing.isEmpty()) {
                    yield ClaimSyncAction.SKIPPED;
                }
                yield ClaimSyncAction.EXCHANGE_REJECTED;
            }
            default -> ClaimSyncAction.SKIPPED;
        };
    }

    private long createExchange(
            ExternalClaimPayload claim, OrderItemId orderItemId, long sellerId) {
        Instant now = timeProvider.now();
        ExchangeClaimId exchangeClaimId = ExchangeClaimId.forNew(UUID.randomUUID().toString());
        ExchangeClaimNumber claimNumber = ExchangeClaimNumber.generate();
        int exchangeQty = resolveQty(claim.requestQuantity());
        ExchangeReason reason = resolveDefaultExchangeReason(claim);
        ExchangeOption exchangeOption =
                new ExchangeOption(0L, "UNKNOWN", 0L, 0L, "UNKNOWN", exchangeQty);
        AmountAdjustment amountAdjustment =
                AmountAdjustment.calculate(
                        Money.zero(), Money.zero(), Money.zero(), Money.zero(), FeePayer.BUYER);

        ExchangeClaim exchangeClaim =
                ExchangeClaim.forNew(
                        exchangeClaimId,
                        claimNumber,
                        orderItemId,
                        sellerId,
                        exchangeQty,
                        reason,
                        exchangeOption,
                        amountAdjustment,
                        null,
                        SYNC_ACTOR,
                        now);

        // 생성 시 HOLDBACK 상태면 즉시 hold 처리
        if ("HOLDBACK".equals(claim.holdbackStatus())) {
            String holdReason =
                    claim.holdbackReason() != null ? claim.holdbackReason() : "외부 채널 보류";
            exchangeClaim.hold(holdReason, now);
        }

        exchangeCommandManager.persist(exchangeClaim);
        requestReturnOrderItem(orderItemId, "교환 요청 동기화", now);
        recordHistory(exchangeClaim.idValue(), orderItemId.value(), null, "REQUESTED", exchangeQty);
        return 0L;
    }

    private long startCollecting(ExchangeClaim exchangeClaim, ExternalClaimPayload claim) {
        Instant now = timeProvider.now();
        String fromStatus = exchangeClaim.status().name();
        attachCollectShipmentIfPresent(exchangeClaim, claim, now);
        exchangeClaim.startCollecting(SYNC_ACTOR, now);
        exchangeCommandManager.persist(exchangeClaim);
        recordHistory(
                exchangeClaim.idValue(),
                exchangeClaim.orderItemIdValue(),
                fromStatus,
                "COLLECTING",
                exchangeClaim.exchangeQty());
        return 0L;
    }

    /**
     * 외부 클레임 페이로드에 수거 배송 정보가 있으면 ClaimShipment를 생성하여 교환 클레임에 연결합니다.
     *
     * <p>택배사 코드 또는 송장번호가 없으면 연결을 생략합니다.
     */
    private void attachCollectShipmentIfPresent(
            ExchangeClaim exchangeClaim, ExternalClaimPayload claim, Instant now) {
        if (!hasCollectShipmentInfo(claim)) {
            return;
        }
        ClaimShipmentId shipmentId = ClaimShipmentId.forNew(UUID.randomUUID().toString());
        ClaimShipment claimShipment =
                ClaimShipment.forSync(
                        shipmentId,
                        claim.collectDeliveryCompany(),
                        claim.collectDeliveryCompany(),
                        claim.collectTrackingNumber(),
                        now);
        claimShipmentCommandManager.persist(claimShipment);
        exchangeClaim.attachCollectShipment(claimShipment);
    }

    private boolean hasCollectShipmentInfo(ExternalClaimPayload claim) {
        return claim.collectDeliveryCompany() != null
                && !claim.collectDeliveryCompany().isBlank()
                && claim.collectTrackingNumber() != null
                && !claim.collectTrackingNumber().isBlank();
    }

    private long completeCollection(ExchangeClaim exchangeClaim) {
        Instant now = timeProvider.now();
        String fromStatus = exchangeClaim.status().name();
        exchangeClaim.completeCollection(SYNC_ACTOR, now);
        exchangeCommandManager.persist(exchangeClaim);
        recordHistory(
                exchangeClaim.idValue(),
                exchangeClaim.orderItemIdValue(),
                fromStatus,
                "COLLECTED",
                exchangeClaim.exchangeQty());
        return 0L;
    }

    private long startShipping(ExchangeClaim exchangeClaim) {
        Instant now = timeProvider.now();
        String fromStatus = exchangeClaim.status().name();
        if (exchangeClaim.status() == ExchangeStatus.COLLECTED) {
            exchangeClaim.startPreparing(SYNC_ACTOR, now);
        }
        String linkedOrderId = "SYNC-" + UUID.randomUUID();
        exchangeClaim.startShipping(linkedOrderId, SYNC_ACTOR, now);
        exchangeCommandManager.persist(exchangeClaim);
        recordHistory(
                exchangeClaim.idValue(),
                exchangeClaim.orderItemIdValue(),
                fromStatus,
                "SHIPPING",
                exchangeClaim.exchangeQty());
        return 0L;
    }

    private long completeExchange(
            ExternalClaimPayload claim, OrderItemId orderItemId, long sellerId) {
        Instant now = timeProvider.now();
        Optional<ExchangeClaim> existing = exchangeReadManager.findByOrderItemId(orderItemId);

        if (existing.isPresent()) {
            ExchangeClaim exchangeClaim = existing.get();
            String fromStatus = exchangeClaim.status().name();
            fastForwardToComplete(exchangeClaim, now);
            exchangeCommandManager.persist(exchangeClaim);
            partialReturnOrderItem(orderItemId, exchangeClaim.exchangeQty(), "교환 완료 동기화", now);
            recordHistory(
                    exchangeClaim.idValue(),
                    orderItemId.value(),
                    fromStatus,
                    "COMPLETED",
                    exchangeClaim.exchangeQty());
            createReversalEntry(orderItemId, sellerId, exchangeClaim.idValue());
            return 0L;
        }

        // 중간상태 건너뜀: 생성 → 수거중 → 수거완료 → 준비중 → 배송중 → 완료 순차 처리
        ExchangeClaimId exchangeClaimId = ExchangeClaimId.forNew(UUID.randomUUID().toString());
        ExchangeClaimNumber claimNumber = ExchangeClaimNumber.generate();
        int exchangeQty = resolveQty(claim.requestQuantity());
        ExchangeReason reason = resolveDefaultExchangeReason(claim);
        ExchangeOption exchangeOption =
                new ExchangeOption(0L, "UNKNOWN", 0L, 0L, "UNKNOWN", exchangeQty);
        AmountAdjustment amountAdjustment =
                AmountAdjustment.calculate(
                        Money.zero(), Money.zero(), Money.zero(), Money.zero(), FeePayer.BUYER);

        ExchangeClaim exchangeClaim =
                ExchangeClaim.forNew(
                        exchangeClaimId,
                        claimNumber,
                        orderItemId,
                        sellerId,
                        exchangeQty,
                        reason,
                        exchangeOption,
                        amountAdjustment,
                        null,
                        SYNC_ACTOR,
                        now);
        exchangeClaim.startCollecting(SYNC_ACTOR, now);
        exchangeClaim.completeCollection(SYNC_ACTOR, now);
        exchangeClaim.startPreparing(SYNC_ACTOR, now);
        String linkedOrderId = "SYNC-" + UUID.randomUUID();
        exchangeClaim.startShipping(linkedOrderId, SYNC_ACTOR, now);
        exchangeClaim.complete(SYNC_ACTOR, now);
        exchangeCommandManager.persist(exchangeClaim);
        partialReturnOrderItem(orderItemId, exchangeQty, "교환 완료 동기화", now);
        recordHistory(exchangeClaim.idValue(), orderItemId.value(), null, "COMPLETED", exchangeQty);
        createReversalEntry(orderItemId, sellerId, exchangeClaim.idValue());
        return 0L;
    }

    private void fastForwardToComplete(ExchangeClaim exchangeClaim, Instant now) {
        ExchangeStatus current = exchangeClaim.status();
        if (current == ExchangeStatus.REQUESTED) {
            exchangeClaim.startCollecting(SYNC_ACTOR, now);
            current = ExchangeStatus.COLLECTING;
        }
        if (current == ExchangeStatus.COLLECTING) {
            exchangeClaim.completeCollection(SYNC_ACTOR, now);
            current = ExchangeStatus.COLLECTED;
        }
        if (current == ExchangeStatus.COLLECTED) {
            exchangeClaim.startPreparing(SYNC_ACTOR, now);
            current = ExchangeStatus.PREPARING;
        }
        if (current == ExchangeStatus.PREPARING) {
            String linkedOrderId = "SYNC-" + UUID.randomUUID();
            exchangeClaim.startShipping(linkedOrderId, SYNC_ACTOR, now);
        }
        exchangeClaim.complete(SYNC_ACTOR, now);
    }

    private long rejectExchange(ExchangeClaim exchangeClaim) {
        Instant now = timeProvider.now();
        String fromStatus = exchangeClaim.status().name();
        exchangeClaim.reject(SYNC_ACTOR, now);
        exchangeCommandManager.persist(exchangeClaim);
        recordHistory(
                exchangeClaim.idValue(),
                exchangeClaim.orderItemIdValue(),
                fromStatus,
                "REJECTED",
                exchangeClaim.exchangeQty());
        return 0L;
    }

    private long holdExchange(ExchangeClaim exchangeClaim, String holdbackReason) {
        Instant now = timeProvider.now();
        try {
            exchangeClaim.hold(holdbackReason, now);
            exchangeCommandManager.persist(exchangeClaim);
            recordHistory(
                    exchangeClaim.idValue(),
                    exchangeClaim.orderItemIdValue(),
                    exchangeClaim.status().name(),
                    "HELD",
                    exchangeClaim.exchangeQty());
        } catch (ExchangeException e) {
            if (e.getErrorCode() == ExchangeErrorCode.ALREADY_HOLD) {
                log.debug("교환 클레임이 이미 보류 상태입니다. exchangeClaimId={}", exchangeClaim.idValue());
                return 0L;
            }
            throw e;
        }
        return 0L;
    }

    private long releaseHoldExchange(ExchangeClaim exchangeClaim) {
        Instant now = timeProvider.now();
        exchangeClaim.releaseHold(now);
        exchangeCommandManager.persist(exchangeClaim);
        recordHistory(
                exchangeClaim.idValue(),
                exchangeClaim.orderItemIdValue(),
                "HELD",
                "HOLD_RELEASED",
                exchangeClaim.exchangeQty());
        return 0L;
    }

    /** 정산 역분개 Entry를 생성한다. 실패해도 클레임 처리를 막지 않는다. */
    private void createReversalEntry(
            OrderItemId orderItemId, long sellerId, String exchangeClaimId) {
        exchangeSettlementProcessor.createReversalEntry(
                orderItemId.value(), sellerId, exchangeClaimId);
    }

    /** 클레임 이력을 생성하고 저장한다. 수량 정보를 message에 포함. */
    private void recordHistory(
            String claimId, String orderItemId, String fromStatus, String toStatus, int qty) {
        String from = fromStatus != null ? fromStatus : "NEW";
        historyCommandManager.persist(
                historyFactory.createStatusChangeBySystemWithQty(
                        ClaimType.EXCHANGE, claimId, orderItemId, from, toStatus, qty));
    }

    /** 교환 요청: OrderItem을 RETURN_REQUESTED 상태로 변경 (수량 누적 없음). */
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

    /** 부분반품 수량만큼 OrderItem의 returnedQty를 증가시킨다. 전체 수량이 소진되면 RETURNED 상태 전환. */
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

    private int resolveQty(Integer requestQuantity) {
        return (requestQuantity != null && requestQuantity > 0) ? requestQuantity : 1;
    }

    private ExchangeReason resolveDefaultExchangeReason(ExternalClaimPayload claim) {
        // externalReasonCode 우선, 없으면 claimReason으로 매핑 시도
        String codeForMapping =
                (claim.externalReasonCode() != null && !claim.externalReasonCode().isBlank())
                        ? claim.externalReasonCode()
                        : claim.claimReason();
        ExchangeReasonType reasonType = parseExchangeReasonType(codeForMapping);
        String detail =
                (claim.claimDetailedReason() != null && !claim.claimDetailedReason().isBlank())
                        ? claim.claimDetailedReason()
                        : "외부 채널 교환";
        return new ExchangeReason(reasonType, detail);
    }

    private ExchangeReasonType parseExchangeReasonType(String rawReason) {
        if (rawReason == null || rawReason.isBlank()) {
            return ExchangeReasonType.OTHER;
        }
        return switch (rawReason) {
                // 내부 코드
            case "SIZE_CHANGE" -> ExchangeReasonType.SIZE_CHANGE;
            case "COLOR_CHANGE" -> ExchangeReasonType.COLOR_CHANGE;
            case "OPTION_CHANGE" -> ExchangeReasonType.OPTION_CHANGE;
            case "WRONG_OPTION_SENT" -> ExchangeReasonType.WRONG_OPTION_SENT;
            case "DEFECTIVE" -> ExchangeReasonType.DEFECTIVE;
                // 네이버 커머스 교환 사유 코드
            case "COLOR_AND_SIZE" -> ExchangeReasonType.OPTION_CHANGE;
            case "BROKEN" -> ExchangeReasonType.DEFECTIVE;
            case "WRONG_DELIVERY" -> ExchangeReasonType.WRONG_OPTION_SENT;
            case "INTENT_CHANGED" -> ExchangeReasonType.OTHER;
            default -> ExchangeReasonType.OTHER;
        };
    }
}
