package com.ryuqq.marketplace.domain.claim.vo;

import com.ryuqq.marketplace.domain.common.vo.Money;

/** 배송비 정보. */
public record ShippingFeeInfo(Money amount, FeePayer payer, boolean includeInPackage) {

    public ShippingFeeInfo {
        if (amount == null) {
            throw new IllegalArgumentException("배송비 금액은 null일 수 없습니다");
        }
        if (payer == null) {
            throw new IllegalArgumentException("배송비 부담 주체는 필수입니다");
        }
    }

    public static ShippingFeeInfo of(Money amount, FeePayer payer, boolean includeInPackage) {
        return new ShippingFeeInfo(amount, payer, includeInPackage);
    }

    public static ShippingFeeInfo free() {
        return new ShippingFeeInfo(Money.zero(), FeePayer.SELLER, false);
    }
}
