package com.ryuqq.marketplace.application.outboundproduct.service.query;

import com.ryuqq.marketplace.application.outboundproduct.dto.query.OmsPartnerSearchParams;
import com.ryuqq.marketplace.application.outboundproduct.factory.OmsPartnerQueryFactory;
import com.ryuqq.marketplace.application.outboundproduct.port.in.query.SearchOmsPartnersByOffsetUseCase;
import com.ryuqq.marketplace.application.seller.assembler.SellerAssembler;
import com.ryuqq.marketplace.application.seller.dto.response.SellerPageResult;
import com.ryuqq.marketplace.application.seller.manager.SellerReadManager;
import com.ryuqq.marketplace.domain.seller.aggregate.Seller;
import com.ryuqq.marketplace.domain.seller.query.SellerSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/** OMS 파트너(셀러) 검색 Service (Offset 기반 페이징). */
@Service
public class SearchOmsPartnersByOffsetService implements SearchOmsPartnersByOffsetUseCase {

    private final SellerReadManager readManager;
    private final OmsPartnerQueryFactory queryFactory;
    private final SellerAssembler assembler;

    public SearchOmsPartnersByOffsetService(
            SellerReadManager readManager,
            OmsPartnerQueryFactory queryFactory,
            SellerAssembler assembler) {
        this.readManager = readManager;
        this.queryFactory = queryFactory;
        this.assembler = assembler;
    }

    @Override
    public SellerPageResult execute(OmsPartnerSearchParams params) {
        SellerSearchCriteria criteria = queryFactory.createCriteria(params);

        List<Seller> sellers = readManager.findByCriteria(criteria);
        long totalElements = readManager.countByCriteria(criteria);

        return assembler.toPageResult(sellers, params.page(), params.size(), totalElements);
    }
}
