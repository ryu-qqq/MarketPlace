package com.ryuqq.marketplace.adapter.in.rest.legacy.shipment.mapper;

import com.ryuqq.marketplace.adapter.in.rest.legacy.shipment.dto.response.LegacyShipmentCompanyCodeResponse;
import com.ryuqq.marketplace.domain.commoncode.aggregate.CommonCode;
import java.util.List;
import org.springframework.stereotype.Component;

/** 레거시 배송 조회 결과 → 응답 DTO 변환 매퍼. */
@Component
public class LegacyShipmentQueryApiMapper {

    /** CommonCode → LegacyShipmentCompanyCodeResponse. */
    public LegacyShipmentCompanyCodeResponse toCompanyCodeResponse(CommonCode commonCode) {
        return new LegacyShipmentCompanyCodeResponse(
                commonCode.displayNameValue(), commonCode.codeValue());
    }

    /** List<CommonCode> → List<LegacyShipmentCompanyCodeResponse>. */
    public List<LegacyShipmentCompanyCodeResponse> toCompanyCodeResponses(
            List<CommonCode> commonCodes) {
        return commonCodes.stream().map(this::toCompanyCodeResponse).toList();
    }
}
