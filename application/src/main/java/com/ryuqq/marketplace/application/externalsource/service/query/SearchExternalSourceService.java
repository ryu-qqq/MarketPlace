package com.ryuqq.marketplace.application.externalsource.service.query;

import com.ryuqq.marketplace.application.externalsource.dto.query.ExternalSourceSearchParams;
import com.ryuqq.marketplace.application.externalsource.dto.response.ExternalSourcePageResult;
import com.ryuqq.marketplace.application.externalsource.dto.response.ExternalSourceResult;
import com.ryuqq.marketplace.application.externalsource.manager.ExternalSourceReadManager;
import com.ryuqq.marketplace.application.externalsource.port.in.query.SearchExternalSourceUseCase;
import com.ryuqq.marketplace.domain.externalsource.aggregate.ExternalSource;
import java.util.List;
import org.springframework.stereotype.Service;

/** 외부 소스 검색 Service. */
@Service
public class SearchExternalSourceService implements SearchExternalSourceUseCase {

    private final ExternalSourceReadManager readManager;

    public SearchExternalSourceService(ExternalSourceReadManager readManager) {
        this.readManager = readManager;
    }

    @Override
    public ExternalSourcePageResult execute(ExternalSourceSearchParams params) {
        List<ExternalSource> sources = readManager.findByCriteria(params);
        long totalElements = readManager.countByCriteria(params);
        List<ExternalSourceResult> results =
                sources.stream().map(ExternalSourceResult::from).toList();
        return ExternalSourcePageResult.of(results, params.page(), params.size(), totalElements);
    }
}
