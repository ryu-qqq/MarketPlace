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
        ShipmentSortKey sortKey = ShipmentSortKey.fromString(params.searchParams().sortKey());
        SortDirection sortDirection = commonVoFactory.parseSortDirection(params.searchParams().sortDirection());
        PageRequest pageRequest = commonVoFactory.createPageRequest(params.searchParams().page(), params.searchParams().size());

        QueryContext<ShipmentSortKey> queryContext =
                commonVoFactory.createQueryContext(
                        sortKey,
                        sortDirection,
                        pageRequest,
                        params.searchParams().includeDeleted());

        List<ShipmentStatus> statuses = ShipmentStatus.fromStringList(params.statuses());
        ShipmentSearchField searchField = ShipmentSearchField.fromString(params.searchField());
        ShipmentDateField dateField = ShipmentDateField.fromString(params.dateField());
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
}
