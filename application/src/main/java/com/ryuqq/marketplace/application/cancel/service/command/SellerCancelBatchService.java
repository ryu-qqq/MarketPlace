package com.ryuqq.marketplace.application.cancel.service.command;

import com.ryuqq.marketplace.application.cancel.dto.CancelBatchResult;
import com.ryuqq.marketplace.application.cancel.dto.command.SellerCancelBatchCommand;
import com.ryuqq.marketplace.application.cancel.dto.command.SellerCancelBatchCommand.SellerCancelItem;
import com.ryuqq.marketplace.application.cancel.factory.CancelCommandFactory;
import com.ryuqq.marketplace.application.cancel.factory.CancelCommandFactory.CancelBundle;
import com.ryuqq.marketplace.application.cancel.internal.CancelPersistenceFacade;
import com.ryuqq.marketplace.application.cancel.port.in.command.SellerCancelBatchUseCase;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.order.manager.OrderItemReadManager;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 판매자 취소 일괄 처리 서비스.
 *
 * <p>판매자 취소는 자동 승인이므로 OrderItem도 즉시 CANCELLED로 전환합니다.
 */
@Service
public class SellerCancelBatchService implements SellerCancelBatchUseCase {

    private static final Logger log = LoggerFactory.getLogger(SellerCancelBatchService.class);

    private final CancelCommandFactory commandFactory;
    private final CancelPersistenceFacade persistenceFacade;
    private final OrderItemReadManager orderItemReadManager;

    public SellerCancelBatchService(
            CancelCommandFactory commandFactory,
            CancelPersistenceFacade persistenceFacade,
            OrderItemReadManager orderItemReadManager) {
        this.commandFactory = commandFactory;
        this.persistenceFacade = persistenceFacade;
        this.orderItemReadManager = orderItemReadManager;
    }

    @Override
    public BatchProcessingResult<String> execute(SellerCancelBatchCommand command) {
        CancelBatchResult batchResult = CancelBatchResult.create("SELLER_CANCEL");
        List<OrderItem> cancelledItems = new ArrayList<>();

        for (SellerCancelItem item : command.items()) {
            try {
                CancelBundle bundle =
                        commandFactory.createSellerCancel(
                                item, command.requestedBy(), command.sellerId());
                batchResult.addSuccess(bundle.cancel(), bundle.outbox(), bundle.history());

                Optional<OrderItem> orderItem =
                        orderItemReadManager.findById(item.orderItemId());
                orderItem.ifPresent(oi -> {
                    oi.cancel(command.requestedBy(), "판매자 취소", commandFactory.now());
                    cancelledItems.add(oi);
                });
            } catch (Exception e) {
                log.warn(
                        "판매자 취소 생성 실패: orderItemId={}, error={}",
                        item.orderItemId(),
                        e.getMessage());
                batchResult.addFailure(String.valueOf(item.orderItemId()), e.getMessage());
            }
        }

        if (batchResult.hasSuccessItems()) {
            persistenceFacade.persistAllWithOutboxesAndHistoriesAndOrderItems(
                    batchResult.cancels(), batchResult.outboxes(),
                    batchResult.histories(), cancelledItems);
        }

        return batchResult.toBatchProcessingResult();
    }
}
