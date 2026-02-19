package com.ryuqq.marketplace.adapter.in.rest.externalsource.controller;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.externalsource.ExternalSourceAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.externalsource.dto.query.SearchExternalSourcesApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.externalsource.dto.response.ExternalSourceApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.externalsource.mapper.ExternalSourceQueryApiMapper;
import com.ryuqq.marketplace.application.externalsource.dto.response.ExternalSourcePageResult;
import com.ryuqq.marketplace.application.externalsource.dto.response.ExternalSourceResult;
import com.ryuqq.marketplace.application.externalsource.port.in.query.GetExternalSourceUseCase;
import com.ryuqq.marketplace.application.externalsource.port.in.query.SearchExternalSourceUseCase;
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

/** 외부 소스 조회 API 컨트롤러. */
@Tag(name = "외부 소스 조회", description = "외부 소스 조회 API")
@RestController
@RequestMapping(ExternalSourceAdminEndpoints.EXTERNAL_SOURCES)
public class ExternalSourceQueryController {

    private final GetExternalSourceUseCase getExternalSourceUseCase;
    private final SearchExternalSourceUseCase searchExternalSourceUseCase;
    private final ExternalSourceQueryApiMapper mapper;

    public ExternalSourceQueryController(
            GetExternalSourceUseCase getExternalSourceUseCase,
            SearchExternalSourceUseCase searchExternalSourceUseCase,
            ExternalSourceQueryApiMapper mapper) {
        this.getExternalSourceUseCase = getExternalSourceUseCase;
        this.searchExternalSourceUseCase = searchExternalSourceUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "외부 소스 단건 조회", description = "외부 소스 ID로 상세 정보를 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "외부 소스를 찾을 수 없음")
    })
    @GetMapping(ExternalSourceAdminEndpoints.EXTERNAL_SOURCE_ID)
    public ResponseEntity<ApiResponse<ExternalSourceApiResponse>> getExternalSource(
            @Parameter(description = "외부 소스 ID", required = true)
                    @PathVariable(ExternalSourceAdminEndpoints.PATH_EXTERNAL_SOURCE_ID)
                    Long externalSourceId) {
        ExternalSourceResult result = getExternalSourceUseCase.execute(externalSourceId);
        ExternalSourceApiResponse response = mapper.toResponse(result);
        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @Operation(summary = "외부 소스 목록 검색", description = "조건에 따라 외부 소스 목록을 검색합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<PageApiResponse<ExternalSourceApiResponse>>>
            searchExternalSourcesByOffset(
                    @ParameterObject @Valid SearchExternalSourcesApiRequest request) {
        ExternalSourcePageResult pageResult =
                searchExternalSourceUseCase.execute(mapper.toSearchParams(request));
        return ResponseEntity.ok(ApiResponse.of(mapper.toPageResponse(pageResult)));
    }
}
