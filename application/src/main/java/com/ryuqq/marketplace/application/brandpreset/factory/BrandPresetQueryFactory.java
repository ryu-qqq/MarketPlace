package com.ryuqq.marketplace.application.brandpreset.factory;

import com.ryuqq.marketplace.application.brandpreset.dto.query.BrandPresetSearchParams;
import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.domain.brandpreset.query.BrandPresetSearchCriteria;
import com.ryuqq.marketplace.domain.brandpreset.query.BrandPresetSortKey;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import org.springframework.stereotype.Component;

/** BrandPreset Query Factory. */
@Component
public class BrandPresetQueryFactory {

    private final CommonVoFactory commonVoFactory;

    public BrandPresetQueryFactory(CommonVoFactory commonVoFactory) {
        this.commonVoFactory = commonVoFactory;
    }

    public BrandPresetSearchCriteria createCriteria(BrandPresetSearchParams params) {
        BrandPresetSortKey sortKey = resolveSortKey(params.commonSearchParams().sortKey());
        SortDirection sortDirection =
                commonVoFactory.parseSortDirection(params.commonSearchParams().sortDirection());
        PageRequest pageRequest =
                commonVoFactory.createPageRequest(
                        params.commonSearchParams().page(), params.commonSearchParams().size());

        QueryContext<BrandPresetSortKey> queryContext =
                commonVoFactory.createQueryContext(
                        sortKey,
                        sortDirection,
                        pageRequest,
                        params.commonSearchParams().includeDeleted());

        return new BrandPresetSearchCriteria(
                params.salesChannelIds(),
                params.statuses(),
                params.searchField(),
                params.searchWord(),
                params.commonSearchParams().startDate(),
                params.commonSearchParams().endDate(),
                queryContext);
    }

    private BrandPresetSortKey resolveSortKey(String sortKeyString) {
        if (sortKeyString == null || sortKeyString.isBlank()) {
            return BrandPresetSortKey.defaultKey();
        }
        for (BrandPresetSortKey key : BrandPresetSortKey.values()) {
            if (key.fieldName().equalsIgnoreCase(sortKeyString)
                    || key.name().equalsIgnoreCase(sortKeyString)) {
                return key;
            }
        }
        return BrandPresetSortKey.defaultKey();
    }
}
