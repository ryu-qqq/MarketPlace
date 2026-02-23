package com.ryuqq.marketplace.adapter.in.rest.legacy.brand.controller;

import static com.ryuqq.marketplace.adapter.in.rest.legacy.brand.LegacyBrandEndpoints.BRANDS;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.brand.LegacyBrandEndpoints.BRAND_EXTERNAL_MAPPING;

import com.ryuqq.marketplace.adapter.in.rest.legacy.brand.dto.request.LegacyBrandMappingInfoRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.brand.dto.response.LegacyExtendedBrandContext;
import com.ryuqq.marketplace.adapter.in.rest.legacy.common.dto.LegacyApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.common.dto.LegacyCustomPageable;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 세토프 어드민용 레거시 브랜드 API 호환 컨트롤러.
 *
 * <p>기존 세토프 연동 호환을 위해 제공되는 레거시 엔드포인트입니다.
 */
@Tag(
        name = "세토프 어드민용 레거시",
        description =
                "세토프 어드민용 레거시 엔드포인트. 기존 세토프 연동 호환을 위해 제공되며, 신규 개발 시에는 동일 기능의 일반 API 사용을 권장합니다.")
@RestController
public class LegacyBrandController {

    @GetMapping(BRANDS)
    public ResponseEntity<LegacyApiResponse<LegacyCustomPageable<LegacyExtendedBrandContext>>>
            fetchBrands(Pageable pageable) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PostMapping(BRAND_EXTERNAL_MAPPING)
    public ResponseEntity<LegacyApiResponse<List<LegacyBrandMappingInfoRequest>>>
            convertExternalBrandToInternal(
                    @PathVariable long siteId,
                    @RequestBody List<LegacyBrandMappingInfoRequest> brandMappingInfos) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
