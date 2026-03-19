package com.ryuqq.marketplace.application.order.dto.response;

import java.util.List;

/**
 * 상품주문 상세 결과 (V5).
 *
 * <p>리스트 항목({@link ProductOrderListResult})의 확장으로, 취소/클레임 전체 이력, 타임라인을 포함합니다.
 */
public record ProductOrderDetailResult(
        ProductOrderListResult.OrderInfo order,
        ProductOrderListResult.ProductOrderInfo productOrder,
        ProductOrderListResult.PaymentInfo payment,
        ProductOrderListResult.ReceiverInfo receiver,
        ProductOrderListResult.DeliveryInfo delivery,
        ProductOrderListResult.CancelSummary cancel,
        ProductOrderListResult.ClaimSummary claim,
        List<OrderCancelResult> cancels,
        List<OrderClaimResult> claims,
        List<OrderHistoryResult> timeLine) {}
