package com.ryuqq.marketplace.application.externalcategorymapping.service.query;

import com.ryuqq.marketplace.application.externalcategorymapping.dto.query.ExternalCategoryMappingSearchParams;
import com.ryuqq.marketplace.application.externalcategorymapping.dto.response.ExternalCategoryMappingPageResult;
import com.ryuqq.marketplace.application.externalcategorymapping.dto.response.ExternalCategoryMappingResult;
import com.ryuqq.marketplace.application.externalcategorymapping.factory.ExternalCategoryMappingQueryFactory;
import com.ryuqq.marketplace.application.externalcategorymapping.manager.ExternalCategoryMappingReadManager;
import com.ryuqq.marketplace.application.externalcategorymapping.port.in.query.SearchExternalCategoryMappingUseCase;
import com.ryuqq.marketplace.domain.externalcategorymapping.aggregate.ExternalCategoryMapping;
import com.ryuqq.marketplace.domain.externalcategorymapping.query.ExternalCategoryMappingSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/** 외부 카테고리 매핑 검색 Service. */
@Service
public class SearchExternalCategoryMappingService implements SearchExternalCategoryMappingUseCase {

    private final ExternalCategoryMappingQueryFactory queryFactory;
    private final ExternalCategoryMappingReadManager readManager;

    public SearchExternalCategoryMappingService(
            ExternalCategoryMappingQueryFactory queryFactory,
            ExternalCategoryMappingReadManager readManager) {
        this.queryFactory = queryFactory;
        this.readManager = readManager;
    }

    @Override
    public ExternalCategoryMappingPageResult execute(ExternalCategoryMappingSearchParams params) {
        ExternalCategoryMappingSearchCriteria criteria = queryFactory.createSearchCriteria(params);
        List<ExternalCategoryMapping> mappings = readManager.findByCriteria(criteria);
        long totalElements = readManager.countByCriteria(criteria);
        List<ExternalCategoryMappingResult> results =
                mappings.stream().map(ExternalCategoryMappingResult::from).toList();
        return ExternalCategoryMappingPageResult.of(
                results, criteria.page(), criteria.size(), totalElements);
    }
}
