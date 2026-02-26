package com.ryuqq.marketplace.application.sellerapplication.factory;

import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.application.sellerapplication.dto.query.SellerApplicationSearchParams;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.sellerapplication.query.SellerApplicationSearchCriteria;
import com.ryuqq.marketplace.domain.sellerapplication.query.SellerApplicationSearchField;
import com.ryuqq.marketplace.domain.sellerapplication.query.SellerApplicationSortKey;
import org.springframework.stereotype.Component;

/**
 * SellerApplication Query Factory.
 *
 * <p>Query DTO를 Domain Criteria로 변환합니다.
 *
 * <p>기본값 처리는 REST API Layer (ApiMapper)에서 수행합니다.
 *
 * @author ryu-qqq
 */
@Component
public class SellerApplicationQueryFactory {

    private final CommonVoFactory commonVoFactory;

    public SellerApplicationQueryFactory(CommonVoFactory commonVoFactory) {
        this.commonVoFactory = commonVoFactory;
    }

    /**
     * SellerApplicationSearchParams로부터 SellerApplicationSearchCriteria 생성.
     *
     * <p>기본값 처리 없이 단순 변환만 수행합니다.
     *
     * @param params 검색 파라미터 (기본값 적용 완료된 상태)
     * @return SellerApplicationSearchCriteria
     */
    public SellerApplicationSearchCriteria createCriteria(SellerApplicationSearchParams params) {
        SellerApplicationSortKey sortKey = resolveSortKey(params.sortKey());
        SortDirection sortDirection = commonVoFactory.parseSortDirection(params.sortDirection());
        PageRequest pageRequest = commonVoFactory.createPageRequest(params.page(), params.size());

        QueryContext<SellerApplicationSortKey> queryContext =
                commonVoFactory.createQueryContext(
                        sortKey,
                        sortDirection,
                        pageRequest,
                        params.searchParams().includeDeleted());

        SellerApplicationSearchField searchField =
                SellerApplicationSearchField.fromString(params.searchField());

        return SellerApplicationSearchCriteria.of(
                params.status(), searchField, params.searchWord(), queryContext);
    }

    private SellerApplicationSortKey resolveSortKey(String sortKeyString) {
        if (sortKeyString == null || sortKeyString.isBlank()) {
            return SellerApplicationSortKey.defaultKey();
        }

        for (SellerApplicationSortKey key : SellerApplicationSortKey.values()) {
            if (key.fieldName().equalsIgnoreCase(sortKeyString)
                    || key.name().equalsIgnoreCase(sortKeyString)) {
                return key;
            }
        }

        return SellerApplicationSortKey.defaultKey();
    }
}
