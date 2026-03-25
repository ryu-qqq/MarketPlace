package com.ryuqq.marketplace.application.exchange.service.command;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.exchange.dto.command.CompleteExchangeBatchCommand;
import com.ryuqq.marketplace.application.exchange.factory.ExchangeCommandFactory;
import com.ryuqq.marketplace.application.exchange.internal.ExchangeBatchResult;
import com.ryuqq.marketplace.application.exchange.internal.ExchangePersistenceBundle;
import com.ryuqq.marketplace.application.exchange.internal.ExchangePersistenceFacade;
import com.ryuqq.marketplace.application.exchange.port.in.command.CompleteExchangeBatchUseCase;
import com.ryuqq.marketplace.application.exchange.validator.ExchangeBatchValidator;
import com.ryuqq.marketplace.application.order.manager.OrderItemReadManager;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 교환 완료 일괄 처리 서비스 (SHIPPING → COMPLETED).
 *
 * <p>교환 완료 시 OrderItem을 RETURNED로 전환합니다.
 */
@Service
public class CompleteExchangeBatchService implements CompleteExchangeBatchUseCase {

    private static final Logger log = LoggerFactory.getLogger(CompleteExchangeBatchService.class);

    private final ExchangeBatchValidator validator;
    private final ExchangeCommandFactory commandFactory;
    private final ExchangePersistenceFacade persistenceFacade;
    private final OrderItemReadManager orderItemReadManager;

    public CompleteExchangeBatchService(
            ExchangeBatchValidator validator,
            ExchangeCommandFactory commandFactory,
            ExchangePersistenceFacade persistenceFacade,
            OrderItemReadManager orderItemReadManager) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.persistenceFacade = persistenceFacade;
        this.orderItemReadManager = orderItemReadManager;
    }

    @Override
    public BatchProcessingResult<String> execute(CompleteExchangeBatchCommand command) {
        List<ExchangeClaim> claims =
                validator.validateAndGet(command.exchangeClaimIds(), command.sellerId());

        ExchangeBatchResult batchResult = ExchangeBatchResult.create("COMPLETE");
        List<OrderItem> returnedItems = new ArrayList<>();

        for (ExchangeClaim claim : claims) {
            try {
                ClaimHistory history =
                        commandFactory.createCompleteBundle(claim, command.processedBy());
                batchResult.addSuccess(claim, history);

                StatusChangeContext<OrderItemId> ctx =
                        commandFactory.createCompleteOrderItemContext(claim.orderItemIdValue());
                Optional<OrderItem> orderItem = orderItemReadManager.findById(ctx.id());
                orderItem.ifPresent(
                        oi -> {
                            if (oi.remainingReturnableQty() > 0) {
                                int effectiveQty =
                                        Math.min(
                                                claim.exchangeQty(),
                                                oi.remainingReturnableQty());
                                oi.partialReturn(
                                        effectiveQty,
                                        command.processedBy(),
                                        "교환 완료",
                                        ctx.changedAt());
                                returnedItems.add(oi);
                            }
                        });
            } catch (Exception e) {
                log.warn("교환 완료 실패: exchangeClaimId={}, error={}", claim.idValue(), e.getMessage());
                batchResult.addFailure(claim.idValue(), e.getMessage());
            }
        }

        if (batchResult.hasSuccessItems()) {
            persistenceFacade.persistAll(
                    ExchangePersistenceBundle.withOrderItems(
                            batchResult.claims(), batchResult.histories(), returnedItems));
        }

        return batchResult.toBatchProcessingResult();
    }
}
