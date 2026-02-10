package com.ryuqq.marketplace.application.brandmapping.service.query;

import com.ryuqq.marketplace.application.brandmapping.assembler.BrandMappingAssembler;
import com.ryuqq.marketplace.application.brandmapping.dto.query.BrandMappingSearchParams;
import com.ryuqq.marketplace.application.brandmapping.dto.response.BrandMappingPageResult;
import com.ryuqq.marketplace.application.brandmapping.factory.BrandMappingQueryFactory;
import com.ryuqq.marketplace.application.brandmapping.manager.BrandMappingReadManager;
import com.ryuqq.marketplace.application.brandmapping.port.in.query.SearchBrandMappingByOffsetUseCase;
import com.ryuqq.marketplace.domain.brandmapping.aggregate.BrandMapping;
import com.ryuqq.marketplace.domain.brandmapping.query.BrandMappingSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/** 브랜드 매핑 검색 Service (Offset 기반 페이징). */
@Service
public class SearchBrandMappingByOffsetService implements SearchBrandMappingByOffsetUseCase {

    private final BrandMappingReadManager readManager;
    private final BrandMappingQueryFactory queryFactory;
    private final BrandMappingAssembler assembler;

    public SearchBrandMappingByOffsetService(
            BrandMappingReadManager readManager,
            BrandMappingQueryFactory queryFactory,
            BrandMappingAssembler assembler) {
        this.readManager = readManager;
        this.queryFactory = queryFactory;
        this.assembler = assembler;
    }

    @Override
    public BrandMappingPageResult execute(BrandMappingSearchParams params) {
        BrandMappingSearchCriteria criteria = queryFactory.createCriteria(params);
        List<BrandMapping> mappings = readManager.findByCriteria(criteria);
        long totalElements = readManager.countByCriteria(criteria);
        return assembler.toPageResult(mappings, params.page(), params.size(), totalElements);
    }
}
