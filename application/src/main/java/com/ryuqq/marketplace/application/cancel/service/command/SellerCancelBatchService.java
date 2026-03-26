package com.ryuqq.marketplace.application.cancel.service.command;

import com.ryuqq.marketplace.application.cancel.dto.command.SellerCancelBatchCommand;
import com.ryuqq.marketplace.application.cancel.dto.command.SellerCancelBatchCommand.SellerCancelItem;
import com.ryuqq.marketplace.application.cancel.factory.CancelCommandFactory;
import com.ryuqq.marketplace.application.cancel.factory.CancelCommandFactory.CancelBundle;
import com.ryuqq.marketplace.application.cancel.internal.CancelBatchResult;
import com.ryuqq.marketplace.application.cancel.internal.CancelPersistenceBundle;
import com.ryuqq.marketplace.application.cancel.internal.CancelPersistenceFacade;
import com.ryuqq.marketplace.application.cancel.port.in.command.SellerCancelBatchUseCase;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.order.manager.OrderItemReadManager;
import com.ryuqq.marketplace.application.shipment.internal.ShipmentCancelHelper;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import java.time.Instant;
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
    private final ShipmentCancelHelper shipmentCancelHelper;

    public SellerCancelBatchService(
            CancelCommandFactory commandFactory,
            CancelPersistenceFacade persistenceFacade,
            OrderItemReadManager orderItemReadManager,
            ShipmentCancelHelper shipmentCancelHelper) {
        this.commandFactory = commandFactory;
        this.persistenceFacade = persistenceFacade;
        this.orderItemReadManager = orderItemReadManager;
        this.shipmentCancelHelper = shipmentCancelHelper;
    }

    @Override
    public BatchProcessingResult<String> execute(SellerCancelBatchCommand command) {
        CancelBatchResult batchResult = CancelBatchResult.create("SELLER_CANCEL");
        List<OrderItem> cancelledItems = new ArrayList<>();
        List<OrderItemId> cancelledOrderItemIds = new ArrayList<>();
        Instant changedAt = null;

        for (SellerCancelItem item : command.items()) {
            try {
                CancelBundle bundle =
                        commandFactory.createSellerCancel(
                                item, command.requestedBy(), command.sellerId());
                batchResult.addSuccess(bundle.cancel(), bundle.outbox(), bundle.history());
                changedAt = bundle.changedAt();

                Optional<OrderItem> orderItem =
                        orderItemReadManager.findById(OrderItemId.of(item.orderItemId()));
                orderItem.ifPresent(
                        oi -> {
                            oi.partialCancel(
                                    item.cancelQty(),
                                    command.requestedBy(),
                                    "판매자 취소",
                                    bundle.changedAt());
                            cancelledItems.add(oi);
                            if (oi.isFullyCancelled()) {
                                cancelledOrderItemIds.add(OrderItemId.of(item.orderItemId()));
                            }
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
            List<Shipment> cancelledShipments =
                    shipmentCancelHelper.cancelPreparingShipments(cancelledOrderItemIds, changedAt);
            persistenceFacade.persistAll(
                    CancelPersistenceBundle.withOrderItemsAndShipments(
                            batchResult.cancels(),
                            batchResult.outboxes(),
                            batchResult.histories(),
                            cancelledItems,
                            cancelledShipments));
        }

        return batchResult.toBatchProcessingResult();
    }
}
