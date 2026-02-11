package com.ryuqq.marketplace.application.notice.factory;

import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.application.notice.dto.query.NoticeCategorySearchParams;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.notice.query.NoticeCategorySearchCriteria;
import com.ryuqq.marketplace.domain.notice.query.NoticeCategorySortKey;
import org.springframework.stereotype.Component;

/** 고시정보 카테고리 Query Factory. */
@Component
public class NoticeCategoryQueryFactory {

    private final CommonVoFactory commonVoFactory;

    public NoticeCategoryQueryFactory(CommonVoFactory commonVoFactory) {
        this.commonVoFactory = commonVoFactory;
    }

    public NoticeCategorySearchCriteria createCriteria(NoticeCategorySearchParams params) {
        NoticeCategorySortKey sortKey = resolveSortKey(params.commonSearchParams().sortKey());
        SortDirection sortDirection =
                commonVoFactory.parseSortDirection(params.commonSearchParams().sortDirection());
        PageRequest pageRequest =
                commonVoFactory.createPageRequest(
                        params.commonSearchParams().page(), params.commonSearchParams().size());

        QueryContext<NoticeCategorySortKey> queryContext =
                commonVoFactory.createQueryContext(
                        sortKey,
                        sortDirection,
                        pageRequest,
                        params.commonSearchParams().includeDeleted());

        return new NoticeCategorySearchCriteria(
                params.active(), params.searchField(), params.searchWord(), queryContext);
    }

    private NoticeCategorySortKey resolveSortKey(String sortKeyString) {
        if (sortKeyString == null || sortKeyString.isBlank()) {
            return NoticeCategorySortKey.defaultKey();
        }
        for (NoticeCategorySortKey key : NoticeCategorySortKey.values()) {
            if (key.fieldName().equalsIgnoreCase(sortKeyString)
                    || key.name().equalsIgnoreCase(sortKeyString)) {
                return key;
            }
        }
        return NoticeCategorySortKey.defaultKey();
    }
}
