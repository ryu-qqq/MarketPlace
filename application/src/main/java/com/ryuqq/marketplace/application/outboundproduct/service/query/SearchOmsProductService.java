package com.ryuqq.marketplace.application.outboundproduct.service.query;

import com.ryuqq.marketplace.application.outboundproduct.dto.query.OmsProductSearchParams;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.OmsProductListResult;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.OmsProductPageResult;
import com.ryuqq.marketplace.application.outboundproduct.factory.OmsProductQueryFactory;
import com.ryuqq.marketplace.application.outboundproduct.manager.OmsProductCompositionReadManager;
import com.ryuqq.marketplace.application.outboundproduct.port.in.query.SearchOmsProductUseCase;
import com.ryuqq.marketplace.domain.outboundproduct.query.OmsProductSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/** OMS 상품 목록 조회 Service. */
@Service
public class SearchOmsProductService implements SearchOmsProductUseCase {

    private final OmsProductCompositionReadManager compositionReadManager;
    private final OmsProductQueryFactory queryFactory;

    public SearchOmsProductService(
            OmsProductCompositionReadManager compositionReadManager,
            OmsProductQueryFactory queryFactory) {
        this.compositionReadManager = compositionReadManager;
        this.queryFactory = queryFactory;
    }

    @Override
    public OmsProductPageResult execute(OmsProductSearchParams params) {
        OmsProductSearchCriteria criteria = queryFactory.createCriteria(params);

        List<OmsProductListResult> results = compositionReadManager.findByCriteria(criteria);
        long totalElements = compositionReadManager.countByCriteria(criteria);

        return OmsProductPageResult.of(results, params.page(), params.size(), totalElements);
    }
}
