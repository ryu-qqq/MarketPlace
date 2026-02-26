package com.ryuqq.marketplace.domain.exchange.vo;

import com.ryuqq.marketplace.domain.claim.vo.FeePayer;
import com.ryuqq.marketplace.domain.common.vo.Money;

/** 교환 금액 조정 정보. */
public record AmountAdjustment(
        Money originalPrice,
        Money targetPrice,
        Money priceDifference,
        boolean additionalPaymentRequired,
        boolean partialRefundRequired,
        Money collectShippingFee,
        Money reshipShippingFee,
        Money totalShippingFee,
        FeePayer shippingFeePayer) {

    public AmountAdjustment {
        if (originalPrice == null) {
            throw new IllegalArgumentException("원래 가격은 null일 수 없습니다");
        }
        if (targetPrice == null) {
            throw new IllegalArgumentException("교환 대상 가격은 null일 수 없습니다");
        }
        if (shippingFeePayer == null) {
            throw new IllegalArgumentException("배송비 부담 주체는 null일 수 없습니다");
        }
    }

    public static AmountAdjustment calculate(
            Money originalPrice,
            Money targetPrice,
            Money collectFee,
            Money reshipFee,
            FeePayer payer) {
        boolean additionalPayment = targetPrice.isGreaterThan(originalPrice);
        boolean partialRefund = originalPrice.isGreaterThan(targetPrice);
        Money difference =
                additionalPayment
                        ? targetPrice.subtract(originalPrice)
                        : originalPrice.subtract(targetPrice);
        Money totalFee = collectFee.add(reshipFee);
        return new AmountAdjustment(
                originalPrice,
                targetPrice,
                difference,
                additionalPayment,
                partialRefund,
                collectFee,
                reshipFee,
                totalFee,
                payer);
    }
}
