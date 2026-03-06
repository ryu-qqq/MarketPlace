package com.ryuqq.marketplace.adapter.in.rest.shipment.mapper;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.request.ShipmentSearchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentDetailApiResponse.ShipmentMethodApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentListApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentSummaryApiResponse;
import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.shipment.dto.query.ShipmentSearchParams;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentDetailResult;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentListResult;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentPageResult;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentSummaryResult;
import java.util.List;
import org.springframework.stereotype.Component;

/** Shipment Query API Mapper. */
@Component
public class ShipmentQueryApiMapper {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;

    public ShipmentSearchParams toSearchParams(ShipmentSearchApiRequest request) {
        int page = request.page() != null ? request.page() : DEFAULT_PAGE;
        int size = request.size() != null ? request.size() : DEFAULT_SIZE;

        CommonSearchParams searchParams =
                CommonSearchParams.of(
                        null,
                        request.startDate(),
                        request.endDate(),
                        request.sortKey(),
                        request.sortDirection(),
                        page,
                        size);

        return new ShipmentSearchParams(
                request.statuses(),
                request.sellerIds(),
                request.shopOrderNos(),
                request.searchField(),
                request.searchWord(),
                request.dateField(),
                searchParams);
    }

    public ShipmentListApiResponse toResponse(ShipmentListResult result) {
        return new ShipmentListApiResponse(
                result.shipmentId(),
                result.shipmentNumber(),
                result.orderId(),
                result.orderNumber(),
                result.status(),
                result.trackingNumber(),
                result.courierName(),
                DateTimeFormatUtils.formatIso8601(result.shippedAt()),
                DateTimeFormatUtils.formatIso8601(result.deliveredAt()),
                DateTimeFormatUtils.formatIso8601(result.createdAt()));
    }

    public ShipmentDetailApiResponse toDetailResponse(ShipmentDetailResult result) {
        ShipmentMethodApiResponse methodResponse = null;
        if (result.shipmentMethod() != null) {
            methodResponse =
                    new ShipmentMethodApiResponse(
                            result.shipmentMethod().type(),
                            result.shipmentMethod().courierCode(),
                            result.shipmentMethod().courierName());
        }

        return new ShipmentDetailApiResponse(
                result.shipmentId(),
                result.shipmentNumber(),
                result.orderId(),
                result.orderNumber(),
                result.status(),
                methodResponse,
                result.trackingNumber(),
                DateTimeFormatUtils.formatIso8601(result.orderConfirmedAt()),
                DateTimeFormatUtils.formatIso8601(result.shippedAt()),
                DateTimeFormatUtils.formatIso8601(result.deliveredAt()),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }

    public ShipmentSummaryApiResponse toSummaryResponse(ShipmentSummaryResult result) {
        return new ShipmentSummaryApiResponse(
                result.ready(),
                result.preparing(),
                result.shipped(),
                result.inTransit(),
                result.delivered(),
                result.failed(),
                result.cancelled());
    }

    public PageApiResponse<ShipmentListApiResponse> toPageResponse(ShipmentPageResult pageResult) {
        List<ShipmentListApiResponse> responses =
                pageResult.results().stream().map(this::toResponse).toList();
        return PageApiResponse.of(
                responses,
                pageResult.pageMeta().page(),
                pageResult.pageMeta().size(),
                pageResult.pageMeta().totalElements());
    }
}
