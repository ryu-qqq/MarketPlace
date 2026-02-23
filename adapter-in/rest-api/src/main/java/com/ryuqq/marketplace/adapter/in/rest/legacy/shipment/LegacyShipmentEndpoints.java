package com.ryuqq.marketplace.adapter.in.rest.legacy.shipment;

/** 레거시 세토프 호환 배송(택배사) API 엔드포인트. */
public final class LegacyShipmentEndpoints {

    private LegacyShipmentEndpoints() {}

    private static final String BASE = "/api/v1/legacy";

    public static final String COMPANY_CODES = BASE + "/shipment/company-codes";
}
