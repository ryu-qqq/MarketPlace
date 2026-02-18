package com.ryuqq.marketplace.adapter.in.rest.productnotice.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.productnotice.ProductNoticeAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.productnotice.dto.command.UpdateProductNoticeApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.productnotice.mapper.ProductNoticeCommandApiMapper;
import com.ryuqq.marketplace.application.productnotice.dto.command.UpdateProductNoticeCommand;
import com.ryuqq.marketplace.application.productnotice.port.in.command.UpdateProductNoticeUseCase;
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
 * ProductNoticeCommandController - 상품 그룹 고시정보 수정 API.
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
@Tag(name = "상품 그룹 고시정보 관리", description = "상품 그룹 고시정보 수정 API")
@RestController
@RequestMapping(ProductNoticeAdminEndpoints.PRODUCT_GROUPS)
public class ProductNoticeCommandController {

    private final UpdateProductNoticeUseCase updateNoticeUseCase;
    private final ProductNoticeCommandApiMapper mapper;

    public ProductNoticeCommandController(
            UpdateProductNoticeUseCase updateNoticeUseCase, ProductNoticeCommandApiMapper mapper) {
        this.updateNoticeUseCase = updateNoticeUseCase;
        this.mapper = mapper;
    }

    /**
     * 상품 그룹 고시정보 수정 API.
     *
     * <p>상품 그룹의 고시정보를 수정합니다. 기존 고시정보가 없으면 새로 생성합니다.
     *
     * @param productGroupId 상품 그룹 ID
     * @param request 고시정보 수정 요청 DTO
     * @return 빈 응답 (204 No Content)
     */
    @Operation(summary = "고시정보 수정", description = "상품 그룹의 고시정보를 수정합니다.")
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
    @RequirePermission(value = "product-group:write", description = "상품 그룹 고시정보 수정")
    @PutMapping(ProductNoticeAdminEndpoints.ID + ProductNoticeAdminEndpoints.NOTICE)
    public ResponseEntity<Void> updateNotice(
            @Parameter(description = "상품 그룹 ID", required = true)
                    @PathVariable(ProductNoticeAdminEndpoints.PATH_PRODUCT_GROUP_ID)
                    Long productGroupId,
            @Valid @RequestBody UpdateProductNoticeApiRequest request) {

        UpdateProductNoticeCommand command = mapper.toCommand(productGroupId, request);
        updateNoticeUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }
}
