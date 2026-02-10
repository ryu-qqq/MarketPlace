package com.ryuqq.marketplace.application.brandmapping.factory;

import com.ryuqq.marketplace.application.brandmapping.dto.query.BrandMappingSearchParams;
import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.domain.brandmapping.query.BrandMappingSearchCriteria;
import com.ryuqq.marketplace.domain.brandmapping.query.BrandMappingSearchField;
import com.ryuqq.marketplace.domain.brandmapping.query.BrandMappingSortKey;
import com.ryuqq.marketplace.domain.brandmapping.vo.BrandMappingStatus;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import java.util.List;
import org.springframework.stereotype.Component;

/** BrandMapping Query Factory. */
@Component
public class BrandMappingQueryFactory {

    private final CommonVoFactory commonVoFactory;

    public BrandMappingQueryFactory(CommonVoFactory commonVoFactory) {
        this.commonVoFactory = commonVoFactory;
    }

    public BrandMappingSearchCriteria createCriteria(BrandMappingSearchParams params) {
        BrandMappingSortKey sortKey = resolveSortKey(params.sortKey());
        SortDirection sortDirection = commonVoFactory.parseSortDirection(params.sortDirection());
        PageRequest pageRequest = commonVoFactory.createPageRequest(params.page(), params.size());

        QueryContext<BrandMappingSortKey> queryContext =
                commonVoFactory.createQueryContext(sortKey, sortDirection, pageRequest);

        List<BrandMappingStatus> statuses =
                params.statuses() != null
                        ? params.statuses().stream().map(BrandMappingStatus::fromString).toList()
                        : List.of();

        BrandMappingSearchField searchField =
                BrandMappingSearchField.fromString(params.searchField());

        return BrandMappingSearchCriteria.of(
                params.salesChannelBrandIds(),
                params.internalBrandIds(),
                params.salesChannelIds(),
                statuses,
                searchField,
                params.searchWord(),
                queryContext);
    }

    private BrandMappingSortKey resolveSortKey(String sortKeyString) {
        if (sortKeyString == null || sortKeyString.isBlank()) {
            return BrandMappingSortKey.defaultKey();
        }
        for (BrandMappingSortKey key : BrandMappingSortKey.values()) {
            if (key.fieldName().equalsIgnoreCase(sortKeyString)
                    || key.name().equalsIgnoreCase(sortKeyString)) {
                return key;
            }
        }
        return BrandMappingSortKey.defaultKey();
    }
}
