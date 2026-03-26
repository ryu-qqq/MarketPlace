package com.ryuqq.marketplace.application.shipment.service.command;

import com.ryuqq.marketplace.application.claimsync.manager.ExternalOrderItemMappingReadManager;
import com.ryuqq.marketplace.application.common.dto.result.OutboxSyncResult;
import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
import com.ryuqq.marketplace.application.shipment.dto.command.ExecuteShipmentOutboxCommand;
import com.ryuqq.marketplace.application.shipment.factory.ShipmentCommandFactory;
import com.ryuqq.marketplace.application.shipment.internal.ShipmentSyncStrategyProvider;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentOutboxCommandManager;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentOutboxReadManager;
import com.ryuqq.marketplace.application.shipment.port.in.command.ExecuteShipmentOutboxUseCase;
import com.ryuqq.marketplace.application.shipment.port.out.client.ShipmentSyncStrategy;
import com.ryuqq.marketplace.application.shop.manager.ShopReadManager;
import com.ryuqq.marketplace.domain.ordermapping.aggregate.ExternalOrderItemMapping;
import com.ryuqq.marketplace.domain.shipment.exception.ExternalMappingNotFoundException;
import com.ryuqq.marketplace.domain.shipment.outbox.aggregate.ShipmentOutbox;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 배송 Outbox 실행 서비스.
 *
 * <p>SQS Consumer에서 수신한 배송 Outbox를 처리합니다. orderItemId로 ExternalOrderItemMapping을 조회하여 채널 코드를 식별하고,
 * ShipmentSyncStrategyProvider를 통해 O(1)로 라우팅합니다.
 *
 * <p><strong>트랜잭션 전략</strong>: {@code @Transactional} 없음. 외부 API 호출이 포함되므로 상태 변경마다 별도 트랜잭션(Manager
 * 레벨)으로 커밋합니다. 낙관적 락 충돌 방지를 위해 re-read 패턴을 적용합니다.
 */
@Service
@ConditionalOnProperty(prefix = "sqs.queues", name = "shipment-outbox")
public class ExecuteShipmentOutboxService implements ExecuteShipmentOutboxUseCase {

    private static final Logger log = LoggerFactory.getLogger(ExecuteShipmentOutboxService.class);

    private final ShipmentOutboxReadManager outboxReadManager;
    private final ShipmentOutboxCommandManager outboxCommandManager;
    private final ShipmentSyncStrategyProvider strategyProvider;
    private final ExternalOrderItemMappingReadManager mappingReadManager;
    private final ShipmentCommandFactory commandFactory;
    private final ShopReadManager shopReadManager;

    public ExecuteShipmentOutboxService(
            ShipmentOutboxReadManager outboxReadManager,
            ShipmentOutboxCommandManager outboxCommandManager,
            ShipmentSyncStrategyProvider strategyProvider,
            ExternalOrderItemMappingReadManager mappingReadManager,
            ShipmentCommandFactory commandFactory,
            ShopReadManager shopReadManager) {
        this.outboxReadManager = outboxReadManager;
        this.outboxCommandManager = outboxCommandManager;
        this.strategyProvider = strategyProvider;
        this.mappingReadManager = mappingReadManager;
        this.commandFactory = commandFactory;
        this.shopReadManager = shopReadManager;
    }

    @Override
    public void execute(ExecuteShipmentOutboxCommand command) {
        ShipmentOutbox outbox = outboxReadManager.getById(command.outboxId());

        try {
            ExternalOrderItemMapping mapping = resolveMapping(outbox);
            String channelCode = mapping.channelCode();
            ShipmentSyncStrategy strategy = strategyProvider.getStrategy(channelCode);
            Shop shop = resolveShop(mapping);
            OutboxSyncResult result = strategy.execute(outbox, shop);

            if (result.isSuccess()) {
                handleSuccess(outbox);
            } else {
                handleFailure(outbox, result);
            }

        } catch (ExternalServiceUnavailableException e) {
            log.warn(
                    "배송 Outbox 외부 서비스 일시 장애 (deferRetry): outboxId={}, error={}",
                    command.outboxId(),
                    e.getMessage());
            handleDeferRetry(outbox);
        } catch (Exception e) {
            log.error(
                    "배송 Outbox 처리 실패: outboxId={}, error={}",
                    command.outboxId(),
                    e.getMessage(),
                    e);
            persistFailureWithReRead(outbox.idValue(), true, "실행 중 예외: " + e.getMessage());
        }
    }

    private ExternalOrderItemMapping resolveMapping(ShipmentOutbox outbox) {
        ExternalOrderItemMapping mapping =
                mappingReadManager.findByOrderItemId(outbox.orderItemIdValue());
        if (mapping == null) {
            throw new ExternalMappingNotFoundException(outbox.orderItemIdValue());
        }
        return mapping;
    }

    private Shop resolveShop(ExternalOrderItemMapping mapping) {
        java.util.List<Shop> shops =
                shopReadManager.findActiveBySalesChannelId(mapping.salesChannelId());
        if (shops.isEmpty()) {
            return null;
        }
        return shops.get(0);
    }

    private void handleSuccess(ShipmentOutbox outbox) {
        ShipmentOutbox fresh = outboxReadManager.getById(outbox.idValue());
        Instant now = commandFactory.createOutboxTransitionContext(outbox.idValue()).changedAt();
        fresh.complete(now);
        outboxCommandManager.persist(fresh);
    }

    private void handleFailure(ShipmentOutbox outbox, OutboxSyncResult result) {
        persistFailureWithReRead(outbox.idValue(), result.retryable(), result.errorMessage());
    }

    private void handleDeferRetry(ShipmentOutbox outbox) {
        try {
            ShipmentOutbox fresh = outboxReadManager.getById(outbox.idValue());
            Instant now =
                    commandFactory.createOutboxTransitionContext(outbox.idValue()).changedAt();
            fresh.recoverFromTimeout(now);
            outboxCommandManager.persist(fresh);
        } catch (RuntimeException e) {
            log.warn("배송 Outbox deferRetry 실패: outboxId={}", outbox.idValue());
        }
    }

    private void persistFailureWithReRead(Long outboxId, boolean retryable, String errorMessage) {
        try {
            ShipmentOutbox fresh = outboxReadManager.getById(outboxId);
            Instant now = commandFactory.createOutboxTransitionContext(outboxId).changedAt();
            fresh.recordFailure(retryable, errorMessage, now);
            outboxCommandManager.persist(fresh);
        } catch (Exception e) {
            log.warn(
                    "배송 Outbox re-read 실패, 상태 변경 건너뜀: outboxId={}, error={}",
                    outboxId,
                    e.getMessage());
        }
    }
}
