package com.ryuqq.marketplace.domain.order.vo;

import com.ryuqq.marketplace.domain.common.vo.Money;
import java.time.Instant;

/**
 * 결제 정보.
 *
 * <p>주문 레벨의 결제 요약 정보입니다. 외부몰에서 수집한 결제 데이터를 기록합니다.
 *
 * @param paymentMethod 결제 수단 (외부몰 원본값, 예: "신용카드", "CARD")
 * @param totalPaymentAmount 총 결제 금액
 * @param paidAt 결제 완료 시각 (nullable)
 */
public record PaymentInfo(String paymentMethod, Money totalPaymentAmount, Instant paidAt) {

    public PaymentInfo {
        if (totalPaymentAmount == null) {
            totalPaymentAmount = Money.zero();
        }
    }

    public static PaymentInfo of(String paymentMethod, Money totalPaymentAmount, Instant paidAt) {
        return new PaymentInfo(paymentMethod, totalPaymentAmount, paidAt);
    }
}
