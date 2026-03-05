package com.ryuqq.marketplace.adapter.in.rest.imagetransform.controller;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.imagetransform.ImageTransformPublicEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.imagetransform.dto.request.ImageTransformCallbackApiRequest;
import com.ryuqq.marketplace.application.imagetransform.dto.command.CompleteImageTransformCallbackCommand;
import com.ryuqq.marketplace.application.imagetransform.port.in.command.CompleteImageTransformCallbackUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * ImageTransformPublicCommandController - 이미지 변환 공개 커맨드 API.
 *
 * <p>FileFlow 변환 완료 콜백을 수신합니다. 인증 없이 접근 가능한 공개 엔드포인트.
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
@Tag(name = "이미지 변환 콜백", description = "FileFlow 이미지 변환 콜백 API")
@RestController
public class ImageTransformPublicCommandController {

    private final CompleteImageTransformCallbackUseCase completeCallbackUseCase;

    public ImageTransformPublicCommandController(
            CompleteImageTransformCallbackUseCase completeCallbackUseCase) {
        this.completeCallbackUseCase = completeCallbackUseCase;
    }

    @Operation(summary = "이미지 변환 콜백", description = "FileFlow 변환 완료 시 콜백으로 호출됩니다.")
    @PostMapping(ImageTransformPublicEndpoints.CALLBACK)
    public ResponseEntity<ApiResponse<Void>> handleCallback(
            @RequestBody ImageTransformCallbackApiRequest request) {
        CompleteImageTransformCallbackCommand command =
                new CompleteImageTransformCallbackCommand(
                        request.transformRequestId(),
                        request.status(),
                        request.resultAssetId(),
                        request.resultCdnUrl(),
                        request.width(),
                        request.height(),
                        request.errorMessage());
        completeCallbackUseCase.execute(command);
        return ResponseEntity.ok(ApiResponse.of(null));
    }
}
