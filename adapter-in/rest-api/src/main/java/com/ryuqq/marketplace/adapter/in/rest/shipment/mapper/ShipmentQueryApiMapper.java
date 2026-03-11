package com.ryuqq.marketplace.adapter.in.rest.shipment.mapper;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.request.ShipmentSearchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentDetailApiResponse.PaymentInfoResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentDetailApiResponse.SettlementInfoResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentListApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentListApiResponse.OrderInfoResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentListApiResponse.ProductOrderInfoResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentListApiResponse.ReceiverInfoResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentListApiResponse.ShipmentInfoResponse;
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
                toShipmentInfoResponse(result.shipment()),
                toOrderInfoResponse(result.order()),
                toProductOrderInfoResponse(result.productOrder()),
                toReceiverInfoResponse(result.receiver()));
    }

    public ShipmentDetailApiResponse toDetailResponse(ShipmentDetailResult result) {
        return new ShipmentDetailApiResponse(
                toShipmentInfoResponse(result.shipment()),
                toOrderInfoResponse(result.order()),
                toProductOrderInfoResponse(result.productOrder()),
                toReceiverInfoResponse(result.receiver()),
                toPaymentInfoResponse(result.payment()),
                toSettlementInfoResponse(result.settlement()));
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

    private ShipmentInfoResponse toShipmentInfoResponse(ShipmentListResult.ShipmentInfo info) {
        return new ShipmentInfoResponse(
                info.shipmentId(),
                info.shipmentNumber(),
                info.status(),
                info.trackingNumber(),
                info.courierCode(),
                info.courierName(),
                DateTimeFormatUtils.formatIso8601(info.orderConfirmedAt()),
                DateTimeFormatUtils.formatIso8601(info.shippedAt()),
                DateTimeFormatUtils.formatIso8601(info.deliveredAt()),
                DateTimeFormatUtils.formatIso8601(info.createdAt()));
    }

    private OrderInfoResponse toOrderInfoResponse(ShipmentListResult.OrderInfo info) {
        return new OrderInfoResponse(
                info.orderId(),
                info.orderNumber(),
                info.status(),
                info.salesChannelId(),
                info.shopId(),
                info.shopCode(),
                info.shopName(),
                info.externalOrderNo(),
                DateTimeFormatUtils.formatIso8601(info.externalOrderedAt()),
                info.buyerName(),
                info.buyerEmail(),
                info.buyerPhone(),
                DateTimeFormatUtils.formatIso8601(info.createdAt()),
                DateTimeFormatUtils.formatIso8601(info.updatedAt()));
    }

    private ProductOrderInfoResponse toProductOrderInfoResponse(
            ShipmentListResult.ProductOrderInfo info) {
        return new ProductOrderInfoResponse(
                info.orderItemId(),
                info.productGroupId(),
                info.productId(),
                info.sellerId(),
                info.brandId(),
                info.skuCode(),
                info.productGroupName(),
                info.brandName(),
                info.sellerName(),
                info.mainImageUrl(),
                info.externalProductId(),
                info.externalOptionId(),
                info.externalProductName(),
                info.externalOptionName(),
                info.externalImageUrl(),
                info.unitPrice(),
                info.quantity(),
                info.totalAmount(),
                info.discountAmount(),
                info.paymentAmount());
    }

    private ReceiverInfoResponse toReceiverInfoResponse(ShipmentListResult.ReceiverInfo info) {
        return new ReceiverInfoResponse(
                info.receiverName(),
                info.receiverPhone(),
                info.receiverZipcode(),
                info.receiverAddress(),
                info.receiverAddressDetail(),
                info.deliveryRequest());
    }

    private PaymentInfoResponse toPaymentInfoResponse(ShipmentDetailResult.PaymentInfo info) {
        if (info == null) {
            return null;
        }
        return new PaymentInfoResponse(
                info.paymentId(),
                info.paymentNumber(),
                info.paymentStatus(),
                info.paymentMethod(),
                info.paymentAgencyId(),
                info.paymentAmount(),
                DateTimeFormatUtils.formatIso8601(info.paidAt()),
                DateTimeFormatUtils.formatIso8601(info.canceledAt()));
    }

    private SettlementInfoResponse toSettlementInfoResponse(
            ShipmentDetailResult.SettlementInfo info) {
        if (info == null) {
            return null;
        }
        return new SettlementInfoResponse(
                info.commissionRate(),
                info.fee(),
                info.expectationSettlementAmount(),
                info.settlementAmount(),
                info.shareRatio(),
                DateTimeFormatUtils.formatIso8601(info.expectedSettlementDay()),
                DateTimeFormatUtils.formatIso8601(info.settlementDay()));
    }
}
