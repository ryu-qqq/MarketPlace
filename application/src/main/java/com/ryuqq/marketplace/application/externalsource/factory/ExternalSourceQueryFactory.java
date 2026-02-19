package com.ryuqq.marketplace.application.externalsource.factory;

import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.application.externalsource.dto.query.ExternalSourceSearchParams;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.externalsource.query.ExternalSourceSearchCriteria;
import com.ryuqq.marketplace.domain.externalsource.query.ExternalSourceSearchField;
import com.ryuqq.marketplace.domain.externalsource.query.ExternalSourceSortKey;
import com.ryuqq.marketplace.domain.externalsource.vo.ExternalSourceStatus;
import com.ryuqq.marketplace.domain.externalsource.vo.ExternalSourceType;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.springframework.stereotype.Component;

/** ExternalSource Query Factory. */
@Component
public class ExternalSourceQueryFactory {

    private final CommonVoFactory commonVoFactory;

    public ExternalSourceQueryFactory(CommonVoFactory commonVoFactory) {
        this.commonVoFactory = commonVoFactory;
    }

    public ExternalSourceSearchCriteria createSearchCriteria(ExternalSourceSearchParams params) {
        List<ExternalSourceType> types = parseTypes(params.types());
        List<ExternalSourceStatus> statuses = parseStatuses(params.statuses());

        ExternalSourceSortKey sortKey = resolveSortKey(params.commonSearchParams().sortKey());
        SortDirection sortDirection =
                commonVoFactory.parseSortDirection(params.commonSearchParams().sortDirection());
        PageRequest pageRequest =
                commonVoFactory.createPageRequest(
                        params.commonSearchParams().page(), params.commonSearchParams().size());

        QueryContext<ExternalSourceSortKey> queryContext =
                commonVoFactory.createQueryContext(
                        sortKey,
                        sortDirection,
                        pageRequest,
                        params.commonSearchParams().includeDeleted());

        ExternalSourceSearchField searchField =
                ExternalSourceSearchField.fromString(params.searchField());
        String searchWord =
                params.searchWord() != null && !params.searchWord().isBlank()
                        ? params.searchWord().trim()
                        : null;

        return ExternalSourceSearchCriteria.of(
                types, statuses, searchField, searchWord, queryContext);
    }

    private ExternalSourceSortKey resolveSortKey(String sortKeyString) {
        if (sortKeyString == null || sortKeyString.isBlank()) {
            return ExternalSourceSortKey.defaultKey();
        }
        for (ExternalSourceSortKey key : ExternalSourceSortKey.values()) {
            if (key.fieldName().equalsIgnoreCase(sortKeyString)
                    || key.name().equalsIgnoreCase(sortKeyString)) {
                return key;
            }
        }
        return ExternalSourceSortKey.defaultKey();
    }

    private List<ExternalSourceType> parseTypes(List<String> types) {
        if (types == null || types.isEmpty()) {
            return Collections.emptyList();
        }
        return types.stream()
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank())
                .map(s -> ExternalSourceType.valueOf(s.trim().toUpperCase(Locale.ROOT)))
                .distinct()
                .toList();
    }

    private List<ExternalSourceStatus> parseStatuses(List<String> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return Collections.emptyList();
        }
        return statuses.stream()
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank())
                .map(s -> ExternalSourceStatus.valueOf(s.trim().toUpperCase(Locale.ROOT)))
                .distinct()
                .toList();
    }
}
