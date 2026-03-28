package com.ryuqq.marketplace.application.legacyconversion.internal;

import com.ryuqq.marketplace.application.legacyconversion.dto.bundle.LegacyOrderStatusSyncBundle;
import com.ryuqq.marketplace.application.legacyconversion.dto.result.LegacyOrderCompositeResult;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyOrderCompositeReadManager;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyOrderConversionOutboxCommandManager;
import com.ryuqq.marketplace.application.order.manager.OrderReadManager;
import com.ryuqq.marketplace.domain.cancel.vo.CancelStatus;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderConversionOutbox;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderIdMapping;
import com.ryuqq.marketplace.domain.order.aggregate.Order;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.order.id.OrderId;
import com.ryuqq.marketplace.domain.order.vo.OrderItemStatus;
import com.ryuqq.marketplace.domain.refund.vo.RefundStatus;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 레거시 주문 상태 동기화 Coordinator.
 *
 * <p>이미 이관된 주문에 대해 레거시 상태 변경을 감지하고, market DB를 업데이트합니다. 트랜잭션 없이 오케스트레이션만 담당합니다.
 *
 * <ul>
 *   <li>레거시 composite 조회 (레거시 DB)
 *   <li>market DB Order 조회
 *   <li>상태 비교
 *   <li>변경 감지 시 UpdateFacade에 위임
 * </ul>
 */
@Component
public class LegacyOrderStatusSyncCoordinator {

    private static final Logger log =
            LoggerFactory.getLogger(LegacyOrderStatusSyncCoordinator.class);

    private final LegacyOrderCompositeReadManager compositeReadManager;
    private final LegacyOrderStatusMapper statusMapper;
    private final OrderReadManager orderReadManager;
    private final LegacyOrderStatusUpdateFacade updateFacade;
    private final LegacyOrderConversionOutboxCommandManager outboxCommandManager;

    public LegacyOrderStatusSyncCoordinator(
            LegacyOrderCompositeReadManager compositeReadManager,
            LegacyOrderStatusMapper statusMapper,
            OrderReadManager orderReadManager,
            LegacyOrderStatusUpdateFacade updateFacade,
            LegacyOrderConversionOutboxCommandManager outboxCommandManager) {
        this.compositeReadManager = compositeReadManager;
        this.statusMapper = statusMapper;
        this.orderReadManager = orderReadManager;
        this.updateFacade = updateFacade;
        this.outboxCommandManager = outboxCommandManager;
    }

    /**
     * 이미 이관된 주문의 상태를 동기화합니다.
     *
     * @param mapping 레거시→내부 ID 매핑
     * @param outbox 변환 Outbox (상태 관리용)
     */
    public void sync(LegacyOrderIdMapping mapping, LegacyOrderConversionOutbox outbox) {
        Instant now = Instant.now();
        long legacyOrderId = mapping.legacyOrderId();

        try {
            // 1. 레거시 상태 조회
            LegacyOrderCompositeResult composite =
                    compositeReadManager.fetchOrderComposite(legacyOrderId);

            // 2. 이관 제외 대상 확인
            if (!statusMapper.isEligibleForMigration(composite.orderStatus())) {
                log.info(
                        "상태 동기화 제외 대상: legacyOrderId={}, status={}",
                        legacyOrderId,
                        composite.orderStatus());
                completeOutbox(outbox, now);
                return;
            }

            // 3. 레거시 상태 → 내부 상태 매핑
            LegacyOrderStatusMapper.OrderStatusResolution resolution =
                    statusMapper.resolve(composite.orderStatus());

            // 4. market DB Order 조회
            OrderId orderId = OrderId.of(mapping.internalOrderId());
            Order order = orderReadManager.getById(orderId);

            // 5. 현재 OrderItem 상태와 비교 (레거시 주문은 단일 아이템)
            OrderItem orderItem = order.items().get(0);
            OrderItemStatus currentStatus = orderItem.status();
            OrderItemStatus targetStatus = resolveTargetStatus(resolution);

            if (currentStatus == targetStatus) {
                log.info("상태 동일, 건너뜀: legacyOrderId={}, status={}", legacyOrderId, currentStatus);
                completeOutbox(outbox, now);
                return;
            }

            // 6. 상태 변경 + Outbox 생성 (트랜잭션)
            LegacyOrderStatusSyncBundle syncBundle =
                    new LegacyOrderStatusSyncBundle(
                            order, orderItem, currentStatus, resolution, legacyOrderId);

            updateFacade.syncStatus(syncBundle, composite, now);

            // 7. 완료
            completeOutbox(outbox, now);

            log.info(
                    "레거시 주문 상태 동기화 완료: legacyOrderId={}, {} → {}",
                    legacyOrderId,
                    currentStatus,
                    targetStatus);

        } catch (Exception e) {
            log.error("레거시 주문 상태 동기화 실패: legacyOrderId={}", legacyOrderId, e);
            outboxCommandManager.failInNewTransaction(
                    outbox, truncateMessage(e.getMessage()), Instant.now());
        }
    }

    /**
     * OrderStatusResolution에서 목표 OrderItemStatus를 도출합니다.
     *
     * <p>LegacyOrderConversionFactory.resolveOrderItemStatus()와 동일한 로직입니다.
     */
    private OrderItemStatus resolveTargetStatus(
            LegacyOrderStatusMapper.OrderStatusResolution resolution) {
        if (resolution.hasCancel() && resolution.cancelStatus() == CancelStatus.COMPLETED) {
            return OrderItemStatus.CANCELLED;
        }
        if (resolution.hasRefund() && resolution.refundStatus() == RefundStatus.COMPLETED) {
            return OrderItemStatus.RETURNED;
        }
        if (resolution.hasRefund()
                && (resolution.refundStatus() == RefundStatus.REQUESTED
                        || resolution.refundStatus() == RefundStatus.COLLECTING
                        || resolution.refundStatus() == RefundStatus.COLLECTED)) {
            return OrderItemStatus.RETURN_REQUESTED;
        }
        if (resolution.needsShipment()) {
            return OrderItemStatus.CONFIRMED;
        }
        return OrderItemStatus.READY;
    }

    private void completeOutbox(LegacyOrderConversionOutbox outbox, Instant now) {
        outbox.complete(now);
        outboxCommandManager.persist(outbox);
    }

    private String truncateMessage(String message) {
        if (message == null) {
            return "알 수 없는 오류";
        }
        return message.length() > 900 ? message.substring(0, 900) : message;
    }
}
