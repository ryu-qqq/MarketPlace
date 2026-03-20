package com.ryuqq.marketplace.application.exchange.service.command;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.exchange.dto.ExchangeBatchResult;
import com.ryuqq.marketplace.application.exchange.dto.command.RequestExchangeBatchCommand;
import com.ryuqq.marketplace.application.exchange.dto.command.RequestExchangeBatchCommand.ExchangeRequestItem;
import com.ryuqq.marketplace.application.exchange.factory.ExchangeCommandFactory;
import com.ryuqq.marketplace.application.exchange.factory.ExchangeCommandFactory.ExchangeClaimWithHistory;
import com.ryuqq.marketplace.application.exchange.internal.ExchangePersistenceBundle;
import com.ryuqq.marketplace.application.exchange.internal.ExchangePersistenceFacade;
import com.ryuqq.marketplace.application.exchange.port.in.command.RequestExchangeBatchUseCase;
import com.ryuqq.marketplace.application.exchange.validator.ExchangeBatchValidator;
import com.ryuqq.marketplace.application.order.manager.OrderItemReadManager;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 교환 요청 일괄 처리 서비스.
 *
 * <p>교환 요청 시 OrderItem을 RETURN_REQUESTED로 전환합니다. 해당 OrderItem에 진행 중인 Refund/Exchange가 있으면 스킵합니다.
 */
@Service
public class RequestExchangeBatchService implements RequestExchangeBatchUseCase {

    private static final Logger log = LoggerFactory.getLogger(RequestExchangeBatchService.class);

    private final ExchangeCommandFactory commandFactory;
    private final ExchangePersistenceFacade persistenceFacade;
    private final OrderItemReadManager orderItemReadManager;
    private final ExchangeBatchValidator validator;

    public RequestExchangeBatchService(
            ExchangeCommandFactory commandFactory,
            ExchangePersistenceFacade persistenceFacade,
            OrderItemReadManager orderItemReadManager,
            ExchangeBatchValidator validator) {
        this.commandFactory = commandFactory;
        this.persistenceFacade = persistenceFacade;
        this.orderItemReadManager = orderItemReadManager;
        this.validator = validator;
    }

    @Override
    public BatchProcessingResult<String> execute(RequestExchangeBatchCommand command) {
        ExchangeBatchResult batchResult = ExchangeBatchResult.create("REQUEST");
        List<OrderItem> returnRequestedItems = new ArrayList<>();

        for (ExchangeRequestItem item : command.items()) {
            try {
                if (validator.hasActiveClaim(item.orderItemId())) {
                    batchResult.addFailure(item.orderItemId(), "해당 주문상품에 진행 중인 클레임이 있습니다");
                    continue;
                }

                ExchangeClaimWithHistory bundle =
                        commandFactory.createExchangeRequest(
                                item, command.requestedBy(), command.sellerId());
                batchResult.addSuccess(bundle.claim(), bundle.history());

                StatusChangeContext<OrderItemId> ctx =
                        commandFactory.createRequestOrderItemContext(item.orderItemId());
                Optional<OrderItem> orderItem =
                        orderItemReadManager.findById(ctx.id());
                orderItem.ifPresent(
                        oi -> {
                            oi.requestReturn(command.requestedBy(), "교환 요청", ctx.changedAt());
                            returnRequestedItems.add(oi);
                        });
            } catch (Exception e) {
                log.warn(
                        "교환 요청 생성 실패: orderItemId={}, error={}",
                        item.orderItemId(),
                        e.getMessage());
                batchResult.addFailure(item.orderItemId(), e.getMessage());
            }
        }

        if (batchResult.hasSuccessItems()) {
            persistenceFacade.persistAll(
                    ExchangePersistenceBundle.withOrderItems(
                            batchResult.claims(), batchResult.histories(), returnRequestedItems));
        }

        return batchResult.toBatchProcessingResult();
    }
}
