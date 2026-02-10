package com.ryuqq.marketplace.application.categorymapping.service.query;

import com.ryuqq.marketplace.application.categorymapping.assembler.CategoryMappingAssembler;
import com.ryuqq.marketplace.application.categorymapping.dto.query.CategoryMappingSearchParams;
import com.ryuqq.marketplace.application.categorymapping.dto.response.CategoryMappingPageResult;
import com.ryuqq.marketplace.application.categorymapping.factory.CategoryMappingQueryFactory;
import com.ryuqq.marketplace.application.categorymapping.manager.CategoryMappingReadManager;
import com.ryuqq.marketplace.application.categorymapping.port.in.query.SearchCategoryMappingByOffsetUseCase;
import com.ryuqq.marketplace.domain.categorymapping.aggregate.CategoryMapping;
import com.ryuqq.marketplace.domain.categorymapping.query.CategoryMappingSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/** 카테고리 매핑 검색 Service (Offset 기반 페이징). */
@Service
public class SearchCategoryMappingByOffsetService implements SearchCategoryMappingByOffsetUseCase {

    private final CategoryMappingReadManager readManager;
    private final CategoryMappingQueryFactory queryFactory;
    private final CategoryMappingAssembler assembler;

    public SearchCategoryMappingByOffsetService(
            CategoryMappingReadManager readManager,
            CategoryMappingQueryFactory queryFactory,
            CategoryMappingAssembler assembler) {
        this.readManager = readManager;
        this.queryFactory = queryFactory;
        this.assembler = assembler;
    }

    @Override
    public CategoryMappingPageResult execute(CategoryMappingSearchParams params) {
        CategoryMappingSearchCriteria criteria = queryFactory.createCriteria(params);
        List<CategoryMapping> mappings = readManager.findByCriteria(criteria);
        long totalElements = readManager.countByCriteria(criteria);
        return assembler.toPageResult(mappings, params.page(), params.size(), totalElements);
    }
}
