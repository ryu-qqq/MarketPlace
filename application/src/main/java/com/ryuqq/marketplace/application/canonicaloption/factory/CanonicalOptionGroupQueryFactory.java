package com.ryuqq.marketplace.application.canonicaloption.factory;

import com.ryuqq.marketplace.application.canonicaloption.dto.query.CanonicalOptionGroupSearchParams;
import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.domain.canonicaloption.query.CanonicalOptionGroupSearchCriteria;
import com.ryuqq.marketplace.domain.canonicaloption.query.CanonicalOptionGroupSortKey;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import org.springframework.stereotype.Component;

/** 캐노니컬 옵션 그룹 Query Factory. */
@Component
public class CanonicalOptionGroupQueryFactory {

    private final CommonVoFactory commonVoFactory;

    public CanonicalOptionGroupQueryFactory(CommonVoFactory commonVoFactory) {
        this.commonVoFactory = commonVoFactory;
    }

    public CanonicalOptionGroupSearchCriteria createCriteria(
            CanonicalOptionGroupSearchParams params) {
        CanonicalOptionGroupSortKey sortKey = resolveSortKey(params.commonSearchParams().sortKey());
        SortDirection sortDirection =
                commonVoFactory.parseSortDirection(params.commonSearchParams().sortDirection());
        PageRequest pageRequest =
                commonVoFactory.createPageRequest(
                        params.commonSearchParams().page(), params.commonSearchParams().size());

        QueryContext<CanonicalOptionGroupSortKey> queryContext =
                commonVoFactory.createQueryContext(
                        sortKey,
                        sortDirection,
                        pageRequest,
                        params.commonSearchParams().includeDeleted());

        return new CanonicalOptionGroupSearchCriteria(
                params.active(), params.searchField(), params.searchWord(), queryContext);
    }

    private CanonicalOptionGroupSortKey resolveSortKey(String sortKeyString) {
        if (sortKeyString == null || sortKeyString.isBlank()) {
            return CanonicalOptionGroupSortKey.defaultKey();
        }
        for (CanonicalOptionGroupSortKey key : CanonicalOptionGroupSortKey.values()) {
            if (key.fieldName().equalsIgnoreCase(sortKeyString)
                    || key.name().equalsIgnoreCase(sortKeyString)) {
                return key;
            }
        }
        return CanonicalOptionGroupSortKey.defaultKey();
    }
}
