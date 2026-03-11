package com.ryuqq.marketplace.application.shipment.dto.response;

import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentListResult.OrderInfo;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentListResult.ProductOrderInfo;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentListResult.ReceiverInfo;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentListResult.ShipmentInfo;
import java.time.Instant;

/**
 * 배송 상세 조회 결과 (V4).
 *
 * <p>배송 목록 결과에 결제/정산 정보를 추가합니다.
 */
public record ShipmentDetailResult(
        ShipmentInfo shipment,
        OrderInfo order,
        ProductOrderInfo productOrder,
        ReceiverInfo receiver,
        PaymentInfo payment,
        SettlementInfo settlement) {

    /**
     * 결제 정보.
     *
     * @param paymentId 결제 ID
     * @param paymentNumber 결제 번호
     * @param paymentStatus 결제 상태
     * @param paymentMethod 결제 수단
     * @param paymentAgencyId PG사 결제 ID
     * @param paymentAmount 결제 금액
     * @param paidAt 결제일시
     * @param canceledAt 결제취소일시
     */
    public record PaymentInfo(
            String paymentId,
            String paymentNumber,
            String paymentStatus,
            String paymentMethod,
            String paymentAgencyId,
            int paymentAmount,
            Instant paidAt,
            Instant canceledAt) {}

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
