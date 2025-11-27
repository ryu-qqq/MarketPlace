package com.ryuqq.marketplace.adapter.in.rest.category.controller;

import jakarta.validation.Valid;

import com.ryuqq.marketplace.adapter.in.rest.category.dto.command.ChangeCategoryStatusApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.category.dto.command.CreateCategoryApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.category.dto.command.MoveCategoryApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.category.dto.command.UpdateCategoryApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.category.dto.response.CategoryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.category.mapper.CategoryApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.application.category.dto.command.ChangeCategoryStatusCommand;
import com.ryuqq.marketplace.application.category.dto.response.CategoryResponse;
import com.ryuqq.marketplace.application.category.port.in.command.ChangeCategoryStatusUseCase;
import com.ryuqq.marketplace.application.category.port.in.command.CreateCategoryUseCase;
import com.ryuqq.marketplace.application.category.port.in.command.MoveCategoryUseCase;
import com.ryuqq.marketplace.application.category.port.in.command.UpdateCategoryUseCase;
import com.ryuqq.marketplace.application.category.port.in.query.GetCategoryUseCase;
import com.ryuqq.marketplace.domain.category.exception.CategoryNotFoundException;

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
 * Category Admin Command Controller
 *
 * <p>카테고리 관리용 Admin API (상태 변경 작업)</p>
 *
 * <p>POST, PATCH, DELETE 엔드포인트를 제공합니다.</p>
 *
 * <p><strong>Zero-Tolerance 규칙 준수</strong>:</p>
 * <ul>
 *   <li>Lombok 금지 - 생성자 주입 명시적 작성</li>
 *   <li>Thin Controller - UseCase로 비즈니스 로직 위임</li>
 *   <li>DTO 변환 - Mapper를 통한 계층 간 DTO 변환</li>
 *   <li>Validation - @Valid를 통한 입력 검증</li>
 * </ul>
 *
 * @author Claude Code
 * @since 2025-11-27
 */
@RestController
@RequestMapping("/api/v1/admin/catalog/categories")
public class CategoryAdminCommandController {

    private final CreateCategoryUseCase createCategoryUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final ChangeCategoryStatusUseCase changeCategoryStatusUseCase;
    private final MoveCategoryUseCase moveCategoryUseCase;
    private final GetCategoryUseCase getCategoryUseCase;
    private final CategoryApiMapper mapper;

    public CategoryAdminCommandController(
            CreateCategoryUseCase createCategoryUseCase,
            UpdateCategoryUseCase updateCategoryUseCase,
            ChangeCategoryStatusUseCase changeCategoryStatusUseCase,
            MoveCategoryUseCase moveCategoryUseCase,
            GetCategoryUseCase getCategoryUseCase,
            CategoryApiMapper mapper) {
        this.createCategoryUseCase = createCategoryUseCase;
        this.updateCategoryUseCase = updateCategoryUseCase;
        this.changeCategoryStatusUseCase = changeCategoryStatusUseCase;
        this.moveCategoryUseCase = moveCategoryUseCase;
        this.getCategoryUseCase = getCategoryUseCase;
        this.mapper = mapper;
    }

    /**
     * 카테고리 생성
     *
     * <p>새로운 카테고리를 생성하고 생성된 카테고리 정보를 반환합니다.</p>
     *
     * @param request 카테고리 생성 요청
     * @return 생성된 카테고리 정보 (201 Created)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryApiResponse>> createCategory(
            @Valid @RequestBody CreateCategoryApiRequest request) {

        var command = mapper.toCreateCommand(request);
        CategoryResponse response = createCategoryUseCase.execute(command);
        CategoryApiResponse apiResponse = mapper.toApiResponse(response);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * 카테고리 수정
     *
     * <p>기존 카테고리의 정보를 수정합니다.</p>
     *
     * @param id 카테고리 ID
     * @param request 카테고리 수정 요청
     * @return 수정된 카테고리 정보 (200 OK)
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryApiResponse>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCategoryApiRequest request) {

        var command = mapper.toUpdateCommand(id, request);
        CategoryResponse response = updateCategoryUseCase.execute(command);
        CategoryApiResponse apiResponse = mapper.toApiResponse(response);

        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * 카테고리 상태 변경
     *
     * <p>카테고리의 상태를 변경합니다 (ACTIVE/INACTIVE/DEPRECATED).</p>
     *
     * @param id 카테고리 ID
     * @param request 상태 변경 요청
     * @return 상태 변경된 카테고리 정보 (200 OK)
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<CategoryApiResponse>> changeCategoryStatus(
            @PathVariable Long id,
            @Valid @RequestBody ChangeCategoryStatusApiRequest request) {

        var command = mapper.toChangeStatusCommand(id, request);
        changeCategoryStatusUseCase.execute(command);

        CategoryResponse response = getCategoryUseCase.getById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        CategoryApiResponse apiResponse = mapper.toApiResponse(response);

        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * 카테고리 이동
     *
     * <p>카테고리를 다른 부모 아래로 이동합니다.</p>
     *
     * @param id 카테고리 ID
     * @param request 이동 요청
     * @return 이동된 카테고리 정보 (200 OK)
     */
    @PatchMapping("/{id}/move")
    public ResponseEntity<ApiResponse<CategoryApiResponse>> moveCategory(
            @PathVariable Long id,
            @Valid @RequestBody MoveCategoryApiRequest request) {

        var command = mapper.toMoveCommand(id, request);
        moveCategoryUseCase.execute(command);

        CategoryResponse response = getCategoryUseCase.getById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        CategoryApiResponse apiResponse = mapper.toApiResponse(response);

        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * 카테고리 삭제 (Soft Delete)
     *
     * <p>카테고리를 INACTIVE 상태로 변경합니다 (물리 삭제 아님).</p>
     *
     * <p>실제 데이터베이스에서 삭제하지 않고 상태만 변경하여
     * 데이터 무결성과 이력 추적을 유지합니다.</p>
     *
     * @param id 카테고리 ID
     * @return 204 No Content (본문 없음)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        // Soft delete: status를 INACTIVE로 변경
        var command = new ChangeCategoryStatusCommand(id, "INACTIVE", null);
        changeCategoryStatusUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }
}
