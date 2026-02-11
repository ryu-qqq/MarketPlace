package com.ryuqq.marketplace.adapter.in.rest.notice.mapper;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.marketplace.adapter.in.rest.notice.dto.query.SearchNoticeCategoriesApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.notice.dto.response.NoticeCategoryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.notice.dto.response.NoticeFieldApiResponse;
import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.notice.dto.query.NoticeCategorySearchParams;
import com.ryuqq.marketplace.application.notice.dto.response.NoticeCategoryPageResult;
import com.ryuqq.marketplace.application.notice.dto.response.NoticeCategoryResult;
import com.ryuqq.marketplace.application.notice.dto.response.NoticeFieldResult;
import java.util.List;
import org.springframework.stereotype.Component;

/** NoticeCategory Query API Mapper. */
@Component
public class NoticeCategoryQueryApiMapper {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;

    public NoticeCategorySearchParams toSearchParams(SearchNoticeCategoriesApiRequest request) {
        int page = request.page() != null ? request.page() : DEFAULT_PAGE;
        int size = request.size() != null ? request.size() : DEFAULT_SIZE;

        CommonSearchParams commonParams =
                CommonSearchParams.of(
                        null, null, null, request.sortKey(), request.sortDirection(), page, size);

        return NoticeCategorySearchParams.of(
                request.active(), request.searchField(), request.searchWord(), commonParams);
    }

    public NoticeCategoryApiResponse toResponse(NoticeCategoryResult result) {
        List<NoticeFieldApiResponse> fieldResponses = result.fields().stream()
                .map(this::toFieldResponse)
                .toList();

        return new NoticeCategoryApiResponse(
                result.id(),
                result.code(),
                result.nameKo(),
                result.nameEn(),
                result.targetCategoryGroup(),
                result.active(),
                fieldResponses,
                DateTimeFormatUtils.formatIso8601(result.createdAt()));
    }

    public List<NoticeCategoryApiResponse> toResponses(List<NoticeCategoryResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    public PageApiResponse<NoticeCategoryApiResponse> toPageResponse(
            NoticeCategoryPageResult pageResult) {
        List<NoticeCategoryApiResponse> responses = toResponses(pageResult.results());
        return PageApiResponse.of(
                responses,
                pageResult.pageMeta().page(),
                pageResult.pageMeta().size(),
                pageResult.pageMeta().totalElements());
    }

    private NoticeFieldApiResponse toFieldResponse(NoticeFieldResult result) {
        return new NoticeFieldApiResponse(
                result.id(),
                result.fieldCode(),
                result.fieldName(),
                result.required(),
                result.sortOrder());
    }
}
