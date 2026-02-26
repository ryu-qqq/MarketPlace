package com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.controller;

import static com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.LegacyProductGroupEndpoints.GROUP_DISPLAY_YN;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.LegacyProductGroupEndpoints.OUT_STOCK;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.LegacyProductGroupEndpoints.PRODUCT_GROUP;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.LegacyProductGroupEndpoints.PRODUCT_GROUP_ID;

import com.ryuqq.marketplace.adapter.in.rest.legacy.common.dto.LegacyApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.response.LegacyProductFetchResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.mapper.LegacyProductCommandApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.request.LegacyCreateProductGroupRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.request.LegacyUpdateDisplayYnRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.request.LegacyUpdateProductGroupRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response.LegacyCreateProductGroupResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.mapper.LegacyInboundApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.mapper.LegacyProductGroupCommandApiMapper;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.command.LegacyRegisterProductGroupCommand;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.command.LegacyUpdateProductGroupCommand;
import com.ryuqq.marketplace.application.legacy.productgroup.port.in.command.LegacyProductGroupFullRegisterUseCase;
import com.ryuqq.marketplace.application.legacy.productgroup.port.in.command.LegacyProductGroupFullUpdateUseCase;
import com.ryuqq.marketplace.application.legacy.productgroup.port.in.command.LegacyProductMarkOutOfStockUseCase;
import com.ryuqq.marketplace.application.legacy.productgroup.port.in.command.LegacyProductUpdateDisplayStatusUseCase;
import com.ryuqq.marketplace.application.legacy.shared.dto.response.LegacyProductRegistrationResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.Set;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 세토프 어드민용 레거시 상품그룹 등록/수정 API 컨트롤러.
 *
 * <p>등록은 InboundProduct 파이프라인을 통해, 수정은 개별 UseCase를 통해 PK 변환 후 기존 내부 Coordinator에 위임합니다.
 */
@Tag(name = "세토프 어드민용 레거시 - 상품그룹", description = "세토프 어드민용 레거시 상품그룹 엔드포인트.")
@RestController
public class LegacyProductGroupCommandController {

    private final LegacyProductGroupFullRegisterUseCase legacyProductGroupFullRegisterUseCase;
    private final LegacyProductGroupFullUpdateUseCase legacyProductGroupFullUpdateUseCase;
    private final LegacyProductUpdateDisplayStatusUseCase legacyProductUpdateDisplayStatusUseCase;
    private final LegacyProductMarkOutOfStockUseCase legacyProductMarkOutOfStockUseCase;
    private final LegacyInboundApiMapper legacyInboundApiMapper;
    private final LegacyProductGroupCommandApiMapper legacyProductGroupCommandApiMapper;
    private final LegacyProductCommandApiMapper legacyProductCommandApiMapper;

    public LegacyProductGroupCommandController(
            LegacyProductGroupFullRegisterUseCase legacyProductGroupFullRegisterUseCase,
            LegacyProductGroupFullUpdateUseCase legacyProductGroupFullUpdateUseCase,
            LegacyProductUpdateDisplayStatusUseCase legacyProductUpdateDisplayStatusUseCase,
            LegacyProductMarkOutOfStockUseCase legacyProductMarkOutOfStockUseCase,
            LegacyInboundApiMapper legacyInboundApiMapper,
            LegacyProductGroupCommandApiMapper legacyProductGroupCommandApiMapper,
            LegacyProductCommandApiMapper legacyProductCommandApiMapper) {
        this.legacyProductGroupFullRegisterUseCase = legacyProductGroupFullRegisterUseCase;
        this.legacyProductGroupFullUpdateUseCase = legacyProductGroupFullUpdateUseCase;
        this.legacyProductUpdateDisplayStatusUseCase = legacyProductUpdateDisplayStatusUseCase;
        this.legacyProductMarkOutOfStockUseCase = legacyProductMarkOutOfStockUseCase;
        this.legacyInboundApiMapper = legacyInboundApiMapper;
        this.legacyProductGroupCommandApiMapper = legacyProductGroupCommandApiMapper;
        this.legacyProductCommandApiMapper = legacyProductCommandApiMapper;
    }

    @Operation(
            summary = "레거시 상품그룹 등록",
            description = "세토프 어드민 호환 상품그룹을 등록합니다. 상품, 옵션, 이미지, 고시정보를 포함한 전체 등록입니다.")
    @PreAuthorize("@access.authenticated()")
    @PostMapping(PRODUCT_GROUP)
    public ResponseEntity<LegacyApiResponse<LegacyCreateProductGroupResponse>>
            registerProductGroupFull(@Valid @RequestBody LegacyCreateProductGroupRequest request) {
        LegacyRegisterProductGroupCommand command = legacyInboundApiMapper.toCommand(request);
        LegacyProductRegistrationResult result =
                legacyProductGroupFullRegisterUseCase.execute(command);
        LegacyCreateProductGroupResponse response =
                legacyProductGroupCommandApiMapper.toCreateResponse(result);

        return ResponseEntity.ok(LegacyApiResponse.of(response));
    }

    @Operation(summary = "레거시 상품그룹 전체 수정", description = "세토프 어드민 호환 상품그룹을 전체 수정합니다.")
    @PreAuthorize("@access.isLegacyProductOwnerOrSuperAdmin(#productGroupId)")
    @PutMapping(PRODUCT_GROUP_ID)
    public ResponseEntity<LegacyApiResponse<Long>> updateProductGroupFull(
            @Parameter(description = "수정할 상품그룹 ID") @PathVariable long productGroupId,
            @Valid @RequestBody LegacyUpdateProductGroupRequest request) {
        LegacyUpdateProductGroupCommand command =
                legacyInboundApiMapper.toUpdateCommand(request, productGroupId);
        legacyProductGroupFullUpdateUseCase.execute(command);
        return ResponseEntity.ok(LegacyApiResponse.of(productGroupId));
    }

    @Operation(summary = "레거시 진열 상태 변경", description = "세토프 어드민 호환 상품그룹의 진열 상태(Y/N)를 변경합니다.")
    @PreAuthorize("@access.isLegacyProductOwnerOrSuperAdmin(#productGroupId)")
    @PatchMapping(GROUP_DISPLAY_YN)
    public ResponseEntity<LegacyApiResponse<Long>> updateGroupDisplayYn(
            @Parameter(description = "상품그룹 ID") @PathVariable long productGroupId,
            @Valid @RequestBody LegacyUpdateDisplayYnRequest request) {
        legacyProductUpdateDisplayStatusUseCase.execute(
                legacyProductGroupCommandApiMapper.toDisplayStatusCommand(productGroupId, request));
        return ResponseEntity.ok(LegacyApiResponse.of(productGroupId));
    }

    @Operation(summary = "레거시 품절 처리", description = "세토프 어드민 호환 상품그룹의 모든 SKU를 품절 처리합니다.")
    @PreAuthorize("@access.isLegacyProductOwnerOrSuperAdmin(#productGroupId)")
    @PatchMapping(OUT_STOCK)
    public ResponseEntity<LegacyApiResponse<Set<LegacyProductFetchResponse>>> outOfStock(
            @Parameter(description = "품절 처리할 상품그룹 ID") @PathVariable long productGroupId) {
        LegacyProductGroupDetailResult result =
                legacyProductMarkOutOfStockUseCase.execute(
                        legacyProductGroupCommandApiMapper.toLegacyMarkOutOfStockCommand(
                                productGroupId));
        Set<LegacyProductFetchResponse> products =
                legacyProductCommandApiMapper.toProductFetchResponses(result, Map.of());
        return ResponseEntity.ok(LegacyApiResponse.of(products));
    }
}
