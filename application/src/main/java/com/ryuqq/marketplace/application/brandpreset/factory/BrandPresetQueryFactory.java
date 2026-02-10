package com.ryuqq.marketplace.application.brandpreset.factory;

import com.ryuqq.marketplace.application.brandpreset.dto.query.BrandPresetSearchParams;
import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.domain.brandpreset.query.BrandPresetSearchCriteria;
import com.ryuqq.marketplace.domain.brandpreset.query.BrandPresetSortKey;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import org.springframework.stereotype.Component;

/** BrandPreset Query Factory. */
@Component
public class BrandPresetQueryFactory {

    private final CommonVoFactory commonVoFactory;

    public BrandPresetQueryFactory(CommonVoFactory commonVoFactory) {
        this.commonVoFactory = commonVoFactory;
    }

    public BrandPresetSearchCriteria createCriteria(BrandPresetSearchParams params) {
        BrandPresetSortKey sortKey = BrandPresetSortKey.defaultKey();
        SortDirection sortDirection =
                params.sortDirection() != null
                        ? commonVoFactory.parseSortDirection(params.sortDirection())
                        : SortDirection.DESC;
        int page = params.page() != null ? params.page() : 0;
        int size = params.size() != null ? params.size() : 20;
        PageRequest pageRequest = commonVoFactory.createPageRequest(page, size);

        QueryContext<BrandPresetSortKey> queryContext =
                commonVoFactory.createQueryContext(sortKey, sortDirection, pageRequest);

        LocalDate startDate = parseDate(params.startDate());
        LocalDate endDate = parseDate(params.endDate());

        return new BrandPresetSearchCriteria(
                params.salesChannelIds(),
                params.statuses(),
                params.searchField(),
                params.searchWord(),
                startDate,
                endDate,
                queryContext);
    }

    private LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(dateString);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
