package com.ryuqq.marketplace.application.saleschannelcategory.service.query;

import com.ryuqq.marketplace.application.saleschannelcategory.assembler.SalesChannelCategoryAssembler;
import com.ryuqq.marketplace.application.saleschannelcategory.dto.query.SalesChannelCategorySearchParams;
import com.ryuqq.marketplace.application.saleschannelcategory.dto.response.SalesChannelCategoryPageResult;
import com.ryuqq.marketplace.application.saleschannelcategory.factory.SalesChannelCategoryQueryFactory;
import com.ryuqq.marketplace.application.saleschannelcategory.manager.SalesChannelCategoryReadManager;
import com.ryuqq.marketplace.application.saleschannelcategory.port.in.query.SearchSalesChannelCategoryByOffsetUseCase;
import com.ryuqq.marketplace.domain.saleschannelcategory.aggregate.SalesChannelCategory;
import com.ryuqq.marketplace.domain.saleschannelcategory.query.SalesChannelCategorySearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/** 외부 채널 카테고리 검색 Service (Offset 기반 페이징). */
@Service
public class SearchSalesChannelCategoryByOffsetService
        implements SearchSalesChannelCategoryByOffsetUseCase {

    private final SalesChannelCategoryReadManager readManager;
    private final SalesChannelCategoryQueryFactory queryFactory;
    private final SalesChannelCategoryAssembler assembler;

    public SearchSalesChannelCategoryByOffsetService(
            SalesChannelCategoryReadManager readManager,
            SalesChannelCategoryQueryFactory queryFactory,
            SalesChannelCategoryAssembler assembler) {
        this.readManager = readManager;
        this.queryFactory = queryFactory;
        this.assembler = assembler;
    }

    @Override
    public SalesChannelCategoryPageResult execute(SalesChannelCategorySearchParams params) {
        SalesChannelCategorySearchCriteria criteria = queryFactory.createCriteria(params);
        List<SalesChannelCategory> categories = readManager.findByCriteria(criteria);
        long totalElements = readManager.countByCriteria(criteria);
        return assembler.toPageResult(categories, params.page(), params.size(), totalElements);
    }
}
