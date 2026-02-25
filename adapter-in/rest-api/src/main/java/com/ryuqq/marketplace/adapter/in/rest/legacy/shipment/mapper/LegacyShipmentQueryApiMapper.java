package com.ryuqq.marketplace.adapter.in.rest.legacy.shipment.mapper;

import com.ryuqq.marketplace.adapter.in.rest.legacy.shipment.dto.response.LegacyShipmentCompanyCodeResponse;
import com.ryuqq.marketplace.application.legacyshipment.dto.response.LegacyShipmentCompanyCodeResult;
import java.util.List;
import org.springframework.stereotype.Component;

/** 레거시 배송 조회 결과 → 응답 DTO 변환 매퍼. */
@Component
public class LegacyShipmentQueryApiMapper {

    /** List<LegacyShipmentCompanyCodeResult> → List<LegacyShipmentCompanyCodeResponse>. */
    public List<LegacyShipmentCompanyCodeResponse> toCompanyCodeResponses(
            List<LegacyShipmentCompanyCodeResult> results) {
        return results.stream()
                .map(
                        r ->
                                new LegacyShipmentCompanyCodeResponse(
                                        r.shipmentCompanyName(), r.shipmentCompanyCode()))
                .toList();
    }
}
