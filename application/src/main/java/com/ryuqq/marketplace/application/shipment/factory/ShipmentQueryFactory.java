package com.ryuqq.marketplace.application.shipment.factory;

import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.application.shipment.dto.query.ShipmentSearchParams;
import com.ryuqq.marketplace.domain.common.vo.DateRange;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.shipment.query.ShipmentDateField;
import com.ryuqq.marketplace.domain.shipment.query.ShipmentSearchCriteria;
import com.ryuqq.marketplace.domain.shipment.query.ShipmentSearchField;
import com.ryuqq.marketplace.domain.shipment.query.ShipmentSortKey;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentStatus;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;

/**
 * Shipment Query Factory.
 *
 * <p>Query DTO를 Domain Criteria로 변환합니다.
 */
@Component
public class ShipmentQueryFactory {

    private final CommonVoFactory commonVoFactory;

    public ShipmentQueryFactory(CommonVoFactory commonVoFactory) {
        this.commonVoFactory = commonVoFactory;
    }

    /**
     * ShipmentSearchParams로부터 ShipmentSearchCriteria 생성.
     *
     * @param params 검색 파라미터
     * @return ShipmentSearchCriteria
     */
    public ShipmentSearchCriteria createCriteria(ShipmentSearchParams params) {
        ShipmentSortKey sortKey = resolveSortKey(params.sortKey());
        SortDirection sortDirection = commonVoFactory.parseSortDirection(params.sortDirection());
        PageRequest pageRequest = commonVoFactory.createPageRequest(params.page(), params.size());

        QueryContext<ShipmentSortKey> queryContext =
                commonVoFactory.createQueryContext(
                        sortKey,
                        sortDirection,
                        pageRequest,
                        params.searchParams().includeDeleted());

        List<ShipmentStatus> statuses = resolveStatuses(params.statuses());
        ShipmentSearchField searchField = ShipmentSearchField.fromString(params.searchField());
        ShipmentDateField dateField = resolveDateField(params.dateField());
        DateRange dateRange =
                commonVoFactory.createDateRange(
                        params.searchParams().startDate(), params.searchParams().endDate());

        return ShipmentSearchCriteria.of(
                statuses,
                params.sellerIds(),
                params.shopOrderNos(),
                searchField,
                params.searchWord(),
                dateRange,
                dateField,
                queryContext);
    }

    private ShipmentSortKey resolveSortKey(String sortKeyString) {
        if (sortKeyString == null || sortKeyString.isBlank()) {
            return ShipmentSortKey.defaultKey();
        }

        for (ShipmentSortKey key : ShipmentSortKey.values()) {
            if (key.fieldName().equalsIgnoreCase(sortKeyString)
                    || key.name().equalsIgnoreCase(sortKeyString)) {
                return key;
            }
        }

        return ShipmentSortKey.defaultKey();
    }

    private List<ShipmentStatus> resolveStatuses(List<String> statusStrings) {
        if (statusStrings == null || statusStrings.isEmpty()) {
            return List.of();
        }

        return statusStrings.stream()
                .map(s -> ShipmentStatus.valueOf(s.toUpperCase(Locale.ROOT)))
                .toList();
    }

    private ShipmentDateField resolveDateField(String dateFieldString) {
        if (dateFieldString == null || dateFieldString.isBlank()) {
            return null;
        }

        for (ShipmentDateField field : ShipmentDateField.values()) {
            if (field.name().equalsIgnoreCase(dateFieldString)) {
                return field;
            }
        }

        return null;
    }
}
