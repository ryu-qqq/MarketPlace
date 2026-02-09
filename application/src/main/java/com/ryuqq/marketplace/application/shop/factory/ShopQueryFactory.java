package com.ryuqq.marketplace.application.shop.factory;

import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.application.shop.dto.query.ShopSearchParams;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.shop.query.ShopSearchCriteria;
import com.ryuqq.marketplace.domain.shop.query.ShopSearchField;
import com.ryuqq.marketplace.domain.shop.query.ShopSortKey;
import com.ryuqq.marketplace.domain.shop.vo.ShopStatus;
import java.util.List;
import org.springframework.stereotype.Component;

/** Shop Query Factory. */
@Component
public class ShopQueryFactory {

    private final CommonVoFactory commonVoFactory;

    public ShopQueryFactory(CommonVoFactory commonVoFactory) {
        this.commonVoFactory = commonVoFactory;
    }

    public ShopSearchCriteria createCriteria(ShopSearchParams params) {
        ShopSortKey sortKey = resolveSortKey(params.sortKey());
        SortDirection sortDirection = commonVoFactory.parseSortDirection(params.sortDirection());
        PageRequest pageRequest = commonVoFactory.createPageRequest(params.page(), params.size());

        QueryContext<ShopSortKey> queryContext =
                commonVoFactory.createQueryContext(
                        sortKey,
                        sortDirection,
                        pageRequest,
                        params.searchParams().includeDeleted());

        ShopSearchField searchField = ShopSearchField.fromString(params.searchField());

        List<ShopStatus> statuses =
                params.statuses() != null
                        ? params.statuses().stream().map(ShopStatus::fromString).toList()
                        : List.of();

        return ShopSearchCriteria.of(statuses, searchField, params.searchWord(), queryContext);
    }

    private ShopSortKey resolveSortKey(String sortKeyString) {
        if (sortKeyString == null || sortKeyString.isBlank()) {
            return ShopSortKey.defaultKey();
        }
        for (ShopSortKey key : ShopSortKey.values()) {
            if (key.fieldName().equalsIgnoreCase(sortKeyString)
                    || key.name().equalsIgnoreCase(sortKeyString)) {
                return key;
            }
        }
        return ShopSortKey.defaultKey();
    }
}
