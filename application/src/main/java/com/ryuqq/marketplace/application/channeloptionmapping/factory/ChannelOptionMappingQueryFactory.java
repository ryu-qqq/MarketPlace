package com.ryuqq.marketplace.application.channeloptionmapping.factory;

import com.ryuqq.marketplace.application.channeloptionmapping.dto.query.ChannelOptionMappingSearchParams;
import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.domain.channeloptionmapping.query.ChannelOptionMappingSearchCriteria;
import com.ryuqq.marketplace.domain.channeloptionmapping.query.ChannelOptionMappingSortKey;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import org.springframework.stereotype.Component;

/** ChannelOptionMapping Query Factory. */
@Component
public class ChannelOptionMappingQueryFactory {

    private final CommonVoFactory commonVoFactory;

    public ChannelOptionMappingQueryFactory(CommonVoFactory commonVoFactory) {
        this.commonVoFactory = commonVoFactory;
    }

    public ChannelOptionMappingSearchCriteria createCriteria(
            ChannelOptionMappingSearchParams params) {
        ChannelOptionMappingSortKey sortKey = resolveSortKey(params.sortKey());
        SortDirection sortDirection = commonVoFactory.parseSortDirection(params.sortDirection());
        PageRequest pageRequest = commonVoFactory.createPageRequest(params.page(), params.size());

        QueryContext<ChannelOptionMappingSortKey> queryContext =
                commonVoFactory.createQueryContext(
                        sortKey,
                        sortDirection,
                        pageRequest,
                        params.searchParams().includeDeleted());

        return ChannelOptionMappingSearchCriteria.of(
                params.salesChannelId(), params.canonicalOptionGroupId(), queryContext);
    }

    private ChannelOptionMappingSortKey resolveSortKey(String sortKeyString) {
        if (sortKeyString == null || sortKeyString.isBlank()) {
            return ChannelOptionMappingSortKey.defaultKey();
        }
        for (ChannelOptionMappingSortKey key : ChannelOptionMappingSortKey.values()) {
            if (key.fieldName().equalsIgnoreCase(sortKeyString)
                    || key.name().equalsIgnoreCase(sortKeyString)) {
                return key;
            }
        }
        return ChannelOptionMappingSortKey.defaultKey();
    }
}
