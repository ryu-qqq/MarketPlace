package com.ryuqq.marketplace.adapter.in.rest.productgroupimage.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.productgroupimage.ProductGroupImageAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.productgroupimage.dto.response.ProductGroupImageUploadStatusApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.productgroupimage.mapper.ProductGroupImageQueryApiMapper;
import com.ryuqq.marketplace.application.productgroupimage.dto.response.ProductGroupImageUploadStatusResult;
import com.ryuqq.marketplace.application.productgroupimage.port.in.query.GetProductGroupImageUploadStatusUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "상품 그룹 이미지 관리", description = "상품 그룹 이미지 조회 API")
@RestController
@RequestMapping(ProductGroupImageAdminEndpoints.PRODUCT_GROUPS)
public class ProductGroupImageQueryController {

    private final GetProductGroupImageUploadStatusUseCase getUploadStatusUseCase;
    private final ProductGroupImageQueryApiMapper mapper;

    public ProductGroupImageQueryController(
            GetProductGroupImageUploadStatusUseCase getUploadStatusUseCase,
            ProductGroupImageQueryApiMapper mapper) {
        this.getUploadStatusUseCase = getUploadStatusUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "이미지 업로드 상태 조회", description = "상품 그룹 이미지의 업로드 상태를 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "상품 그룹을 찾을 수 없음")
    })
    @PreAuthorize("@access.isSellerOwnerOr(#productGroupId, 'product-group:read')")
    @RequirePermission(value = "product-group:read", description = "상품 그룹 이미지 업로드 상태 조회")
    @GetMapping(
            ProductGroupImageAdminEndpoints.ID
                    + ProductGroupImageAdminEndpoints.IMAGES
                    + ProductGroupImageAdminEndpoints.UPLOAD_STATUS)
    public ResponseEntity<ApiResponse<ProductGroupImageUploadStatusApiResponse>> getUploadStatus(
            @Parameter(description = "상품 그룹 ID", required = true)
                    @PathVariable(ProductGroupImageAdminEndpoints.PATH_PRODUCT_GROUP_ID)
                    Long productGroupId) {

        ProductGroupImageUploadStatusResult result = getUploadStatusUseCase.execute(productGroupId);
        return ResponseEntity.ok(ApiResponse.of(mapper.toResponse(result)));
    }
}
