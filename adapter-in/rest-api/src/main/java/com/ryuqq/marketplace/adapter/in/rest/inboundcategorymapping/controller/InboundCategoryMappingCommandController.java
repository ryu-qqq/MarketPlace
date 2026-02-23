package com.ryuqq.marketplace.adapter.in.rest.inboundcategorymapping.controller;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.inboundcategorymapping.InboundCategoryMappingAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.inboundcategorymapping.dto.command.BatchRegisterInboundCategoryMappingApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundcategorymapping.dto.command.RegisterInboundCategoryMappingApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundcategorymapping.dto.command.UpdateInboundCategoryMappingApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundcategorymapping.dto.response.InboundCategoryMappingIdApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.inboundcategorymapping.mapper.InboundCategoryMappingCommandApiMapper;
import com.ryuqq.marketplace.application.inboundcategorymapping.dto.command.BatchRegisterInboundCategoryMappingCommand;
import com.ryuqq.marketplace.application.inboundcategorymapping.dto.command.RegisterInboundCategoryMappingCommand;
import com.ryuqq.marketplace.application.inboundcategorymapping.dto.command.UpdateInboundCategoryMappingCommand;
import com.ryuqq.marketplace.application.inboundcategorymapping.port.in.command.BatchRegisterInboundCategoryMappingUseCase;
import com.ryuqq.marketplace.application.inboundcategorymapping.port.in.command.RegisterInboundCategoryMappingUseCase;
import com.ryuqq.marketplace.application.inboundcategorymapping.port.in.command.UpdateInboundCategoryMappingUseCase;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 인바운드 카테고리 매핑 커맨드 API 컨트롤러. */
@Tag(name = "인바운드 카테고리 매핑 관리", description = "인바운드 카테고리 매핑 관리 API (등록/수정)")
@RestController
@RequestMapping(InboundCategoryMappingAdminEndpoints.CATEGORY_MAPPINGS)
public class InboundCategoryMappingCommandController {

    private final RegisterInboundCategoryMappingUseCase registerInboundCategoryMappingUseCase;
    private final BatchRegisterInboundCategoryMappingUseCase
            batchRegisterInboundCategoryMappingUseCase;
    private final UpdateInboundCategoryMappingUseCase updateInboundCategoryMappingUseCase;
    private final InboundCategoryMappingCommandApiMapper mapper;

    public InboundCategoryMappingCommandController(
            RegisterInboundCategoryMappingUseCase registerInboundCategoryMappingUseCase,
            BatchRegisterInboundCategoryMappingUseCase batchRegisterInboundCategoryMappingUseCase,
            UpdateInboundCategoryMappingUseCase updateInboundCategoryMappingUseCase,
            InboundCategoryMappingCommandApiMapper mapper) {
        this.registerInboundCategoryMappingUseCase = registerInboundCategoryMappingUseCase;
        this.batchRegisterInboundCategoryMappingUseCase =
                batchRegisterInboundCategoryMappingUseCase;
        this.updateInboundCategoryMappingUseCase = updateInboundCategoryMappingUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "인바운드 카테고리 매핑 등록", description = "외부 소스에 카테고리 매핑을 등록합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "등록 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<InboundCategoryMappingIdApiResponse>>
            registerInboundCategoryMapping(
                    @Parameter(description = "외부 소스 ID", required = true) @RequestParam
                            Long inboundSourceId,
                    @Valid @RequestBody RegisterInboundCategoryMappingApiRequest request) {

        RegisterInboundCategoryMappingCommand command = mapper.toCommand(inboundSourceId, request);
        Long id = registerInboundCategoryMappingUseCase.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(InboundCategoryMappingIdApiResponse.of(id)));
    }

    @Operation(summary = "인바운드 카테고리 매핑 일괄 등록", description = "외부 소스에 카테고리 매핑을 일괄 등록합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "일괄 등록 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청")
    })
    @PostMapping(InboundCategoryMappingAdminEndpoints.BATCH)
    public ResponseEntity<ApiResponse<List<Long>>> batchRegisterInboundCategoryMapping(
            @Parameter(description = "외부 소스 ID", required = true) @RequestParam
                    Long inboundSourceId,
            @Valid @RequestBody BatchRegisterInboundCategoryMappingApiRequest request) {

        BatchRegisterInboundCategoryMappingCommand command =
                mapper.toBatchCommand(inboundSourceId, request);
        List<Long> ids = batchRegisterInboundCategoryMappingUseCase.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(ids));
    }

    @Operation(summary = "인바운드 카테고리 매핑 수정", description = "인바운드 카테고리 매핑 정보를 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "204",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "매핑을 찾을 수 없음")
    })
    @PatchMapping(InboundCategoryMappingAdminEndpoints.CATEGORY_MAPPING_ID)
    public ResponseEntity<Void> updateInboundCategoryMapping(
            @Parameter(description = "매핑 ID", required = true)
                    @PathVariable(InboundCategoryMappingAdminEndpoints.PATH_ID)
                    Long id,
            @Valid @RequestBody UpdateInboundCategoryMappingApiRequest request) {

        UpdateInboundCategoryMappingCommand command = mapper.toCommand(id, request);
        updateInboundCategoryMappingUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }
}
