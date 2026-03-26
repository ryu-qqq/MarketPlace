package com.ryuqq.marketplace.application.refund.service.command;

import com.ryuqq.marketplace.application.claimsync.manager.ExternalOrderItemMappingReadManager;
import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.dto.result.OutboxSyncResult;
import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
import com.ryuqq.marketplace.application.refund.dto.command.ExecuteRefundOutboxCommand;
import com.ryuqq.marketplace.application.refund.factory.RefundCommandFactory;
import com.ryuqq.marketplace.application.refund.manager.RefundOutboxCommandManager;
import com.ryuqq.marketplace.application.refund.manager.RefundOutboxReadManager;
import com.ryuqq.marketplace.application.refund.port.in.command.ExecuteRefundOutboxUseCase;
import com.ryuqq.marketplace.application.refund.port.out.client.RefundClaimSyncStrategy;
import com.ryuqq.marketplace.application.shop.manager.ShopReadManager;
import com.ryuqq.marketplace.domain.ordermapping.aggregate.ExternalOrderItemMapping;
import com.ryuqq.marketplace.domain.refund.outbox.aggregate.RefundOutbox;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 환불 Outbox 실행 서비스.
 *
 * <p>SQS Consumer에서 수신한 환불 Outbox를 처리합니다. Strategy 패턴으로 외부 API를 호출하고, 결과에 따라 Outbox 상태를 업데이트합니다.
 *
 * <p><strong>트랜잭션 전략</strong>: {@code @Transactional} 없음. 외부 API 호출이 포함되므로 상태 변경마다 별도 트랜잭션(Manager
 * 레벨)으로 커밋합니다. 낙관적 락 충돌 방지를 위해 re-read 패턴을 적용합니다.
 */
@Service
@ConditionalOnProperty(prefix = "sqs.queues", name = "refund-outbox")
public class ExecuteRefundOutboxService implements ExecuteRefundOutboxUseCase {

    private static final Logger log = LoggerFactory.getLogger(ExecuteRefundOutboxService.class);

    private final RefundOutboxReadManager outboxReadManager;
    private final RefundOutboxCommandManager outboxCommandManager;
    private final RefundClaimSyncStrategy claimSyncStrategy;
    private final RefundCommandFactory commandFactory;
    private final ExternalOrderItemMappingReadManager mappingReadManager;
    private final ShopReadManager shopReadManager;

    public ExecuteRefundOutboxService(
            RefundOutboxReadManager outboxReadManager,
            RefundOutboxCommandManager outboxCommandManager,
            RefundClaimSyncStrategy claimSyncStrategy,
            RefundCommandFactory commandFactory,
            ExternalOrderItemMappingReadManager mappingReadManager,
            ShopReadManager shopReadManager) {
        this.outboxReadManager = outboxReadManager;
        this.outboxCommandManager = outboxCommandManager;
        this.claimSyncStrategy = claimSyncStrategy;
        this.commandFactory = commandFactory;
        this.mappingReadManager = mappingReadManager;
        this.shopReadManager = shopReadManager;
    }

    @Override
    public void execute(ExecuteRefundOutboxCommand command) {
        RefundOutbox outbox = outboxReadManager.getById(command.outboxId());

        try {
            Shop shop = resolveShop(outbox.orderItemIdValue());
            OutboxSyncResult result = claimSyncStrategy.execute(outbox, shop);

            if (result.isSuccess()) {
                handleSuccess(outbox);
            } else {
                handleFailure(outbox, result);
            }

        } catch (ExternalServiceUnavailableException e) {
            log.warn(
                    "환불 Outbox 외부 서비스 일시 장애 (deferRetry): outboxId={}, error={}",
                    command.outboxId(),
                    e.getMessage());
            handleDeferRetry(outbox);
        } catch (Exception e) {
            log.error(
                    "환불 Outbox 처리 실패: outboxId={}, error={}",
                    command.outboxId(),
                    e.getMessage(),
                    e);
            persistFailureWithReRead(outbox.idValue(), true, "실행 중 예외: " + e.getMessage());
        }
    }

    private Shop resolveShop(String orderItemId) {
        ExternalOrderItemMapping mapping = mappingReadManager.findByOrderItemId(orderItemId);
        if (mapping == null) {
            throw new IllegalStateException("외부 주문 매핑을 찾을 수 없습니다: orderItemId=" + orderItemId);
        }
        List<Shop> shops = shopReadManager.findActiveBySalesChannelId(mapping.salesChannelId());
        if (shops.isEmpty()) {
            throw new IllegalStateException(
                    "활성 Shop을 찾을 수 없습니다: salesChannelId=" + mapping.salesChannelId());
        }
        return shops.get(0);
    }

    private void handleSuccess(RefundOutbox outbox) {
        StatusChangeContext<Long> ctx = commandFactory.createOutboxChangeContext(outbox.idValue());
        RefundOutbox fresh = outboxReadManager.getById(ctx.id());
        fresh.complete(ctx.changedAt());
        outboxCommandManager.persist(fresh);
    }

    private void handleFailure(RefundOutbox outbox, OutboxSyncResult result) {
        persistFailureWithReRead(outbox.idValue(), result.retryable(), result.errorMessage());
    }

    private void handleDeferRetry(RefundOutbox outbox) {
        try {
            StatusChangeContext<Long> ctx =
                    commandFactory.createOutboxChangeContext(outbox.idValue());
            RefundOutbox fresh = outboxReadManager.getById(ctx.id());
            fresh.recoverFromTimeout(ctx.changedAt());
            outboxCommandManager.persist(fresh);
        } catch (RuntimeException e) {
            log.warn("환불 Outbox deferRetry 실패: outboxId={}", outbox.idValue());
        }
    }

    private void persistFailureWithReRead(Long outboxId, boolean retryable, String errorMessage) {
        try {
            StatusChangeContext<Long> ctx = commandFactory.createOutboxChangeContext(outboxId);
            RefundOutbox fresh = outboxReadManager.getById(ctx.id());
            fresh.recordFailure(retryable, errorMessage, ctx.changedAt());
            outboxCommandManager.persist(fresh);
        } catch (Exception e) {
            log.warn(
                    "환불 Outbox re-read 실패, 상태 변경 건너뜀: outboxId={}, error={}",
                    outboxId,
                    e.getMessage());
        }
    }
}
