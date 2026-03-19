package com.ryuqq.marketplace.application.shipment.assembler;

import com.ryuqq.marketplace.application.order.dto.composite.ProductOrderDetailData;
import com.ryuqq.marketplace.application.order.dto.response.OrderItemResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderListResult;
import com.ryuqq.marketplace.application.order.dto.response.PaymentResult;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentDetailResult;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentDetailResult.PaymentInfo;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentListResult;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentListResult.OrderInfo;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentListResult.ProductOrderInfo;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentListResult.ReceiverInfo;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentListResult.ShipmentInfo;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentPageResult;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentSummaryResult;
import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentMethod;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentStatus;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Shipment Assembler.
 *
 * <p>Shipment Domain + OrderItemResult + OrderListResult → ShipmentListResult/ShipmentDetailResult
 * 변환을 담당합니다.
 */
@Component
public class ShipmentAssembler {

    /**
     * Shipment + OrderItemResult + OrderListResult → ShipmentListResult 변환.
     *
     * @param shipment Shipment 도메인 객체
     * @param item 주문 상품 조회 결과
     * @param order 주문 기본 정보
     * @return ShipmentListResult
     */
    public ShipmentListResult toListResult(
            Shipment shipment, OrderItemResult item, OrderListResult order) {
        return new ShipmentListResult(
                toShipmentInfo(shipment),
                toOrderInfo(order),
                toProductOrderInfo(item),
                toReceiverInfo(item));
    }

    /**
     * Shipment + ProductOrderDetailData → ShipmentDetailResult 변환.
     *
     * @param shipment Shipment 도메인 객체
     * @param detailData 상품주문 상세 데이터 (item + order + payment)
     * @return ShipmentDetailResult
     */
    public ShipmentDetailResult toDetailResult(
            Shipment shipment, ProductOrderDetailData detailData) {
        return new ShipmentDetailResult(
                toShipmentInfo(shipment),
                toOrderInfo(detailData.order()),
                toProductOrderInfo(detailData.item()),
                toReceiverInfo(detailData.item()),
                toPaymentInfo(detailData.payment()));
    }

    /**
     * 페이지 결과 생성.
     *
     * @param results 배송 목록 결과
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param totalCount 전체 개수
     * @return ShipmentPageResult
     */
    public ShipmentPageResult toPageResult(
            List<ShipmentListResult> results, int page, int size, long totalCount) {
        PageMeta pageMeta = PageMeta.of(page, size, totalCount);
        return new ShipmentPageResult(results, pageMeta);
    }

    /**
     * 상태별 카운트 → ShipmentSummaryResult 변환.
     *
     * @param statusCounts 상태별 카운트 맵
     * @return ShipmentSummaryResult
     */
    public ShipmentSummaryResult toSummaryResult(Map<ShipmentStatus, Long> statusCounts) {
        return new ShipmentSummaryResult(
                statusCounts.getOrDefault(ShipmentStatus.READY, 0L).intValue(),
                statusCounts.getOrDefault(ShipmentStatus.PREPARING, 0L).intValue(),
                statusCounts.getOrDefault(ShipmentStatus.SHIPPED, 0L).intValue(),
                statusCounts.getOrDefault(ShipmentStatus.IN_TRANSIT, 0L).intValue(),
                statusCounts.getOrDefault(ShipmentStatus.DELIVERED, 0L).intValue(),
                statusCounts.getOrDefault(ShipmentStatus.FAILED, 0L).intValue(),
                statusCounts.getOrDefault(ShipmentStatus.CANCELLED, 0L).intValue());
    }

    private ShipmentInfo toShipmentInfo(Shipment shipment) {
        ShipmentMethod method = shipment.shipmentMethod();
        return new ShipmentInfo(
                shipment.idValue(),
                shipment.shipmentNumberValue(),
                shipment.status().name(),
                shipment.trackingNumber(),
                method != null ? method.courierCode() : null,
                method != null ? method.courierName() : null,
                shipment.orderConfirmedAt(),
                shipment.shippedAt(),
                shipment.deliveredAt(),
                shipment.createdAt());
    }

    private OrderInfo toOrderInfo(OrderListResult order) {
        return new OrderInfo(
                order.orderId(),
                order.orderNumber(),
                null,
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

    private ReceiverInfo toReceiverInfo(OrderItemResult item) {
        return new ReceiverInfo(
                item.receiverName(),
                item.receiverPhone(),
                item.receiverZipcode(),
                item.receiverAddress(),
                item.receiverAddressDetail(),
                item.deliveryRequest());
    }

    private PaymentInfo toPaymentInfo(PaymentResult payment) {
        if (payment == null) {
            return null;
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
}
