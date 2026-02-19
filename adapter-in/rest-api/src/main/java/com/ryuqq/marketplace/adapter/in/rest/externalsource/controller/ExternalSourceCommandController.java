package com.ryuqq.marketplace.adapter.in.rest.externalsource.controller;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.externalsource.ExternalSourceAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.externalsource.dto.command.RegisterExternalSourceApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.externalsource.dto.command.UpdateExternalSourceApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.externalsource.dto.response.ExternalSourceIdApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.externalsource.mapper.ExternalSourceCommandApiMapper;
import com.ryuqq.marketplace.application.externalsource.dto.command.RegisterExternalSourceCommand;
import com.ryuqq.marketplace.application.externalsource.dto.command.UpdateExternalSourceCommand;
import com.ryuqq.marketplace.application.externalsource.port.in.command.RegisterExternalSourceUseCase;
import com.ryuqq.marketplace.application.externalsource.port.in.command.UpdateExternalSourceUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 외부 소스 커맨드 API 컨트롤러. */
@Tag(name = "외부 소스 관리", description = "외부 소스 관리 API (생성/수정)")
@RestController
@RequestMapping(ExternalSourceAdminEndpoints.EXTERNAL_SOURCES)
public class ExternalSourceCommandController {

    private final RegisterExternalSourceUseCase registerExternalSourceUseCase;
    private final UpdateExternalSourceUseCase updateExternalSourceUseCase;
    private final ExternalSourceCommandApiMapper mapper;

    public ExternalSourceCommandController(
            RegisterExternalSourceUseCase registerExternalSourceUseCase,
            UpdateExternalSourceUseCase updateExternalSourceUseCase,
            ExternalSourceCommandApiMapper mapper) {
        this.registerExternalSourceUseCase = registerExternalSourceUseCase;
        this.updateExternalSourceUseCase = updateExternalSourceUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "외부 소스 등록", description = "새로운 외부 소스를 등록합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "등록 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<ExternalSourceIdApiResponse>> registerExternalSource(
            @Valid @RequestBody RegisterExternalSourceApiRequest request) {

        RegisterExternalSourceCommand command = mapper.toCommand(request);
        Long externalSourceId = registerExternalSourceUseCase.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(ExternalSourceIdApiResponse.of(externalSourceId)));
    }

    @Operation(summary = "외부 소스 수정", description = "외부 소스 정보를 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "204",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "외부 소스를 찾을 수 없음")
    })
    @PatchMapping(ExternalSourceAdminEndpoints.EXTERNAL_SOURCE_ID)
    public ResponseEntity<Void> updateExternalSource(
            @Parameter(description = "외부 소스 ID", required = true)
                    @PathVariable(ExternalSourceAdminEndpoints.PATH_EXTERNAL_SOURCE_ID)
                    Long externalSourceId,
            @Valid @RequestBody UpdateExternalSourceApiRequest request) {

        UpdateExternalSourceCommand command = mapper.toCommand(externalSourceId, request);
        updateExternalSourceUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }
}
