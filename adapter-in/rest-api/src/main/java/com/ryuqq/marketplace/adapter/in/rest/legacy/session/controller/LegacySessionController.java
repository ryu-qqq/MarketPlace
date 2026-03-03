package com.ryuqq.marketplace.adapter.in.rest.legacy.session.controller;

import static com.ryuqq.marketplace.adapter.in.rest.legacy.session.LegacySessionEndpoints.IMAGE_PRESIGNED;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.legacy.common.dto.LegacyApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.session.dto.request.LegacyPresignedUrlApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.session.dto.response.LegacyPresignedUrlApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.session.mapper.LegacySessionCommandApiMapper;
import com.ryuqq.marketplace.application.legacy.session.dto.command.LegacyGetPresignedUrlCommand;
import com.ryuqq.marketplace.application.legacy.session.dto.response.LegacyPresignedUrlResult;
import com.ryuqq.marketplace.application.legacy.session.port.in.command.LegacyGetPresignedUrlUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 세토프 어드민용 레거시 이미지 업로드 세션 컨트롤러.
 *
 * <p>세토프 어드민의 ImageController.getContent와 호환되는 Presigned URL 발급 엔드포인트를 제공합니다.
 */
@Tag(name = "세토프 어드민용 레거시 - 이미지 업로드", description = "세토프 어드민 호환 Presigned URL 발급 엔드포인트.")
@RestController
public class LegacySessionController {

    private final LegacyGetPresignedUrlUseCase legacyGetPresignedUrlUseCase;
    private final LegacySessionCommandApiMapper mapper;

    public LegacySessionController(
            LegacyGetPresignedUrlUseCase legacyGetPresignedUrlUseCase,
            LegacySessionCommandApiMapper mapper) {
        this.legacyGetPresignedUrlUseCase = legacyGetPresignedUrlUseCase;
        this.mapper = mapper;
    }

    @Operation(
            summary = "레거시 Presigned URL 발급",
            description = "세토프 어드민 호환 Presigned URL을 발급합니다. 클라이언트가 직접 S3에 업로드할 수 있습니다.")
    @PreAuthorize("@access.authenticated()")
    @RequirePermission(value = "legacy:image:write", description = "레거시 Presigned URL 발급")
    @PostMapping(IMAGE_PRESIGNED)
    public ResponseEntity<LegacyApiResponse<LegacyPresignedUrlApiResponse>> getPresignedUrl(
            @Valid @RequestBody LegacyPresignedUrlApiRequest request) {
        LegacyGetPresignedUrlCommand command = mapper.toCommand(request);
        LegacyPresignedUrlResult result = legacyGetPresignedUrlUseCase.execute(command);
        return ResponseEntity.ok(LegacyApiResponse.success(mapper.toApiResponse(result)));
    }
}
