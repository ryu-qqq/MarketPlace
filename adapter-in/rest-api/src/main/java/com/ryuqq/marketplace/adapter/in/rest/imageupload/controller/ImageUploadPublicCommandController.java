package com.ryuqq.marketplace.adapter.in.rest.imageupload.controller;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.imageupload.ImageUploadPublicEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.imageupload.dto.request.ImageUploadCallbackApiRequest;
import com.ryuqq.marketplace.application.imageupload.dto.command.CompleteImageUploadCallbackCommand;
import com.ryuqq.marketplace.application.imageupload.port.in.command.CompleteImageUploadCallbackUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * ImageUploadPublicCommandController - 이미지 업로드 공개 커맨드 API.
 *
 * <p>FileFlow 다운로드 완료 콜백을 수신합니다. 인증 없이 접근 가능한 공개 엔드포인트.
 *
 * <p>API-CTR-001: @RestController 어노테이션 필수.
 *
 * <p>API-CTR-003: UseCase(Port-In) 인터페이스 의존.
 *
 * <p>API-CTR-004: ResponseEntity + ApiResponse 래핑.
 *
 * <p>API-CTR-005: Controller @Transactional 금지.
 *
 * <p>API-CTR-007: Controller 비즈니스 로직 금지.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag(name = "이미지 업로드 콜백", description = "FileFlow 이미지 업로드 콜백 API")
@RestController
public class ImageUploadPublicCommandController {

    private final CompleteImageUploadCallbackUseCase completeCallbackUseCase;

    public ImageUploadPublicCommandController(
            CompleteImageUploadCallbackUseCase completeCallbackUseCase) {
        this.completeCallbackUseCase = completeCallbackUseCase;
    }

    @Operation(
            summary = "이미지 업로드 콜백",
            description = "FileFlow 다운로드 태스크 완료 시 콜백으로 호출됩니다.")
    @PostMapping(ImageUploadPublicEndpoints.CALLBACK)
    public ResponseEntity<ApiResponse<Void>> handleCallback(
            @RequestBody ImageUploadCallbackApiRequest request) {
        CompleteImageUploadCallbackCommand command =
                new CompleteImageUploadCallbackCommand(
                        request.downloadTaskId(),
                        request.s3Key(),
                        request.status(),
                        request.errorMessage());
        completeCallbackUseCase.execute(command);
        return ResponseEntity.ok(ApiResponse.of(null));
    }
}
