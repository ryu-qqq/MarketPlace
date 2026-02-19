package com.ryuqq.marketplace.adapter.in.rest.externalbrandmapping.controller;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.externalbrandmapping.ExternalBrandMappingAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.externalbrandmapping.dto.query.SearchExternalBrandMappingsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.externalbrandmapping.dto.response.ExternalBrandMappingApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.externalbrandmapping.mapper.ExternalBrandMappingQueryApiMapper;
import com.ryuqq.marketplace.application.externalbrandmapping.dto.response.ExternalBrandMappingPageResult;
import com.ryuqq.marketplace.application.externalbrandmapping.port.in.query.SearchExternalBrandMappingUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 외부 브랜드 매핑 조회 API 컨트롤러. */
@Tag(name = "외부 브랜드 매핑 조회", description = "외부 브랜드 매핑 조회 API")
@RestController
@RequestMapping(ExternalBrandMappingAdminEndpoints.BRAND_MAPPINGS)
public class ExternalBrandMappingQueryController {

    private final SearchExternalBrandMappingUseCase searchExternalBrandMappingUseCase;
    private final ExternalBrandMappingQueryApiMapper mapper;

    public ExternalBrandMappingQueryController(
            SearchExternalBrandMappingUseCase searchExternalBrandMappingUseCase,
            ExternalBrandMappingQueryApiMapper mapper) {
        this.searchExternalBrandMappingUseCase = searchExternalBrandMappingUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "외부 브랜드 매핑 목록 검색", description = "외부 소스 ID에 해당하는 브랜드 매핑 목록을 검색합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<PageApiResponse<ExternalBrandMappingApiResponse>>>
            searchExternalBrandMappingsByOffset(
                    @Parameter(description = "외부 소스 ID", required = true)
                            @PathVariable(
                                    ExternalBrandMappingAdminEndpoints.PATH_EXTERNAL_SOURCE_ID)
                            Long externalSourceId,
                    @ParameterObject @Valid SearchExternalBrandMappingsApiRequest request) {
        ExternalBrandMappingPageResult pageResult =
                searchExternalBrandMappingUseCase.execute(
                        mapper.toSearchParams(externalSourceId, request));
        return ResponseEntity.ok(ApiResponse.of(mapper.toPageResponse(pageResult)));
    }
}
