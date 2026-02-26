package com.ryuqq.marketplace.adapter.in.rest.inboundcategorymapping.controller;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.inboundcategorymapping.InboundCategoryMappingAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.inboundcategorymapping.dto.query.SearchInboundCategoryMappingsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundcategorymapping.dto.response.InboundCategoryMappingApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.inboundcategorymapping.mapper.InboundCategoryMappingQueryApiMapper;
import com.ryuqq.marketplace.application.inboundcategorymapping.dto.response.InboundCategoryMappingPageResult;
import com.ryuqq.marketplace.application.inboundcategorymapping.port.in.query.SearchInboundCategoryMappingUseCase;
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

/** 인바운드 카테고리 매핑 조회 API 컨트롤러. */
@Tag(name = "인바운드 카테고리 매핑 조회", description = "인바운드 카테고리 매핑 조회 API")
@RestController
@RequestMapping(InboundCategoryMappingAdminEndpoints.CATEGORY_MAPPINGS)
public class InboundCategoryMappingQueryController {

    private final SearchInboundCategoryMappingUseCase searchInboundCategoryMappingUseCase;
    private final InboundCategoryMappingQueryApiMapper mapper;

    public InboundCategoryMappingQueryController(
            SearchInboundCategoryMappingUseCase searchInboundCategoryMappingUseCase,
            InboundCategoryMappingQueryApiMapper mapper) {
        this.searchInboundCategoryMappingUseCase = searchInboundCategoryMappingUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "인바운드 카테고리 매핑 목록 검색", description = "외부 소스 ID에 해당하는 카테고리 매핑 목록을 검색합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<PageApiResponse<InboundCategoryMappingApiResponse>>>
            searchInboundCategoryMappingsByOffset(
                    @Parameter(description = "외부 소스 ID", required = true) @RequestParam
                            Long inboundSourceId,
                    @ParameterObject @Valid SearchInboundCategoryMappingsApiRequest request) {
        InboundCategoryMappingPageResult pageResult =
                searchInboundCategoryMappingUseCase.execute(
                        mapper.toSearchParams(inboundSourceId, request));
        return ResponseEntity.ok(ApiResponse.of(mapper.toPageResponse(pageResult)));
    }
}
