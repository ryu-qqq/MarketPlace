package com.ryuqq.marketplace.adapter.in.rest.shipment.mapper;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderListApiResponseV4;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.request.ShipmentSearchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentDetailApiResponse.PaymentInfoResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentListApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentListApiResponse.OrderInfoResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentListApiResponse.ProductOrderInfoResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentListApiResponse.ReceiverInfoResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentListApiResponse.ShipmentInfoResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentListApiResponseV4;
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
                toPaymentInfoResponse(result.payment()));
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

    public PageApiResponse<ShipmentListApiResponseV4> toPageResponseV4(
            ShipmentPageResult pageResult) {
        List<ShipmentListApiResponseV4> responses =
                pageResult.results().stream().map(this::toResponseV4).toList();
        return PageApiResponse.of(
                responses,
                pageResult.pageMeta().page(),
                pageResult.pageMeta().size(),
                pageResult.pageMeta().totalElements());
    }

    private ShipmentListApiResponseV4 toResponseV4(ShipmentListResult result) {
        ShipmentListResult.ShipmentInfo s = result.shipment();
        ShipmentListResult.OrderInfo o = result.order();
        ShipmentListResult.ProductOrderInfo p = result.productOrder();
        ShipmentListResult.ReceiverInfo r = result.receiver();

        return new ShipmentListApiResponseV4(
                p != null ? nullToEmpty(p.orderItemId()) : "",
                p != null ? nullToEmpty(p.orderItemNumber()) : "",
                s != null ? nullToEmpty(s.shipmentNumber()) : "",
                s != null ? nullToEmpty(s.status()) : "",
                s != null ? nullToEmpty(s.trackingNumber()) : "",
                s != null ? nullToEmpty(s.courierCode()) : "",
                s != null ? DateTimeFormatUtils.formatIso8601(s.orderConfirmedAt()) : "",
                s != null ? DateTimeFormatUtils.formatIso8601(s.shippedAt()) : "",
                s != null ? DateTimeFormatUtils.formatIso8601(s.deliveredAt()) : "",
                s != null ? DateTimeFormatUtils.formatIso8601(s.createdAt()) : "",
                new ShipmentListApiResponseV4.ShipmentMethodV4(
                        "COURIER", s != null ? nullToEmpty(s.courierName()) : ""),
                toOrderProductApiResponse(p),
                new ShipmentListApiResponseV4.ReceiverInfoV4(
                        r != null ? nullToEmpty(r.receiverName()) : "",
                        r != null ? nullToEmpty(r.receiverPhone()) : "",
                        r != null ? nullToEmpty(r.receiverAddress()) : "",
                        r != null ? nullToEmpty(r.receiverAddressDetail()) : "",
                        r != null ? nullToEmpty(r.receiverZipcode()) : ""),
                new ShipmentListApiResponseV4.ExternalOrderInfoV4(
                        o != null ? nullToEmpty(o.shopCode()) : "",
                        o != null ? nullToEmpty(o.externalOrderNo()) : ""),
                new ShipmentListApiResponseV4.CancelInfoV4(""));
    }

    private OrderListApiResponseV4.OrderProductApiResponse toOrderProductApiResponse(
            ShipmentListResult.ProductOrderInfo p) {
        if (p == null) {
            return new OrderListApiResponseV4.OrderProductApiResponse(
                    "", "", null, null, 0, 0, "", "", "", 0, "", 0, 0, 0, "", "", List.of());
        }
        return new OrderListApiResponseV4.OrderProductApiResponse(
                nullToEmpty(p.orderItemId()),
                nullToEmpty(p.productGroupName()),
                new OrderListApiResponseV4.PriceApiResponse(
                        p.unitPrice(), p.unitPrice(), p.unitPrice(), p.discountAmount(), 0, 0),
                new OrderListApiResponseV4.BrandApiResponse(0, nullToEmpty(p.brandName())),
                p.productGroupId(),
                p.productId(),
                nullToEmpty(p.sellerName()),
                nullToEmpty(p.mainImageUrl()),
                "",
                p.quantity(),
                "",
                p.unitPrice(),
                p.paymentAmount(),
                0,
                nullToEmpty(p.externalOptionName()),
                nullToEmpty(p.skuCode()),
                List.of());
    }

    private String nullToEmpty(String value) {
        return value != null ? value : "";
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
}
