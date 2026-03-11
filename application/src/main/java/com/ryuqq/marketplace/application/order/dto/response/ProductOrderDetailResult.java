package com.ryuqq.marketplace.application.order.dto.response;

import java.time.Instant;
import java.util.List;

/**
 * 상품주문 상세 결과 (V5).
 *
 * <p>리스트 항목({@link ProductOrderListResult})의 확장으로, 정산 정보와 취소/클레임 전체 이력, 타임라인을 포함합니다.
 */
public record ProductOrderDetailResult(
        ProductOrderListResult.OrderInfo order,
        ProductOrderListResult.ProductOrderInfo productOrder,
        ProductOrderListResult.PaymentInfo payment,
        ProductOrderListResult.ReceiverInfo receiver,
        ProductOrderListResult.DeliveryInfo delivery,
        ProductOrderListResult.CancelSummary cancel,
        ProductOrderListResult.ClaimSummary claim,
        SettlementInfo settlement,
        List<OrderCancelResult> cancels,
        List<OrderClaimResult> claims,
        List<OrderHistoryResult> timeLine) {

    /**
     * 정산 정보.
     *
     * @param commissionRate 수수료율
     * @param fee 수수료
     * @param expectationSettlementAmount 예상 정산금액
     * @param settlementAmount 정산금액
     * @param shareRatio 배분비율
     * @param expectedSettlementDay 예상 정산일
     * @param settlementDay 정산일
     */
    public record SettlementInfo(
            int commissionRate,
            int fee,
            int expectationSettlementAmount,
            int settlementAmount,
            int shareRatio,
            Instant expectedSettlementDay,
            Instant settlementDay) {}
}
