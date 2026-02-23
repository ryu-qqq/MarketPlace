package com.ryuqq.marketplace.adapter.in.rest.legacy.shipment.controller;

import static com.ryuqq.marketplace.adapter.in.rest.legacy.shipment.LegacyShipmentEndpoints.COMPANY_CODES;

import com.ryuqq.marketplace.adapter.in.rest.legacy.common.dto.LegacyApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.shipment.dto.response.LegacyShipmentCompanyCodeResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 세토프 어드민용 레거시 배송(택배사) API 호환 컨트롤러.
 *
 * <p>기존 세토프 연동 호환을 위해 제공되는 레거시 엔드포인트입니다. OMS(사방넷)가 호출하는 GET /shipment/company-codes (택배사 코드 목록 조회)를
 * 제공합니다.
 */
@Tag(
        name = "세토프 어드민용 레거시",
        description =
                "세토프 어드민용 레거시 엔드포인트. 기존 세토프 연동 호환을 위해 제공되며, 신규 개발 시에는 동일 기능의 일반 API 사용을 권장합니다.")
@RestController
public class LegacyShipmentController {

    @GetMapping(COMPANY_CODES)
    public ResponseEntity<LegacyApiResponse<List<LegacyShipmentCompanyCodeResponse>>>
            getCompanyCodes() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
