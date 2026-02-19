package com.ryuqq.marketplace.adapter.in.rest.externalcategorymapping.controller;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.externalcategorymapping.ExternalCategoryMappingAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.externalcategorymapping.dto.query.SearchExternalCategoryMappingsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.externalcategorymapping.dto.response.ExternalCategoryMappingApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.externalcategorymapping.mapper.ExternalCategoryMappingQueryApiMapper;
import com.ryuqq.marketplace.application.externalcategorymapping.dto.response.ExternalCategoryMappingPageResult;
import com.ryuqq.marketplace.application.externalcategorymapping.port.in.query.SearchExternalCategoryMappingUseCase;
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

/** 외부 카테고리 매핑 조회 API 컨트롤러. */
@Tag(name = "외부 카테고리 매핑 조회", description = "외부 카테고리 매핑 조회 API")
@RestController
@RequestMapping(ExternalCategoryMappingAdminEndpoints.CATEGORY_MAPPINGS)
public class ExternalCategoryMappingQueryController {

    private final SearchExternalCategoryMappingUseCase searchExternalCategoryMappingUseCase;
    private final ExternalCategoryMappingQueryApiMapper mapper;

    public ExternalCategoryMappingQueryController(
            SearchExternalCategoryMappingUseCase searchExternalCategoryMappingUseCase,
            ExternalCategoryMappingQueryApiMapper mapper) {
        this.searchExternalCategoryMappingUseCase = searchExternalCategoryMappingUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "외부 카테고리 매핑 목록 검색", description = "외부 소스 ID에 해당하는 카테고리 매핑 목록을 검색합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<PageApiResponse<ExternalCategoryMappingApiResponse>>>
            searchExternalCategoryMappingsByOffset(
                    @Parameter(description = "외부 소스 ID", required = true)
                            @PathVariable(
                                    ExternalCategoryMappingAdminEndpoints.PATH_EXTERNAL_SOURCE_ID)
                            Long externalSourceId,
                    @ParameterObject @Valid SearchExternalCategoryMappingsApiRequest request) {
        ExternalCategoryMappingPageResult pageResult =
                searchExternalCategoryMappingUseCase.execute(
                        mapper.toSearchParams(externalSourceId, request));
        return ResponseEntity.ok(ApiResponse.of(mapper.toPageResponse(pageResult)));
    }
}
