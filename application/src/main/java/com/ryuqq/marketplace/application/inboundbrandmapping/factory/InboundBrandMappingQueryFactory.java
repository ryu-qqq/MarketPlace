package com.ryuqq.marketplace.application.inboundbrandmapping.factory;

import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.application.inboundbrandmapping.dto.query.InboundBrandMappingSearchParams;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.inboundbrandmapping.query.InboundBrandMappingSearchCriteria;
import com.ryuqq.marketplace.domain.inboundbrandmapping.query.InboundBrandMappingSearchField;
import com.ryuqq.marketplace.domain.inboundbrandmapping.query.InboundBrandMappingSortKey;
import com.ryuqq.marketplace.domain.inboundbrandmapping.vo.InboundBrandMappingStatus;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.springframework.stereotype.Component;

/** InboundBrandMapping Query Factory. */
@Component
public class InboundBrandMappingQueryFactory {

    private final CommonVoFactory commonVoFactory;

    public InboundBrandMappingQueryFactory(CommonVoFactory commonVoFactory) {
        this.commonVoFactory = commonVoFactory;
    }

    public InboundBrandMappingSearchCriteria createSearchCriteria(
            InboundBrandMappingSearchParams params) {
        List<InboundBrandMappingStatus> statuses = parseStatuses(params.statuses());

        InboundBrandMappingSortKey sortKey = resolveSortKey(params.commonSearchParams().sortKey());
        SortDirection sortDirection =
                commonVoFactory.parseSortDirection(params.commonSearchParams().sortDirection());
        PageRequest pageRequest =
                commonVoFactory.createPageRequest(
                        params.commonSearchParams().page(), params.commonSearchParams().size());

        QueryContext<InboundBrandMappingSortKey> queryContext =
                commonVoFactory.createQueryContext(
                        sortKey,
                        sortDirection,
                        pageRequest,
                        params.commonSearchParams().includeDeleted());

        InboundBrandMappingSearchField searchField =
                InboundBrandMappingSearchField.fromString(params.searchField());
        String searchWord =
                params.searchWord() != null && !params.searchWord().isBlank()
                        ? params.searchWord().trim()
                        : null;

        return InboundBrandMappingSearchCriteria.of(
                params.inboundSourceId(), statuses, searchField, searchWord, queryContext);
    }

    private InboundBrandMappingSortKey resolveSortKey(String sortKeyString) {
        if (sortKeyString == null || sortKeyString.isBlank()) {
            return InboundBrandMappingSortKey.defaultKey();
        }
        for (InboundBrandMappingSortKey key : InboundBrandMappingSortKey.values()) {
            if (key.fieldName().equalsIgnoreCase(sortKeyString)
                    || key.name().equalsIgnoreCase(sortKeyString)) {
                return key;
            }
        }
        return InboundBrandMappingSortKey.defaultKey();
    }

    private List<InboundBrandMappingStatus> parseStatuses(List<String> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return Collections.emptyList();
        }
        return statuses.stream()
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank())
                .map(s -> InboundBrandMappingStatus.valueOf(s.trim().toUpperCase(Locale.ROOT)))
                .distinct()
                .toList();
    }
}
