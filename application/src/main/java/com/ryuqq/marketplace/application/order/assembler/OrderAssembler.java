package com.ryuqq.marketplace.application.order.assembler;

import com.ryuqq.marketplace.application.order.dto.response.OrderCancelResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderClaimResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderItemResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderListResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderSummaryResult;
import com.ryuqq.marketplace.application.order.dto.response.PaymentResult;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderDetailResult;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.CancelSummary;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.ClaimSummary;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.DeliveryInfo;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.OrderInfo;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.PaymentInfo;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.ProductOrderInfo;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.ReceiverInfo;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderPageResult;
import com.ryuqq.marketplace.application.order.internal.ProductOrderDetailBundle;
import com.ryuqq.marketplace.application.order.internal.ProductOrderListBundle;
import com.ryuqq.marketplace.domain.cancel.vo.CancelStatus;
import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeStatus;
import com.ryuqq.marketplace.domain.order.vo.OrderItemStatus;
import com.ryuqq.marketplace.domain.refund.vo.RefundStatus;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Order Assembler.
 *
 * <p>Domain/Composite 데이터 → Result 변환을 담당합니다. 번들(Bundle) DTO를 받아 최종 응답 DTO를 조립합니다.
 *
 * <p>상태 판별은 도메인 Enum(CancelStatus, ExchangeStatus, RefundStatus)의 isCompleted()/isActive() 메서드에
 * 위임합니다. (APP-ASM-002, DOM-VO-003)
 */
@Component
public class OrderAssembler {

    /** 주문상품 상태별 카운트 → OrderSummaryResult 변환. */
    public OrderSummaryResult toSummaryResult(Map<OrderItemStatus, Long> statusCounts) {
        return new OrderSummaryResult(
                statusCounts.getOrDefault(OrderItemStatus.READY, 0L),
                statusCounts.getOrDefault(OrderItemStatus.CONFIRMED, 0L),
                statusCounts.getOrDefault(OrderItemStatus.CANCELLED, 0L),
                statusCounts.getOrDefault(OrderItemStatus.RETURN_REQUESTED, 0L),
                statusCounts.getOrDefault(OrderItemStatus.RETURNED, 0L));
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

        return new ProductOrderDetailResult(
                orderInfo,
                productOrderInfo,
                paymentInfo,
                receiverInfo,
                deliveryInfo,
                cancelSummary,
                claimSummary,
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
                    null, null, 0, 0, null, null, null, null, null, null, null, null, null);
        }
        return new OrderInfo(
                order.orderId(),
                order.orderNumber(),
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
                item.orderItemNumber(),
                item.productGroupId(),
                item.sellerId(),
                item.brandId(),
                item.categoryId(),
                item.productId(),
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
                item.regularPrice(),
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
        return new DeliveryInfo(item.orderItemStatus(), item.externalOrderStatus());
    }

    /**
     * 취소 내역 → CancelSummary 변환.
     *
     * <p>완료된 취소의 수량 합산, 취소 가능 수량 계산, 최근 취소 추출을 수행합니다. 상태 판별은 CancelStatus 도메인 Enum에 위임합니다.
     */
    private CancelSummary toCancelSummary(List<OrderCancelResult> cancels, int orderQuantity) {
        if (cancels.isEmpty()) {
            return CancelSummary.none(orderQuantity);
        }

        int totalCancelledQty =
                cancels.stream()
                        .filter(c -> isCancelCompleted(c.cancelStatus()))
                        .mapToInt(OrderCancelResult::quantity)
                        .sum();

        int cancelableQty = Math.max(0, orderQuantity - totalCancelledQty);

        boolean hasActiveCancel = cancels.stream().anyMatch(c -> isCancelActive(c.cancelStatus()));

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
     * <p>활성 클레임 수, 총 클레임 수량, 클레임 가능 수량, 최근 클레임을 계산합니다. 상태 판별은 claimType 기반으로
     * ExchangeStatus/RefundStatus 도메인 Enum에 위임합니다.
     */
    private ClaimSummary toClaimSummary(List<OrderClaimResult> claims, int orderQuantity) {
        if (claims.isEmpty()) {
            return ClaimSummary.none(orderQuantity);
        }

        long activeCount =
                claims.stream().filter(c -> isClaimActive(c.claimType(), c.claimStatus())).count();

        int totalClaimedQty =
                claims.stream()
                        .filter(
                                c ->
                                        !isClaimTerminal(c.claimType(), c.claimStatus())
                                                || isClaimCompleted(c.claimType(), c.claimStatus()))
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

    // ==================== 도메인 Enum 기반 상태 판별 (DOM-VO-003) ====================

    private boolean isCancelCompleted(String cancelStatus) {
        try {
            return CancelStatus.valueOf(cancelStatus).isCompleted();
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private boolean isCancelActive(String cancelStatus) {
        try {
            return CancelStatus.valueOf(cancelStatus).isActive();
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private boolean isClaimActive(String claimType, String claimStatus) {
        try {
            return switch (claimType) {
                case "EXCHANGE" -> ExchangeStatus.valueOf(claimStatus).isActive();
                case "REFUND" -> RefundStatus.valueOf(claimStatus).isActive();
                default -> false;
            };
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private boolean isClaimCompleted(String claimType, String claimStatus) {
        try {
            return switch (claimType) {
                case "EXCHANGE" -> ExchangeStatus.valueOf(claimStatus).isCompleted();
                case "REFUND" -> RefundStatus.valueOf(claimStatus).isCompleted();
                default -> false;
            };
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private boolean isClaimTerminal(String claimType, String claimStatus) {
        try {
            return switch (claimType) {
                case "EXCHANGE" -> ExchangeStatus.valueOf(claimStatus).isTerminal();
                case "REFUND" -> RefundStatus.valueOf(claimStatus).isTerminal();
                default -> true;
            };
        } catch (IllegalArgumentException e) {
            return true;
        }
    }
}
