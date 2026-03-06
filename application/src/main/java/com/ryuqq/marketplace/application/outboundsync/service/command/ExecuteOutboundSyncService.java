package com.ryuqq.marketplace.application.outboundsync.service.command;

import com.ryuqq.marketplace.application.outboundproduct.manager.OutboundProductCommandManager;
import com.ryuqq.marketplace.application.outboundproduct.manager.OutboundProductReadManager;
import com.ryuqq.marketplace.application.outboundsync.dto.command.ExecuteOutboundSyncCommand;
import com.ryuqq.marketplace.application.outboundsync.dto.vo.OutboundSyncExecutionContext;
import com.ryuqq.marketplace.application.outboundsync.dto.vo.OutboundSyncExecutionResult;
import com.ryuqq.marketplace.application.outboundsync.internal.OutboundSyncStrategyRouter;
import com.ryuqq.marketplace.application.outboundsync.manager.OutboundSyncOutboxCommandManager;
import com.ryuqq.marketplace.application.outboundsync.manager.OutboundSyncOutboxReadManager;
import com.ryuqq.marketplace.application.outboundsync.port.in.command.ExecuteOutboundSyncUseCase;
import com.ryuqq.marketplace.application.outboundsync.port.out.strategy.OutboundSyncExecutionStrategy;
import com.ryuqq.marketplace.application.sellersaleschannel.manager.SellerSalesChannelReadManager;
import com.ryuqq.marketplace.domain.outboundproduct.aggregate.OutboundProduct;
import com.ryuqq.marketplace.domain.outboundsync.aggregate.OutboundSyncOutbox;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncType;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 외부 채널 연동 실행 서비스.
 *
 * <p>SQS 컨슈머에서 수신한 메시지를 처리합니다. Strategy 패턴으로 채널별 분기하여 외부 API를 호출하고, 결과에 따라 Outbox 및 OutboundProduct
 * 상태를 업데이트합니다.
 *
 * <p><strong>트랜잭션 전략</strong>: {@code @Transactional} 없음. 외부 API 호출이 포함되므로 상태 변경마다 별도 트랜잭션(Manager
 * 레벨)으로 커밋합니다. 낙관적 락 충돌 방지를 위해 re-read 패턴을 적용합니다.
 *
 * @see com.ryuqq.marketplace.application.seller.internal.SellerAuthOutboxProcessor
 */
@Service
public class ExecuteOutboundSyncService implements ExecuteOutboundSyncUseCase {

    private static final Logger log = LoggerFactory.getLogger(ExecuteOutboundSyncService.class);

    private final OutboundSyncOutboxReadManager outboxReadManager;
    private final OutboundSyncOutboxCommandManager outboxCommandManager;
    private final OutboundSyncStrategyRouter strategyRouter;
    private final SellerSalesChannelReadManager channelReadManager;
    private final OutboundProductReadManager productReadManager;
    private final OutboundProductCommandManager productCommandManager;

    public ExecuteOutboundSyncService(
            OutboundSyncOutboxReadManager outboxReadManager,
            OutboundSyncOutboxCommandManager outboxCommandManager,
            OutboundSyncStrategyRouter strategyRouter,
            SellerSalesChannelReadManager channelReadManager,
            OutboundProductReadManager productReadManager,
            OutboundProductCommandManager productCommandManager) {
        this.outboxReadManager = outboxReadManager;
        this.outboxCommandManager = outboxCommandManager;
        this.strategyRouter = strategyRouter;
        this.channelReadManager = channelReadManager;
        this.productReadManager = productReadManager;
        this.productCommandManager = productCommandManager;
    }

    @Override
    public void execute(ExecuteOutboundSyncCommand command) {
        OutboundSyncOutbox outbox = outboxReadManager.getById(command.outboxId());

        try {
            SellerSalesChannel channel =
                    channelReadManager.getBySellerIdAndSalesChannelId(
                            SellerId.of(outbox.sellerIdValue()),
                            SalesChannelId.of(outbox.salesChannelIdValue()));

            OutboundSyncExecutionStrategy strategy =
                    strategyRouter.route(channel.channelCode(), command.syncType());

            OutboundSyncExecutionContext context =
                    new OutboundSyncExecutionContext(
                            outbox, channel, command.productGroupId(), command.syncType());

            OutboundSyncExecutionResult result = strategy.execute(context);

            if (result.success()) {
                handleSuccess(outbox, command, result);
            } else {
                handleFailure(outbox, result);
            }

        } catch (Exception e) {
            log.error(
                    "OutboundSync 실행 중 예외: outboxId={}, error={}",
                    command.outboxId(),
                    e.getMessage(),
                    e);
            handleException(outbox, e);
        }
    }

    private void handleSuccess(
            OutboundSyncOutbox outbox,
            ExecuteOutboundSyncCommand command,
            OutboundSyncExecutionResult result) {
        Instant now = Instant.now();

        OutboundSyncOutbox freshOutbox = outboxReadManager.getById(outbox.idValue());
        freshOutbox.complete(now);
        outboxCommandManager.persist(freshOutbox);

        if (command.syncType() == SyncType.CREATE) {
            try {
                OutboundProduct product =
                        productReadManager.getByProductGroupIdAndSalesChannelId(
                                command.productGroupId(), command.salesChannelId());
                product.registerExternalProduct(result.externalProductId(), now);
                productCommandManager.persist(product);
            } catch (Exception e) {
                log.warn(
                        "OutboundProduct 상태 업데이트 실패 (Outbox는 COMPLETED): outboxId={}, error={}",
                        outbox.idValue(),
                        e.getMessage());
            }
        }

        if (command.syncType() == SyncType.DELETE) {
            try {
                OutboundProduct product =
                        productReadManager.getByProductGroupIdAndSalesChannelId(
                                command.productGroupId(), command.salesChannelId());
                product.deregister(now);
                productCommandManager.persist(product);
            } catch (Exception e) {
                log.warn(
                        "OutboundProduct DEREGISTERED 변경 실패 (Outbox는 COMPLETED): outboxId={},"
                                + " error={}",
                        outbox.idValue(),
                        e.getMessage());
            }
        }

        log.info(
                "OutboundSync 완료: outboxId={}, productGroupId={}, salesChannelId={},"
                        + " syncType={}, externalProductId={}",
                command.outboxId(),
                command.productGroupId(),
                command.salesChannelId(),
                command.syncType(),
                result.externalProductId());
    }

    /**
     * 전략 실행 실패 처리 (re-read 패턴).
     *
     * <p>낙관적 락 충돌 방지: 최신 Outbox를 다시 조회한 뒤 실패 상태를 기록합니다.
     */
    private void handleFailure(OutboundSyncOutbox outbox, OutboundSyncExecutionResult result) {
        persistFailureWithReRead(outbox.idValue(), result.retryable(), result.errorMessage());
    }

    private void handleException(OutboundSyncOutbox outbox, Exception e) {
        persistFailureWithReRead(outbox.idValue(), true, "실행 중 예외: " + e.getMessage());
    }

    private void persistFailureWithReRead(Long outboxId, boolean retryable, String errorMessage) {
        try {
            OutboundSyncOutbox freshOutbox = outboxReadManager.getById(outboxId);
            freshOutbox.recordFailure(retryable, errorMessage, Instant.now());
            outboxCommandManager.persist(freshOutbox);
        } catch (Exception reReadEx) {
            log.warn(
                    "Outbox re-read 실패, 상태 변경 건너뜀: outboxId={}, error={}",
                    outboxId,
                    reReadEx.getMessage());
        }
    }
}
