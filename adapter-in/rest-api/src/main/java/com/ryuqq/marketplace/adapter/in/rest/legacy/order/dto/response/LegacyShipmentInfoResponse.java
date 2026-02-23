package com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response;

/** 세토프 ShipmentInfo 호환 응답 DTO. */
public record LegacyShipmentInfoResponse(long orderId, String courierCode, String trackingNumber) {}
