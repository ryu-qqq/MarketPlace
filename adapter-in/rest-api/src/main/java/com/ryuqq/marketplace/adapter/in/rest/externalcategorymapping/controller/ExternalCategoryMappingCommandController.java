package com.ryuqq.marketplace.adapter.in.rest.externalcategorymapping.controller;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.externalcategorymapping.ExternalCategoryMappingAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.externalcategorymapping.dto.command.BatchRegisterExternalCategoryMappingApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.externalcategorymapping.dto.command.RegisterExternalCategoryMappingApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.externalcategorymapping.dto.command.UpdateExternalCategoryMappingApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.externalcategorymapping.dto.response.ExternalCategoryMappingIdApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.externalcategorymapping.mapper.ExternalCategoryMappingCommandApiMapper;
import com.ryuqq.marketplace.application.externalcategorymapping.dto.command.BatchRegisterExternalCategoryMappingCommand;
import com.ryuqq.marketplace.application.externalcategorymapping.dto.command.RegisterExternalCategoryMappingCommand;
import com.ryuqq.marketplace.application.externalcategorymapping.dto.command.UpdateExternalCategoryMappingCommand;
import com.ryuqq.marketplace.application.externalcategorymapping.port.in.command.BatchRegisterExternalCategoryMappingUseCase;
import com.ryuqq.marketplace.application.externalcategorymapping.port.in.command.RegisterExternalCategoryMappingUseCase;
import com.ryuqq.marketplace.application.externalcategorymapping.port.in.command.UpdateExternalCategoryMappingUseCase;
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

/** 외부 카테고리 매핑 커맨드 API 컨트롤러. */
@Tag(name = "외부 카테고리 매핑 관리", description = "외부 카테고리 매핑 관리 API (등록/수정)")
@RestController
@RequestMapping(ExternalCategoryMappingAdminEndpoints.CATEGORY_MAPPINGS)
public class ExternalCategoryMappingCommandController {

    private final RegisterExternalCategoryMappingUseCase registerExternalCategoryMappingUseCase;
    private final BatchRegisterExternalCategoryMappingUseCase
            batchRegisterExternalCategoryMappingUseCase;
    private final UpdateExternalCategoryMappingUseCase updateExternalCategoryMappingUseCase;
    private final ExternalCategoryMappingCommandApiMapper mapper;

    public ExternalCategoryMappingCommandController(
            RegisterExternalCategoryMappingUseCase registerExternalCategoryMappingUseCase,
            BatchRegisterExternalCategoryMappingUseCase batchRegisterExternalCategoryMappingUseCase,
            UpdateExternalCategoryMappingUseCase updateExternalCategoryMappingUseCase,
            ExternalCategoryMappingCommandApiMapper mapper) {
        this.registerExternalCategoryMappingUseCase = registerExternalCategoryMappingUseCase;
        this.batchRegisterExternalCategoryMappingUseCase =
                batchRegisterExternalCategoryMappingUseCase;
        this.updateExternalCategoryMappingUseCase = updateExternalCategoryMappingUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "외부 카테고리 매핑 등록", description = "외부 소스에 카테고리 매핑을 등록합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "등록 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<ExternalCategoryMappingIdApiResponse>>
            registerExternalCategoryMapping(
                    @Parameter(description = "외부 소스 ID", required = true)
                            @PathVariable(
                                    ExternalCategoryMappingAdminEndpoints.PATH_EXTERNAL_SOURCE_ID)
                            Long externalSourceId,
                    @Valid @RequestBody RegisterExternalCategoryMappingApiRequest request) {

        RegisterExternalCategoryMappingCommand command =
                mapper.toCommand(externalSourceId, request);
        Long id = registerExternalCategoryMappingUseCase.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(ExternalCategoryMappingIdApiResponse.of(id)));
    }

    @Operation(summary = "외부 카테고리 매핑 일괄 등록", description = "외부 소스에 카테고리 매핑을 일괄 등록합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "일괄 등록 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청")
    })
    @PostMapping(ExternalCategoryMappingAdminEndpoints.BATCH)
    public ResponseEntity<ApiResponse<List<Long>>> batchRegisterExternalCategoryMapping(
            @Parameter(description = "외부 소스 ID", required = true)
                    @PathVariable(ExternalCategoryMappingAdminEndpoints.PATH_EXTERNAL_SOURCE_ID)
                    Long externalSourceId,
            @Valid @RequestBody BatchRegisterExternalCategoryMappingApiRequest request) {

        BatchRegisterExternalCategoryMappingCommand command =
                mapper.toBatchCommand(externalSourceId, request);
        List<Long> ids = batchRegisterExternalCategoryMappingUseCase.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(ids));
    }

    @Operation(summary = "외부 카테고리 매핑 수정", description = "외부 카테고리 매핑 정보를 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "204",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "매핑을 찾을 수 없음")
    })
    @PatchMapping(ExternalCategoryMappingAdminEndpoints.CATEGORY_MAPPING_ID)
    public ResponseEntity<Void> updateExternalCategoryMapping(
            @Parameter(description = "외부 소스 ID", required = true)
                    @PathVariable(ExternalCategoryMappingAdminEndpoints.PATH_EXTERNAL_SOURCE_ID)
                    Long externalSourceId,
            @Parameter(description = "매핑 ID", required = true)
                    @PathVariable(ExternalCategoryMappingAdminEndpoints.PATH_ID)
                    Long id,
            @Valid @RequestBody UpdateExternalCategoryMappingApiRequest request) {

        UpdateExternalCategoryMappingCommand command = mapper.toCommand(id, request);
        updateExternalCategoryMappingUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }
}
