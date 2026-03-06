package com.ryuqq.marketplace.application.outboundproduct.factory;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.outboundproduct.dto.query.OmsProductSearchParams;
import com.ryuqq.marketplace.domain.common.vo.DateRange;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.outboundproduct.query.OmsProductSearchCriteria;
import com.ryuqq.marketplace.domain.outboundproduct.query.OmsProductSearchField;
import com.ryuqq.marketplace.domain.outboundproduct.query.OmsProductSortKey;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncStatus;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupStatus;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;

/** OMS 상품 Query Factory. OmsProductSearchParams → OmsProductSearchCriteria 변환. */
@Component
public class OmsProductQueryFactory {

    public OmsProductSearchCriteria createCriteria(OmsProductSearchParams params) {
        CommonSearchParams common = params.commonSearchParams();
        QueryContext<OmsProductSortKey> queryContext =
                common.toQueryContext(OmsProductSortKey.class);

        List<ProductGroupStatus> statuses = parseStatuses(params.statuses());
        List<SyncStatus> syncStatuses = parseSyncStatuses(params.syncStatuses());
        OmsProductSearchField searchField = OmsProductSearchField.fromString(params.searchField());
        DateRange dateRange =
                (common.startDate() != null || common.endDate() != null)
                        ? DateRange.of(common.startDate(), common.endDate())
                        : null;

        return new OmsProductSearchCriteria(
                statuses,
                syncStatuses,
                params.shopIds(),
                params.partnerIds(),
                params.productCodes(),
                searchField,
                params.searchWord(),
                dateRange,
                params.dateType(),
                queryContext);
    }

    private List<ProductGroupStatus> parseStatuses(List<String> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return List.of();
        }
        return statuses.stream()
                .map(
                        s -> {
                            try {
                                return ProductGroupStatus.valueOf(s);
                            } catch (IllegalArgumentException e) {
                                return null;
                            }
                        })
                .filter(Objects::nonNull)
                .toList();
    }

    private List<SyncStatus> parseSyncStatuses(List<String> syncStatuses) {
        if (syncStatuses == null || syncStatuses.isEmpty()) {
            return List.of();
        }
        return syncStatuses.stream()
                .map(
                        s -> {
                            try {
                                return SyncStatus.valueOf(s);
                            } catch (IllegalArgumentException e) {
                                return null;
                            }
                        })
                .filter(Objects::nonNull)
                .toList();
    }
}
