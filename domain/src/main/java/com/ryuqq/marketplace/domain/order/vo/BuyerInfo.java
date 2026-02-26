package com.ryuqq.marketplace.domain.order.vo;

import com.ryuqq.marketplace.domain.common.vo.Email;
import com.ryuqq.marketplace.domain.common.vo.PhoneNumber;

/** 구매자 정보. */
public record BuyerInfo(BuyerName buyerName, Email email, PhoneNumber phoneNumber) {

    public static BuyerInfo of(BuyerName buyerName, Email email, PhoneNumber phoneNumber) {
        return new BuyerInfo(buyerName, email, phoneNumber);
    }
}
