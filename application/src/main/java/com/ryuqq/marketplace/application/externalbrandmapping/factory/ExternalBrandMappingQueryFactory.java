package com.ryuqq.marketplace.application.externalbrandmapping.factory;

import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.application.externalbrandmapping.dto.query.ExternalBrandMappingSearchParams;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.externalbrandmapping.query.ExternalBrandMappingSearchCriteria;
import com.ryuqq.marketplace.domain.externalbrandmapping.query.ExternalBrandMappingSearchField;
import com.ryuqq.marketplace.domain.externalbrandmapping.query.ExternalBrandMappingSortKey;
import com.ryuqq.marketplace.domain.externalbrandmapping.vo.ExternalBrandMappingStatus;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.springframework.stereotype.Component;

/** ExternalBrandMapping Query Factory. */
@Component
public class ExternalBrandMappingQueryFactory {

    private final CommonVoFactory commonVoFactory;

    public ExternalBrandMappingQueryFactory(CommonVoFactory commonVoFactory) {
        this.commonVoFactory = commonVoFactory;
    }

    public ExternalBrandMappingSearchCriteria createSearchCriteria(
            ExternalBrandMappingSearchParams params) {
        List<ExternalBrandMappingStatus> statuses = parseStatuses(params.statuses());

        ExternalBrandMappingSortKey sortKey = resolveSortKey(params.commonSearchParams().sortKey());
        SortDirection sortDirection =
                commonVoFactory.parseSortDirection(params.commonSearchParams().sortDirection());
        PageRequest pageRequest =
                commonVoFactory.createPageRequest(
                        params.commonSearchParams().page(), params.commonSearchParams().size());

        QueryContext<ExternalBrandMappingSortKey> queryContext =
                commonVoFactory.createQueryContext(
                        sortKey,
                        sortDirection,
                        pageRequest,
                        params.commonSearchParams().includeDeleted());

        ExternalBrandMappingSearchField searchField =
                ExternalBrandMappingSearchField.fromString(params.searchField());
        String searchWord =
                params.searchWord() != null && !params.searchWord().isBlank()
                        ? params.searchWord().trim()
                        : null;

        return ExternalBrandMappingSearchCriteria.of(
                params.externalSourceId(), statuses, searchField, searchWord, queryContext);
    }

    private ExternalBrandMappingSortKey resolveSortKey(String sortKeyString) {
        if (sortKeyString == null || sortKeyString.isBlank()) {
            return ExternalBrandMappingSortKey.defaultKey();
        }
        for (ExternalBrandMappingSortKey key : ExternalBrandMappingSortKey.values()) {
            if (key.fieldName().equalsIgnoreCase(sortKeyString)
                    || key.name().equalsIgnoreCase(sortKeyString)) {
                return key;
            }
        }
        return ExternalBrandMappingSortKey.defaultKey();
    }

    private List<ExternalBrandMappingStatus> parseStatuses(List<String> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return Collections.emptyList();
        }
        return statuses.stream()
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank())
                .map(s -> ExternalBrandMappingStatus.valueOf(s.trim().toUpperCase(Locale.ROOT)))
                .distinct()
                .toList();
    }
}
