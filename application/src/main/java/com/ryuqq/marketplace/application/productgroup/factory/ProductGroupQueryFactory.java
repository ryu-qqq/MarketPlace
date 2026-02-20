package com.ryuqq.marketplace.application.productgroup.factory;

import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.application.productgroup.dto.query.ProductGroupSearchParams;
import com.ryuqq.marketplace.domain.common.vo.DateRange;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.productgroup.query.ProductGroupSearchCriteria;
import com.ryuqq.marketplace.domain.productgroup.query.ProductGroupSearchField;
import com.ryuqq.marketplace.domain.productgroup.query.ProductGroupSortKey;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupStatus;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;

/** ProductGroup Query Factory. */
@Component
public class ProductGroupQueryFactory {

    private final CommonVoFactory commonVoFactory;

    public ProductGroupQueryFactory(CommonVoFactory commonVoFactory) {
        this.commonVoFactory = commonVoFactory;
    }

    public ProductGroupSearchCriteria createCriteria(ProductGroupSearchParams params) {
        ProductGroupSortKey sortKey = resolveSortKey(params.sortKey());
        SortDirection sortDirection = commonVoFactory.parseSortDirection(params.sortDirection());
        PageRequest pageRequest = commonVoFactory.createPageRequest(params.page(), params.size());

        QueryContext<ProductGroupSortKey> queryContext =
                commonVoFactory.createQueryContext(
                        sortKey,
                        sortDirection,
                        pageRequest,
                        params.searchParams().includeDeleted());

        ProductGroupSearchField searchField =
                ProductGroupSearchField.fromString(params.searchField());

        List<ProductGroupStatus> statuses =
                params.statuses() != null
                        ? params.statuses().stream()
                                .map(s -> ProductGroupStatus.valueOf(s.toUpperCase(Locale.ROOT)))
                                .toList()
                        : List.of();

        List<Long> brandIds = params.brandIds() != null ? params.brandIds() : List.of();
        List<Long> categoryIds = params.categoryIds() != null ? params.categoryIds() : List.of();
        List<Long> productGroupIds =
                params.productGroupIds() != null ? params.productGroupIds() : List.of();

        DateRange dateRange =
                (params.startDate() != null || params.endDate() != null)
                        ? DateRange.of(params.startDate(), params.endDate())
                        : null;

        return ProductGroupSearchCriteria.of(
                statuses,
                params.sellerIds(),
                brandIds,
                categoryIds,
                productGroupIds,
                searchField,
                params.searchWord(),
                dateRange,
                queryContext);
    }

    private ProductGroupSortKey resolveSortKey(String sortKeyString) {
        if (sortKeyString == null || sortKeyString.isBlank()) {
            return ProductGroupSortKey.defaultKey();
        }
        for (ProductGroupSortKey key : ProductGroupSortKey.values()) {
            if (key.fieldName().equalsIgnoreCase(sortKeyString)
                    || key.name().equalsIgnoreCase(sortKeyString)) {
                return key;
            }
        }
        return ProductGroupSortKey.defaultKey();
    }
}
