package com.ryuqq.marketplace.application.externalsource.service.query;

import com.ryuqq.marketplace.application.externalsource.dto.query.ExternalSourceSearchParams;
import com.ryuqq.marketplace.application.externalsource.dto.response.ExternalSourcePageResult;
import com.ryuqq.marketplace.application.externalsource.dto.response.ExternalSourceResult;
import com.ryuqq.marketplace.application.externalsource.factory.ExternalSourceQueryFactory;
import com.ryuqq.marketplace.application.externalsource.manager.ExternalSourceReadManager;
import com.ryuqq.marketplace.application.externalsource.port.in.query.SearchExternalSourceUseCase;
import com.ryuqq.marketplace.domain.externalsource.aggregate.ExternalSource;
import com.ryuqq.marketplace.domain.externalsource.query.ExternalSourceSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/** 외부 소스 검색 Service. */
@Service
public class SearchExternalSourceService implements SearchExternalSourceUseCase {

    private final ExternalSourceQueryFactory queryFactory;
    private final ExternalSourceReadManager readManager;

    public SearchExternalSourceService(
            ExternalSourceQueryFactory queryFactory, ExternalSourceReadManager readManager) {
        this.queryFactory = queryFactory;
        this.readManager = readManager;
    }

    @Override
    public ExternalSourcePageResult execute(ExternalSourceSearchParams params) {
        ExternalSourceSearchCriteria criteria = queryFactory.createSearchCriteria(params);
        List<ExternalSource> sources = readManager.findByCriteria(criteria);
        long totalElements = readManager.countByCriteria(criteria);
        List<ExternalSourceResult> results =
                sources.stream().map(ExternalSourceResult::from).toList();
        return ExternalSourcePageResult.of(
                results, criteria.page(), criteria.size(), totalElements);
    }
}
