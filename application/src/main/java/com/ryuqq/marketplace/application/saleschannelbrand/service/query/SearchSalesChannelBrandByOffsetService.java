package com.ryuqq.marketplace.application.saleschannelbrand.service.query;

import com.ryuqq.marketplace.application.saleschannelbrand.assembler.SalesChannelBrandAssembler;
import com.ryuqq.marketplace.application.saleschannelbrand.dto.query.SalesChannelBrandSearchParams;
import com.ryuqq.marketplace.application.saleschannelbrand.dto.response.SalesChannelBrandPageResult;
import com.ryuqq.marketplace.application.saleschannelbrand.factory.SalesChannelBrandQueryFactory;
import com.ryuqq.marketplace.application.saleschannelbrand.manager.SalesChannelBrandReadManager;
import com.ryuqq.marketplace.application.saleschannelbrand.port.in.query.SearchSalesChannelBrandByOffsetUseCase;
import com.ryuqq.marketplace.domain.saleschannelbrand.aggregate.SalesChannelBrand;
import com.ryuqq.marketplace.domain.saleschannelbrand.query.SalesChannelBrandSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/** 외부채널 브랜드 검색 Service (Offset 기반 페이징). */
@Service
public class SearchSalesChannelBrandByOffsetService
        implements SearchSalesChannelBrandByOffsetUseCase {

    private final SalesChannelBrandReadManager readManager;
    private final SalesChannelBrandQueryFactory queryFactory;
    private final SalesChannelBrandAssembler assembler;

    public SearchSalesChannelBrandByOffsetService(
            SalesChannelBrandReadManager readManager,
            SalesChannelBrandQueryFactory queryFactory,
            SalesChannelBrandAssembler assembler) {
        this.readManager = readManager;
        this.queryFactory = queryFactory;
        this.assembler = assembler;
    }

    @Override
    public SalesChannelBrandPageResult execute(SalesChannelBrandSearchParams params) {
        SalesChannelBrandSearchCriteria criteria = queryFactory.createCriteria(params);
        List<SalesChannelBrand> brands = readManager.findByCriteria(criteria);
        long totalElements = readManager.countByCriteria(criteria);
        return assembler.toPageResult(brands, params.page(), params.size(), totalElements);
    }
}
