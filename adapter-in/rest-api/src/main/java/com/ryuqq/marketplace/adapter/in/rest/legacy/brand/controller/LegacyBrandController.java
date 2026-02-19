package com.ryuqq.marketplace.adapter.in.rest.legacy.brand.controller;

import static com.ryuqq.marketplace.adapter.in.rest.legacy.brand.LegacyBrandEndpoints.BRANDS;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.brand.LegacyBrandEndpoints.BRAND_EXTERNAL_MAPPING;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.brand.dto.request.LegacyBrandMappingInfoRequest;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** 세토프 레거시 브랜드 API 호환 컨트롤러. */
@RestController
public class LegacyBrandController {

    @GetMapping(BRANDS)
    public ResponseEntity<ApiResponse<Object>> fetchBrands(Pageable pageable) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PostMapping(BRAND_EXTERNAL_MAPPING)
    public ResponseEntity<ApiResponse<List<LegacyBrandMappingInfoRequest>>>
            convertExternalBrandToInternal(
                    @PathVariable long siteId,
                    @RequestBody List<LegacyBrandMappingInfoRequest> brandMappingInfos) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
