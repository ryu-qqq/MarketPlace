package com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.request;

/** 세토프 ShipmentInfo 호환 요청 DTO. */
public record LegacyShipmentInfoRequest(
        String invoiceNo, String shipmentType, String companyCode) {}
