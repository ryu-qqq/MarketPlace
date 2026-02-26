package com.ryuqq.marketplace.adapter.in.rest.productgroupdescription.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.productgroupdescription.ProductGroupDescriptionAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.productgroupdescription.dto.command.UpdateProductGroupDescriptionApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.productgroupdescription.mapper.ProductGroupDescriptionCommandApiMapper;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.UpdateProductGroupDescriptionCommand;
import com.ryuqq.marketplace.application.productgroupdescription.port.in.command.UpdateProductGroupDescriptionUseCase;
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
 * ProductGroupDescriptionCommandController - 상품 그룹 상세 설명 수정 API.
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
@Tag(name = "상품 그룹 상세 설명 관리", description = "상품 그룹 상세 설명 수정 API")
@RestController
@RequestMapping(ProductGroupDescriptionAdminEndpoints.PRODUCT_GROUPS)
public class ProductGroupDescriptionCommandController {

    private final UpdateProductGroupDescriptionUseCase updateDescriptionUseCase;
    private final ProductGroupDescriptionCommandApiMapper mapper;

    public ProductGroupDescriptionCommandController(
            UpdateProductGroupDescriptionUseCase updateDescriptionUseCase,
            ProductGroupDescriptionCommandApiMapper mapper) {
        this.updateDescriptionUseCase = updateDescriptionUseCase;
        this.mapper = mapper;
    }

    /**
     * 상품 그룹 상세 설명 수정 API.
     *
     * <p>상품 그룹의 상세 설명을 수정합니다. 기존 설명이 없으면 새로 생성합니다.
     *
     * @param productGroupId 상품 그룹 ID
     * @param request 상세 설명 수정 요청 DTO
     * @return 빈 응답 (204 No Content)
     */
    @Operation(summary = "상세 설명 수정", description = "상품 그룹의 상세 설명을 수정합니다.")
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
    @RequirePermission(value = "product-group:write", description = "상품 그룹 상세 설명 수정")
    @PutMapping(
            ProductGroupDescriptionAdminEndpoints.ID
                    + ProductGroupDescriptionAdminEndpoints.DESCRIPTION)
    public ResponseEntity<Void> updateDescription(
            @Parameter(description = "상품 그룹 ID", required = true)
                    @PathVariable(ProductGroupDescriptionAdminEndpoints.PATH_PRODUCT_GROUP_ID)
                    Long productGroupId,
            @Valid @RequestBody UpdateProductGroupDescriptionApiRequest request) {

        UpdateProductGroupDescriptionCommand command = mapper.toCommand(productGroupId, request);
        updateDescriptionUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }
}
