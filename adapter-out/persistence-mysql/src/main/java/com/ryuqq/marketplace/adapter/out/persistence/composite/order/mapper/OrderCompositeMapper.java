package com.ryuqq.marketplace.adapter.out.persistence.composite.order.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.composite.order.dto.OrderCancelProjectionDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.order.dto.OrderClaimProjectionDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.order.dto.OrderDetailCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.order.dto.OrderHistoryProjectionDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.order.dto.OrderItemProjectionDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.order.dto.OrderListProjectionDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.order.dto.PaymentProjectionDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.order.dto.ProductOrderDetailProjectionDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.order.dto.ProductOrderListProjectionDto;
import com.ryuqq.marketplace.application.order.dto.composite.ProductOrderDetailData;
import com.ryuqq.marketplace.application.order.dto.response.OrderCancelResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderClaimResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderDetailResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderHistoryResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderItemResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderListResult;
import com.ryuqq.marketplace.application.order.dto.response.PaymentResult;
import org.springframework.stereotype.Component;

/** Order Composite DTO → Application Result 변환 Mapper. */
@Component
public class OrderCompositeMapper {

    public OrderListResult toListResult(OrderListProjectionDto dto) {
        return new OrderListResult(
                dto.orderId(),
                dto.orderNumber(),
                dto.status(),
                dto.salesChannelId(),
                dto.shopId(),
                dto.shopCode(),
                dto.shopName(),
                dto.externalOrderNo(),
                dto.externalOrderedAt(),
                dto.buyerName(),
                dto.buyerEmail(),
                dto.buyerPhone(),
                dto.paymentId(),
                dto.paymentNumber(),
                dto.paymentStatus(),
                dto.paymentMethod(),
                dto.paymentAmount(),
                dto.paidAt(),
                dto.itemCount(),
                dto.createdAt(),
                dto.updatedAt());
    }

    public OrderDetailResult toDetailResult(OrderDetailCompositeDto composite) {
        OrderListProjectionDto order = composite.order();
        return new OrderDetailResult(
                order.orderId(),
                order.orderNumber(),
                order.status(),
                order.salesChannelId(),
                order.shopId(),
                order.shopCode(),
                order.shopName(),
                order.externalOrderNo(),
                order.externalOrderedAt(),
                new OrderDetailResult.BuyerInfoResult(
                        order.buyerName(), order.buyerEmail(), order.buyerPhone()),
                toPaymentResult(composite),
                composite.items().stream().map(this::toItemResult).toList(),
                composite.histories().stream().map(this::toHistoryResult).toList(),
                composite.cancels().stream().map(this::toCancelResult).toList(),
                composite.claims().stream().map(this::toClaimResult).toList(),
                order.createdAt(),
                order.updatedAt());
    }

    private PaymentResult toPaymentResult(OrderDetailCompositeDto composite) {
        OrderListProjectionDto order = composite.order();
        if (order.paymentStatus() == null) {
            return null;
        }
        return new PaymentResult(
                order.paymentId(),
                order.paymentNumber(),
                order.paymentStatus(),
                order.paymentMethod(),
                null,
                order.paymentAmount(),
                order.paidAt(),
                null);
    }

    public PaymentResult toPaymentResult(PaymentProjectionDto dto) {
        if (dto == null) {
            return null;
        }
        return new PaymentResult(
                dto.paymentId(),
                dto.paymentNumber(),
                dto.paymentStatus(),
                dto.paymentMethod(),
                dto.paymentAgencyId(),
                dto.paymentAmount(),
                dto.paidAt(),
                dto.canceledAt());
    }

    public OrderItemResult toItemResult(OrderItemProjectionDto dto) {
        return new OrderItemResult(
                dto.orderItemId() != null ? dto.orderItemId() : 0L,
                dto.orderId(),
                dto.productGroupId(),
                dto.productId(),
                dto.sellerId(),
                dto.brandId(),
                dto.skuCode(),
                dto.productGroupName(),
                dto.brandName(),
                dto.sellerName(),
                dto.mainImageUrl(),
                dto.externalProductId(),
                dto.externalOptionId(),
                dto.externalProductName(),
                dto.externalOptionName(),
                dto.externalImageUrl(),
                dto.unitPrice(),
                dto.quantity(),
                dto.totalAmount(),
                dto.discountAmount(),
                dto.paymentAmount(),
                dto.receiverName(),
                dto.receiverPhone(),
                dto.receiverZipcode(),
                dto.receiverAddress(),
                dto.receiverAddressDetail(),
                dto.deliveryRequest(),
                dto.deliveryStatus(),
                dto.shipmentCompanyCode(),
                dto.invoice(),
                dto.shipmentCompletedDate(),
                dto.commissionRate(),
                dto.fee(),
                dto.expectationSettlementAmount(),
                dto.settlementAmount(),
                dto.shareRatio(),
                dto.expectedSettlementDay(),
                dto.settlementDay());
    }

    public OrderHistoryResult toHistoryResult(OrderHistoryProjectionDto dto) {
        return new OrderHistoryResult(
                dto.historyId() != null ? dto.historyId() : 0L,
                dto.fromStatus(),
                dto.toStatus(),
                dto.changedBy(),
                dto.reason(),
                dto.changedAt());
    }

    public OrderCancelResult toCancelResult(OrderCancelProjectionDto dto) {
        return new OrderCancelResult(
                dto.cancelId() != null ? dto.cancelId() : 0L,
                dto.orderItemId(),
                dto.cancelNumber(),
                dto.cancelStatus(),
                dto.quantity(),
                dto.reasonType(),
                dto.reasonDetail(),
                dto.originalAmount(),
                dto.refundAmount(),
                dto.refundMethod(),
                dto.refundedAt(),
                dto.requestedAt(),
                dto.completedAt());
    }

    /**
     * ProductOrderDetailProjectionDto → ProductOrderDetailData (상세 조회용, item + order + payment 한번에
     * 추출).
     */
    public ProductOrderDetailData toDetailData(ProductOrderDetailProjectionDto dto) {
        OrderItemResult item =
                new OrderItemResult(
                        dto.orderItemId() != null ? dto.orderItemId() : 0L,
                        dto.orderId(),
                        dto.productGroupId(),
                        dto.productId(),
                        dto.sellerId(),
                        dto.brandId(),
                        dto.skuCode(),
                        dto.productGroupName(),
                        dto.brandName(),
                        dto.sellerName(),
                        dto.mainImageUrl(),
                        dto.externalProductId(),
                        dto.externalOptionId(),
                        dto.externalProductName(),
                        dto.externalOptionName(),
                        dto.externalImageUrl(),
                        dto.unitPrice(),
                        dto.quantity(),
                        dto.totalAmount(),
                        dto.discountAmount(),
                        dto.itemPaymentAmount(),
                        dto.receiverName(),
                        dto.receiverPhone(),
                        dto.receiverZipcode(),
                        dto.receiverAddress(),
                        dto.receiverAddressDetail(),
                        dto.deliveryRequest(),
                        dto.deliveryStatus(),
                        dto.shipmentCompanyCode(),
                        dto.invoice(),
                        dto.shipmentCompletedDate(),
                        dto.commissionRate(),
                        dto.fee(),
                        dto.expectationSettlementAmount(),
                        dto.settlementAmount(),
                        dto.shareRatio(),
                        dto.expectedSettlementDay(),
                        dto.settlementDay());

        OrderListResult order =
                new OrderListResult(
                        dto.orderId(),
                        dto.orderNumber(),
                        dto.status(),
                        dto.salesChannelId(),
                        dto.shopId(),
                        dto.shopCode(),
                        dto.shopName(),
                        dto.externalOrderNo(),
                        dto.externalOrderedAt(),
                        dto.buyerName(),
                        dto.buyerEmail(),
                        dto.buyerPhone(),
                        dto.paymentId(),
                        dto.paymentNumber(),
                        dto.paymentStatus(),
                        dto.paymentMethod(),
                        dto.paymentAmount(),
                        dto.paidAt(),
                        0L,
                        dto.orderCreatedAt(),
                        dto.orderUpdatedAt());

        PaymentResult payment =
                dto.paymentId() != null
                        ? new PaymentResult(
                                dto.paymentId(),
                                dto.paymentNumber(),
                                dto.paymentStatus(),
                                dto.paymentMethod(),
                                dto.paymentAgencyId(),
                                dto.paymentAmount(),
                                dto.paidAt(),
                                dto.canceledAt())
                        : null;

        return new ProductOrderDetailData(item, order, payment);
    }

    /** ProductOrderListProjectionDto → OrderItemResult (리스트 조회용, 정산 필드 미포함). */
    public OrderItemResult toItemResultFromProjection(ProductOrderListProjectionDto dto) {
        return new OrderItemResult(
                dto.orderItemId() != null ? dto.orderItemId() : 0L,
                dto.orderId(),
                dto.productGroupId(),
                dto.productId(),
                dto.sellerId(),
                dto.brandId(),
                dto.skuCode(),
                dto.productGroupName(),
                dto.brandName(),
                dto.sellerName(),
                dto.mainImageUrl(),
                dto.externalProductId(),
                dto.externalOptionId(),
                dto.externalProductName(),
                dto.externalOptionName(),
                dto.externalImageUrl(),
                dto.unitPrice(),
                dto.quantity(),
                dto.totalAmount(),
                dto.discountAmount(),
                dto.itemPaymentAmount(),
                dto.receiverName(),
                dto.receiverPhone(),
                dto.receiverZipcode(),
                dto.receiverAddress(),
                dto.receiverAddressDetail(),
                dto.deliveryRequest(),
                dto.deliveryStatus(),
                dto.shipmentCompanyCode(),
                dto.invoice(),
                dto.shipmentCompletedDate(),
                0,
                0,
                0,
                0,
                0,
                null,
                null);
    }

    public OrderClaimResult toClaimResult(OrderClaimProjectionDto dto) {
        return new OrderClaimResult(
                dto.claimId() != null ? dto.claimId() : 0L,
                dto.orderItemId(),
                dto.claimNumber(),
                dto.claimType(),
                dto.claimStatus(),
                dto.quantity(),
                dto.reasonType(),
                dto.reasonDetail(),
                dto.collectMethod(),
                dto.originalAmount(),
                dto.deductionAmount(),
                dto.deductionReason(),
                dto.refundAmount(),
                dto.refundMethod(),
                dto.refundedAt(),
                dto.requestedAt(),
                dto.completedAt(),
                dto.rejectedAt());
    }
}
