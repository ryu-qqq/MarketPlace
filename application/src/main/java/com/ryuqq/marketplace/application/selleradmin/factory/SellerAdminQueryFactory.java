package com.ryuqq.marketplace.application.selleradmin.factory;

import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.application.selleradmin.dto.query.SellerAdminApplicationSearchParams;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.selleradmin.query.SellerAdminSearchCriteria;
import com.ryuqq.marketplace.domain.selleradmin.query.SellerAdminSearchField;
import com.ryuqq.marketplace.domain.selleradmin.query.SellerAdminSortKey;
import org.springframework.stereotype.Component;

/**
 * SellerAdmin Query Factory.
 *
 * <p>Query DTO를 Domain Criteria로 변환합니다.
 *
 * <p>기본값 처리는 REST API Layer (ApiMapper)에서 수행합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Component
public class SellerAdminQueryFactory {

    private final CommonVoFactory commonVoFactory;

    public SellerAdminQueryFactory(CommonVoFactory commonVoFactory) {
        this.commonVoFactory = commonVoFactory;
    }

    /**
     * SellerAdminApplicationSearchParams로부터 SellerAdminSearchCriteria 생성.
     *
     * <p>기본값 처리 없이 단순 변환만 수행합니다.
     *
     * @param params 검색 파라미터 (기본값 적용 완료된 상태)
     * @return SellerAdminSearchCriteria
     */
    public SellerAdminSearchCriteria createCriteria(SellerAdminApplicationSearchParams params) {
        SellerAdminSortKey sortKey = resolveSortKey(params.commonSearchParams().sortKey());
        SortDirection sortDirection =
                commonVoFactory.parseSortDirection(params.commonSearchParams().sortDirection());
        PageRequest pageRequest =
                commonVoFactory.createPageRequest(
                        params.commonSearchParams().page(), params.commonSearchParams().size());

        QueryContext<SellerAdminSortKey> queryContext =
                commonVoFactory.createQueryContext(
                        sortKey,
                        sortDirection,
                        pageRequest,
                        params.commonSearchParams().includeDeleted());

        SellerAdminSearchField searchField =
                SellerAdminSearchField.fromString(params.searchField());

        return SellerAdminSearchCriteria.of(
                params.sellerIds(),
                params.status(),
                searchField,
                params.searchWord(),
                params.dateRange(),
                queryContext);
    }

    private SellerAdminSortKey resolveSortKey(String sortKeyString) {
        if (sortKeyString == null || sortKeyString.isBlank()) {
            return SellerAdminSortKey.defaultKey();
        }

        for (SellerAdminSortKey key : SellerAdminSortKey.values()) {
            if (key.fieldName().equalsIgnoreCase(sortKeyString)
                    || key.name().equalsIgnoreCase(sortKeyString)) {
                return key;
            }
        }

        return SellerAdminSortKey.defaultKey();
    }
}
