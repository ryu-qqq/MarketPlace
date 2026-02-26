package com.ryuqq.marketplace.adapter.in.rest.imagevariant.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.imagevariant.ImageVariantAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.imagevariant.dto.command.RequestImageTransformApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.imagevariant.mapper.ImageVariantCommandApiMapper;
import com.ryuqq.marketplace.application.imagetransform.port.in.command.RequestImageTransformUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ImageVariantCommandController - 이미지 Variant 관리 API.
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
@Tag(name = "이미지 변형", description = "상품 이미지의 해상도별 변형(배리언트) 관리. 소/중/대/원본 WebP 변환 요청 및 조회를 제공합니다.")
@RestController
@RequestMapping(ImageVariantAdminEndpoints.IMAGE_VARIANTS)
public class ImageVariantCommandController {

    private final RequestImageTransformUseCase requestImageTransformUseCase;
    private final ImageVariantCommandApiMapper mapper;

    public ImageVariantCommandController(
            RequestImageTransformUseCase requestImageTransformUseCase,
            ImageVariantCommandApiMapper mapper) {
        this.requestImageTransformUseCase = requestImageTransformUseCase;
        this.mapper = mapper;
    }

    /**
     * 수동 이미지 변환 요청 API.
     *
     * <p>상품 그룹의 이미지에 대한 Variant 변환을 수동으로 요청합니다.
     *
     * @param productGroupId 상품 그룹 ID
     * @return 빈 응답 (202 Accepted)
     */
    @Operation(summary = "수동 변환 요청", description = "상품 그룹 이미지의 해상도별 변형(배리언트) 생성을 수동으로 요청합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "202",
                description = "변환 요청 접수"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "상품 그룹을 찾을 수 없음")
    })
    @PreAuthorize("@access.isSellerOwnerOr(#productGroupId, 'product-group:write')")
    @RequirePermission(value = "product-group:write", description = "이미지 변형 변환 요청")
    @PostMapping(ImageVariantAdminEndpoints.TRANSFORM_REQUEST)
    public ResponseEntity<Void> requestTransform(
            @Parameter(description = "상품 그룹 ID", required = true)
                    @PathVariable(ImageVariantAdminEndpoints.PATH_PRODUCT_GROUP_ID)
                    Long productGroupId,
            @RequestBody(required = false) RequestImageTransformApiRequest request) {

        requestImageTransformUseCase.execute(mapper.toCommand(productGroupId, request));

        return ResponseEntity.accepted().build();
    }
}
