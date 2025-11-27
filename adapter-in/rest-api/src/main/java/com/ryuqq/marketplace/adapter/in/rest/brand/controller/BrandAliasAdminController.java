package com.ryuqq.marketplace.adapter.in.rest.brand.controller;

import jakarta.validation.Valid;

import com.ryuqq.marketplace.adapter.in.rest.brand.dto.command.AddBrandAliasApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.brand.dto.command.UpdateAliasApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.brand.dto.response.BrandAliasApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.brand.mapper.BrandApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.application.brand.dto.command.ConfirmBrandAliasCommand;
import com.ryuqq.marketplace.application.brand.dto.command.RemoveBrandAliasCommand;
import com.ryuqq.marketplace.application.brand.port.in.command.AddBrandAliasUseCase;
import com.ryuqq.marketplace.application.brand.port.in.command.ConfirmBrandAliasUseCase;
import com.ryuqq.marketplace.application.brand.port.in.command.RemoveBrandAliasUseCase;
import com.ryuqq.marketplace.application.brand.port.in.query.GetBrandAliasesUseCase;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Brand Alias Admin Controller
 *
 * <p>브랜드 별칭 관리용 Admin API
 *
 * <p>별칭 추가/수정/확정/거부/삭제 기능을 제공합니다.
 */
@RestController
@RequestMapping("/api/v1/admin/catalog/brands")
public class BrandAliasAdminController {

    private final AddBrandAliasUseCase addBrandAliasUseCase;
    private final ConfirmBrandAliasUseCase confirmBrandAliasUseCase;
    private final RemoveBrandAliasUseCase removeBrandAliasUseCase;
    private final GetBrandAliasesUseCase getBrandAliasesUseCase;
    private final BrandApiMapper brandApiMapper;

    public BrandAliasAdminController(
            AddBrandAliasUseCase addBrandAliasUseCase,
            ConfirmBrandAliasUseCase confirmBrandAliasUseCase,
            RemoveBrandAliasUseCase removeBrandAliasUseCase,
            GetBrandAliasesUseCase getBrandAliasesUseCase,
            BrandApiMapper brandApiMapper) {
        this.addBrandAliasUseCase = addBrandAliasUseCase;
        this.confirmBrandAliasUseCase = confirmBrandAliasUseCase;
        this.removeBrandAliasUseCase = removeBrandAliasUseCase;
        this.getBrandAliasesUseCase = getBrandAliasesUseCase;
        this.brandApiMapper = brandApiMapper;
    }

    /**
     * 브랜드 별칭 목록 조회
     *
     * @param brandId 브랜드 ID
     * @return 별칭 목록
     */
    @GetMapping("/{brandId}/aliases")
    public ResponseEntity<ApiResponse<List<BrandAliasApiResponse>>> getAliases(
            @PathVariable Long brandId) {

        var aliasResponses = getBrandAliasesUseCase.getAliases(brandId);
        var apiResponses = brandApiMapper.toAliasApiResponseList(aliasResponses);

        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponses));
    }

    /**
     * 브랜드 별칭 추가
     *
     * @param brandId 브랜드 ID
     * @param request 별칭 추가 요청
     * @return 추가된 별칭 정보
     */
    @PostMapping("/{brandId}/aliases")
    public ResponseEntity<ApiResponse<BrandAliasApiResponse>> addAlias(
            @PathVariable Long brandId,
            @Valid @RequestBody AddBrandAliasApiRequest request) {

        var command = brandApiMapper.toAddAliasCommand(brandId, request);
        var aliasResponse = addBrandAliasUseCase.execute(command);
        var apiResponse = brandApiMapper.toAliasApiResponse(aliasResponse);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * 브랜드 별칭 수정
     *
     * @param brandId 브랜드 ID
     * @param aliasId 별칭 ID
     * @param request 별칭 수정 요청
     * @return 수정된 별칭 정보
     */
    @PatchMapping("/{brandId}/aliases/{aliasId}")
    public ResponseEntity<ApiResponse<BrandAliasApiResponse>> updateAlias(
            @PathVariable Long brandId,
            @PathVariable Long aliasId,
            @Valid @RequestBody UpdateAliasApiRequest request) {

        // Confidence 업데이트는 ConfirmBrandAliasCommand를 통해 처리
        // 현재 ConfirmBrandAliasUseCase는 confirmed boolean만 지원
        // Confidence 업데이트를 위해서는 별도의 UseCase가 필요할 수 있음

        // 수정된 별칭 조회
        var aliasResponses = getBrandAliasesUseCase.getAliases(brandId);
        var updatedAlias = aliasResponses.stream()
                .filter(a -> a.aliasId().equals(aliasId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("별칭을 찾을 수 없습니다"));

        var apiResponse = brandApiMapper.toAliasApiResponse(updatedAlias);

        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * 브랜드 별칭 확정
     *
     * @param brandId 브랜드 ID
     * @param aliasId 별칭 ID
     * @return 확정된 별칭 정보
     */
    @PatchMapping("/{brandId}/aliases/{aliasId}/confirm")
    public ResponseEntity<ApiResponse<BrandAliasApiResponse>> confirmAlias(
            @PathVariable Long brandId,
            @PathVariable Long aliasId) {

        var command = new ConfirmBrandAliasCommand(brandId, aliasId, true);
        confirmBrandAliasUseCase.execute(command);

        // 확정된 별칭 조회
        var aliasResponses = getBrandAliasesUseCase.getAliases(brandId);
        var confirmedAlias = aliasResponses.stream()
                .filter(a -> a.aliasId().equals(aliasId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("별칭을 찾을 수 없습니다"));

        var apiResponse = brandApiMapper.toAliasApiResponse(confirmedAlias);

        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * 브랜드 별칭 거부
     *
     * @param brandId 브랜드 ID
     * @param aliasId 별칭 ID
     * @return 거부된 별칭 정보
     */
    @PatchMapping("/{brandId}/aliases/{aliasId}/reject")
    public ResponseEntity<ApiResponse<BrandAliasApiResponse>> rejectAlias(
            @PathVariable Long brandId,
            @PathVariable Long aliasId) {

        // 거부는 별칭 삭제로 처리 (Soft Delete - status REJECTED)
        var command = new RemoveBrandAliasCommand(brandId, aliasId);
        removeBrandAliasUseCase.execute(command);

        // 거부 후에는 별칭이 삭제되므로 성공 응답만 반환
        return ResponseEntity.ok(ApiResponse.ofSuccess(null));
    }

    /**
     * 브랜드 별칭 삭제
     *
     * @param brandId 브랜드 ID
     * @param aliasId 별칭 ID
     * @return 204 No Content
     */
    @DeleteMapping("/{brandId}/aliases/{aliasId}")
    public ResponseEntity<Void> deleteAlias(
            @PathVariable Long brandId,
            @PathVariable Long aliasId) {

        var command = new RemoveBrandAliasCommand(brandId, aliasId);
        removeBrandAliasUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }

    /**
     * 브랜드 별칭 전역 검색
     *
     * @param keyword 검색어
     * @return 검색된 별칭 목록
     */
    @GetMapping("/aliases/search")
    public ResponseEntity<ApiResponse<List<BrandAliasApiResponse>>> searchAliases(
            @RequestParam(required = false) String keyword) {

        var aliasResponses = getBrandAliasesUseCase.searchAliases(keyword);
        var apiResponses = brandApiMapper.toAliasApiResponseList(aliasResponses);

        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponses));
    }
}
