package com.ryuqq.marketplace.application.shipment.internal;

import com.ryuqq.marketplace.application.common.dto.command.BulkStatusChangeContext;
import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.common.dto.result.BatchItemResult;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.shipment.dto.command.ShipBatchCommand.ShipBatchItem;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentReadManager;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import com.ryuqq.marketplace.domain.shipment.outbox.aggregate.ShipmentOutbox;
import com.ryuqq.marketplace.domain.shipment.outbox.vo.ShipmentOutboxType;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentShipData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * 배송 일괄 처리 프로세서.
 *
 * <p>bulk fetch → 상태변경 → 아웃박스 생성 → 결과수집 → bulk persist 파이프라인을 담당합니다.
 */
@Component
public class ShipmentBatchProcessor {

    private final ShipmentReadManager readManager;
    private final ShipmentPersistFacade persistFacade;

    public ShipmentBatchProcessor(
            ShipmentReadManager readManager, ShipmentPersistFacade persistFacade) {
        this.readManager = readManager;
        this.persistFacade = persistFacade;
    }

    /**
     * 발주확인 일괄 처리.
     *
     * <p>bulk fetch → prepare → CONFIRM 아웃박스 생성 → bulk persist.
     */
    public BatchProcessingResult<String> confirmBatch(
            BulkStatusChangeContext<OrderItemId> context) {
        Map<String, Shipment> shipmentMap = fetchShipmentMap(context.ids());

        List<BatchItemResult<String>> results = new ArrayList<>();
        List<Shipment> succeeded = new ArrayList<>();
        List<ShipmentOutbox> outboxes = new ArrayList<>();

        for (OrderItemId orderItemId : context.ids()) {
            String idStr = orderItemId.value();
            Shipment shipment = shipmentMap.get(idStr);

            if (shipment == null) {
                results.add(notFoundResult(idStr));
                continue;
            }

            if (!shipment.canPrepare()) {
                results.add(invalidStatusResult(idStr, shipment));
                continue;
            }

            shipment.prepare(context.changedAt());
            succeeded.add(shipment);
            outboxes.add(
                    ShipmentOutbox.forNew(
                            orderItemId,
                            ShipmentOutboxType.CONFIRM,
                            ShipmentOutboxPayloadBuilder.confirmPayload(),
                            context.changedAt()));
            results.add(BatchItemResult.success(idStr));
        }

        persistFacade.persistAllWithOutboxes(succeeded, outboxes);
        return BatchProcessingResult.from(results);
    }

    /**
     * 송장등록 일괄 처리.
     *
     * <p>bulk fetch → ship → SHIP 아웃박스 생성 (페이로드 포함) → bulk persist.
     */
    public BatchProcessingResult<String> shipBatch(
            List<UpdateContext<OrderItemId, ShipmentShipData>> contexts,
            Map<String, ShipBatchItem> itemMap) {
        List<OrderItemId> orderItemIds = contexts.stream().map(UpdateContext::id).toList();
        Map<String, Shipment> shipmentMap = fetchShipmentMap(orderItemIds);

        List<BatchItemResult<String>> results = new ArrayList<>();
        List<Shipment> succeeded = new ArrayList<>();
        List<ShipmentOutbox> outboxes = new ArrayList<>();

        for (UpdateContext<OrderItemId, ShipmentShipData> ctx : contexts) {
            String orderItemIdValue = ctx.id().value();
            String idStr = orderItemIdValue;
            Shipment shipment = shipmentMap.get(orderItemIdValue);

            if (shipment == null) {
                results.add(notFoundResult(idStr));
                continue;
            }

            if (!shipment.canShip()) {
                results.add(invalidStatusResult(idStr, shipment));
                continue;
            }

            ShipmentShipData shipData = ctx.updateData();
            shipment.ship(shipData.trackingNumber(), shipData.method(), ctx.changedAt());
            succeeded.add(shipment);

            ShipBatchItem batchItem = itemMap.get(orderItemIdValue);
            String payload =
                    batchItem != null ? ShipmentOutboxPayloadBuilder.shipPayload(batchItem) : "{}";
            outboxes.add(
                    ShipmentOutbox.forNew(
                            ctx.id(), ShipmentOutboxType.SHIP, payload, ctx.changedAt()));
            results.add(BatchItemResult.success(idStr));
        }

        persistFacade.persistAllWithOutboxes(succeeded, outboxes);
        return BatchProcessingResult.from(results);
    }

    private Map<String, Shipment> fetchShipmentMap(List<OrderItemId> orderItemIds) {
        return readManager.findByOrderItemIds(orderItemIds).stream()
                .collect(Collectors.toMap(Shipment::orderItemIdValue, Function.identity()));
    }

    private static BatchItemResult<String> notFoundResult(String id) {
        return BatchItemResult.failure(id, "NOT_FOUND", "배송 정보를 찾을 수 없습니다: " + id);
    }

    private static BatchItemResult<String> invalidStatusResult(String id, Shipment shipment) {
        return BatchItemResult.failure(
                id, "INVALID_STATUS", "처리할 수 없는 상태입니다: " + shipment.status());
    }
}
