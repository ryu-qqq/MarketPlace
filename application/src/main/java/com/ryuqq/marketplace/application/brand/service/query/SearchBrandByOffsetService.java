package com.ryuqq.marketplace.application.brand.service.query;

import com.ryuqq.marketplace.application.brand.assembler.BrandAssembler;
import com.ryuqq.marketplace.application.brand.dto.query.BrandSearchParams;
import com.ryuqq.marketplace.application.brand.dto.response.BrandPageResult;
import com.ryuqq.marketplace.application.brand.factory.BrandQueryFactory;
import com.ryuqq.marketplace.application.brand.manager.BrandReadManager;
import com.ryuqq.marketplace.application.brand.port.in.query.SearchBrandByOffsetUseCase;
import com.ryuqq.marketplace.domain.brand.aggregate.Brand;
import com.ryuqq.marketplace.domain.brand.query.BrandSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/** 브랜드 검색 Service (Offset 기반 페이징). */
@Service
public class SearchBrandByOffsetService implements SearchBrandByOffsetUseCase {

    private final BrandReadManager readManager;
    private final BrandQueryFactory queryFactory;
    private final BrandAssembler assembler;

    public SearchBrandByOffsetService(
            BrandReadManager readManager,
            BrandQueryFactory queryFactory,
            BrandAssembler assembler) {
        this.readManager = readManager;
        this.queryFactory = queryFactory;
        this.assembler = assembler;
    }

    @Override
    public BrandPageResult execute(BrandSearchParams params) {
        BrandSearchCriteria criteria = queryFactory.createCriteria(params);
        List<Brand> brands = readManager.findByCriteria(criteria);
        long totalElements = readManager.countByCriteria(criteria);
        return assembler.toPageResult(brands, params.page(), params.size(), totalElements);
    }
}
