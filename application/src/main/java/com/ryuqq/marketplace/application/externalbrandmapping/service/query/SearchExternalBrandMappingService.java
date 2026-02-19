package com.ryuqq.marketplace.application.externalbrandmapping.service.query;

import com.ryuqq.marketplace.application.externalbrandmapping.dto.query.ExternalBrandMappingSearchParams;
import com.ryuqq.marketplace.application.externalbrandmapping.dto.response.ExternalBrandMappingPageResult;
import com.ryuqq.marketplace.application.externalbrandmapping.dto.response.ExternalBrandMappingResult;
import com.ryuqq.marketplace.application.externalbrandmapping.factory.ExternalBrandMappingQueryFactory;
import com.ryuqq.marketplace.application.externalbrandmapping.manager.ExternalBrandMappingReadManager;
import com.ryuqq.marketplace.application.externalbrandmapping.port.in.query.SearchExternalBrandMappingUseCase;
import com.ryuqq.marketplace.domain.externalbrandmapping.aggregate.ExternalBrandMapping;
import com.ryuqq.marketplace.domain.externalbrandmapping.query.ExternalBrandMappingSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/** 외부 브랜드 매핑 검색 Service. */
@Service
public class SearchExternalBrandMappingService implements SearchExternalBrandMappingUseCase {

    private final ExternalBrandMappingQueryFactory queryFactory;
    private final ExternalBrandMappingReadManager readManager;

    public SearchExternalBrandMappingService(
            ExternalBrandMappingQueryFactory queryFactory,
            ExternalBrandMappingReadManager readManager) {
        this.queryFactory = queryFactory;
        this.readManager = readManager;
    }

    @Override
    public ExternalBrandMappingPageResult execute(ExternalBrandMappingSearchParams params) {
        ExternalBrandMappingSearchCriteria criteria = queryFactory.createSearchCriteria(params);
        List<ExternalBrandMapping> mappings = readManager.findByCriteria(criteria);
        long totalElements = readManager.countByCriteria(criteria);
        List<ExternalBrandMappingResult> results =
                mappings.stream().map(ExternalBrandMappingResult::from).toList();
        return ExternalBrandMappingPageResult.of(
                results, criteria.page(), criteria.size(), totalElements);
    }
}
