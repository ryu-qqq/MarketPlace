package com.ryuqq.marketplace.application.categorypreset.service.query;

import com.ryuqq.marketplace.application.categorypreset.assembler.CategoryPresetAssembler;
import com.ryuqq.marketplace.application.categorypreset.dto.query.CategoryPresetSearchParams;
import com.ryuqq.marketplace.application.categorypreset.dto.response.CategoryPresetPageResult;
import com.ryuqq.marketplace.application.categorypreset.dto.response.CategoryPresetResult;
import com.ryuqq.marketplace.application.categorypreset.factory.CategoryPresetQueryFactory;
import com.ryuqq.marketplace.application.categorypreset.manager.CategoryPresetReadManager;
import com.ryuqq.marketplace.application.categorypreset.port.in.query.SearchCategoryPresetByOffsetUseCase;
import com.ryuqq.marketplace.domain.categorypreset.query.CategoryPresetSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/** 카테고리 프리셋 검색 Service (Offset 기반 페이징). */
@Service
public class SearchCategoryPresetByOffsetService implements SearchCategoryPresetByOffsetUseCase {

    private final CategoryPresetReadManager readManager;
    private final CategoryPresetQueryFactory queryFactory;
    private final CategoryPresetAssembler assembler;

    public SearchCategoryPresetByOffsetService(
            CategoryPresetReadManager readManager,
            CategoryPresetQueryFactory queryFactory,
            CategoryPresetAssembler assembler) {
        this.readManager = readManager;
        this.queryFactory = queryFactory;
        this.assembler = assembler;
    }

    @Override
    public CategoryPresetPageResult execute(CategoryPresetSearchParams params) {
        CategoryPresetSearchCriteria criteria = queryFactory.createCriteria(params);
        List<CategoryPresetResult> results = readManager.findByCriteria(criteria);
        long totalElements = readManager.countByCriteria(criteria);
        return assembler.toPageResult(results, params.page(), params.size(), totalElements);
    }
}
