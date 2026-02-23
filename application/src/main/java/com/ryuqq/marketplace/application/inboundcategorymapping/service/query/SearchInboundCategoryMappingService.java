package com.ryuqq.marketplace.application.inboundcategorymapping.service.query;

import com.ryuqq.marketplace.application.inboundcategorymapping.dto.query.InboundCategoryMappingSearchParams;
import com.ryuqq.marketplace.application.inboundcategorymapping.dto.response.InboundCategoryMappingPageResult;
import com.ryuqq.marketplace.application.inboundcategorymapping.dto.response.InboundCategoryMappingResult;
import com.ryuqq.marketplace.application.inboundcategorymapping.factory.InboundCategoryMappingQueryFactory;
import com.ryuqq.marketplace.application.inboundcategorymapping.manager.InboundCategoryMappingReadManager;
import com.ryuqq.marketplace.application.inboundcategorymapping.port.in.query.SearchInboundCategoryMappingUseCase;
import com.ryuqq.marketplace.domain.inboundcategorymapping.aggregate.InboundCategoryMapping;
import com.ryuqq.marketplace.domain.inboundcategorymapping.query.InboundCategoryMappingSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/** 외부 카테고리 매핑 검색 Service. */
@Service
public class SearchInboundCategoryMappingService implements SearchInboundCategoryMappingUseCase {

    private final InboundCategoryMappingQueryFactory queryFactory;
    private final InboundCategoryMappingReadManager readManager;

    public SearchInboundCategoryMappingService(
            InboundCategoryMappingQueryFactory queryFactory,
            InboundCategoryMappingReadManager readManager) {
        this.queryFactory = queryFactory;
        this.readManager = readManager;
    }

    @Override
    public InboundCategoryMappingPageResult execute(InboundCategoryMappingSearchParams params) {
        InboundCategoryMappingSearchCriteria criteria = queryFactory.createSearchCriteria(params);
        List<InboundCategoryMapping> mappings = readManager.findByCriteria(criteria);
        long totalElements = readManager.countByCriteria(criteria);
        List<InboundCategoryMappingResult> results =
                mappings.stream().map(InboundCategoryMappingResult::from).toList();
        return InboundCategoryMappingPageResult.of(
                results, criteria.page(), criteria.size(), totalElements);
    }
}
