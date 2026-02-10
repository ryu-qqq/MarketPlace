package com.ryuqq.marketplace.application.brandpreset.service.query;

import com.ryuqq.marketplace.application.brandpreset.assembler.BrandPresetAssembler;
import com.ryuqq.marketplace.application.brandpreset.dto.query.BrandPresetSearchParams;
import com.ryuqq.marketplace.application.brandpreset.dto.response.BrandPresetPageResult;
import com.ryuqq.marketplace.application.brandpreset.dto.response.BrandPresetResult;
import com.ryuqq.marketplace.application.brandpreset.factory.BrandPresetQueryFactory;
import com.ryuqq.marketplace.application.brandpreset.manager.BrandPresetReadManager;
import com.ryuqq.marketplace.application.brandpreset.port.in.query.SearchBrandPresetByOffsetUseCase;
import com.ryuqq.marketplace.domain.brandpreset.query.BrandPresetSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/** 브랜드 프리셋 검색 Service (Offset 기반 페이징). */
@Service
public class SearchBrandPresetByOffsetService implements SearchBrandPresetByOffsetUseCase {

    private final BrandPresetReadManager readManager;
    private final BrandPresetQueryFactory queryFactory;
    private final BrandPresetAssembler assembler;

    public SearchBrandPresetByOffsetService(
            BrandPresetReadManager readManager,
            BrandPresetQueryFactory queryFactory,
            BrandPresetAssembler assembler) {
        this.readManager = readManager;
        this.queryFactory = queryFactory;
        this.assembler = assembler;
    }

    @Override
    public BrandPresetPageResult execute(BrandPresetSearchParams params) {
        BrandPresetSearchCriteria criteria = queryFactory.createCriteria(params);
        List<BrandPresetResult> results = readManager.findByCriteria(criteria);
        long totalElements = readManager.countByCriteria(criteria);
        return assembler.toPageResult(results, criteria.page(), criteria.size(), totalElements);
    }
}
