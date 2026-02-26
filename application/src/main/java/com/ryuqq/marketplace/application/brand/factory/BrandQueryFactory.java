package com.ryuqq.marketplace.application.brand.factory;

import com.ryuqq.marketplace.application.brand.dto.query.BrandSearchParams;
import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.domain.brand.query.BrandSearchCriteria;
import com.ryuqq.marketplace.domain.brand.query.BrandSearchField;
import com.ryuqq.marketplace.domain.brand.query.BrandSortKey;
import com.ryuqq.marketplace.domain.brand.vo.BrandStatus;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import java.util.List;
import org.springframework.stereotype.Component;

/** Brand Query Factory. */
@Component
public class BrandQueryFactory {

    private final CommonVoFactory commonVoFactory;

    public BrandQueryFactory(CommonVoFactory commonVoFactory) {
        this.commonVoFactory = commonVoFactory;
    }

    public BrandSearchCriteria createCriteria(BrandSearchParams params) {
        BrandSortKey sortKey = resolveSortKey(params.sortKey());
        SortDirection sortDirection = commonVoFactory.parseSortDirection(params.sortDirection());
        PageRequest pageRequest = commonVoFactory.createPageRequest(params.page(), params.size());

        QueryContext<BrandSortKey> queryContext =
                commonVoFactory.createQueryContext(
                        sortKey,
                        sortDirection,
                        pageRequest,
                        params.searchParams().includeDeleted());

        BrandSearchField searchField = BrandSearchField.fromString(params.searchField());

        List<BrandStatus> statuses =
                params.statuses() != null
                        ? params.statuses().stream().map(BrandStatus::fromString).toList()
                        : List.of();

        return BrandSearchCriteria.of(statuses, searchField, params.searchWord(), queryContext);
    }

    private BrandSortKey resolveSortKey(String sortKeyString) {
        if (sortKeyString == null || sortKeyString.isBlank()) {
            return BrandSortKey.defaultKey();
        }
        for (BrandSortKey key : BrandSortKey.values()) {
            if (key.fieldName().equalsIgnoreCase(sortKeyString)
                    || key.name().equalsIgnoreCase(sortKeyString)) {
                return key;
            }
        }
        return BrandSortKey.defaultKey();
    }
}
