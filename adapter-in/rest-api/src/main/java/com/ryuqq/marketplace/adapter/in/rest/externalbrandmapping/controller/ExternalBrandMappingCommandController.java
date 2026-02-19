package com.ryuqq.marketplace.adapter.in.rest.externalbrandmapping.controller;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.externalbrandmapping.ExternalBrandMappingAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.externalbrandmapping.dto.command.BatchRegisterExternalBrandMappingApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.externalbrandmapping.dto.command.RegisterExternalBrandMappingApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.externalbrandmapping.dto.command.UpdateExternalBrandMappingApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.externalbrandmapping.dto.response.ExternalBrandMappingIdApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.externalbrandmapping.mapper.ExternalBrandMappingCommandApiMapper;
import com.ryuqq.marketplace.application.externalbrandmapping.dto.command.BatchRegisterExternalBrandMappingCommand;
import com.ryuqq.marketplace.application.externalbrandmapping.dto.command.RegisterExternalBrandMappingCommand;
import com.ryuqq.marketplace.application.externalbrandmapping.dto.command.UpdateExternalBrandMappingCommand;
import com.ryuqq.marketplace.application.externalbrandmapping.port.in.command.BatchRegisterExternalBrandMappingUseCase;
import com.ryuqq.marketplace.application.externalbrandmapping.port.in.command.RegisterExternalBrandMappingUseCase;
import com.ryuqq.marketplace.application.externalbrandmapping.port.in.command.UpdateExternalBrandMappingUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 외부 브랜드 매핑 커맨드 API 컨트롤러. */
@Tag(name = "외부 브랜드 매핑 관리", description = "외부 브랜드 매핑 관리 API (등록/수정)")
@RestController
@RequestMapping(ExternalBrandMappingAdminEndpoints.BRAND_MAPPINGS)
public class ExternalBrandMappingCommandController {

    private final RegisterExternalBrandMappingUseCase registerExternalBrandMappingUseCase;
    private final BatchRegisterExternalBrandMappingUseCase batchRegisterExternalBrandMappingUseCase;
    private final UpdateExternalBrandMappingUseCase updateExternalBrandMappingUseCase;
    private final ExternalBrandMappingCommandApiMapper mapper;

    public ExternalBrandMappingCommandController(
            RegisterExternalBrandMappingUseCase registerExternalBrandMappingUseCase,
            BatchRegisterExternalBrandMappingUseCase batchRegisterExternalBrandMappingUseCase,
            UpdateExternalBrandMappingUseCase updateExternalBrandMappingUseCase,
            ExternalBrandMappingCommandApiMapper mapper) {
        this.registerExternalBrandMappingUseCase = registerExternalBrandMappingUseCase;
        this.batchRegisterExternalBrandMappingUseCase = batchRegisterExternalBrandMappingUseCase;
        this.updateExternalBrandMappingUseCase = updateExternalBrandMappingUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "외부 브랜드 매핑 등록", description = "외부 소스에 브랜드 매핑을 등록합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "등록 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<ExternalBrandMappingIdApiResponse>>
            registerExternalBrandMapping(
                    @Parameter(description = "외부 소스 ID", required = true)
                            @PathVariable(
                                    ExternalBrandMappingAdminEndpoints.PATH_EXTERNAL_SOURCE_ID)
                            Long externalSourceId,
                    @Valid @RequestBody RegisterExternalBrandMappingApiRequest request) {

        RegisterExternalBrandMappingCommand command = mapper.toCommand(externalSourceId, request);
        Long id = registerExternalBrandMappingUseCase.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(ExternalBrandMappingIdApiResponse.of(id)));
    }

    @Operation(summary = "외부 브랜드 매핑 일괄 등록", description = "외부 소스에 브랜드 매핑을 일괄 등록합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "일괄 등록 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청")
    })
    @PostMapping(ExternalBrandMappingAdminEndpoints.BATCH)
    public ResponseEntity<ApiResponse<List<Long>>> batchRegisterExternalBrandMapping(
            @Parameter(description = "외부 소스 ID", required = true)
                    @PathVariable(ExternalBrandMappingAdminEndpoints.PATH_EXTERNAL_SOURCE_ID)
                    Long externalSourceId,
            @Valid @RequestBody BatchRegisterExternalBrandMappingApiRequest request) {

        BatchRegisterExternalBrandMappingCommand command =
                mapper.toBatchCommand(externalSourceId, request);
        List<Long> ids = batchRegisterExternalBrandMappingUseCase.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(ids));
    }

    @Operation(summary = "외부 브랜드 매핑 수정", description = "외부 브랜드 매핑 정보를 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "204",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "매핑을 찾을 수 없음")
    })
    @PatchMapping(ExternalBrandMappingAdminEndpoints.BRAND_MAPPING_ID)
    public ResponseEntity<Void> updateExternalBrandMapping(
            @Parameter(description = "외부 소스 ID", required = true)
                    @PathVariable(ExternalBrandMappingAdminEndpoints.PATH_EXTERNAL_SOURCE_ID)
                    Long externalSourceId,
            @Parameter(description = "매핑 ID", required = true)
                    @PathVariable(ExternalBrandMappingAdminEndpoints.PATH_ID)
                    Long id,
            @Valid @RequestBody UpdateExternalBrandMappingApiRequest request) {

        UpdateExternalBrandMappingCommand command = mapper.toCommand(id, request);
        updateExternalBrandMappingUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }
}
