package com.ryuqq.marketplace.adapter.in.rest.session.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.session.UploadSessionAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.session.dto.command.CompleteUploadSessionApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.session.dto.command.GenerateUploadUrlApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.session.dto.command.LegacyImagePresignedApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.session.dto.response.GenerateUploadUrlApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.session.dto.response.LegacyImagePresignedApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.session.mapper.LegacyImagePresignedApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.session.mapper.UploadSessionCommandApiMapper;
import com.ryuqq.marketplace.application.common.dto.response.PresignedUrlResponse;
import com.ryuqq.marketplace.application.uploadsession.port.in.command.CompleteUploadSessionUseCase;
import com.ryuqq.marketplace.application.uploadsession.port.in.command.GenerateUploadUrlUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * UploadSessionCommandController - 업로드 세션 관리 API.
 *
 * <p>API-CTR-001: Controller는 @RestController로 정의.
 *
 * <p>API-CTR-003: UseCase(Port-In) 인터페이스 의존.
 *
 * <p>API-CTR-010: CQRS Controller 분리.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag(name = "Upload Session", description = "파일 업로드 세션 관리")
@RestController
@RequestMapping(UploadSessionAdminEndpoints.UPLOAD_SESSIONS)
public class UploadSessionCommandController {

    private final GenerateUploadUrlUseCase generateUploadUrlUseCase;
    private final CompleteUploadSessionUseCase completeUploadSessionUseCase;
    private final UploadSessionCommandApiMapper mapper;
    private final LegacyImagePresignedApiMapper legacyMapper;

    public UploadSessionCommandController(
            GenerateUploadUrlUseCase generateUploadUrlUseCase,
            CompleteUploadSessionUseCase completeUploadSessionUseCase,
            UploadSessionCommandApiMapper mapper,
            LegacyImagePresignedApiMapper legacyMapper) {
        this.generateUploadUrlUseCase = generateUploadUrlUseCase;
        this.completeUploadSessionUseCase = completeUploadSessionUseCase;
        this.mapper = mapper;
        this.legacyMapper = legacyMapper;
    }

    /**
     * Presigned URL 발급 API.
     *
     * <p>클라이언트 사이드 업로드를 위한 Presigned URL을 발급합니다.
     *
     * @param request 업로드 URL 발급 요청
     * @return Presigned URL 정보 (201 Created)
     */
    @Operation(
            summary = "Presigned URL 발급",
            description = "클라이언트 사이드 업로드를 위한 Presigned URL을 발급합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "Presigned URL 발급 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청")
    })
    @PreAuthorize("@access.authenticated()")
    @RequirePermission(value = "file:write", description = "파일 업로드 URL 발급")
    @PostMapping
    public ResponseEntity<ApiResponse<GenerateUploadUrlApiResponse>> generateUploadUrl(
            @Valid @RequestBody GenerateUploadUrlApiRequest request) {

        PresignedUrlResponse result =
                generateUploadUrlUseCase.execute(mapper.toPresignedUploadUrlRequest(request));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(mapper.toApiResponse(result)));
    }

    /**
     * 업로드 세션 완료 처리 API.
     *
     * <p>클라이언트가 S3에 파일 업로드를 완료한 후 호출합니다.
     *
     * @param sessionId 업로드 세션 ID
     * @param request 완료 처리 요청
     * @return 빈 응답 (200 OK)
     */
    @Operation(summary = "업로드 완료 처리", description = "클라이언트가 S3에 파일 업로드를 완료한 후 호출합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "완료 처리 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "세션을 찾을 수 없음")
    })
    @PreAuthorize("@access.authenticated()")
    @RequirePermission(value = "file:write", description = "파일 업로드 완료 처리")
    @PostMapping(UploadSessionAdminEndpoints.COMPLETE)
    public ResponseEntity<ApiResponse<Void>> completeUploadSession(
            @Parameter(description = "업로드 세션 ID", required = true)
                    @PathVariable(UploadSessionAdminEndpoints.PATH_SESSION_ID)
                    String sessionId,
            @Valid @RequestBody CompleteUploadSessionApiRequest request) {

        completeUploadSessionUseCase.execute(mapper.toCompleteCommand(sessionId, request));

        return ResponseEntity.ok(ApiResponse.of());
    }

    /**
     * 레거시 호환 Presigned URL 발급 API.
     *
     * <p>프론트 OMS가 {@code /api/v1/image/presigned}로 호출하는 레거시 호환 엔드포인트.
     */
    @Operation(
            summary = "레거시 호환 Presigned URL 발급",
            description = "프론트 OMS 호환 Presigned URL을 발급합니다.")
    @PreAuthorize("@access.authenticated()")
    @PostMapping(UploadSessionAdminEndpoints.LEGACY_IMAGE_PRESIGNED)
    public ResponseEntity<ApiResponse<LegacyImagePresignedApiResponse>> legacyGetPresignedUrl(
            @Valid @RequestBody LegacyImagePresignedApiRequest request) {

        PresignedUrlResponse result = generateUploadUrlUseCase.execute(legacyMapper.toCommand(request));
        return ResponseEntity.ok(ApiResponse.of(legacyMapper.toResponse(result)));
    }
}
