package com.ryuqq.marketplace.adapter.in.rest.canonicaloption.mapper;

import com.ryuqq.marketplace.adapter.in.rest.canonicaloption.dto.query.SearchCanonicalOptionGroupsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.canonicaloption.dto.response.CanonicalOptionGroupApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.canonicaloption.dto.response.CanonicalOptionValueApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.marketplace.application.canonicaloption.dto.query.CanonicalOptionGroupSearchParams;
import com.ryuqq.marketplace.application.canonicaloption.dto.response.CanonicalOptionGroupPageResult;
import com.ryuqq.marketplace.application.canonicaloption.dto.response.CanonicalOptionGroupResult;
import com.ryuqq.marketplace.application.canonicaloption.dto.response.CanonicalOptionValueResult;
import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import java.util.List;
import org.springframework.stereotype.Component;

/** CanonicalOptionGroup Query API Mapper. */
@Component
public class CanonicalOptionGroupQueryApiMapper {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;

    public CanonicalOptionGroupSearchParams toSearchParams(
            SearchCanonicalOptionGroupsApiRequest request) {
        int page = request.page() != null ? request.page() : DEFAULT_PAGE;
        int size = request.size() != null ? request.size() : DEFAULT_SIZE;

        CommonSearchParams commonParams =
                CommonSearchParams.of(
                        null, null, null, request.sortKey(), request.sortDirection(), page, size);

        return CanonicalOptionGroupSearchParams.of(
                request.active(), request.searchField(), request.searchWord(), commonParams);
    }

    public CanonicalOptionGroupApiResponse toResponse(CanonicalOptionGroupResult result) {
        List<CanonicalOptionValueApiResponse> valueResponses = result.values().stream()
                .map(this::toValueResponse)
                .toList();

        return new CanonicalOptionGroupApiResponse(
                result.id(),
                result.code(),
                result.nameKo(),
                result.nameEn(),
                result.active(),
                valueResponses,
                DateTimeFormatUtils.formatIso8601(result.createdAt()));
    }

    public List<CanonicalOptionGroupApiResponse> toResponses(
            List<CanonicalOptionGroupResult> results) {
        return results.stream().map(this::toResponse).toList();
    }

    public PageApiResponse<CanonicalOptionGroupApiResponse> toPageResponse(
            CanonicalOptionGroupPageResult pageResult) {
        List<CanonicalOptionGroupApiResponse> responses = toResponses(pageResult.results());
        return PageApiResponse.of(
                responses,
                pageResult.pageMeta().page(),
                pageResult.pageMeta().size(),
                pageResult.pageMeta().totalElements());
    }

    private CanonicalOptionValueApiResponse toValueResponse(CanonicalOptionValueResult result) {
        return new CanonicalOptionValueApiResponse(
                result.id(),
                result.code(),
                result.nameKo(),
                result.nameEn(),
                result.sortOrder());
    }
}
