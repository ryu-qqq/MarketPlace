package com.ryuqq.marketplace.domain.order.vo;

import com.ryuqq.marketplace.domain.common.vo.Money;

/**
 * 외부몰이 제공한 주문 상품 가격 정보. 외부몰 쿠폰 등이 반영된 실제 거래 금액입니다.
 *
 * <p>paymentAmount = totalAmount - discountAmount
 */
public record ExternalOrderItemPrice(
        Money unitPrice,
        int quantity,
        Money totalAmount,
        Money discountAmount,
        Money sellerBurdenDiscountAmount,
        Money paymentAmount) {

    public ExternalOrderItemPrice {
        if (unitPrice == null) {
            throw new IllegalArgumentException("개당 판매가는 필수입니다");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다");
        }
        if (totalAmount == null) {
            throw new IllegalArgumentException("합계 금액은 필수입니다");
        }
        if (paymentAmount == null) {
            throw new IllegalArgumentException("실결제 금액은 필수입니다");
        }
        if (discountAmount == null) {
            discountAmount = Money.zero();
        }
        if (sellerBurdenDiscountAmount == null) {
            sellerBurdenDiscountAmount = Money.zero();
        }
    }

    public static ExternalOrderItemPrice of(
            Money unitPrice,
            int quantity,
            Money totalAmount,
            Money discountAmount,
            Money sellerBurdenDiscountAmount,
            Money paymentAmount) {
        return new ExternalOrderItemPrice(
                unitPrice, quantity, totalAmount, discountAmount, sellerBurdenDiscountAmount, paymentAmount);
    }
}
