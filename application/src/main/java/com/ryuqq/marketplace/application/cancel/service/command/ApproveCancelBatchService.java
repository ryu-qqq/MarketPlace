package com.ryuqq.marketplace.application.cancel.service.command;

import com.ryuqq.marketplace.application.cancel.dto.command.ApproveCancelBatchCommand;
import com.ryuqq.marketplace.application.cancel.factory.CancelCommandFactory;
import com.ryuqq.marketplace.application.cancel.factory.CancelCommandFactory.OutboxWithHistory;
import com.ryuqq.marketplace.application.cancel.internal.CancelBatchResult;
import com.ryuqq.marketplace.application.cancel.internal.CancelPersistenceBundle;
import com.ryuqq.marketplace.application.cancel.internal.CancelPersistenceFacade;
import com.ryuqq.marketplace.application.cancel.port.in.command.ApproveCancelBatchUseCase;
import com.ryuqq.marketplace.application.cancel.validator.CancelBatchValidator;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.order.manager.OrderItemReadManager;
import com.ryuqq.marketplace.application.shipment.internal.ShipmentCancelHelper;
import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
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
 * 취소 승인 일괄 처리 서비스.
 *
 * <p>취소 승인 시 OrderItem도 CANCELLED로 전환합니다.
 */
@Service
public class ApproveCancelBatchService implements ApproveCancelBatchUseCase {

    private static final Logger log = LoggerFactory.getLogger(ApproveCancelBatchService.class);

    private final CancelBatchValidator validator;
    private final CancelCommandFactory commandFactory;
    private final CancelPersistenceFacade persistenceFacade;
    private final OrderItemReadManager orderItemReadManager;
    private final ShipmentCancelHelper shipmentCancelHelper;

    public ApproveCancelBatchService(
            CancelBatchValidator validator,
            CancelCommandFactory commandFactory,
            CancelPersistenceFacade persistenceFacade,
            OrderItemReadManager orderItemReadManager,
            ShipmentCancelHelper shipmentCancelHelper) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.persistenceFacade = persistenceFacade;
        this.orderItemReadManager = orderItemReadManager;
        this.shipmentCancelHelper = shipmentCancelHelper;
    }

    @Override
    public BatchProcessingResult<String> execute(ApproveCancelBatchCommand command) {
        List<Cancel> cancels = validator.validateAndGet(command.cancelIds(), command.sellerId());

        CancelBatchResult batchResult = CancelBatchResult.create("APPROVE");
        List<OrderItem> cancelledItems = new ArrayList<>();
        List<OrderItemId> cancelledOrderItemIds = new ArrayList<>();
        Instant changedAt = null;

        for (Cancel cancel : cancels) {
            try {
                OutboxWithHistory bundle =
                        commandFactory.createApproveBundle(cancel, command.processedBy());
                cancel.approve(command.processedBy(), bundle.changedAt());
                batchResult.addSuccess(cancel, bundle.outbox(), bundle.history());
                changedAt = bundle.changedAt();

                Optional<OrderItem> orderItem =
                        orderItemReadManager.findById(OrderItemId.of(cancel.orderItemIdValue()));
                orderItem.ifPresent(
                        oi -> {
                            oi.partialCancel(
                                    cancel.cancelQty(),
                                    command.processedBy(),
                                    "취소 승인",
                                    bundle.changedAt());
                            cancelledItems.add(oi);
                            if (oi.isFullyCancelled()) {
                                cancelledOrderItemIds.add(cancel.orderItemId());
                            }
                        });
            } catch (Exception e) {
                log.warn("취소 승인 실패: cancelId={}, error={}", cancel.idValue(), e.getMessage());
                batchResult.addFailure(cancel.idValue(), e.getMessage());
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
