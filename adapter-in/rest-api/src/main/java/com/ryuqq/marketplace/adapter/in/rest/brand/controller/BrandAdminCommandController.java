package com.ryuqq.marketplace.adapter.in.rest.brand.controller;

import jakarta.validation.Valid;

import com.ryuqq.marketplace.adapter.in.rest.brand.dto.command.ChangeBrandStatusApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.brand.dto.command.CreateBrandApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.brand.dto.command.UpdateBrandApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.brand.dto.response.BrandApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.brand.mapper.BrandApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.application.brand.dto.command.ChangeBrandStatusCommand;
import com.ryuqq.marketplace.application.brand.port.in.command.ChangeBrandStatusUseCase;
import com.ryuqq.marketplace.application.brand.port.in.command.CreateBrandUseCase;
import com.ryuqq.marketplace.application.brand.port.in.command.UpdateBrandUseCase;
import com.ryuqq.marketplace.application.brand.port.in.query.GetBrandUseCase;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Brand Admin Command Controller
 *
 * <p>브랜드 관리용 Admin API (상태 변경 작업)
 *
 * <p>POST, PATCH, DELETE 엔드포인트를 제공합니다.
 *
 * <p>Zero-Tolerance 규칙 준수:
 * <ul>
 *   <li>Lombok 금지 - 생성자 주입 명시적 작성</li>
 *   <li>Thin Controller - UseCase로 비즈니스 로직 위임</li>
 *   <li>DTO 변환 - Mapper를 통한 계층 간 DTO 변환</li>
 *   <li>Validation - @Valid를 통한 입력 검증</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/admin/catalog/brands")
public class BrandAdminCommandController {

    private final CreateBrandUseCase createBrandUseCase;
    private final UpdateBrandUseCase updateBrandUseCase;
    private final ChangeBrandStatusUseCase changeBrandStatusUseCase;
    private final GetBrandUseCase getBrandUseCase;
    private final BrandApiMapper brandApiMapper;

    public BrandAdminCommandController(
            CreateBrandUseCase createBrandUseCase,
            UpdateBrandUseCase updateBrandUseCase,
            ChangeBrandStatusUseCase changeBrandStatusUseCase,
            GetBrandUseCase getBrandUseCase,
            BrandApiMapper brandApiMapper) {
        this.createBrandUseCase = createBrandUseCase;
        this.updateBrandUseCase = updateBrandUseCase;
        this.changeBrandStatusUseCase = changeBrandStatusUseCase;
        this.getBrandUseCase = getBrandUseCase;
        this.brandApiMapper = brandApiMapper;
    }

    /**
     * 브랜드 생성
     *
     * <p>새로운 브랜드를 생성하고 생성된 브랜드 정보를 반환합니다.
     *
     * @param request 브랜드 생성 요청 (브랜드명, 설명 등)
     * @return 생성된 브랜드 정보 (201 Created)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<BrandApiResponse>> createBrand(
            @Valid @RequestBody CreateBrandApiRequest request) {

        var command = brandApiMapper.toCreateCommand(request);
        var brandResponse = createBrandUseCase.execute(command);
        var apiResponse = brandApiMapper.toApiResponse(brandResponse);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * 브랜드 수정
     *
     * <p>기존 브랜드의 정보를 수정합니다.
     *
     * @param id 브랜드 ID
     * @param request 브랜드 수정 요청 (브랜드명, 설명 등)
     * @return 수정된 브랜드 정보 (200 OK)
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandApiResponse>> updateBrand(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBrandApiRequest request) {

        var command = brandApiMapper.toUpdateCommand(id, request);
        var brandResponse = updateBrandUseCase.execute(command);
        var apiResponse = brandApiMapper.toApiResponse(brandResponse);

        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * 브랜드 상태 변경
     *
     * <p>브랜드의 활성화 상태를 변경합니다 (ACTIVE/INACTIVE).
     *
     * @param id 브랜드 ID
     * @param request 상태 변경 요청 (status)
     * @return 상태 변경된 브랜드 정보 (200 OK)
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<BrandApiResponse>> changeBrandStatus(
            @PathVariable Long id,
            @Valid @RequestBody ChangeBrandStatusApiRequest request) {

        var command = brandApiMapper.toChangeStatusCommand(id, request);
        changeBrandStatusUseCase.execute(command);

        var brandDetailResponse = getBrandUseCase.getById(id);
        var apiResponse = new BrandApiResponse(
            brandDetailResponse.brandId(),
            brandDetailResponse.code(),
            brandDetailResponse.canonicalName(),
            brandDetailResponse.nameKo(),
            brandDetailResponse.nameEn(),
            brandDetailResponse.shortName(),
            brandDetailResponse.countryCode(),
            brandDetailResponse.department(),
            brandDetailResponse.isLuxury(),
            brandDetailResponse.status(),
            brandDetailResponse.logoUrl()
        );

        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * 브랜드 삭제 (Soft Delete)
     *
     * <p>브랜드를 INACTIVE 상태로 변경합니다 (물리 삭제 아님).
     *
     * <p>실제 데이터베이스에서 삭제하지 않고 상태만 변경하여
     * 데이터 무결성과 이력 추적을 유지합니다.
     *
     * @param id 브랜드 ID
     * @return 204 No Content (본문 없음)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable Long id) {
        // Soft delete: status를 INACTIVE로 변경
        var command = new ChangeBrandStatusCommand(id, "INACTIVE");
        changeBrandStatusUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }
}
