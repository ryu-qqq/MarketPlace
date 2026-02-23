package com.ryuqq.marketplace.adapter.in.rest.inboundbrandmapping.controller;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.inboundbrandmapping.InboundBrandMappingAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.inboundbrandmapping.dto.query.SearchInboundBrandMappingsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundbrandmapping.dto.response.InboundBrandMappingApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.inboundbrandmapping.mapper.InboundBrandMappingQueryApiMapper;
import com.ryuqq.marketplace.application.inboundbrandmapping.dto.response.InboundBrandMappingPageResult;
import com.ryuqq.marketplace.application.inboundbrandmapping.port.in.query.SearchInboundBrandMappingUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 인바운드 브랜드 매핑 조회 API 컨트롤러. */
@Tag(name = "인바운드 브랜드 매핑 조회", description = "인바운드 브랜드 매핑 조회 API")
@RestController
@RequestMapping(InboundBrandMappingAdminEndpoints.BRAND_MAPPINGS)
public class InboundBrandMappingQueryController {

    private final SearchInboundBrandMappingUseCase searchInboundBrandMappingUseCase;
    private final InboundBrandMappingQueryApiMapper mapper;

    public InboundBrandMappingQueryController(
            SearchInboundBrandMappingUseCase searchInboundBrandMappingUseCase,
            InboundBrandMappingQueryApiMapper mapper) {
        this.searchInboundBrandMappingUseCase = searchInboundBrandMappingUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "인바운드 브랜드 매핑 목록 검색", description = "외부 소스 ID에 해당하는 브랜드 매핑 목록을 검색합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<PageApiResponse<InboundBrandMappingApiResponse>>>
            searchInboundBrandMappingsByOffset(
                    @Parameter(description = "외부 소스 ID", required = true) @RequestParam
                            Long inboundSourceId,
                    @ParameterObject @Valid SearchInboundBrandMappingsApiRequest request) {
        InboundBrandMappingPageResult pageResult =
                searchInboundBrandMappingUseCase.execute(
                        mapper.toSearchParams(inboundSourceId, request));
        return ResponseEntity.ok(ApiResponse.of(mapper.toPageResponse(pageResult)));
    }
}
