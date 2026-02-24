package com.ryuqq.marketplace.application.productgroup.service.query;

import com.ryuqq.marketplace.application.productgroup.assembler.ProductGroupAssembler;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupExcelBundle;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupExcelCompositeResult;
import com.ryuqq.marketplace.application.productgroup.dto.query.ProductGroupSearchParams;
import com.ryuqq.marketplace.application.productgroup.factory.ProductGroupQueryFactory;
import com.ryuqq.marketplace.application.productgroup.internal.ProductGroupReadFacade;
import com.ryuqq.marketplace.application.productgroup.port.in.query.SearchProductGroupForExcelUseCase;
import com.ryuqq.marketplace.domain.productgroup.query.ProductGroupSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/** 상품 그룹 엑셀 다운로드용 검색 Service. */
@Service
public class SearchProductGroupForExcelService implements SearchProductGroupForExcelUseCase {

    private final ProductGroupReadFacade readFacade;
    private final ProductGroupQueryFactory queryFactory;
    private final ProductGroupAssembler assembler;

    public SearchProductGroupForExcelService(
            ProductGroupReadFacade readFacade,
            ProductGroupQueryFactory queryFactory,
            ProductGroupAssembler assembler) {
        this.readFacade = readFacade;
        this.queryFactory = queryFactory;
        this.assembler = assembler;
    }

    @Override
    public List<ProductGroupExcelCompositeResult> execute(ProductGroupSearchParams params) {
        ProductGroupSearchCriteria criteria = queryFactory.createCriteria(params);
        ProductGroupExcelBundle bundle = readFacade.getExcelBundle(criteria);
        return assembler.toExcelResults(bundle);
    }
}
