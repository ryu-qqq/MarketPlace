package com.ryuqq.marketplace.adapter.in.rest.order.mapper;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.query.SearchOrdersApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderDetailApiResponse.CancelInfoApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderDetailApiResponse.ClaimInfoApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderDetailApiResponse.SettlementApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderDetailApiResponse.TimeLineApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderDetailApiResponseV4;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderListApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderListApiResponse.CancelSummaryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderListApiResponse.CancelSummaryApiResponse.LatestCancelApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderListApiResponse.ClaimSummaryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderListApiResponse.ClaimSummaryApiResponse.LatestClaimApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderListApiResponse.DeliveryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderListApiResponse.OrderInfoApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderListApiResponse.PaymentInfoApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderListApiResponse.ProductOrderApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderListApiResponse.ReceiverApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderListApiResponseV4;
import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.order.dto.query.OrderSearchParams;
import com.ryuqq.marketplace.application.order.dto.response.OrderCancelResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderClaimResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderHistoryResult;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderDetailResult;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderPageResult;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/** Order Query API Mapper. */
@Component
public class OrderQueryApiMapper {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;

    public OrderSearchParams toSearchParams(SearchOrdersApiRequest request) {
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

        return new OrderSearchParams(
                request.status(),
                request.searchField(),
                request.searchWord(),
                request.dateField(),
                searchParams);
    }

    // ==================== V5 상품주문 리스트 ====================

    public OrderListApiResponse toListResponse(ProductOrderListResult result) {
        return new OrderListApiResponse(
                toOrderInfoApi(result.order()),
                toProductOrderApi(result.productOrder()),
                toPaymentInfoApi(result.payment()),
                toReceiverApi(result.receiver()),
                toDeliveryApi(result.delivery()),
                toCancelSummaryApi(result.cancel()),
                toClaimSummaryApi(result.claim()));
    }

    public PageApiResponse<OrderListApiResponse> toPageResponse(ProductOrderPageResult pageResult) {
        List<OrderListApiResponse> responses =
                pageResult.productOrders().stream().map(this::toListResponse).toList();
        return PageApiResponse.of(
                responses,
                pageResult.pageMeta().page(),
                pageResult.pageMeta().size(),
                pageResult.pageMeta().totalElements());
    }

    // ==================== V4 호환 리스트 (legacyOrderId, settlementInfo 제외, 기본값 0/"")
    // ====================

    public OrderListApiResponseV4 toListResponseV4(ProductOrderListResult result) {
        return new OrderListApiResponseV4(
                nullToEmpty(result.productOrder() != null ? result.productOrder().orderItemId() : null),
                nullToEmpty(result.productOrder() != null ? result.productOrder().orderItemNumber() : null),
                toBuyerInfoV4(result.order()),
                toPaymentDetailV4(result.payment(), result.order()),
                toReceiverInfoV4(result.receiver()),
                toPaymentShipmentInfoV4(result.delivery()),
                toOrderProductV4(result.order(), result.productOrder()),
                toExternalOrderInfoV4(result.order()),
                toCancelSummaryV4(result.cancel()),
                toClaimSummaryV4(result.claim()));
    }

    public PageApiResponse<OrderListApiResponseV4> toPageResponseV4(
            ProductOrderPageResult pageResult) {
        List<OrderListApiResponseV4> responses =
                pageResult.productOrders().stream().map(this::toListResponseV4).toList();
        return PageApiResponse.of(
                responses,
                pageResult.pageMeta().page(),
                pageResult.pageMeta().size(),
                pageResult.pageMeta().totalElements());
    }

    // ==================== V5 상품주문 상세 ====================

    public OrderDetailApiResponse toDetailResponse(ProductOrderDetailResult result) {
        return new OrderDetailApiResponse(
                toOrderInfoApi(result.order()),
                toProductOrderApi(result.productOrder()),
                toPaymentInfoApi(result.payment()),
                toReceiverApi(result.receiver()),
                toDeliveryApi(result.delivery()),
                toCancelSummaryApi(result.cancel()),
                toClaimSummaryApi(result.claim()),
                toSettlementApi(result.settlement()),
                result.cancels().stream().map(this::toCancelInfoApi).toList(),
                result.claims().stream().map(this::toClaimInfoApi).toList(),
                result.timeLine().stream().map(this::toTimeLineApi).toList());
    }

    // ==================== V4 호환 상세 (legacyOrderId 제외, 기본값 0/"") ====================

    public OrderDetailApiResponseV4 toDetailResponseV4(ProductOrderDetailResult result) {
        return new OrderDetailApiResponseV4(
                nullToEmpty(result.productOrder() != null ? result.productOrder().orderItemId() : null),
                nullToEmpty(result.productOrder() != null ? result.productOrder().orderItemNumber() : null),
                toBuyerInfoV4(result.order()),
                toPaymentDetailV4(result.payment(), result.order()),
                toReceiverInfoV4(result.receiver()),
                toPaymentShipmentInfoV4(result.delivery()),
                toSettlementInfoV4(result.settlement()),
                toOrderProductV4(result.order(), result.productOrder()),
                toExternalOrderInfoV4(result.order()),
                toCancelSummaryV4(result.cancel()),
                toClaimSummaryV4(result.claim()),
                toOrderHistoriesV4(result.order(), result.timeLine()),
                result.cancels().stream()
                        .map(c -> String.valueOf(c.cancelId()))
                        .collect(Collectors.toList()),
                result.cancels().stream()
                        .sorted(
                                Comparator.comparing(
                                        OrderCancelResult::requestedAt,
                                        Comparator.nullsFirst(Comparator.reverseOrder())))
                        .limit(3)
                        .map(this::toCancelItemV4)
                        .collect(Collectors.toList()),
                result.claims().stream()
                        .map(c -> String.valueOf(c.claimId()))
                        .collect(Collectors.toList()),
                result.claims().stream()
                        .sorted(
                                Comparator.comparing(
                                        OrderClaimResult::requestedAt,
                                        Comparator.nullsFirst(Comparator.reverseOrder())))
                        .limit(3)
                        .map(this::toClaimItemV4)
                        .collect(Collectors.toList()));
    }

    // ==================== V5 공통 변환 메서드 ====================

    private OrderInfoApiResponse toOrderInfoApi(ProductOrderListResult.OrderInfo order) {
        if (order == null) {
            return null;
        }
        return new OrderInfoApiResponse(
                order.orderId(),
                order.orderNumber(),
                null,
                order.salesChannelId(),
                order.shopId(),
                order.shopCode(),
                order.shopName(),
                order.externalOrderNo(),
                formatIso(order.externalOrderedAt()),
                order.buyerName(),
                order.buyerEmail(),
                order.buyerPhone(),
                formatIso(order.createdAt()),
                formatIso(order.updatedAt()));
    }

    private ProductOrderApiResponse toProductOrderApi(
            ProductOrderListResult.ProductOrderInfo productOrder) {
        if (productOrder == null) {
            return null;
        }
        return new ProductOrderApiResponse(
                productOrder.orderItemId(),
                productOrder.orderItemNumber(),
                productOrder.productGroupId(),
                productOrder.productId(),
                productOrder.sellerId(),
                productOrder.brandId(),
                productOrder.skuCode(),
                productOrder.productGroupName(),
                productOrder.brandName(),
                productOrder.sellerName(),
                productOrder.mainImageUrl(),
                productOrder.externalProductId(),
                productOrder.externalOptionId(),
                productOrder.externalProductName(),
                productOrder.externalOptionName(),
                productOrder.externalImageUrl(),
                productOrder.unitPrice(),
                productOrder.quantity(),
                productOrder.totalAmount(),
                productOrder.discountAmount(),
                productOrder.paymentAmount());
    }

    private PaymentInfoApiResponse toPaymentInfoApi(ProductOrderListResult.PaymentInfo payment) {
        if (payment == null) {
            return null;
        }
        return new PaymentInfoApiResponse(
                payment.paymentId(),
                payment.paymentNumber(),
                payment.paymentStatus(),
                payment.paymentMethod(),
                payment.paymentAgencyId(),
                payment.paymentAmount(),
                formatIso(payment.paidAt()),
                formatIso(payment.canceledAt()));
    }

    private ReceiverApiResponse toReceiverApi(ProductOrderListResult.ReceiverInfo receiver) {
        if (receiver == null) {
            return null;
        }
        return new ReceiverApiResponse(
                receiver.receiverName(),
                receiver.receiverPhone(),
                receiver.receiverZipcode(),
                receiver.receiverAddress(),
                receiver.receiverAddressDetail(),
                receiver.deliveryRequest());
    }

    private DeliveryApiResponse toDeliveryApi(ProductOrderListResult.DeliveryInfo delivery) {
        if (delivery == null) {
            return null;
        }
        return new DeliveryApiResponse(
                delivery.deliveryStatus(),
                delivery.shipmentCompanyCode(),
                delivery.invoice(),
                formatIso(delivery.shipmentCompletedDate()));
    }

    private CancelSummaryApiResponse toCancelSummaryApi(
            ProductOrderListResult.CancelSummary cancel) {
        if (cancel == null) {
            return null;
        }
        LatestCancelApiResponse latestApi = null;
        if (cancel.latest() != null) {
            latestApi =
                    new LatestCancelApiResponse(
                            cancel.latest().cancelId(),
                            cancel.latest().cancelNumber(),
                            cancel.latest().status(),
                            cancel.latest().qty(),
                            formatIso(cancel.latest().requestedAt()));
        }
        return new CancelSummaryApiResponse(
                cancel.hasActiveCancel(),
                cancel.totalCancelledQty(),
                cancel.cancelableQty(),
                latestApi);
    }

    private ClaimSummaryApiResponse toClaimSummaryApi(ProductOrderListResult.ClaimSummary claim) {
        if (claim == null) {
            return null;
        }
        LatestClaimApiResponse latestApi = null;
        if (claim.latest() != null) {
            latestApi =
                    new LatestClaimApiResponse(
                            claim.latest().claimId(),
                            claim.latest().claimNumber(),
                            claim.latest().type(),
                            claim.latest().status(),
                            claim.latest().qty(),
                            formatIso(claim.latest().requestedAt()));
        }
        return new ClaimSummaryApiResponse(
                claim.hasActiveClaim(),
                claim.activeCount(),
                claim.totalClaimedQty(),
                claim.claimableQty(),
                latestApi);
    }

    private SettlementApiResponse toSettlementApi(
            ProductOrderDetailResult.SettlementInfo settlement) {
        if (settlement == null) {
            return null;
        }
        return new SettlementApiResponse(
                settlement.commissionRate(),
                settlement.fee(),
                settlement.expectationSettlementAmount(),
                settlement.settlementAmount(),
                settlement.shareRatio(),
                formatIso(settlement.expectedSettlementDay()),
                formatIso(settlement.settlementDay()));
    }

    private CancelInfoApiResponse toCancelInfoApi(OrderCancelResult cancel) {
        return new CancelInfoApiResponse(
                String.valueOf(cancel.cancelId()),
                cancel.orderItemId(),
                cancel.cancelNumber(),
                cancel.cancelStatus(),
                cancel.quantity(),
                cancel.reasonType(),
                cancel.reasonDetail(),
                cancel.originalAmount(),
                cancel.refundAmount(),
                cancel.refundMethod(),
                formatIso(cancel.refundedAt()),
                formatIso(cancel.requestedAt()),
                formatIso(cancel.completedAt()));
    }

    private ClaimInfoApiResponse toClaimInfoApi(OrderClaimResult claim) {
        return new ClaimInfoApiResponse(
                String.valueOf(claim.claimId()),
                claim.orderItemId(),
                claim.claimNumber(),
                claim.claimType(),
                claim.claimStatus(),
                claim.quantity(),
                claim.reasonType(),
                claim.reasonDetail(),
                claim.collectMethod(),
                claim.originalAmount(),
                claim.deductionAmount(),
                claim.deductionReason(),
                claim.refundAmount(),
                claim.refundMethod(),
                formatIso(claim.refundedAt()),
                formatIso(claim.requestedAt()),
                formatIso(claim.completedAt()),
                formatIso(claim.rejectedAt()));
    }

    private TimeLineApiResponse toTimeLineApi(OrderHistoryResult history) {
        return new TimeLineApiResponse(
                history.historyId(),
                history.fromStatus(),
                history.toStatus(),
                history.changedBy(),
                history.reason(),
                formatIso(history.changedAt()));
    }

    private String formatIso(Instant instant) {
        return DateTimeFormatUtils.formatIso8601(instant);
    }

    private String formatYyyyMmDdHhMmSs(Instant instant) {
        if (instant == null) {
            return "";
        }
        return DateTimeFormatUtils.formatIso8601(instant);
    }

    private String nullToEmpty(String value) {
        return value != null ? value : "";
    }

    // ==================== V4 변환 (기본값: 숫자 0, 문자열 "") ====================

    private OrderListApiResponseV4.BuyerInfoApiResponse toBuyerInfoV4(
            ProductOrderListResult.OrderInfo order) {
        if (order == null) {
            return new OrderListApiResponseV4.BuyerInfoApiResponse("", "", "");
        }
        return new OrderListApiResponseV4.BuyerInfoApiResponse(
                nullToEmpty(order.buyerName()),
                nullToEmpty(order.buyerEmail()),
                nullToEmpty(order.buyerPhone()));
    }

    private OrderListApiResponseV4.PaymentDetailApiResponse toPaymentDetailV4(
            ProductOrderListResult.PaymentInfo payment, ProductOrderListResult.OrderInfo order) {
        if (payment == null) {
            return new OrderListApiResponseV4.PaymentDetailApiResponse(
                    "", "", "", "", "", "", "", 0L, "", 0, 0, 0);
        }
        return new OrderListApiResponseV4.PaymentDetailApiResponse(
                nullToEmpty(payment.paymentId()),
                nullToEmpty(payment.paymentNumber()),
                nullToEmpty(payment.paymentAgencyId()),
                nullToEmpty(payment.paymentStatus()),
                nullToEmpty(payment.paymentMethod()),
                formatYyyyMmDdHhMmSs(payment.paidAt()),
                formatYyyyMmDdHhMmSs(payment.canceledAt()),
                0L,
                order != null ? nullToEmpty(order.shopCode()) : "",
                payment.paymentAmount(),
                payment.paymentAmount(),
                0);
    }

    private OrderListApiResponseV4.ReceiverInfoApiResponse toReceiverInfoV4(
            ProductOrderListResult.ReceiverInfo receiver) {
        if (receiver == null) {
            return new OrderListApiResponseV4.ReceiverInfoApiResponse("", "", "", "", "", "");
        }
        return new OrderListApiResponseV4.ReceiverInfoApiResponse(
                nullToEmpty(receiver.receiverName()),
                nullToEmpty(receiver.receiverPhone()),
                nullToEmpty(receiver.receiverAddress()),
                nullToEmpty(receiver.receiverAddressDetail()),
                nullToEmpty(receiver.receiverZipcode()),
                nullToEmpty(receiver.deliveryRequest()));
    }

    private OrderListApiResponseV4.PaymentShipmentInfoApiResponse toPaymentShipmentInfoV4(
            ProductOrderListResult.DeliveryInfo delivery) {
        if (delivery == null) {
            return new OrderListApiResponseV4.PaymentShipmentInfoApiResponse("", "", "", "");
        }
        return new OrderListApiResponseV4.PaymentShipmentInfoApiResponse(
                nullToEmpty(delivery.deliveryStatus()),
                nullToEmpty(delivery.shipmentCompanyCode()),
                nullToEmpty(delivery.invoice()),
                formatYyyyMmDdHhMmSs(delivery.shipmentCompletedDate()));
    }

    private OrderListApiResponseV4.OrderProductApiResponse toOrderProductV4(
            ProductOrderListResult.OrderInfo order,
            ProductOrderListResult.ProductOrderInfo productOrder) {
        if (productOrder == null) {
            return new OrderListApiResponseV4.OrderProductApiResponse(
                    "",
                    "",
                    new OrderListApiResponseV4.PriceApiResponse(0, 0, 0, 0, 0, 0),
                    new OrderListApiResponseV4.BrandApiResponse(0L, ""),
                    0L,
                    0L,
                    "",
                    "",
                    "",
                    0,
                    "",
                    0,
                    0,
                    0,
                    "",
                    "",
                    List.of());
        }
        int unitPrice = productOrder.unitPrice();
        int discountRate = unitPrice > 0 ? (productOrder.discountAmount() * 100 / unitPrice) : 0;
        return new OrderListApiResponseV4.OrderProductApiResponse(
                order != null ? nullToEmpty(order.orderId()) : "",
                nullToEmpty(productOrder.productGroupName()),
                new OrderListApiResponseV4.PriceApiResponse(
                        unitPrice,
                        unitPrice,
                        unitPrice,
                        productOrder.discountAmount(),
                        discountRate,
                        discountRate),
                new OrderListApiResponseV4.BrandApiResponse(
                        productOrder.brandId(), nullToEmpty(productOrder.brandName())),
                productOrder.productGroupId(),
                productOrder.productId(),
                nullToEmpty(productOrder.sellerName()),
                nullToEmpty(productOrder.mainImageUrl()),
                "",
                productOrder.quantity(),
                "",
                unitPrice,
                productOrder.totalAmount(),
                0,
                nullToEmpty(productOrder.externalOptionName()),
                nullToEmpty(productOrder.skuCode()),
                List.of());
    }

    private OrderListApiResponseV4.ExternalOrderInfoApiResponse toExternalOrderInfoV4(
            ProductOrderListResult.OrderInfo order) {
        if (order == null
                || (order.externalOrderNo() == null || order.externalOrderNo().isBlank())) {
            return null;
        }
        return new OrderListApiResponseV4.ExternalOrderInfoApiResponse(
                order.shopId(),
                nullToEmpty(order.shopCode()),
                nullToEmpty(order.externalOrderNo()),
                "",
                formatYyyyMmDdHhMmSs(order.externalOrderedAt()));
    }

    private OrderListApiResponseV4.CancelSummaryV4ApiResponse toCancelSummaryV4(
            ProductOrderListResult.CancelSummary cancel) {
        if (cancel == null) {
            return null;
        }
        OrderListApiResponseV4.CancelSummaryV4ApiResponse.LatestCancelV4ApiResponse latest = null;
        if (cancel.latest() != null) {
            latest =
                    new OrderListApiResponseV4.CancelSummaryV4ApiResponse.LatestCancelV4ApiResponse(
                            nullToEmpty(cancel.latest().cancelId()),
                            nullToEmpty(cancel.latest().cancelNumber()),
                            "",
                            nullToEmpty(cancel.latest().status()),
                            cancel.latest().qty(),
                            formatYyyyMmDdHhMmSs(cancel.latest().requestedAt()));
        }
        return new OrderListApiResponseV4.CancelSummaryV4ApiResponse(
                cancel.hasActiveCancel(),
                cancel.totalCancelledQty(),
                cancel.cancelableQty(),
                latest);
    }

    private OrderListApiResponseV4.ClaimSummaryV4ApiResponse toClaimSummaryV4(
            ProductOrderListResult.ClaimSummary claim) {
        if (claim == null) {
            return null;
        }
        OrderListApiResponseV4.ClaimSummaryV4ApiResponse.LatestClaimV4ApiResponse latest = null;
        if (claim.latest() != null) {
            latest =
                    new OrderListApiResponseV4.ClaimSummaryV4ApiResponse.LatestClaimV4ApiResponse(
                            nullToEmpty(claim.latest().claimId()),
                            nullToEmpty(claim.latest().claimNumber()),
                            nullToEmpty(claim.latest().type()),
                            nullToEmpty(claim.latest().status()),
                            claim.latest().qty(),
                            formatYyyyMmDdHhMmSs(claim.latest().requestedAt()));
        }
        return new OrderListApiResponseV4.ClaimSummaryV4ApiResponse(
                claim.hasActiveClaim(),
                claim.activeCount(),
                claim.totalClaimedQty(),
                claim.claimableQty(),
                latest);
    }

    // ==================== V4 상세 전용 변환 ====================

    private OrderDetailApiResponseV4.SettlementInfoApiResponse toSettlementInfoV4(
            ProductOrderDetailResult.SettlementInfo settlement) {
        if (settlement == null) {
            return new OrderDetailApiResponseV4.SettlementInfoApiResponse(0, 0, 0, 0, 0, "", "");
        }
        return new OrderDetailApiResponseV4.SettlementInfoApiResponse(
                settlement.commissionRate(),
                settlement.fee(),
                settlement.expectationSettlementAmount(),
                settlement.settlementAmount(),
                settlement.shareRatio(),
                formatYyyyMmDdHhMmSs(settlement.expectedSettlementDay()),
                formatYyyyMmDdHhMmSs(settlement.settlementDay()));
    }

    private List<OrderDetailApiResponseV4.OrderHistoryItemApiResponse> toOrderHistoriesV4(
            ProductOrderListResult.OrderInfo order, List<OrderHistoryResult> timeLine) {
        if (timeLine == null) {
            return List.of();
        }
        String orderId = order != null ? nullToEmpty(order.orderId()) : "";
        return timeLine.stream()
                .map(
                        h ->
                                new OrderDetailApiResponseV4.OrderHistoryItemApiResponse(
                                        orderId,
                                        nullToEmpty(h.reason()),
                                        "",
                                        nullToEmpty(h.toStatus()),
                                        formatYyyyMmDdHhMmSs(h.changedAt()),
                                        formatYyyyMmDdHhMmSs(h.changedAt())))
                .collect(Collectors.toList());
    }

    private OrderDetailApiResponseV4.CancelItemApiResponse toCancelItemV4(
            OrderCancelResult cancel) {
        OrderDetailApiResponseV4.CancelItemApiResponse.ReasonApiResponse reason =
                new OrderDetailApiResponseV4.CancelItemApiResponse.ReasonApiResponse(
                        nullToEmpty(cancel.reasonType()), nullToEmpty(cancel.reasonDetail()));

        OrderDetailApiResponseV4.CancelItemApiResponse.CancelRefundInfoApiResponse refundInfo =
                new OrderDetailApiResponseV4.CancelItemApiResponse.CancelRefundInfoApiResponse(
                        cancel.originalAmount(),
                        cancel.refundAmount(),
                        nullToEmpty(cancel.refundMethod()),
                        formatYyyyMmDdHhMmSs(cancel.refundedAt()));

        return new OrderDetailApiResponseV4.CancelItemApiResponse(
                String.valueOf(cancel.cancelId()),
                nullToEmpty(cancel.cancelNumber()),
                "",
                nullToEmpty(cancel.cancelStatus()),
                cancel.quantity(),
                reason,
                refundInfo,
                formatYyyyMmDdHhMmSs(cancel.requestedAt()),
                formatYyyyMmDdHhMmSs(cancel.completedAt()),
                formatYyyyMmDdHhMmSs(cancel.requestedAt()));
    }

    private OrderDetailApiResponseV4.ClaimItemApiResponse toClaimItemV4(OrderClaimResult claim) {
        OrderDetailApiResponseV4.ClaimItemApiResponse.ClaimReasonApiResponse reason =
                new OrderDetailApiResponseV4.ClaimItemApiResponse.ClaimReasonApiResponse(
                        nullToEmpty(claim.reasonType()), nullToEmpty(claim.reasonDetail()));

        OrderDetailApiResponseV4.ClaimItemApiResponse.ClaimRefundInfoApiResponse refundInfo =
                new OrderDetailApiResponseV4.ClaimItemApiResponse.ClaimRefundInfoApiResponse(
                        claim.originalAmount(),
                        claim.deductionAmount(),
                        nullToEmpty(claim.deductionReason()),
                        claim.refundAmount(),
                        nullToEmpty(claim.refundMethod()),
                        formatYyyyMmDdHhMmSs(claim.refundedAt()));

        return new OrderDetailApiResponseV4.ClaimItemApiResponse(
                String.valueOf(claim.claimId()),
                nullToEmpty(claim.claimNumber()),
                nullToEmpty(claim.claimType()),
                nullToEmpty(claim.claimStatus()),
                claim.quantity(),
                reason,
                nullToEmpty(claim.collectMethod()),
                refundInfo,
                null,
                formatYyyyMmDdHhMmSs(claim.requestedAt()),
                formatYyyyMmDdHhMmSs(claim.completedAt()),
                formatYyyyMmDdHhMmSs(claim.rejectedAt()),
                formatYyyyMmDdHhMmSs(claim.requestedAt()));
    }
}
