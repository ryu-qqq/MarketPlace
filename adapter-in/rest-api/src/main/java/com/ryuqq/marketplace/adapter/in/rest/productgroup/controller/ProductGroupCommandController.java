package com.ryuqq.marketplace.adapter.in.rest.productgroup.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.common.security.MarketAccessChecker;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.ProductGroupAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.command.BatchChangeProductGroupStatusApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.command.BatchRegisterProductGroupApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.command.RegisterProductGroupApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.command.UpdateProductGroupBasicInfoApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.command.UpdateProductGroupFullApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response.BatchProductGroupResultApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response.ProductGroupIdApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.mapper.ProductGroupCommandApiMapper;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.productgroup.dto.command.BatchChangeProductGroupStatusCommand;
import com.ryuqq.marketplace.application.productgroup.dto.command.RegisterProductGroupCommand;
import com.ryuqq.marketplace.application.productgroup.dto.command.UpdateProductGroupBasicInfoCommand;
import com.ryuqq.marketplace.application.productgroup.dto.command.UpdateProductGroupFullCommand;
import com.ryuqq.marketplace.application.productgroup.port.in.command.BatchChangeProductGroupStatusUseCase;
import com.ryuqq.marketplace.application.productgroup.port.in.command.BatchRegisterProductGroupFullUseCase;
import com.ryuqq.marketplace.application.productgroup.port.in.command.RegisterProductGroupFullUseCase;
import com.ryuqq.marketplace.application.productgroup.port.in.command.UpdateProductGroupBasicInfoUseCase;
import com.ryuqq.marketplace.application.productgroup.port.in.command.UpdateProductGroupFullUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ProductGroupCommandController - 상품 그룹 수정 API.
 *
 * <p>상품 그룹 등록/수정/상태변경 엔드포인트를 제공합니다.
 *
 * <p>API-CTR-001: Controller는 @RestController로 정의.
 *
 * <p>API-CTR-004: ResponseEntity&lt;ApiResponse&lt;T&gt;&gt; 래핑 필수.
 *
 * <p>API-CTR-003: UseCase(Port-In) 인터페이스 의존.
 *
 * <p>API-CTR-002: DELETE 메서드 금지 (소프트 삭제는 PATCH).
 *
 * <p>API-CTR-005: Controller에서 @Transactional 금지.
 *
 * <p>API-CTR-007: Controller에 비즈니스 로직 포함 금지.
 *
 * <p>API-CTR-009: @Valid 어노테이션 필수.
 *
 * <p>API-CTR-010: CQRS Controller 분리.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag(name = "상품 그룹 관리", description = "상품 그룹 등록/수정 API")
@RestController
@RequestMapping(ProductGroupAdminEndpoints.PRODUCT_GROUPS)
public class ProductGroupCommandController {

    private final RegisterProductGroupFullUseCase registerUseCase;
    private final BatchRegisterProductGroupFullUseCase batchRegisterUseCase;
    private final UpdateProductGroupFullUseCase updateUseCase;
    private final UpdateProductGroupBasicInfoUseCase updateBasicInfoUseCase;
    private final BatchChangeProductGroupStatusUseCase batchChangeStatusUseCase;
    private final ProductGroupCommandApiMapper mapper;
    private final MarketAccessChecker accessChecker;

    /**
     * ProductGroupCommandController 생성자.
     *
     * @param registerUseCase 상품 그룹 등록 UseCase
     * @param batchRegisterUseCase 상품 그룹 배치 등록 UseCase
     * @param updateUseCase 상품 그룹 전체 수정 UseCase
     * @param updateBasicInfoUseCase 기본 정보 수정 UseCase
     * @param batchChangeStatusUseCase 상품 그룹 배치 상태 변경 UseCase
     * @param mapper Command API 매퍼
     * @param accessChecker 접근 권한 검사기
     */
    public ProductGroupCommandController(
            RegisterProductGroupFullUseCase registerUseCase,
            BatchRegisterProductGroupFullUseCase batchRegisterUseCase,
            UpdateProductGroupFullUseCase updateUseCase,
            UpdateProductGroupBasicInfoUseCase updateBasicInfoUseCase,
            BatchChangeProductGroupStatusUseCase batchChangeStatusUseCase,
            ProductGroupCommandApiMapper mapper,
            MarketAccessChecker accessChecker) {
        this.registerUseCase = registerUseCase;
        this.batchRegisterUseCase = batchRegisterUseCase;
        this.updateUseCase = updateUseCase;
        this.updateBasicInfoUseCase = updateBasicInfoUseCase;
        this.batchChangeStatusUseCase = batchChangeStatusUseCase;
        this.mapper = mapper;
        this.accessChecker = accessChecker;
    }

    /**
     * 상품 그룹 등록 API.
     *
     * <p>ProductGroup + Description + Notice + Products를 한번에 등록합니다.
     *
     * @param request 등록 요청 DTO
     * @return 생성된 상품 그룹 ID
     */
    @Operation(summary = "상품 그룹 등록", description = "상품 그룹과 하위 상품들을 한번에 등록합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "등록 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청")
    })
    @PreAuthorize("@access.hasPermission('product-group:write')")
    @RequirePermission(value = "product-group:write", description = "상품 그룹 등록")
    @PostMapping
    public ResponseEntity<ProductGroupIdApiResponse> registerProductGroup(
            @Valid @RequestBody RegisterProductGroupApiRequest request) {

        RegisterProductGroupCommand command = mapper.toCommand(request);
        Long productGroupId = registerUseCase.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ProductGroupIdApiResponse.of(productGroupId));
    }

    /**
     * 상품 그룹 배치 등록 API.
     *
     * <p>여러 상품 그룹을 한번에 등록합니다. 각 항목은 독립 트랜잭션으로 처리되며, 일부 실패 시 나머지는 정상 등록됩니다.
     *
     * @param request 배치 등록 요청 DTO (최대 100건)
     * @return 배치 처리 결과 (항목별 성공/실패)
     */
    @Operation(
            summary = "상품 그룹 배치 등록",
            description = "여러 상품 그룹을 한번에 등록합니다. 각 항목은 독립적으로 처리되며 일부 실패 시 나머지는 정상 등록됩니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "배치 등록 처리 완료 (개별 결과 확인 필요)"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청")
    })
    @PreAuthorize("@access.hasPermission('product-group:write')")
    @RequirePermission(value = "product-group:write", description = "상품 그룹 배치 등록")
    @PostMapping(ProductGroupAdminEndpoints.BATCH)
    public ResponseEntity<BatchProductGroupResultApiResponse> batchRegisterProductGroups(
            @Valid @RequestBody BatchRegisterProductGroupApiRequest request) {

        List<RegisterProductGroupCommand> commands = mapper.toCommands(request);
        BatchProcessingResult<Long> result = batchRegisterUseCase.execute(commands);

        return ResponseEntity.ok(BatchProductGroupResultApiResponse.from(result));
    }

    /**
     * 상품 그룹 전체 수정 API.
     *
     * <p>ProductGroup + Description + Notice + Products를 한번에 수정합니다.
     *
     * <p>기존 Product는 soft delete하고 새로 생성하는 전략을 사용합니다.
     *
     * @param productGroupId 상품 그룹 ID
     * @param request 수정 요청 DTO
     * @return 빈 응답 (204 No Content)
     */
    @Operation(
            summary = "상품 그룹 전체 수정",
            description = "상품 그룹과 하위 상품들을 한번에 수정합니다. 기존 상품은 soft delete되고 새로 생성됩니다.")
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
    @RequirePermission(value = "product-group:write", description = "상품 그룹 전체 수정")
    @PutMapping(ProductGroupAdminEndpoints.ID)
    public ResponseEntity<Void> updateProductGroupFull(
            @Parameter(description = "상품 그룹 ID", required = true)
                    @PathVariable(ProductGroupAdminEndpoints.PATH_PRODUCT_GROUP_ID)
                    Long productGroupId,
            @Valid @RequestBody UpdateProductGroupFullApiRequest request) {

        UpdateProductGroupFullCommand command = mapper.toCommand(productGroupId, request);
        updateUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }

    /**
     * 상품 그룹 기본 정보 수정 API.
     *
     * <p>상품명, 브랜드, 카테고리, 배송정책, 반품정책을 수정합니다.
     *
     * @param productGroupId 상품 그룹 ID
     * @param request 기본 정보 수정 요청 DTO
     * @return 빈 응답 (204 No Content)
     */
    @Operation(summary = "기본 정보 수정", description = "상품 그룹의 기본 정보를 수정합니다.")
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
    @RequirePermission(value = "product-group:write", description = "상품 그룹 기본 정보 수정")
    @PatchMapping(ProductGroupAdminEndpoints.ID + ProductGroupAdminEndpoints.BASIC_INFO)
    public ResponseEntity<Void> updateBasicInfo(
            @Parameter(description = "상품 그룹 ID", required = true)
                    @PathVariable(ProductGroupAdminEndpoints.PATH_PRODUCT_GROUP_ID)
                    Long productGroupId,
            @Valid @RequestBody UpdateProductGroupBasicInfoApiRequest request) {

        UpdateProductGroupBasicInfoCommand command = mapper.toCommand(productGroupId, request);
        updateBasicInfoUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }

    /**
     * 상품 그룹 배치 상태 변경 API.
     *
     * <p>여러 상품 그룹의 상태를 일괄 변경합니다. 현재 인증된 사용자의 셀러 소유권을 검증합니다.
     *
     * @param request 배치 상태 변경 요청 DTO
     * @return 빈 응답 (204 No Content)
     */
    @Operation(summary = "상품 그룹 배치 상태 변경", description = "여러 상품 그룹의 상태를 일괄 변경합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "204",
                description = "상태 변경 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "403",
                description = "소유권 검증 실패")
    })
    @PreAuthorize("@access.hasPermission('product-group:write')")
    @RequirePermission(value = "product-group:write", description = "상품 그룹 배치 상태 변경")
    @PatchMapping(ProductGroupAdminEndpoints.STATUS)
    public ResponseEntity<Void> batchChangeStatus(
            @Valid @RequestBody BatchChangeProductGroupStatusApiRequest request) {

        long sellerId = accessChecker.resolveCurrentSellerId();
        BatchChangeProductGroupStatusCommand command = mapper.toCommand(sellerId, request);
        batchChangeStatusUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }
}
