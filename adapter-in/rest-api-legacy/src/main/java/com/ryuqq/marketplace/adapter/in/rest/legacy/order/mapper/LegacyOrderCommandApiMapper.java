package com.ryuqq.marketplace.adapter.in.rest.legacy.order.mapper;

import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.request.LegacyUpdateOrderRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyUpdateOrderResponse;
import com.ryuqq.marketplace.application.legacy.order.dto.command.LegacyOrderUpdateCommand;
import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderUpdateResult;
import org.springframework.stereotype.Component;

/**
 * 레거시 주문 커맨드 API Mapper.
 *
 * <p>UpdateOrderRequest → UpdateCommand, UpdateResult → UpdateOrderResponse 변환.
 */
@Component
public class LegacyOrderCommandApiMapper {

    public LegacyOrderUpdateCommand toCommand(LegacyUpdateOrderRequest request) {
        return new LegacyOrderUpdateCommand(
                request.type(),
                request.orderId(),
                request.orderStatus(),
                request.byPass(),
                request.changeReason(),
                request.changeDetailReason(),
                request.shipmentInfo() != null ? request.shipmentInfo().invoiceNo() : null,
                request.shipmentInfo() != null ? request.shipmentInfo().companyCode() : null,
                request.shipmentInfo() != null ? request.shipmentInfo().shipmentType() : null);
    }

    public LegacyUpdateOrderResponse toResponse(LegacyOrderUpdateResult result) {
        return new LegacyUpdateOrderResponse(
                result.orderId(),
                result.userId(),
                result.toBeOrderStatus(),
                result.asIsOrderStatus(),
                result.changeReason(),
                result.changeDetailReason());
    }
}
