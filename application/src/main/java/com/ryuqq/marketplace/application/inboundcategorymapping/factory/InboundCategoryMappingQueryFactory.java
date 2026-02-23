package com.ryuqq.marketplace.application.inboundcategorymapping.factory;

import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.application.inboundcategorymapping.dto.query.InboundCategoryMappingSearchParams;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.inboundcategorymapping.query.InboundCategoryMappingSearchCriteria;
import com.ryuqq.marketplace.domain.inboundcategorymapping.query.InboundCategoryMappingSearchField;
import com.ryuqq.marketplace.domain.inboundcategorymapping.query.InboundCategoryMappingSortKey;
import com.ryuqq.marketplace.domain.inboundcategorymapping.vo.InboundCategoryMappingStatus;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.springframework.stereotype.Component;

/** InboundCategoryMapping Query Factory. */
@Component
public class InboundCategoryMappingQueryFactory {

    private final CommonVoFactory commonVoFactory;

    public InboundCategoryMappingQueryFactory(CommonVoFactory commonVoFactory) {
        this.commonVoFactory = commonVoFactory;
    }

    public InboundCategoryMappingSearchCriteria createSearchCriteria(
            InboundCategoryMappingSearchParams params) {
        List<InboundCategoryMappingStatus> statuses = parseStatuses(params.statuses());

        InboundCategoryMappingSortKey sortKey =
                resolveSortKey(params.commonSearchParams().sortKey());
        SortDirection sortDirection =
                commonVoFactory.parseSortDirection(params.commonSearchParams().sortDirection());
        PageRequest pageRequest =
                commonVoFactory.createPageRequest(
                        params.commonSearchParams().page(), params.commonSearchParams().size());

        QueryContext<InboundCategoryMappingSortKey> queryContext =
                commonVoFactory.createQueryContext(
                        sortKey,
                        sortDirection,
                        pageRequest,
                        params.commonSearchParams().includeDeleted());

        InboundCategoryMappingSearchField searchField =
                InboundCategoryMappingSearchField.fromString(params.searchField());
        String searchWord =
                params.searchWord() != null && !params.searchWord().isBlank()
                        ? params.searchWord().trim()
                        : null;

        return InboundCategoryMappingSearchCriteria.of(
                params.inboundSourceId(), statuses, searchField, searchWord, queryContext);
    }

    private InboundCategoryMappingSortKey resolveSortKey(String sortKeyString) {
        if (sortKeyString == null || sortKeyString.isBlank()) {
            return InboundCategoryMappingSortKey.defaultKey();
        }
        for (InboundCategoryMappingSortKey key : InboundCategoryMappingSortKey.values()) {
            if (key.fieldName().equalsIgnoreCase(sortKeyString)
                    || key.name().equalsIgnoreCase(sortKeyString)) {
                return key;
            }
        }
        return InboundCategoryMappingSortKey.defaultKey();
    }

    private List<InboundCategoryMappingStatus> parseStatuses(List<String> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return Collections.emptyList();
        }
        return statuses.stream()
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank())
                .map(s -> InboundCategoryMappingStatus.valueOf(s.trim().toUpperCase(Locale.ROOT)))
                .distinct()
                .toList();
    }
}
