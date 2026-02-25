package com.ryuqq.marketplace.adapter.in.rest.legacy.shipment.dto.response;

/**
 * 세토프 택배사 코드 호환 응답 DTO.
 *
 * <p>GET /shipment/company-codes - 택배사 코드 목록 조회
 */
public record LegacyShipmentCompanyCodeResponse(
        String shipmentCompanyName, String shipmentCompanyCode) {}
