package com.ryuqq.marketplace.adapter.in.rest.externalmapping.controller;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.externalmapping.ExternalMappingAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.externalmapping.dto.command.ResolveExternalMappingApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.externalmapping.dto.response.ResolvedMappingApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.externalmapping.mapper.ExternalMappingApiMapper;
import com.ryuqq.marketplace.application.externalmapping.dto.query.ExternalMappingResolveQuery;
import com.ryuqq.marketplace.application.externalmapping.dto.response.ResolvedMappingResult;
import com.ryuqq.marketplace.application.externalmapping.port.in.query.ResolveExternalMappingUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 외부 매핑 통합 조회 API 컨트롤러. */
@Tag(name = "외부 매핑 통합 조회", description = "외부 코드로 내부 ID를 조회하는 API")
@RestController
@RequestMapping(ExternalMappingAdminEndpoints.EXTERNAL_MAPPINGS)
public class ExternalMappingResolveController {

    private final ResolveExternalMappingUseCase resolveExternalMappingUseCase;
    private final ExternalMappingApiMapper mapper;

    public ExternalMappingResolveController(
            ResolveExternalMappingUseCase resolveExternalMappingUseCase,
            ExternalMappingApiMapper mapper) {
        this.resolveExternalMappingUseCase = resolveExternalMappingUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "외부 매핑 통합 조회", description = "외부 소스 코드, 브랜드 코드, 카테고리 코드로 내부 ID를 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "매핑을 찾을 수 없음")
    })
    @PostMapping(ExternalMappingAdminEndpoints.RESOLVE)
    public ResponseEntity<ApiResponse<ResolvedMappingApiResponse>> resolveExternalMapping(
            @Valid @RequestBody ResolveExternalMappingApiRequest request) {

        ExternalMappingResolveQuery query = mapper.toQuery(request);
        ResolvedMappingResult result = resolveExternalMappingUseCase.execute(query);

        return ResponseEntity.ok(ApiResponse.of(mapper.toResponse(result)));
    }
}
