package com.ryuqq.marketplace.adapter.in.rest.productgroupimage.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.productgroupimage.ProductGroupImageAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.productgroupimage.dto.command.UpdateProductGroupImagesApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.productgroupimage.mapper.ProductGroupImageCommandApiMapper;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand;
import com.ryuqq.marketplace.application.productgroupimage.port.in.command.UpdateProductGroupImagesUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ProductGroupImageCommandController - 상품 그룹 이미지 수정 API.
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
@Tag(name = "상품 그룹 이미지 관리", description = "상품 그룹 이미지 수정 API")
@RestController
@RequestMapping(ProductGroupImageAdminEndpoints.PRODUCT_GROUPS)
public class ProductGroupImageCommandController {

    private final UpdateProductGroupImagesUseCase updateImagesUseCase;
    private final ProductGroupImageCommandApiMapper mapper;

    public ProductGroupImageCommandController(
            UpdateProductGroupImagesUseCase updateImagesUseCase,
            ProductGroupImageCommandApiMapper mapper) {
        this.updateImagesUseCase = updateImagesUseCase;
        this.mapper = mapper;
    }

    /**
     * 상품 그룹 이미지 수정 API.
     *
     * <p>상품 그룹의 이미지를 전체 교체합니다.
     *
     * @param productGroupId 상품 그룹 ID
     * @param request 이미지 수정 요청 DTO
     * @return 빈 응답 (204 No Content)
     */
    @Operation(summary = "이미지 수정", description = "상품 그룹의 이미지를 전체 교체합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "204",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "상품 그룹을 찾을 수 없음")
    })
    @PreAuthorize("@access.isSellerOwnerOr(#productGroupId, 'product-group:write')")
    @RequirePermission(value = "product-group:write", description = "상품 그룹 이미지 수정")
    @PutMapping(ProductGroupImageAdminEndpoints.ID + ProductGroupImageAdminEndpoints.IMAGES)
    public ResponseEntity<Void> updateImages(
            @Parameter(description = "상품 그룹 ID", required = true)
                    @PathVariable(ProductGroupImageAdminEndpoints.PATH_PRODUCT_GROUP_ID)
                    Long productGroupId,
            @Valid @RequestBody UpdateProductGroupImagesApiRequest request) {

        UpdateProductGroupImagesCommand command = mapper.toCommand(productGroupId, request);
        updateImagesUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }
}
