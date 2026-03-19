package com.ryuqq.marketplace.application.refund.service.command;

import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.order.manager.OrderItemReadManager;
import com.ryuqq.marketplace.application.refund.dto.RefundBatchResult;
import com.ryuqq.marketplace.application.refund.dto.command.RequestRefundBatchCommand;
import com.ryuqq.marketplace.application.refund.dto.command.RequestRefundBatchCommand.RefundRequestItem;
import com.ryuqq.marketplace.application.refund.factory.RefundCommandFactory;
import com.ryuqq.marketplace.application.refund.factory.RefundCommandFactory.RefundBundle;
import com.ryuqq.marketplace.application.refund.internal.RefundPersistenceFacade;
import com.ryuqq.marketplace.application.refund.port.in.command.RequestRefundBatchUseCase;
import com.ryuqq.marketplace.application.refund.validator.RefundBatchValidator;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 환불 요청 일괄 처리 서비스.
 *
 * <p>환불 요청 시 OrderItem을 RETURN_REQUESTED로 전환합니다. 해당 OrderItem에 진행 중인 Refund/Exchange가 있으면 스킵합니다.
 */
@Service
public class RequestRefundBatchService implements RequestRefundBatchUseCase {

    private static final Logger log = LoggerFactory.getLogger(RequestRefundBatchService.class);

    private final RefundCommandFactory commandFactory;
    private final RefundPersistenceFacade persistenceFacade;
    private final OrderItemReadManager orderItemReadManager;
    private final RefundBatchValidator validator;

    public RequestRefundBatchService(
            RefundCommandFactory commandFactory,
            RefundPersistenceFacade persistenceFacade,
            OrderItemReadManager orderItemReadManager,
            RefundBatchValidator validator) {
        this.commandFactory = commandFactory;
        this.persistenceFacade = persistenceFacade;
        this.orderItemReadManager = orderItemReadManager;
        this.validator = validator;
    }

    @Override
    public BatchProcessingResult<String> execute(RequestRefundBatchCommand command) {
        RefundBatchResult batchResult = RefundBatchResult.create("REQUEST");
        List<OrderItem> returnRequestedItems = new ArrayList<>();

        for (RefundRequestItem item : command.items()) {
            try {
                if (validator.hasActiveClaim(item.orderItemId())) {
                    batchResult.addFailure(item.orderItemId(), "해당 주문상품에 진행 중인 클레임이 있습니다");
                    continue;
                }

                RefundBundle bundle =
                        commandFactory.createRefundRequest(
                                item, command.requestedBy(), command.sellerId());
                batchResult.addSuccess(bundle.claim(), bundle.outbox(), bundle.history());

                Optional<OrderItem> orderItem =
                        orderItemReadManager.findById(OrderItemId.of(item.orderItemId()));
                orderItem.ifPresent(
                        oi -> {
                            if (oi.status()
                                    .canTransitionTo(
                                            com.ryuqq.marketplace.domain.order.vo.OrderItemStatus
                                                    .RETURN_REQUESTED)) {
                                oi.requestReturn(
                                        command.requestedBy(), "환불 요청", commandFactory.now());
                                returnRequestedItems.add(oi);
                            }
                        });
            } catch (Exception e) {
                log.warn(
                        "환불 요청 생성 실패: orderItemId={}, error={}",
                        item.orderItemId(),
                        e.getMessage());
                batchResult.addFailure(item.orderItemId(), e.getMessage());
            }
        }

        if (batchResult.hasSuccessItems()) {
            persistenceFacade.persistAllWithOutboxesAndHistoriesAndOrderItems(
                    batchResult.claims(),
                    batchResult.outboxes(),
                    batchResult.histories(),
                    returnRequestedItems);
        }

        return batchResult.toBatchProcessingResult();
    }
}
