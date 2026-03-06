package com.ryuqq.marketplace.application.productgroup.service.query;

import com.ryuqq.marketplace.application.category.manager.CategoryReadManager;
import com.ryuqq.marketplace.application.productgroup.assembler.ProductGroupAssembler;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupListBundle;
import com.ryuqq.marketplace.application.productgroup.dto.query.ProductGroupSearchParams;
import com.ryuqq.marketplace.application.productgroup.dto.response.ProductGroupPageResult;
import com.ryuqq.marketplace.application.productgroup.factory.ProductGroupQueryFactory;
import com.ryuqq.marketplace.application.productgroup.internal.ProductGroupReadFacade;
import com.ryuqq.marketplace.application.productgroup.port.in.query.SearchProductGroupByOffsetUseCase;
import com.ryuqq.marketplace.domain.productgroup.query.ProductGroupSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/** 상품 그룹 검색 Service (Offset 기반 페이징). */
@Service
public class SearchProductGroupByOffsetService implements SearchProductGroupByOffsetUseCase {

    private final ProductGroupReadFacade readFacade;
    private final ProductGroupQueryFactory queryFactory;
    private final ProductGroupAssembler assembler;
    private final CategoryReadManager categoryReadManager;

    public SearchProductGroupByOffsetService(
            ProductGroupReadFacade readFacade,
            ProductGroupQueryFactory queryFactory,
            ProductGroupAssembler assembler,
            CategoryReadManager categoryReadManager) {
        this.readFacade = readFacade;
        this.queryFactory = queryFactory;
        this.assembler = assembler;
        this.categoryReadManager = categoryReadManager;
    }

    @Override
    public ProductGroupPageResult execute(ProductGroupSearchParams params) {
        ProductGroupSearchParams expandedParams = expandCategoryIds(params);
        ProductGroupSearchCriteria criteria = queryFactory.createCriteria(expandedParams);
        ProductGroupListBundle bundle = readFacade.getListBundle(criteria);
        return assembler.toPageResult(bundle, criteria.page(), criteria.size());
    }

    private ProductGroupSearchParams expandCategoryIds(ProductGroupSearchParams params) {
        if (params.categoryIds() == null || params.categoryIds().isEmpty()) {
            return params;
        }
        List<Long> expandedIds = categoryReadManager.expandWithDescendants(params.categoryIds());
        return params.withCategoryIds(expandedIds);
    }
}
