package com.ryuqq.marketplace.application.order.assembler;

import com.ryuqq.marketplace.application.order.dto.composite.ProductOrderDetailBundle;
import com.ryuqq.marketplace.application.order.dto.composite.ProductOrderListBundle;
import com.ryuqq.marketplace.application.order.dto.response.OrderCancelResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderClaimResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderItemResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderListResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderSummaryResult;
import com.ryuqq.marketplace.application.order.dto.response.PaymentResult;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderDetailResult;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderDetailResult.SettlementInfo;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.CancelSummary;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.ClaimSummary;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.DeliveryInfo;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.OrderInfo;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.PaymentInfo;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.ProductOrderInfo;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.ReceiverInfo;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderPageResult;
import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import com.ryuqq.marketplace.domain.order.vo.OrderStatus;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

/**
 * Order Assembler.
 *
 * <p>Domain/Composite 데이터 → Result 변환을 담당합니다. 번들(Bundle) DTO를 받아 최종 응답 DTO를 조립합니다.
 */
@Component
public class OrderAssembler {

    /** 완료된 취소 상태 집합. */
    private static final Set<String> COMPLETED_CANCEL_STATUSES = Set.of("COMPLETED", "REFUNDED");

    /** 활성 클레임 상태 집합 (진행 중인 클레임). */
    private static final Set<String> ACTIVE_CLAIM_STATUSES =
            Set.of("REQUESTED", "ACCEPTED", "IN_PROGRESS", "COLLECTING");

    /** 완료된 클레임 상태 집합. */
    private static final Set<String> COMPLETED_CLAIM_STATUSES =
            Set.of("COMPLETED", "REFUNDED", "EXCHANGED");

    /**
     * 상태별 카운트 → OrderSummaryResult 변환.
     *
     * @param statusCounts 상태별 카운트 맵
     * @return OrderSummaryResult
     */
    public OrderSummaryResult toSummaryResult(Map<OrderStatus, Long> statusCounts) {
        return new OrderSummaryResult(
                statusCounts.getOrDefault(OrderStatus.ORDERED, 0L).intValue(),
                statusCounts.getOrDefault(OrderStatus.PREPARING, 0L).intValue(),
                statusCounts.getOrDefault(OrderStatus.SHIPPED, 0L).intValue(),
                statusCounts.getOrDefault(OrderStatus.DELIVERED, 0L).intValue(),
                statusCounts.getOrDefault(OrderStatus.CONFIRMED, 0L).intValue(),
                statusCounts.getOrDefault(OrderStatus.CANCELLED, 0L).intValue(),
                statusCounts.getOrDefault(OrderStatus.CLAIM_IN_PROGRESS, 0L).intValue(),
                statusCounts.getOrDefault(OrderStatus.REFUNDED, 0L).intValue(),
                statusCounts.getOrDefault(OrderStatus.EXCHANGED, 0L).intValue());
    }

    // ==================== V5 상품주문 리스트 조립 ====================

    /**
     * ProductOrderListBundle → ProductOrderPageResult 변환.
     *
     * <p>각 OrderItemResult를 중심으로 주문/결제/배송/수령인/취소요약/클레임요약을 조립하고 PageMeta와 함께 반환합니다.
     *
     * @param bundle ReadFacade에서 조회한 번들
     * @param page 현재 페이지
     * @param size 페이지 크기
     * @return 페이지 결과
     */
    public ProductOrderPageResult toProductOrderPageResult(
            ProductOrderListBundle bundle, int page, int size) {
        List<ProductOrderListResult> productOrders =
                bundle.orderItems().stream()
                        .map(item -> toProductOrderListResult(item, bundle))
                        .toList();
        PageMeta pageMeta = PageMeta.of(page, size, bundle.totalElements());
        return new ProductOrderPageResult(productOrders, pageMeta);
    }

    // ==================== V5 상품주문 상세 조립 ====================

    /**
     * ProductOrderDetailBundle → ProductOrderDetailResult 변환.
     *
     * <p>리스트와 동일한 공통 블록(order/productOrder/payment/receiver/delivery/cancel/claim)을 재사용하고, 정산/취소 전체
     * 이력/클레임 전체 이력/타임라인을 추가합니다.
     *
     * @param bundle ReadFacade에서 조회한 상세 번들
     * @return 상세 결과
     */
    public ProductOrderDetailResult toProductOrderDetailResult(ProductOrderDetailBundle bundle) {
        OrderItemResult item = bundle.item();
        OrderListResult order = bundle.order();

        OrderInfo orderInfo = toOrderInfo(order);
        ProductOrderInfo productOrderInfo = toProductOrderInfo(item);
        PaymentInfo paymentInfo = toPaymentInfoFromPaymentResult(bundle.payment());
        ReceiverInfo receiverInfo = toReceiverInfo(item);
        DeliveryInfo deliveryInfo = toDeliveryInfo(item);

        CancelSummary cancelSummary = toCancelSummary(bundle.cancels(), item.quantity());
        ClaimSummary claimSummary = toClaimSummary(bundle.claims(), item.quantity());

        SettlementInfo settlement = toSettlementInfo(item);

        return new ProductOrderDetailResult(
                orderInfo,
                productOrderInfo,
                paymentInfo,
                receiverInfo,
                deliveryInfo,
                cancelSummary,
                claimSummary,
                settlement,
                bundle.cancels(),
                bundle.claims(),
                bundle.histories());
    }

    private PaymentInfo toPaymentInfoFromPaymentResult(PaymentResult payment) {
        if (payment == null) {
            return new PaymentInfo(null, null, null, null, null, 0, null, null);
        }
        return new PaymentInfo(
                payment.paymentId(),
                payment.paymentNumber(),
                payment.paymentStatus(),
                payment.paymentMethod(),
                payment.paymentAgencyId(),
                payment.paymentAmount(),
                payment.paidAt(),
                payment.canceledAt());
    }

    private SettlementInfo toSettlementInfo(OrderItemResult item) {
        return new SettlementInfo(
                item.commissionRate(),
                item.fee(),
                item.expectationSettlementAmount(),
                item.settlementAmount(),
                item.shareRatio(),
                item.expectedSettlementDay(),
                item.settlementDay());
    }

    // ==================== V5 상품주문 리스트 조립 (private) ====================

    private ProductOrderListResult toProductOrderListResult(
            OrderItemResult item, ProductOrderListBundle bundle) {

        OrderListResult order = bundle.ordersById().get(item.orderId());

        OrderInfo orderInfo = toOrderInfo(order);
        ProductOrderInfo productOrderInfo = toProductOrderInfo(item);
        PaymentInfo paymentInfo = toPaymentInfoFromOrder(order);
        ReceiverInfo receiverInfo = toReceiverInfo(item);
        DeliveryInfo deliveryInfo = toDeliveryInfo(item);

        List<OrderCancelResult> cancels =
                bundle.cancelsByItemId().getOrDefault(item.orderItemId(), List.of());
        CancelSummary cancelSummary = toCancelSummary(cancels, item.quantity());

        List<OrderClaimResult> claims =
                bundle.claimsByItemId().getOrDefault(item.orderItemId(), List.of());
        ClaimSummary claimSummary = toClaimSummary(claims, item.quantity());

        return new ProductOrderListResult(
                orderInfo,
                productOrderInfo,
                paymentInfo,
                receiverInfo,
                deliveryInfo,
                cancelSummary,
                claimSummary);
    }

    private OrderInfo toOrderInfo(OrderListResult order) {
        if (order == null) {
            return new OrderInfo(
                    null, null, null, 0, 0, null, null, null, null, null, null, null, null, null);
        }
        return new OrderInfo(
                order.orderId(),
                order.orderNumber(),
                order.status(),
                order.salesChannelId(),
                order.shopId(),
                order.shopCode(),
                order.shopName(),
                order.externalOrderNo(),
                order.externalOrderedAt(),
                order.buyerName(),
                order.buyerEmail(),
                order.buyerPhone(),
                order.createdAt(),
                order.updatedAt());
    }

    private ProductOrderInfo toProductOrderInfo(OrderItemResult item) {
        return new ProductOrderInfo(
                item.orderItemId(),
                item.productGroupId(),
                item.productId(),
                item.sellerId(),
                item.brandId(),
                item.skuCode(),
                item.productGroupName(),
                item.brandName(),
                item.sellerName(),
                item.mainImageUrl(),
                item.externalProductId(),
                item.externalOptionId(),
                item.externalProductName(),
                item.externalOptionName(),
                item.externalImageUrl(),
                item.unitPrice(),
                item.quantity(),
                item.totalAmount(),
                item.discountAmount(),
                item.paymentAmount());
    }

    private PaymentInfo toPaymentInfoFromOrder(OrderListResult order) {
        if (order == null) {
            return new PaymentInfo(null, null, null, null, null, 0, null, null);
        }
        return new PaymentInfo(
                order.paymentId(),
                order.paymentNumber(),
                order.paymentStatus(),
                order.paymentMethod(),
                null,
                order.paymentAmount(),
                order.paidAt(),
                null);
    }

    private ReceiverInfo toReceiverInfo(OrderItemResult item) {
        return new ReceiverInfo(
                item.receiverName(),
                item.receiverPhone(),
                item.receiverZipcode(),
                item.receiverAddress(),
                item.receiverAddressDetail(),
                item.deliveryRequest());
    }

    private DeliveryInfo toDeliveryInfo(OrderItemResult item) {
        return new DeliveryInfo(
                item.deliveryStatus(),
                item.shipmentCompanyCode(),
                item.invoice(),
                item.shipmentCompletedDate());
    }

    /**
     * 취소 내역 → CancelSummary 변환.
     *
     * <p>완료된 취소의 수량 합산, 취소 가능 수량 계산, 최근 취소 추출을 수행합니다.
     */
    private CancelSummary toCancelSummary(List<OrderCancelResult> cancels, int orderQuantity) {
        if (cancels.isEmpty()) {
            return CancelSummary.none(orderQuantity);
        }

        int totalCancelledQty =
                cancels.stream()
                        .filter(c -> COMPLETED_CANCEL_STATUSES.contains(c.cancelStatus()))
                        .mapToInt(OrderCancelResult::quantity)
                        .sum();

        int cancelableQty = Math.max(0, orderQuantity - totalCancelledQty);

        boolean hasActiveCancel =
                cancels.stream()
                        .anyMatch(
                                c ->
                                        !COMPLETED_CANCEL_STATUSES.contains(c.cancelStatus())
                                                && !"REJECTED".equals(c.cancelStatus()));

        OrderCancelResult latest =
                cancels.stream()
                        .max(
                                Comparator.comparing(
                                        OrderCancelResult::requestedAt,
                                        Comparator.nullsFirst(Comparator.naturalOrder())))
                        .orElse(null);

        CancelSummary.LatestCancel latestCancel =
                latest == null
                        ? null
                        : new CancelSummary.LatestCancel(
                                String.valueOf(latest.cancelId()),
                                latest.cancelNumber(),
                                latest.cancelStatus(),
                                latest.quantity(),
                                latest.requestedAt());

        return new CancelSummary(hasActiveCancel, totalCancelledQty, cancelableQty, latestCancel);
    }

    /**
     * 클레임 내역 → ClaimSummary 변환.
     *
     * <p>활성 클레임 수, 총 클레임 수량, 클레임 가능 수량, 최근 클레임을 계산합니다.
     */
    private ClaimSummary toClaimSummary(List<OrderClaimResult> claims, int orderQuantity) {
        if (claims.isEmpty()) {
            return ClaimSummary.none(orderQuantity);
        }

        long activeCount =
                claims.stream()
                        .filter(c -> ACTIVE_CLAIM_STATUSES.contains(c.claimStatus()))
                        .count();

        int totalClaimedQty =
                claims.stream()
                        .filter(
                                c ->
                                        COMPLETED_CLAIM_STATUSES.contains(c.claimStatus())
                                                || ACTIVE_CLAIM_STATUSES.contains(c.claimStatus()))
                        .mapToInt(OrderClaimResult::quantity)
                        .sum();

        int claimableQty = Math.max(0, orderQuantity - totalClaimedQty);

        boolean hasActiveClaim = activeCount > 0;

        OrderClaimResult latest =
                claims.stream()
                        .max(
                                Comparator.comparing(
                                        OrderClaimResult::requestedAt,
                                        Comparator.nullsFirst(Comparator.naturalOrder())))
                        .orElse(null);

        ClaimSummary.LatestClaim latestClaim =
                latest == null
                        ? null
                        : new ClaimSummary.LatestClaim(
                                String.valueOf(latest.claimId()),
                                latest.claimNumber(),
                                latest.claimType(),
                                latest.claimStatus(),
                                latest.quantity(),
                                latest.requestedAt());

        return new ClaimSummary(
                hasActiveClaim, (int) activeCount, totalClaimedQty, claimableQty, latestClaim);
    }
}
