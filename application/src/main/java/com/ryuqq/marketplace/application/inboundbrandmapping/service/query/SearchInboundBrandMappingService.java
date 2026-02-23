package com.ryuqq.marketplace.application.inboundbrandmapping.service.query;

import com.ryuqq.marketplace.application.inboundbrandmapping.dto.query.InboundBrandMappingSearchParams;
import com.ryuqq.marketplace.application.inboundbrandmapping.dto.response.InboundBrandMappingPageResult;
import com.ryuqq.marketplace.application.inboundbrandmapping.dto.response.InboundBrandMappingResult;
import com.ryuqq.marketplace.application.inboundbrandmapping.factory.InboundBrandMappingQueryFactory;
import com.ryuqq.marketplace.application.inboundbrandmapping.manager.InboundBrandMappingReadManager;
import com.ryuqq.marketplace.application.inboundbrandmapping.port.in.query.SearchInboundBrandMappingUseCase;
import com.ryuqq.marketplace.domain.inboundbrandmapping.aggregate.InboundBrandMapping;
import com.ryuqq.marketplace.domain.inboundbrandmapping.query.InboundBrandMappingSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/** 외부 브랜드 매핑 검색 Service. */
@Service
public class SearchInboundBrandMappingService implements SearchInboundBrandMappingUseCase {

    private final InboundBrandMappingQueryFactory queryFactory;
    private final InboundBrandMappingReadManager readManager;

    public SearchInboundBrandMappingService(
            InboundBrandMappingQueryFactory queryFactory,
            InboundBrandMappingReadManager readManager) {
        this.queryFactory = queryFactory;
        this.readManager = readManager;
    }

    @Override
    public InboundBrandMappingPageResult execute(InboundBrandMappingSearchParams params) {
        InboundBrandMappingSearchCriteria criteria = queryFactory.createSearchCriteria(params);
        List<InboundBrandMapping> mappings = readManager.findByCriteria(criteria);
        long totalElements = readManager.countByCriteria(criteria);
        List<InboundBrandMappingResult> results =
                mappings.stream().map(InboundBrandMappingResult::from).toList();
        return InboundBrandMappingPageResult.of(
                results, criteria.page(), criteria.size(), totalElements);
    }
}
