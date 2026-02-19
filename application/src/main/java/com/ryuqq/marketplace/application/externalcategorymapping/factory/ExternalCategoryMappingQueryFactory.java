package com.ryuqq.marketplace.application.externalcategorymapping.factory;

import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.application.externalcategorymapping.dto.query.ExternalCategoryMappingSearchParams;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.externalcategorymapping.query.ExternalCategoryMappingSearchCriteria;
import com.ryuqq.marketplace.domain.externalcategorymapping.query.ExternalCategoryMappingSearchField;
import com.ryuqq.marketplace.domain.externalcategorymapping.query.ExternalCategoryMappingSortKey;
import com.ryuqq.marketplace.domain.externalcategorymapping.vo.ExternalCategoryMappingStatus;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.springframework.stereotype.Component;

/** ExternalCategoryMapping Query Factory. */
@Component
public class ExternalCategoryMappingQueryFactory {

    private final CommonVoFactory commonVoFactory;

    public ExternalCategoryMappingQueryFactory(CommonVoFactory commonVoFactory) {
        this.commonVoFactory = commonVoFactory;
    }

    public ExternalCategoryMappingSearchCriteria createSearchCriteria(
            ExternalCategoryMappingSearchParams params) {
        List<ExternalCategoryMappingStatus> statuses = parseStatuses(params.statuses());

        ExternalCategoryMappingSortKey sortKey =
                resolveSortKey(params.commonSearchParams().sortKey());
        SortDirection sortDirection =
                commonVoFactory.parseSortDirection(params.commonSearchParams().sortDirection());
        PageRequest pageRequest =
                commonVoFactory.createPageRequest(
                        params.commonSearchParams().page(), params.commonSearchParams().size());

        QueryContext<ExternalCategoryMappingSortKey> queryContext =
                commonVoFactory.createQueryContext(
                        sortKey,
                        sortDirection,
                        pageRequest,
                        params.commonSearchParams().includeDeleted());

        ExternalCategoryMappingSearchField searchField =
                ExternalCategoryMappingSearchField.fromString(params.searchField());
        String searchWord =
                params.searchWord() != null && !params.searchWord().isBlank()
                        ? params.searchWord().trim()
                        : null;

        return ExternalCategoryMappingSearchCriteria.of(
                params.externalSourceId(), statuses, searchField, searchWord, queryContext);
    }

    private ExternalCategoryMappingSortKey resolveSortKey(String sortKeyString) {
        if (sortKeyString == null || sortKeyString.isBlank()) {
            return ExternalCategoryMappingSortKey.defaultKey();
        }
        for (ExternalCategoryMappingSortKey key : ExternalCategoryMappingSortKey.values()) {
            if (key.fieldName().equalsIgnoreCase(sortKeyString)
                    || key.name().equalsIgnoreCase(sortKeyString)) {
                return key;
            }
        }
        return ExternalCategoryMappingSortKey.defaultKey();
    }

    private List<ExternalCategoryMappingStatus> parseStatuses(List<String> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return Collections.emptyList();
        }
        return statuses.stream()
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank())
                .map(s -> ExternalCategoryMappingStatus.valueOf(s.trim().toUpperCase(Locale.ROOT)))
                .distinct()
                .toList();
    }
}
