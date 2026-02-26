package com.ryuqq.marketplace.application.category.service.query;

import com.ryuqq.marketplace.application.category.assembler.CategoryAssembler;
import com.ryuqq.marketplace.application.category.dto.query.CategorySearchParams;
import com.ryuqq.marketplace.application.category.dto.response.CategoryPageResult;
import com.ryuqq.marketplace.application.category.factory.CategoryQueryFactory;
import com.ryuqq.marketplace.application.category.manager.CategoryReadManager;
import com.ryuqq.marketplace.application.category.port.in.query.SearchCategoryByOffsetUseCase;
import com.ryuqq.marketplace.domain.category.aggregate.Category;
import com.ryuqq.marketplace.domain.category.query.CategorySearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/** 카테고리 검색 Service (Offset 기반 페이징). */
@Service
public class SearchCategoryByOffsetService implements SearchCategoryByOffsetUseCase {

    private final CategoryReadManager readManager;
    private final CategoryQueryFactory queryFactory;
    private final CategoryAssembler assembler;

    public SearchCategoryByOffsetService(
            CategoryReadManager readManager,
            CategoryQueryFactory queryFactory,
            CategoryAssembler assembler) {
        this.readManager = readManager;
        this.queryFactory = queryFactory;
        this.assembler = assembler;
    }

    @Override
    public CategoryPageResult execute(CategorySearchParams params) {
        CategorySearchCriteria criteria = queryFactory.createCriteria(params);
        List<Category> categories = readManager.findByCriteria(criteria);
        long totalElements = readManager.countByCriteria(criteria);
        return assembler.toPageResult(categories, params.page(), params.size(), totalElements);
    }
}
