package com.ryuqq.marketplace.application.shipment.service.command;

import com.ryuqq.marketplace.application.common.dto.result.BatchItemResult;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.order.manager.OrderItemReadManager;
import com.ryuqq.marketplace.application.shipment.dto.command.ConfirmShipmentBatchCommand;
import com.ryuqq.marketplace.application.shipment.dto.command.ConfirmShipmentBundle;
import com.ryuqq.marketplace.application.shipment.factory.ShipmentCommandFactory;
import com.ryuqq.marketplace.application.shipment.internal.ShipmentPersistFacade;
import com.ryuqq.marketplace.application.shipment.port.in.command.ConfirmShipmentBatchUseCase;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 발주확인 일괄 처리 Service.
 *
 * <p>orderItemIds 기반으로 OrderItem 조회 → 소유권 검증 → 상태변경(READY→CONFIRMED) → Shipment 생성 → ShipmentOutbox
 * 생성 → 번들 persist.
 */
@Service
public class ConfirmShipmentBatchService implements ConfirmShipmentBatchUseCase {

    private final OrderItemReadManager orderItemReadManager;
    private final ShipmentCommandFactory commandFactory;
    private final ShipmentPersistFacade persistFacade;

    public ConfirmShipmentBatchService(
            OrderItemReadManager orderItemReadManager,
            ShipmentCommandFactory commandFactory,
            ShipmentPersistFacade persistFacade) {
        this.orderItemReadManager = orderItemReadManager;
        this.commandFactory = commandFactory;
        this.persistFacade = persistFacade;
    }

    @Override
    public BatchProcessingResult<String> execute(ConfirmShipmentBatchCommand command) {
        List<OrderItem> orderItems = orderItemReadManager.findAllByIds(command.orderItemIds());

        List<BatchItemResult<String>> results = new ArrayList<>();
        List<OrderItem> confirmable = new ArrayList<>();

        for (OrderItem item : orderItems) {
            String idStr = item.idValue();

            if (!validateOwnership(item, command.sellerId())) {
                results.add(BatchItemResult.failure(idStr, "FORBIDDEN", "해당 주문상품에 대한 권한이 없습니다"));
                continue;
            }

            if (!item.isConfirmable()) {
                results.add(
                        BatchItemResult.failure(
                                idStr, "INVALID_STATUS", "발주확인할 수 없는 상태입니다: " + item.status()));
                continue;
            }

            item.confirm();
            confirmable.add(item);
            results.add(BatchItemResult.success(idStr));
        }

        if (!confirmable.isEmpty()) {
            ConfirmShipmentBundle bundle = commandFactory.createConfirmBundle(confirmable);
            persistFacade.persistConfirmBundle(bundle);
        }

        return BatchProcessingResult.from(results);
    }

    private boolean validateOwnership(OrderItem item, Long sellerId) {
        if (sellerId == null) {
            return true;
        }
        return item.sellerId() == sellerId;
    }
}
