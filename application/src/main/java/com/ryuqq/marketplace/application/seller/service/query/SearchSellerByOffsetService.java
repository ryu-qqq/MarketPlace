package com.ryuqq.marketplace.application.seller.service.query;

import com.ryuqq.marketplace.application.seller.assembler.SellerAssembler;
import com.ryuqq.marketplace.application.seller.dto.query.SellerSearchParams;
import com.ryuqq.marketplace.application.seller.dto.response.SellerPageResult;
import com.ryuqq.marketplace.application.seller.factory.SellerQueryFactory;
import com.ryuqq.marketplace.application.seller.manager.SellerReadManager;
import com.ryuqq.marketplace.application.seller.port.in.query.SearchSellerByOffsetUseCase;
import com.ryuqq.marketplace.domain.seller.aggregate.Seller;
import com.ryuqq.marketplace.domain.seller.query.SellerSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 셀러 검색 Service (Offset 기반 페이징).
 *
 * <p>APP-UC-001: Offset 페이징은 Search{Domain}ByOffsetService
 *
 * <p>QueryFactory를 통해 Params → Criteria 변환
 *
 * <p>Assembler를 통해 SellerPageResult 생성
 */
@Service
public class SearchSellerByOffsetService implements SearchSellerByOffsetUseCase {

    private final SellerReadManager readManager;
    private final SellerQueryFactory queryFactory;
    private final SellerAssembler assembler;

    public SearchSellerByOffsetService(
            SellerReadManager readManager,
            SellerQueryFactory queryFactory,
            SellerAssembler assembler) {
        this.readManager = readManager;
        this.queryFactory = queryFactory;
        this.assembler = assembler;
    }

    @Override
    public SellerPageResult execute(SellerSearchParams params) {
        SellerSearchCriteria criteria = queryFactory.createCriteria(params);

        List<Seller> sellers = readManager.findByCriteria(criteria);
        long totalElements = readManager.countByCriteria(criteria);

        return assembler.toPageResult(sellers, params.page(), params.size(), totalElements);
    }
}
