package com.ryuqq.marketplace.domain.order.vo;

import com.ryuqq.marketplace.domain.common.vo.Address;
import com.ryuqq.marketplace.domain.common.vo.PhoneNumber;

/** 수령인 정보. */
public record ReceiverInfo(
        String receiverName, PhoneNumber receiverPhone, Address address, String deliveryRequest) {

    public ReceiverInfo {
        if (receiverName == null || receiverName.isBlank()) {
            throw new IllegalArgumentException("수령인 이름은 필수입니다");
        }
    }

    public static ReceiverInfo of(
            String receiverName,
            PhoneNumber receiverPhone,
            Address address,
            String deliveryRequest) {
        return new ReceiverInfo(receiverName, receiverPhone, address, deliveryRequest);
    }
}
