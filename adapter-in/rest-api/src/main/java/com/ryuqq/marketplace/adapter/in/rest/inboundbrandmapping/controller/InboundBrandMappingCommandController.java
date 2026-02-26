package com.ryuqq.marketplace.adapter.in.rest.inboundbrandmapping.controller;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.inboundbrandmapping.InboundBrandMappingAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.inboundbrandmapping.dto.command.BatchRegisterInboundBrandMappingApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundbrandmapping.dto.command.RegisterInboundBrandMappingApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundbrandmapping.dto.command.UpdateInboundBrandMappingApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundbrandmapping.dto.response.InboundBrandMappingIdApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.inboundbrandmapping.mapper.InboundBrandMappingCommandApiMapper;
import com.ryuqq.marketplace.application.inboundbrandmapping.dto.command.BatchRegisterInboundBrandMappingCommand;
import com.ryuqq.marketplace.application.inboundbrandmapping.dto.command.RegisterInboundBrandMappingCommand;
import com.ryuqq.marketplace.application.inboundbrandmapping.dto.command.UpdateInboundBrandMappingCommand;
import com.ryuqq.marketplace.application.inboundbrandmapping.port.in.command.BatchRegisterInboundBrandMappingUseCase;
import com.ryuqq.marketplace.application.inboundbrandmapping.port.in.command.RegisterInboundBrandMappingUseCase;
import com.ryuqq.marketplace.application.inboundbrandmapping.port.in.command.UpdateInboundBrandMappingUseCase;
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

/** 인바운드 브랜드 매핑 커맨드 API 컨트롤러. */
@Tag(name = "인바운드 브랜드 매핑 관리", description = "인바운드 브랜드 매핑 관리 API (등록/수정)")
@RestController
@RequestMapping(InboundBrandMappingAdminEndpoints.BRAND_MAPPINGS)
public class InboundBrandMappingCommandController {

    private final RegisterInboundBrandMappingUseCase registerInboundBrandMappingUseCase;
    private final BatchRegisterInboundBrandMappingUseCase batchRegisterInboundBrandMappingUseCase;
    private final UpdateInboundBrandMappingUseCase updateInboundBrandMappingUseCase;
    private final InboundBrandMappingCommandApiMapper mapper;

    public InboundBrandMappingCommandController(
            RegisterInboundBrandMappingUseCase registerInboundBrandMappingUseCase,
            BatchRegisterInboundBrandMappingUseCase batchRegisterInboundBrandMappingUseCase,
            UpdateInboundBrandMappingUseCase updateInboundBrandMappingUseCase,
            InboundBrandMappingCommandApiMapper mapper) {
        this.registerInboundBrandMappingUseCase = registerInboundBrandMappingUseCase;
        this.batchRegisterInboundBrandMappingUseCase = batchRegisterInboundBrandMappingUseCase;
        this.updateInboundBrandMappingUseCase = updateInboundBrandMappingUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "인바운드 브랜드 매핑 등록", description = "외부 소스에 브랜드 매핑을 등록합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "등록 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<InboundBrandMappingIdApiResponse>>
            registerInboundBrandMapping(
                    @Parameter(description = "외부 소스 ID", required = true) @RequestParam
                            Long inboundSourceId,
                    @Valid @RequestBody RegisterInboundBrandMappingApiRequest request) {

        RegisterInboundBrandMappingCommand command = mapper.toCommand(inboundSourceId, request);
        Long id = registerInboundBrandMappingUseCase.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(InboundBrandMappingIdApiResponse.of(id)));
    }

    @Operation(summary = "인바운드 브랜드 매핑 일괄 등록", description = "외부 소스에 브랜드 매핑을 일괄 등록합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "일괄 등록 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청")
    })
    @PostMapping(InboundBrandMappingAdminEndpoints.BATCH)
    public ResponseEntity<ApiResponse<List<Long>>> batchRegisterInboundBrandMapping(
            @Parameter(description = "외부 소스 ID", required = true) @RequestParam
                    Long inboundSourceId,
            @Valid @RequestBody BatchRegisterInboundBrandMappingApiRequest request) {

        BatchRegisterInboundBrandMappingCommand command =
                mapper.toBatchCommand(inboundSourceId, request);
        List<Long> ids = batchRegisterInboundBrandMappingUseCase.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(ids));
    }

    @Operation(summary = "인바운드 브랜드 매핑 수정", description = "인바운드 브랜드 매핑 정보를 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "204",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "매핑을 찾을 수 없음")
    })
    @PatchMapping(InboundBrandMappingAdminEndpoints.BRAND_MAPPING_ID)
    public ResponseEntity<Void> updateInboundBrandMapping(
            @Parameter(description = "매핑 ID", required = true)
                    @PathVariable(InboundBrandMappingAdminEndpoints.PATH_ID)
                    Long id,
            @Valid @RequestBody UpdateInboundBrandMappingApiRequest request) {

        UpdateInboundBrandMappingCommand command = mapper.toCommand(id, request);
        updateInboundBrandMappingUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }
}
