package com.ryuqq.marketplace.domain.order.vo;

import java.time.Instant;

/** 배송 정보. */
public record PaymentShipmentInfo(
        DeliveryStatus deliveryStatus,
        String shipmentCompanyCode,
        String invoice,
        Instant shipmentCompletedDate) {}
