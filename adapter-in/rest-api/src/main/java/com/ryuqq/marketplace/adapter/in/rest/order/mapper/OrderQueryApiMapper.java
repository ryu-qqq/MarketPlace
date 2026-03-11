package com.ryuqq.marketplace.adapter.in.rest.order.mapper;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.query.SearchOrdersApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderDetailApiResponse.CancelInfoApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderDetailApiResponse.ClaimInfoApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderDetailApiResponse.SettlementApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderDetailApiResponse.TimeLineApiResponse;
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
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderSummaryApiResponse;
import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.order.dto.query.OrderSearchParams;
import com.ryuqq.marketplace.application.order.dto.response.OrderCancelResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderClaimResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderHistoryResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderSummaryResult;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderDetailResult;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderPageResult;
import java.time.Instant;
import java.util.List;
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

    public OrderSummaryApiResponse toSummaryResponse(OrderSummaryResult result) {
        return new OrderSummaryApiResponse(
                result.ordered(),
                result.preparing(),
                result.shipped(),
                result.delivered(),
                result.confirmed(),
                result.cancelled(),
                result.claimInProgress(),
                result.refunded(),
                result.exchanged());
    }

    // ==================== V5 공통 변환 메서드 ====================

    private OrderInfoApiResponse toOrderInfoApi(ProductOrderListResult.OrderInfo order) {
        if (order == null) {
            return null;
        }
        return new OrderInfoApiResponse(
                order.orderId(),
                order.orderNumber(),
                order.status(),
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
}
