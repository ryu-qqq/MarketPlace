package com.ryuqq.marketplace.adapter.in.rest.productgroupdescription.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.productgroupdescription.ProductGroupDescriptionAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.productgroupdescription.dto.response.DescriptionPublishStatusApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.productgroupdescription.mapper.ProductGroupDescriptionQueryApiMapper;
import com.ryuqq.marketplace.application.productgroupdescription.dto.response.DescriptionPublishStatusResult;
import com.ryuqq.marketplace.application.productgroupdescription.port.in.query.GetDescriptionPublishStatusUseCase;
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

/**
 * ProductGroupDescriptionQueryController - 상품 그룹 상세 설명 조회 API.
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
@Tag(name = "상품 그룹 상세설명 관리", description = "상품 그룹 상세 설명 조회 API")
@RestController
@RequestMapping(ProductGroupDescriptionAdminEndpoints.PRODUCT_GROUPS)
public class ProductGroupDescriptionQueryController {

    private final GetDescriptionPublishStatusUseCase getPublishStatusUseCase;
    private final ProductGroupDescriptionQueryApiMapper mapper;

    public ProductGroupDescriptionQueryController(
            GetDescriptionPublishStatusUseCase getPublishStatusUseCase,
            ProductGroupDescriptionQueryApiMapper mapper) {
        this.getPublishStatusUseCase = getPublishStatusUseCase;
        this.mapper = mapper;
    }

    /**
     * 상세설명 발행 상태 조회 API.
     *
     * <p>상품 그룹 상세설명의 발행 상태와 이미지 업로드 상태를 조회합니다.
     *
     * @param productGroupId 상품 그룹 ID
     * @return 발행 상태 응답
     */
    @Operation(summary = "상세설명 발행 상태 조회", description = "상품 그룹 상세설명의 발행 상태와 이미지 업로드 상태를 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "상품 그룹을 찾을 수 없음")
    })
    @PreAuthorize("@access.isSellerOwnerOr(#productGroupId, 'product-group:read')")
    @RequirePermission(value = "product-group:read", description = "상세 설명 발행 상태 조회")
    @GetMapping(
            ProductGroupDescriptionAdminEndpoints.ID
                    + ProductGroupDescriptionAdminEndpoints.DESCRIPTION
                    + ProductGroupDescriptionAdminEndpoints.PUBLISH_STATUS)
    public ResponseEntity<ApiResponse<DescriptionPublishStatusApiResponse>> getPublishStatus(
            @Parameter(description = "상품 그룹 ID", required = true)
                    @PathVariable(ProductGroupDescriptionAdminEndpoints.PATH_PRODUCT_GROUP_ID)
                    Long productGroupId) {

        DescriptionPublishStatusResult result = getPublishStatusUseCase.execute(productGroupId);
        return ResponseEntity.ok(ApiResponse.of(mapper.toResponse(result)));
    }
}
