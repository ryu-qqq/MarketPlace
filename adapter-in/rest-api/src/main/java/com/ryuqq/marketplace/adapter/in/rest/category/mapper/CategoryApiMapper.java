package com.ryuqq.marketplace.adapter.in.rest.category.mapper;

import com.ryuqq.marketplace.adapter.in.rest.category.dto.command.ChangeCategoryStatusApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.category.dto.command.CreateCategoryApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.category.dto.command.MoveCategoryApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.category.dto.command.UpdateCategoryApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.category.dto.query.CategorySearchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.category.dto.response.CategoryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.category.dto.response.CategoryPathApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.category.dto.response.CategoryTreeApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.category.dto.response.CategoryTreeNodeApiResponse;
import com.ryuqq.marketplace.application.category.dto.command.ChangeCategoryStatusCommand;
import com.ryuqq.marketplace.application.category.dto.command.CreateCategoryCommand;
import com.ryuqq.marketplace.application.category.dto.command.MoveCategoryCommand;
import com.ryuqq.marketplace.application.category.dto.command.UpdateCategoryCommand;
import com.ryuqq.marketplace.application.category.dto.query.CategorySearchQuery;
import com.ryuqq.marketplace.application.category.dto.response.CategoryPathResponse;
import com.ryuqq.marketplace.application.category.dto.response.CategoryResponse;
import com.ryuqq.marketplace.application.category.dto.response.CategoryTreeNode;
import com.ryuqq.marketplace.application.category.dto.response.CategoryTreeResponse;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Category API Mapper
 *
 * <p>API Request DTO를 Application Command로 변환하고,
 * Application Response를 API Response로 변환합니다.</p>
 *
 * <p><strong>Zero-Tolerance 규칙 준수</strong>:</p>
 * <ul>
 *   <li>Lombok 금지 - Plain Java class</li>
 *   <li>Thin Mapper - 단순 DTO 변환만 수행</li>
 * </ul>
 *
 * @author Claude Code
 * @since 2025-11-27
 */
@Component
public class CategoryApiMapper {

    // ========== Request → Command ==========

    /**
     * 생성 요청을 Command로 변환
     *
     * @param request API 요청 DTO
     * @return Application Command
     */
    public CreateCategoryCommand toCreateCommand(CreateCategoryApiRequest request) {
        return new CreateCategoryCommand(
            request.parentId(),
            request.code(),
            request.nameKo(),
            request.nameEn(),
            request.sortOrder(),
            request.isListable(),
            request.isVisible(),
            request.department(),
            request.productGroup(),
            request.genderScope(),
            request.ageGroup(),
            request.displayName(),
            request.seoSlug(),
            request.iconUrl()
        );
    }

    /**
     * 수정 요청을 Command로 변환
     *
     * @param categoryId 카테고리 ID
     * @param request API 요청 DTO
     * @return Application Command
     */
    public UpdateCategoryCommand toUpdateCommand(Long categoryId, UpdateCategoryApiRequest request) {
        return new UpdateCategoryCommand(
            categoryId,
            request.nameKo(),
            request.nameEn(),
            request.isListable(),
            request.isVisible(),
            request.sortOrder(),
            request.displayName(),
            request.seoSlug(),
            request.iconUrl()
        );
    }

    /**
     * 상태 변경 요청을 Command로 변환
     *
     * @param categoryId 카테고리 ID
     * @param request API 요청 DTO
     * @return Application Command
     */
    public ChangeCategoryStatusCommand toChangeStatusCommand(
            Long categoryId, ChangeCategoryStatusApiRequest request) {
        return new ChangeCategoryStatusCommand(
            categoryId,
            request.status(),
            request.replacementCategoryId()
        );
    }

    /**
     * 이동 요청을 Command로 변환
     *
     * @param categoryId 카테고리 ID
     * @param request API 요청 DTO
     * @return Application Command
     */
    public MoveCategoryCommand toMoveCommand(Long categoryId, MoveCategoryApiRequest request) {
        return new MoveCategoryCommand(
            categoryId,
            request.newParentId(),
            request.newSortOrder()
        );
    }

    // ========== Query Mapping ==========

    /**
     * 검색 요청을 Query로 변환
     *
     * @param request API 요청 DTO
     * @return Application Query
     */
    public CategorySearchQuery toSearchQuery(CategorySearchApiRequest request) {
        return new CategorySearchQuery(
            request.keyword(),
            request.department(),
            request.productGroup(),
            null, // genderScope
            request.isLeaf(),
            request.isListable()
        );
    }

    /**
     * 검색 요청을 Pageable로 변환
     *
     * @param request API 요청 DTO
     * @return Spring Pageable
     */
    public Pageable toPageable(CategorySearchApiRequest request) {
        return PageRequest.of(request.page(), request.size());
    }

    // ========== Response Mapping ==========

    /**
     * Application Response를 API Response로 변환
     *
     * @param response Application Response
     * @return API Response
     */
    public CategoryApiResponse toApiResponse(CategoryResponse response) {
        return new CategoryApiResponse(
            response.id(),
            response.code(),
            response.nameKo(),
            response.nameEn(),
            response.parentId(),
            response.depth(),
            response.path(),
            response.sortOrder(),
            response.isLeaf(),
            response.status(),
            response.isVisible(),
            response.isListable(),
            response.department(),
            response.productGroup(),
            response.genderScope(),
            response.ageGroup(),
            response.displayName(),
            response.seoSlug(),
            response.iconUrl()
        );
    }

    /**
     * Tree Response를 API Tree Response로 변환
     *
     * @param response Application Tree Response
     * @return API Tree Response
     */
    public CategoryTreeApiResponse toTreeApiResponse(CategoryTreeResponse response) {
        List<CategoryTreeNodeApiResponse> roots = response.roots().stream()
            .map(this::toTreeNodeApiResponse)
            .toList();
        return new CategoryTreeApiResponse(roots, response.totalCount());
    }

    /**
     * Tree Node를 API Tree Node로 변환 (재귀)
     *
     * @param node Application Tree Node
     * @return API Tree Node
     */
    private CategoryTreeNodeApiResponse toTreeNodeApiResponse(CategoryTreeNode node) {
        CategoryApiResponse categoryResponse = toApiResponse(node.category());
        CategoryTreeNodeApiResponse apiNode = new CategoryTreeNodeApiResponse(categoryResponse);

        for (CategoryTreeNode child : node.children()) {
            apiNode.addChild(toTreeNodeApiResponse(child));
        }

        return apiNode;
    }

    /**
     * Path Response를 API Path Response로 변환
     *
     * @param response Application Path Response
     * @return API Path Response
     */
    public CategoryPathApiResponse toPathApiResponse(CategoryPathResponse response) {
        List<CategoryApiResponse> ancestors = response.ancestors().stream()
            .map(this::toApiResponse)
            .toList();
        return new CategoryPathApiResponse(response.categoryId(), ancestors);
    }

    /**
     * Response 목록을 API Response 목록으로 변환
     *
     * @param responses Application Response 목록
     * @return API Response 목록
     */
    public List<CategoryApiResponse> toApiResponseList(List<CategoryResponse> responses) {
        return responses.stream()
            .map(this::toApiResponse)
            .toList();
    }
}
