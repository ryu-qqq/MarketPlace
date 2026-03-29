package com.ryuqq.marketplace.application.legacy.productgroup.service.query;

import com.ryuqq.marketplace.application.legacy.productgroup.dto.query.LegacyProductGroupSearchParams;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.response.LegacyProductGroupPageResult;
import com.ryuqq.marketplace.application.legacy.productgroup.factory.LegacyProductGroupQueryFactory;
import com.ryuqq.marketplace.application.legacy.productgroup.port.in.query.LegacySearchProductGroupByOffsetUseCase;
import com.ryuqq.marketplace.application.legacy.shared.assembler.LegacyProductGroupFromMarketAssembler;
import com.ryuqq.marketplace.application.product.dto.response.ProductResult;
import com.ryuqq.marketplace.application.productgroup.dto.query.ProductGroupSearchParams;
import com.ryuqq.marketplace.application.productgroup.dto.response.ProductGroupPageResult;
import com.ryuqq.marketplace.application.productgroup.port.in.query.SearchProductGroupByOffsetUseCase;
import com.ryuqq.marketplace.application.productgroup.port.out.query.ProductGroupCompositionQueryPort;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * 레거시 상품그룹 목록 조회 서비스 (Offset 기반 페이징).
 *
 * <p>레거시 검색 파라미터를 표준으로 변환 후, 표준 UseCase로 market 스키마에서 조회합니다.
 */
@Service
public class LegacySearchProductGroupByOffsetService
        implements LegacySearchProductGroupByOffsetUseCase {

    private final SearchProductGroupByOffsetUseCase searchUseCase;
    private final LegacyProductGroupQueryFactory queryFactory;
    private final LegacyProductGroupFromMarketAssembler assembler;
    private final ProductGroupCompositionQueryPort compositionQueryPort;

    public LegacySearchProductGroupByOffsetService(
            SearchProductGroupByOffsetUseCase searchUseCase,
            LegacyProductGroupQueryFactory queryFactory,
            LegacyProductGroupFromMarketAssembler assembler,
            ProductGroupCompositionQueryPort compositionQueryPort) {
        this.searchUseCase = searchUseCase;
        this.queryFactory = queryFactory;
        this.assembler = assembler;
        this.compositionQueryPort = compositionQueryPort;
    }

    @Override
    public LegacyProductGroupPageResult execute(LegacyProductGroupSearchParams params) {
        ProductGroupSearchParams standardParams = queryFactory.toStandardSearchParams(params);
        ProductGroupPageResult pageResult = searchUseCase.execute(standardParams);

        List<Long> productGroupIds =
                pageResult.results().stream()
                        .map(r -> r.id())
                        .toList();

        Map<Long, List<ProductResult>> productsMap =
                compositionQueryPort.findProductsWithOptionNamesByProductGroupIds(productGroupIds);

        return assembler.toPageResult(pageResult, productsMap, params.page(), params.size());
    }
}
