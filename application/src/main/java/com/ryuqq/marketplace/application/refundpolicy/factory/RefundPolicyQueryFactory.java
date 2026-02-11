package com.ryuqq.marketplace.application.refundpolicy.factory;

import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.application.refundpolicy.dto.query.RefundPolicySearchParams;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.refundpolicy.query.RefundPolicySearchCriteria;
import com.ryuqq.marketplace.domain.refundpolicy.query.RefundPolicySortKey;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import org.springframework.stereotype.Component;

/**
 * RefundPolicyQueryFactory - 환불 정책 Query Factory
 *
 * <p>SearchParams → SearchCriteria 변환을 담당합니다.
 *
 * @author ryu-qqq
 */
@Component
public class RefundPolicyQueryFactory {

    private final CommonVoFactory commonVoFactory;

    public RefundPolicyQueryFactory(CommonVoFactory commonVoFactory) {
        this.commonVoFactory = commonVoFactory;
    }

    public RefundPolicySearchCriteria createCriteria(RefundPolicySearchParams params) {
        RefundPolicySortKey sortKey = resolveSortKey(params.sortKey());
        SortDirection sortDirection = commonVoFactory.parseSortDirection(params.sortDirection());
        PageRequest pageRequest = commonVoFactory.createPageRequest(params.page(), params.size());

        QueryContext<RefundPolicySortKey> queryContext =
                commonVoFactory.createQueryContext(
                        sortKey,
                        sortDirection,
                        pageRequest,
                        params.searchParams().includeDeleted());

        SellerId sellerId = SellerId.of(params.sellerId());

        return new RefundPolicySearchCriteria(sellerId, queryContext, params.active());
    }

    private RefundPolicySortKey resolveSortKey(String sortKeyString) {
        if (sortKeyString == null || sortKeyString.isBlank()) {
            return RefundPolicySortKey.defaultKey();
        }

        for (RefundPolicySortKey key : RefundPolicySortKey.values()) {
            if (key.fieldName().equalsIgnoreCase(sortKeyString)
                    || key.name().equalsIgnoreCase(sortKeyString)) {
                return key;
            }
        }

        return RefundPolicySortKey.defaultKey();
    }
}
