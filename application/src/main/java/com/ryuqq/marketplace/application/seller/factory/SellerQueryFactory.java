package com.ryuqq.marketplace.application.seller.factory;

import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.application.seller.dto.query.SellerSearchParams;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.seller.query.SellerSearchCriteria;
import com.ryuqq.marketplace.domain.seller.query.SellerSearchField;
import com.ryuqq.marketplace.domain.seller.query.SellerSortKey;
import org.springframework.stereotype.Component;

/**
 * Seller Query Factory.
 *
 * <p>Query DTO를 Domain Criteria로 변환합니다.
 *
 * <p>기본값 처리는 REST API Layer (ApiMapper)에서 수행합니다.
 */
@Component
public class SellerQueryFactory {

    private final CommonVoFactory commonVoFactory;

    public SellerQueryFactory(CommonVoFactory commonVoFactory) {
        this.commonVoFactory = commonVoFactory;
    }

    /**
     * SellerSearchParams로부터 SellerSearchCriteria 생성.
     *
     * <p>기본값 처리 없이 단순 변환만 수행합니다.
     *
     * @param params 검색 파라미터 (기본값 적용 완료된 상태)
     * @return SellerSearchCriteria
     */
    public SellerSearchCriteria createCriteria(SellerSearchParams params) {
        SellerSortKey sortKey = resolveSortKey(params.sortKey());
        SortDirection sortDirection = commonVoFactory.parseSortDirection(params.sortDirection());
        PageRequest pageRequest = commonVoFactory.createPageRequest(params.page(), params.size());

        QueryContext<SellerSortKey> queryContext =
                commonVoFactory.createQueryContext(
                        sortKey,
                        sortDirection,
                        pageRequest,
                        params.searchParams().includeDeleted());

        SellerSearchField searchField = SellerSearchField.fromString(params.searchField());

        return SellerSearchCriteria.of(
                params.active(), searchField, params.searchWord(), queryContext);
    }

    private SellerSortKey resolveSortKey(String sortKeyString) {
        if (sortKeyString == null || sortKeyString.isBlank()) {
            return SellerSortKey.defaultKey();
        }

        for (SellerSortKey key : SellerSortKey.values()) {
            if (key.fieldName().equalsIgnoreCase(sortKeyString)
                    || key.name().equalsIgnoreCase(sortKeyString)) {
                return key;
            }
        }

        return SellerSortKey.defaultKey();
    }
}
