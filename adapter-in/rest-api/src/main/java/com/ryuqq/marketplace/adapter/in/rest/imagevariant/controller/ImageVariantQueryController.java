package com.ryuqq.marketplace.adapter.in.rest.imagevariant.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.imagevariant.ImageVariantAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.imagevariant.dto.response.ImageVariantApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.imagevariant.mapper.ImageVariantQueryApiMapper;
import com.ryuqq.marketplace.application.imagevariant.dto.response.ImageVariantResult;
import com.ryuqq.marketplace.application.imagevariant.port.in.query.GetImageVariantsByImageIdUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ImageVariantQueryController - 이미지 Variant 조회 API.
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
@Tag(name = "Image Variant", description = "이미지 Variant 조회")
@RestController
@RequestMapping(ImageVariantAdminEndpoints.IMAGE_VARIANTS)
public class ImageVariantQueryController {

    private final GetImageVariantsByImageIdUseCase getImageVariantsByImageIdUseCase;
    private final ImageVariantQueryApiMapper mapper;

    public ImageVariantQueryController(
            GetImageVariantsByImageIdUseCase getImageVariantsByImageIdUseCase,
            ImageVariantQueryApiMapper mapper) {
        this.getImageVariantsByImageIdUseCase = getImageVariantsByImageIdUseCase;
        this.mapper = mapper;
    }

    /**
     * 특정 이미지의 Variant 목록 조회 API.
     *
     * <p>특정 이미지 ID에 대한 모든 Variant를 조회합니다.
     *
     * @param productGroupId 상품 그룹 ID
     * @param imageId 이미지 ID
     * @return Variant 목록 응답
     */
    @Operation(summary = "Variant 목록 조회", description = "특정 이미지의 Variant 목록을 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "이미지를 찾을 수 없음")
    })
    @PreAuthorize("@access.isSellerOwnerOr(#productGroupId, 'product-group:read')")
    @RequirePermission(value = "product-group:read", description = "이미지 Variant 목록 조회")
    @GetMapping(ImageVariantAdminEndpoints.VARIANTS)
    public ResponseEntity<ApiResponse<List<ImageVariantApiResponse>>> getVariantsByImageId(
            @Parameter(description = "상품 그룹 ID", required = true)
                    @PathVariable(ImageVariantAdminEndpoints.PATH_PRODUCT_GROUP_ID)
                    Long productGroupId,
            @Parameter(description = "이미지 ID", required = true)
                    @PathVariable(ImageVariantAdminEndpoints.PATH_IMAGE_ID)
                    Long imageId) {

        List<ImageVariantResult> results = getImageVariantsByImageIdUseCase.execute(imageId);
        List<ImageVariantApiResponse> responses = mapper.toApiResponses(results);

        return ResponseEntity.ok(ApiResponse.of(responses));
    }
}
