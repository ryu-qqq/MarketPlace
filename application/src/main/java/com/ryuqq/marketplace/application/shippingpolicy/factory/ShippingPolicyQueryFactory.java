package com.ryuqq.marketplace.application.shippingpolicy.factory;

import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.application.shippingpolicy.dto.query.ShippingPolicySearchParams;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.query.ShippingPolicySearchCriteria;
import com.ryuqq.marketplace.domain.shippingpolicy.query.ShippingPolicySortKey;
import org.springframework.stereotype.Component;

/**
 * ShippingPolicyQueryFactory - 배송 정책 Query Factory
 *
 * <p>SearchParams → SearchCriteria 변환을 담당합니다.
 *
 * @author ryu-qqq
 */
@Component
public class ShippingPolicyQueryFactory {

    private final CommonVoFactory commonVoFactory;

    public ShippingPolicyQueryFactory(CommonVoFactory commonVoFactory) {
        this.commonVoFactory = commonVoFactory;
    }

    public ShippingPolicySearchCriteria createCriteria(ShippingPolicySearchParams params) {
        ShippingPolicySortKey sortKey = resolveSortKey(params.sortKey());
        SortDirection sortDirection = commonVoFactory.parseSortDirection(params.sortDirection());
        PageRequest pageRequest = commonVoFactory.createPageRequest(params.page(), params.size());

        QueryContext<ShippingPolicySortKey> queryContext =
                commonVoFactory.createQueryContext(
                        sortKey,
                        sortDirection,
                        pageRequest,
                        params.searchParams().includeDeleted());

        SellerId sellerId = SellerId.of(params.sellerId());

        return new ShippingPolicySearchCriteria(sellerId, queryContext, params.active());
    }

    private ShippingPolicySortKey resolveSortKey(String sortKeyString) {
        if (sortKeyString == null || sortKeyString.isBlank()) {
            return ShippingPolicySortKey.defaultKey();
        }

        for (ShippingPolicySortKey key : ShippingPolicySortKey.values()) {
            if (key.fieldName().equalsIgnoreCase(sortKeyString)
                    || key.name().equalsIgnoreCase(sortKeyString)) {
                return key;
            }
        }

        return ShippingPolicySortKey.defaultKey();
    }
}
