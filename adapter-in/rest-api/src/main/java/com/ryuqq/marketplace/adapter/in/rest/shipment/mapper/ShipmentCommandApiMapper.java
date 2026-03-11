package com.ryuqq.marketplace.adapter.in.rest.shipment.mapper;

import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.request.ConfirmShipmentBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.request.ShipBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.request.ShipBatchApiRequest.ShipBatchItemApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.request.ShipSingleApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.BatchResultApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.BatchResultApiResponse.BatchResultItemApiResponse;
import com.ryuqq.marketplace.application.common.dto.result.BatchItemResult;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.shipment.dto.command.ConfirmShipmentBatchCommand;
import com.ryuqq.marketplace.application.shipment.dto.command.ShipBatchCommand;
import com.ryuqq.marketplace.application.shipment.dto.command.ShipBatchCommand.ShipBatchItem;
import com.ryuqq.marketplace.application.shipment.dto.command.ShipSingleCommand;
import java.util.List;
import org.springframework.stereotype.Component;

/** Shipment Command API Mapper. */
@Component
public class ShipmentCommandApiMapper {

    public ConfirmShipmentBatchCommand toConfirmBatchCommand(
            ConfirmShipmentBatchApiRequest request, Long sellerId) {
        return new ConfirmShipmentBatchCommand(request.orderItemIds(), sellerId);
    }

    public ShipBatchCommand toShipBatchCommand(ShipBatchApiRequest request) {
        List<ShipBatchItem> items = request.items().stream().map(this::toShipBatchItem).toList();
        return new ShipBatchCommand(items);
    }

    public ShipSingleCommand toShipSingleCommand(long orderItemId, ShipSingleApiRequest request) {
        return new ShipSingleCommand(
                orderItemId,
                request.trackingNumber(),
                request.courierCode(),
                request.courierName(),
                request.shipmentMethodType());
    }

    public BatchResultApiResponse toBatchResultResponse(BatchProcessingResult<String> result) {
        List<BatchResultItemApiResponse> items =
                result.results().stream().map(this::toBatchResultItem).toList();
        return new BatchResultApiResponse(
                result.totalCount(), result.successCount(), result.failureCount(), items);
    }

    private ShipBatchItem toShipBatchItem(ShipBatchItemApiRequest request) {
        return new ShipBatchItem(
                request.orderItemId(),
                request.trackingNumber(),
                request.courierCode(),
                request.courierName(),
                request.shipmentMethodType());
    }

    private BatchResultItemApiResponse toBatchResultItem(BatchItemResult<String> item) {
        return new BatchResultItemApiResponse(
                item.id(), item.success(), item.errorCode(), item.errorMessage());
    }
}
