package com.ryuqq.marketplace.adapter.in.rest.category.mapper;

import com.ryuqq.marketplace.adapter.in.rest.category.dto.query.SearchCategoriesApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.category.dto.response.CategoryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.marketplace.application.category.dto.query.CategorySearchParams;
import com.ryuqq.marketplace.application.category.dto.response.CategoryPageResult;
import com.ryuqq.marketplace.application.category.dto.response.CategoryResult;
import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import java.util.List;
import org.springframework.stereotype.Component;

/** Category Query API Mapper. */
@Component
public class CategoryQueryApiMapper {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;

    public CategorySearchParams toSearchParams(SearchCategoriesApiRequest request) {
        int page = request.page() != null ? request.page() : DEFAULT_PAGE;
        int size = request.size() != null ? request.size() : DEFAULT_SIZE;

        CommonSearchParams searchParams =
                CommonSearchParams.of(
                        null, null, null, request.sortKey(), request.sortDirection(), page, size);

        return CategorySearchParams.of(
                request.parentId(),
                request.depth(),
                request.leaf(),
                request.statuses(),
                request.departments(),
                request.categoryGroups(),
                request.searchField(),
                request.searchWord(),
                searchParams);
    }

    public CategoryApiResponse toResponse(CategoryResult result) {
        return new CategoryApiResponse(
                result.id(),
                result.code(),
                result.nameKo(),
                result.nameEn(),
                result.parentId(),
                result.depth(),
                result.path(),
                result.sortOrder(),
                result.leaf(),
                result.status(),
                result.department(),
                result.categoryGroup(),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }

    public List<CategoryApiResponse> toResponses(List<CategoryResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    public PageApiResponse<CategoryApiResponse> toPageResponse(CategoryPageResult pageResult) {
        List<CategoryApiResponse> responses = toResponses(pageResult.results());
        return PageApiResponse.of(
                responses,
                pageResult.pageMeta().page(),
                pageResult.pageMeta().size(),
                pageResult.pageMeta().totalElements());
    }
}
